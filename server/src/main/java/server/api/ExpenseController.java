package server.api;

import commons.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.*;
import server.service.ExpenseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository repoExp;
    private final ExpenseEventRepository repoExpEv;
    private final TagRepository repoTag;
    private final ExpenseService service;
    public ExpenseController(ExpenseRepository repoExp, ExpenseEventRepository repoExpEv,
                             TagRepository repoTag,
                             ExpenseService service) {
        this.repoExp = repoExp;
        this.repoExpEv = repoExpEv;
        this.repoTag = repoTag;
        this.service = service;
    }
    //here to put the POST APIs

    /**
     * A function to add an expense to an event and to create Expense-Event connection
     * @param eventId the id of the event
     * @param expense the expense
     * @return the expense which was added
     */
    @PostMapping(path = { "/saved"})
    public ResponseEntity<Expense> addExpenseToEvent(@RequestParam("eventId") long eventId,
                                                     @RequestBody Expense expense)
    {
        if (expense==null) {
            System.out.println("is null");
            return ResponseEntity.badRequest().build();
        }
        //for storing it in the database
        Expense saved=repoExp.save(expense);
        System.out.println("expense saved old:"+expense);

        //create ExpenseEvent connection
        repoExpEv.save(new ExpenseEvent(saved.getExpenseId(),eventId));
        //we need this line because if someone is still playing with this expense, we need it to be complete.
        expense.setExpenseId(saved.getExpenseId());
        //to save with id
        listeners.forEach((k, l) -> l.accept(saved));
        return ResponseEntity.ok(saved);
    }

    @Transactional
    @MessageMapping("expenses/tag/{eventId}")
    public Expense addExpenseMessage(@DestinationVariable @NonNull Long eventId, @Payload Expense expense) {
            return addExpenseToEvent(eventId, expense).getBody();
    }

    @PostMapping(path = { "/tags"})
    public ResponseEntity<Tag> addTag(@RequestBody Tag tag) {
        if (tag==null) {
            return ResponseEntity.badRequest().build();
        }
        if(tag.getId()==null)
        {
            return ResponseEntity.notFound().build();
        }
        if(repoTag.existsById(new TagId(tag.getId().getName(),tag.getId().getEventId())))
        {
            return ResponseEntity.notFound().build();
        }

        Tag saved=repoTag.save(tag);
        return ResponseEntity.ok(saved);
    }

    @MessageMapping("/expenses")
    @SendTo("/topic/expenses")
    public Tag addTagMessage(Tag tag) {
        addTag(tag);
        return tag;
    }

    @GetMapping(path={"/"})
    public ResponseEntity<Expense> getExpenseById(@RequestParam("expenseId") long expenseId)
    {
        if(!repoExp.existsById(expenseId))
            return ResponseEntity.notFound().build();
        Expense ex=repoExp.findById(expenseId).get();
        return ResponseEntity.ok(ex);
    }
    @GetMapping(path={"/deletedDebts"})
    public ResponseEntity<Boolean> resetDebtsFromExpenseId(@RequestParam("eventId") long eventId,
                                                           @RequestParam("expenseId") long expenseId)
    {
        if(!repoExp.existsById(expenseId))
            return ResponseEntity.notFound().build();
        Expense ex=repoExp.findById(expenseId).get();
        service.resetDebtsFromThisExpense(ex,eventId);
        return ResponseEntity.ok(true);
    }
    /**
     * This is the function that we use in the event page.
     * @param eventId event id
     * @param authorId author id
     * @return their expenses
     */
    @GetMapping(path={"/author"})
    public ResponseEntity<List<Expense>> getExpenseByAuthorInEvent(@RequestParam("eventId") long eventId,
                                                   @RequestParam("authorId") long authorId)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        //initially take all expenses
        List<Expense> ans=getAllFromEvent(eventId).getBody();
        if(ans==null)
            return ResponseEntity.notFound().build();
        //filter them
        ans=ans.stream().filter(x-> {
            //if he is the author
            if (x.getAuthor().getParticipantID() == authorId)
                return true;
            return false;
        }).toList();
        if(ans.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ans);
    }
    @GetMapping(path={"/participantIncluded"})
    public ResponseEntity<List<Expense>> getExpensePInvolvedInEvent(@RequestParam("eventId") long eventId,
                                                                    @RequestParam("authorId") long authorId)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        //initially take all expenses
        List<Expense> ans=getAllFromEvent(eventId).getBody();
        if(ans==null)
            return ResponseEntity.notFound().build();
        //filter them
        ans=ans.stream().filter(x->{
            //if he is the author
            if(x.getAuthor().getParticipantID()==authorId)
                return true;
            //if he is included
            List<Long> participantIds=x.getParticipants().stream().map(y->y.getParticipantID()).toList();
            if(participantIds.contains(authorId))
                return true;
            return false;
        }).toList();
        System.out.println(ans);
        if(ans.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ans);
    }

    @GetMapping(path={"/allFromEvent"})
    public ResponseEntity<List<Expense>> getAllFromEvent(@RequestParam("eventId") long eventId)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        List<Expense> ans=repoExp.findAllExpOfAnEvent(eventId);
        return ResponseEntity.ok(ans);
    }
    @GetMapping(path={"/all"})
    public ResponseEntity<List<Expense>> getAll()
    {
        List<Expense> ans=repoExp.findAll();
        return ResponseEntity.ok(ans);
    }

    private Map<Object, Consumer<Expense>> listeners = new HashMap<>();
    @GetMapping(path={"/allFromEvent/updates"})
    public DeferredResult<ResponseEntity<Expense>> getUpdatesFromEvent(@RequestParam("eventId") long eventId) {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Expense>>(5000L, noContent);
        var key = new Object();
        listeners.put(key, e -> {
            res.setResult(ResponseEntity.ok(e));
        });
        res.onCompletion(() -> {
            listeners.remove(key);
        });
        return res;
    }

    @GetMapping(path={"/tags"})
    public ResponseEntity<Tag> getTag(@RequestParam("tag") String tagName,@RequestParam("eventId") long eventId)
    {
        if(repoTag.existsById(new TagId(tagName,eventId)))
        {
            Tag tag=repoTag.getTagByIdFromEvent(tagName,eventId);
            return ResponseEntity.ok(tag);

        }
        else
         return ResponseEntity.notFound().build();
    }
    @GetMapping(path={"/allTags"})
    public ResponseEntity<List<Tag>> getAllTagsFromEvent(@RequestParam("eventId") long eventId)
    {
        List<Tag> tags= repoTag.getAllTagsFromEvent(eventId);
        return ResponseEntity.ok(tags);
    }
    //here to put the PUT APIs (update)


    @Transactional
    @MessageMapping("expenses/edits/{expenseId}")
    public Expense updateExpenseMessage(@DestinationVariable @NonNull Long expenseId, @Payload Expense expense) {
        updateExpense(expenseId, expense);
        return expense;
    }
    /**
     * This functions updates the content of an expense and its participants. It doesn t update the
     * debts. (This should be handled in the add expense controller)
     * @param expenseId the id of the expense
     * @param expense the new content of the expense which has an invalid id (we use expenseId)
     * @return the new expense with expenseId as id
     */
    @PutMapping(path={"/"})
    public ResponseEntity<Expense> updateExpense(@RequestParam("expenseId") long expenseId,
                                                 @RequestBody Expense expense)
    {
        if(expenseId<0)
            return ResponseEntity.badRequest().build();
        if(!repoExp.existsById(expenseId))
            return ResponseEntity.notFound().build();
        //get the old expense
        Expense oldExpense=repoExp.findById(expenseId).get();
        //update the old expense
        oldExpense.setAuthor(expense.getAuthor());
        oldExpense.setContent(expense.getContent());
        oldExpense.setMoney(expense.getMoney());
        oldExpense.setCurrency(expense.getCurrency());
        oldExpense.setDate(expense.getDate());
        oldExpense.setType(expense.getType());
        //updates participants
        oldExpense.setParticipants(expense.getParticipants());
        Expense newExpense=repoExp.save(oldExpense);

        return ResponseEntity.ok(newExpense);
    }
    @PutMapping(path={"/tags"})
    public ResponseEntity<Tag> updateTag(@RequestParam("tagName") String tagName,
                                             @RequestParam("eventId") long eventId,
                                             @RequestBody Tag tag)
    {
        //tagName is th the name of the current tag. tag.getId().getName() is the new name
        if(!repoTag.existsById(new TagId(tagName,eventId)))
            return ResponseEntity.notFound().build();
        repoTag.updateTag(tagName,eventId,tag.getId().getName(),tag.getColor());
        Tag newTag=repoTag.getTagByIdFromEvent(tag.getId().getName(),eventId);
        if(tag==null)
            return ResponseEntity.status(304).build();//not modified
        //we need to update all the expenses from this event that had tagName as type
        List<Expense> expensesOfEvent=repoExp.findAllExpOfAnEvent(eventId);
        for(Expense e:expensesOfEvent)
            if(e.getType().equals(tagName))
            {
                e.setType(tag.getId().getName());
                repoExp.save(e);
            }
        return ResponseEntity.ok(newTag);
    }
    //here to put the DELETE APIs


    @Transactional
    @MessageMapping("expenses/{eventId}")
    public Expense deleteExpenseMessage(@DestinationVariable @NonNull Long eventId, @Payload Expense expense) {
        deleteExpById(eventId, expense.getExpenseId());
        return expense;
    }

    /**
     * This function deletes an expense from an event, and it's
     * expense-event connection with the event. If the expense-event
     * connection is missing, then the expense won't be removed
     * The only exception is when the eventId is 0, but this is just for testing!
     * @param eventId id of the event
     * @param expenseId id of the expense
     * @return true if everything is alright
     */
    @DeleteMapping(path={"/"})
    public ResponseEntity<Boolean> deleteExpById(@RequestParam("eventId")long eventId,
                                                 @RequestParam("expenseId") long expenseId)
    {
        if(eventId<0 || expenseId<0)
            return ResponseEntity.badRequest().build();

        if(!repoExp.existsById(expenseId))
            return ResponseEntity.notFound().build();
        //first we need to delete the connection with the event
        Integer a=repoExp.deleteExpenseEventCon(eventId,expenseId);
        if(a==0)
        {
            System.out.println("The connection between the event and the expense was not deleted");
            //444-no response
            //we use eventId=0 to be able to delete an expense that has no eventId
            //to use only for testing
            if(eventId!=0)
                return ResponseEntity.status(444).build();
        }
        //now let s change the debts
        Expense ex=repoExp.findById(expenseId).get();
        service.resetDebtsFromThisExpense(ex,eventId);
        //then we delete the expense
        ex.setParticipants(null);
        repoExp.deleteById(expenseId);
        if(repoExp.existsById(expenseId))
        {
            System.out.println("Something was not deleted, be careful");
            //417 expectation failed
            return ResponseEntity.status(417).build();
        }
        return ResponseEntity.ok(true);
    }

    /**
     * This function deletes all expenses from an event and all the
     * expense-event connections.
     * @param eventId id of the event
     * @return the number of expenses deleted
     */
    @DeleteMapping(path={"/allFromEvent"})
    public ResponseEntity<Integer> deleteAllExpensesFromEvent(@RequestParam("eventId")long eventId)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        List<Expense> expenses=repoExp.findAllExpOfAnEvent(eventId);
        //delete all Expense-Event connections
        //delete all expenses related to the event
        if(expenses!=null) {
            for(Expense e:expenses)
                deleteExpById(eventId,e.getExpenseId());
        }
        return ResponseEntity.ok(expenses.size());
    }
    @DeleteMapping (path={"/tags"})
    public ResponseEntity<Tag> deleteTag(@RequestParam("tagName") String tagName,
                                         @RequestParam("eventId")long eventId)
    {
        TagId tagId=new TagId(tagName,eventId);
        if(!repoTag.existsById(tagId))
            return ResponseEntity.notFound().build();
        repoTag.deleteById(new TagId(tagName,eventId));
        //we need to update all the expenses from this event that had tagName as type
        List<Expense> expensesOfEvent=repoExp.findAllExpOfAnEvent(eventId);
        for(Expense e:expensesOfEvent)
            if(e.getType().equals(tagName))
            {
                e.setType("other");
                repoExp.save(e);
            }
        return ResponseEntity.ok().build();
    }
}
