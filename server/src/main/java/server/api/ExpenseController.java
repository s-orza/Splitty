package server.api;

import commons.Expense;
import commons.Participant;
import commons.ParticipantExpense;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ExpenseEventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantExpenseRepository;

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
        if(!participants.isEmpty()) {
            //now we need to create connections between the expense and the participants
            for (Participant p : participants) {
                ParticipantExpense pe = new ParticipantExpense(saved.getExpenseId(), p.getParticipantID());
                repoPaExp.save(pe);
            }
        }
        return ResponseEntity.ok(expense);
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
    public List<Expense> getExpenseByAuthorName(@RequestParam("name") String name)
    {
        return repoExp.findByAuthor(name);
    }
    @GetMapping(path={"events"})
    public List<Expense> getExpenseByAuthorInEvent(@RequestParam("eventId") long eventId,
                                                   @RequestParam("author") String author)
    {
        return repoExp.findEventByAuthor(eventId,author);
    }


    @GetMapping(path={"/all"})
    public List<Expense> getAll()
    {
        return repoExp.findAll();
    }
}
