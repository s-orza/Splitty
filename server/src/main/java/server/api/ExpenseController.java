package server.api;

import commons.Expense;
import commons.Participant;
import commons.ParticipantExpense;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ExpenseEventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantExpenseRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository repoExp;
    private final ExpenseEventRepository repoExpEv;
    private final ParticipantExpenseRepository repoPaExp;
    public ExpenseController(ExpenseRepository repoExp, ExpenseEventRepository repoExpEv,
                             ParticipantExpenseRepository repoPaExp) {
        this.repoExp = repoExp;
        this.repoExpEv = repoExpEv;
        this.repoPaExp = repoPaExp;
    }
    //here to put the POST APIs
    @PostMapping(path = { "/s"})
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense)
    {
        if (expense == null) {
            System.out.println("is null");
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
    @PostMapping(path = { "/ss"})
    public Expense addExpensee(@RequestBody Expense expense)
    {
        if (expense == null) {
            System.out.println("is null");
            return null;
        }
        if(expense.getExpenseId()<0 || repoExp.existsById(expense.getExpenseId()))
            return null;
        List<Participant> participants=expense.getParticipants();
        //in order to be able to store it in the database
        expense.setParticipants(null);
        Expense saved=repoExp.save(expense);
        System.out.println("saved");
        if(!participants.isEmpty()) {
            //now we need to create connections between the expense and the participants
            for (Participant p : participants) {
                ParticipantExpense pe = new ParticipantExpense(saved.getExpenseId(), p.getParticipantID());
                repoPaExp.save(pe);
            }
        }
        return saved;
    }
    //here to put the GET APIs
    /**
     *
     * @param id the id of an expense
     * @return it returns the expense with that id
     */
    @GetMapping(path={"/"})
    public ResponseEntity<Expense> getExpenseById(@RequestParam("id") long id)
    {
        if(id<0 || !repoExp.existsById(id))
            return ResponseEntity.badRequest().build();
        System.out.println(id);
        return ResponseEntity.ok(repoExp.findById(id).get());
    }
    @GetMapping(path={"/name"})
    public ResponseEntity<List<Expense>> getExpenseByAuthorName(@RequestParam("name") String name)
    {
        List<Expense> ans=repoExp.findByAuthor(name);
        return ResponseEntity.ok(ans);
    }
    @GetMapping(path={"/events"})
    public ResponseEntity<List<Expense>> getExpenseByAuthorInEvent(@RequestParam("eventId") long eventId,
                                                   @RequestParam("author") String author)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        List<Expense> ans=repoExp.findEventByAuthor(eventId,author);
        return ResponseEntity.ok(ans);
    }
    @GetMapping(path={"events/personInvolved"})
    public ResponseEntity<List<Expense>> getExpensePInvolvedInEvent(@RequestParam("eventId") long eventId,
                                                   @RequestParam("author") String author)
    {
        if(eventId<0)
            return ResponseEntity.badRequest().build();
        List<Expense> ans=repoExp.findEventsThatInvolvesName(eventId,author);
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
        List<Expense> ans=repoExp.findAllExp();
        return ResponseEntity.ok(ans);
    }
    //here to put the PUT APIs (update)

    //here to put the DELETE APIs
    @DeleteMapping(path={"/"})
    public ResponseEntity<Integer> deleteExpById(@RequestParam("eventId")long eventId,
                                                 @RequestParam("expenseId") long expenseId)
    {
        if(eventId<0 || expenseId<0)
            return ResponseEntity.badRequest().build();

        if(!repoExp.existsById(expenseId))
            return ResponseEntity.notFound().build();
        //repoExp.deleteById(expenseId);
        //first we need to delete the connection with the event
        Integer a=repoExp.deleteExpenseEventCon(eventId,expenseId);
        if(a==0)
        {
            System.out.println("The connection between the event and the expense was not deleted");
            //444-no response
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
        return ResponseEntity.ok().build();
    }
}
