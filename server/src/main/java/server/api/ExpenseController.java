package server.api;

import commons.Expense;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ExpenseRepository;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository repo;
    public ExpenseController(ExpenseRepository repo) {
        this.repo = repo;
    }
    @GetMapping(path={"","/{id}"})
    public ResponseEntity<Expense> getExpenseById(@PathVariable("id") long id)
    {
        if(id<0 || !repo.existsById(id))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(repo.findById(id).get());
    }
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense)
    {
        if(expense==null)
            return ResponseEntity.badRequest().build();
        Expense saved=repo.save(expense);
        return ResponseEntity.ok(saved);
    }

    @GetMapping(path={"/all"})
    public List<Expense> getAll(long id)
    {
        return repo.findAll();
    }
}
