package server.api;

import commons.Event;
import commons.Debt;
import commons.DebtManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.DebtRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/debt")
public class DebtController {

    private final DebtRepository repo;

    /**
     * Constructor for the DebtController
     * @param repo the repository that holds all the debts (of a debtManager)
     */
    public DebtController(DebtRepository repo) {
        this.repo = repo;
    }

    /**
     * Get request for retrieving one debt by ID
     * @param id the id of the debt to retrieve
     * @return OK - 200 and the debt of the ID if it is found,
     *         else if the id is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Debt> getDebtById(@PathVariable("id") long id) {
        // check if id is valid
        if (id < 0) {return ResponseEntity.badRequest().build();}

        // return debt if it exists, else create builder with notFound status
        Optional<Debt> debtOptional = repo.findById(id);
        return debtOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get request for retrieving all debts of a DebtManager
     * @return OK - 200 and a list with all debts of a DebtManager
     */
    @GetMapping
    public ResponseEntity<List<Debt>> getAllParticipants() {
        List<Debt> debts = repo.findAll();
        return ResponseEntity.ok(debts);
    }
}
