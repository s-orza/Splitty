package server.api;

import commons.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ExpenseEventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantExpenseRepository;
import server.database.TagRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository repoExp;
    private final ExpenseEventRepository repoExpEv;
    private final ParticipantExpenseRepository repoPaExp;
    private final TagRepository repoTag;
    public ExpenseController(ExpenseRepository repoExp, ExpenseEventRepository repoExpEv,
                             ParticipantExpenseRepository repoPaExp, TagRepository repoTag) {
        this.repoExp = repoExp;
        this.repoExpEv = repoExpEv;
        this.repoPaExp = repoPaExp;
        this.repoTag = repoTag;
    }
    public void putParticipants(Expense expense)
    {
        List<Participant> participantList;
        //this line we need to update in future when we would have the APIs for participants
        //List<Long> particpantsIds=repoPaExp.getAllParticipantsIdFromExpense(expense.getExpenseId());
        participantList=new ArrayList<>();
        //expense.setParticipants(participantList);
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
        List<Participant> participants = expense.getParticipants();
        //for storing it in the database
        expense.setParticipants(null);
        Expense saved=repoExp.save(expense);
        System.out.println("expense saved");
        //save participant-expense stuff
        //PARTICIPANTS NEED TO HAVE IDs
        if(participants!=null && !participants.isEmpty()) {
            //now we need to create connections between the expense and the participants
            for (Participant p : participants) {
                ParticipantExpense pe = new ParticipantExpense(saved.getExpenseId(), p.getParticipantID());
                repoPaExp.save(pe);
            }
        }
        //create ExpenseEvent connection
        repoExpEv.save(new ExpenseEvent(saved.getExpenseId(),eventId));
        System.out.println("expenseEvent saved");
        return ResponseEntity.ok(saved);
    }
    @PostMapping(path = { "/s"})
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense)
    {
        if (expense==null) {
            System.out.println("expense is null");
            return ResponseEntity.badRequest().build();
        }
        List<Participant> participants=expense.getParticipants();
        //in order to be able to store it in the database
        expense.setParticipants(null);
        Expense saved=repoExp.save(expense);
        System.out.println("saved");
        if(participants!=null && !participants.isEmpty()) {
            //now we need to create connections between the expense and the participants
            for (Participant p : participants) {
                ParticipantExpense pe = new ParticipantExpense(saved.getExpenseId(), p.getParticipantID());
                repoPaExp.save(pe);
            }
        }
        return ResponseEntity.ok(saved);
    }
    @PostMapping(path = { "/tags"})
    public ResponseEntity<Tag> addTag(@RequestBody Tag tag) {
        if (tag==null) {
            System.out.println("tag is null");
            return ResponseEntity.badRequest().build();
        }
        if(repoTag.existsById(tag.getName()))
        {
            System.out.println("Already in the database");
            return ResponseEntity.badRequest().build();
        }
        repoTag.save(tag);
        Tag saved=repoTag.findById(tag.getName()).get();
        return ResponseEntity.ok(saved);
    }
    //here to put the GET APIs
    /**
     *
     * @param expenseId the id of an expense
     * @return it returns the expense with that id
     */
    @GetMapping(path={"/"})
    public ResponseEntity<Expense> getExpenseById(@RequestParam("id") long expenseId)
    {
        if(expenseId<0)
            return ResponseEntity.badRequest().build();
        if(!repoExp.existsById(expenseId))
            return ResponseEntity.notFound().build();
        System.out.println(expenseId);
        Expense expense=repoExp.findById(expenseId).get();
        putParticipants(expense);
        return ResponseEntity.ok(expense);
    }
    @GetMapping(path={"/author"})
    public ResponseEntity<List<Expense>> getExpenseByAuthorName(@RequestParam("authorId") long authorId)
    {
        List<Expense> ans=repoExp.findByAuthor(authorId);
        for(Expense e:ans)
            putParticipants(e);
        return ResponseEntity.ok(ans);
    }
    @GetMapping(path={"/events"})
    public ResponseEntity<List<Expense>> getExpenseByAuthorInEvent(@RequestParam("eventId") long eventId,
                                                   @RequestParam("author") String author)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        List<Expense> ans=repoExp.findEventByAuthor(eventId,author);
        for(Expense e:ans)
            putParticipants(e);
        return ResponseEntity.ok(ans);
    }
    @GetMapping(path={"events/personInvolved"})
    public ResponseEntity<List<Expense>> getExpensePInvolvedInEvent(@RequestParam("eventId") long eventId,
                                                   @RequestParam("author") String author)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        List<Expense> ans=repoExp.findEventsThatInvolvesName(eventId,author);
        for(Expense e:ans)
            putParticipants(e);
        return ResponseEntity.ok(ans);
    }

    @GetMapping(path={"/allFromEvent"})
    public ResponseEntity<List<Expense>> getAllFromEvent(@RequestParam("eventId") long eventId)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        List<Expense> ans=repoExp.findAllExpOfAnEvent(eventId);
        for(Expense e:ans)
            putParticipants(e);
        return ResponseEntity.ok(ans);
    }
    @GetMapping(path={"/all"})
    public ResponseEntity<List<Expense>> getAll()
    {
        List<Expense> ans=repoExp.findAllExp();
        for(Expense e:ans)
            putParticipants(e);
        return ResponseEntity.ok(ans);
    }
    @GetMapping(path={"/tags"})
    public ResponseEntity<Tag> getTag(@RequestParam("tag") String tagName)
    {
        if(repoTag.existsById(tagName))
        {
            Tag tag=repoTag.findById(tagName).get();
            return ResponseEntity.ok(tag);
        }
        return ResponseEntity.notFound().build();
    }
    //here to put the PUT APIs (update)
    @PutMapping(path={"/"})
    public ResponseEntity<Expense> updateExpense(@RequestParam("expenseId") long expenseId,
                                                 @RequestBody Expense expense)
    {
        if(expenseId<0)
            return ResponseEntity.badRequest().build();
        if(!repoExp.existsById(expenseId))
            return ResponseEntity.notFound().build();
        Integer a=repoExp.updateExpenseWithId(expenseId,expense.getAuthor().getParticipantID(),expense.getContent(),
                expense.getMoney(),expense.getCurrency(),expense.getDate(),expense.getType());
        //if a>0 means we updated something
        System.out.println(a);
        Expense newExpense=repoExp.findById(expenseId).get();
        putParticipants(newExpense);
        return ResponseEntity.ok(newExpense);
    }
    //here to put the DELETE APIs

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
        //then we delete the expense
        Integer b=repoExp.deleteWithId(expenseId);
        if(b==0)
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
        repoExp.deleteAllExpensesEventCon(eventId);
        //delete all expenses related to the event
        if(expenses!=null) {
            for(Expense e:expenses)
                repoExp.deleteWithId(e.getExpenseId());
        }
        return ResponseEntity.ok(expenses.size());
    }
    @DeleteMapping (path={"/tags"})
    public ResponseEntity<Tag> deleteTag(@RequestParam("tag") String tagName)
    {
        if(repoTag.existsById(tagName))
        {
            Tag tag=repoTag.findById(tagName).get();
            repoTag.deleteById(tagName);
            return ResponseEntity.ok(tag);
        }
        return ResponseEntity.notFound().build();
    }
}
