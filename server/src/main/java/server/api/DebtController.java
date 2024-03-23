package server.api;

import commons.Debt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.DebtRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/debt")
public class DebtController {

    private final DebtRepository repo;

    /**
     * Constructor for the DebtController
     * @param repo the repository that holds all the debts
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
     * Get request for retrieving all debts
     * @return OK - 200 and a list with all debts
     */
    @GetMapping("")
    public ResponseEntity<List<Debt>> getAllDebts() {
        List<Debt> debts = repo.findAll();
        return ResponseEntity.ok(debts);
    }

    /**
     * Get request for a list of debts
     * @param ids the list of ids to find
     * @return OK - 200 and the debt of the ID if it is found,
     *         and else NOT FOUND - 404
     */
    @GetMapping("")
    public ResponseEntity<List<Debt>> getListOfDebts(List<Long> ids){
        List<Debt> debts = new ArrayList<>();

        for (Long id: ids){
            if (repo.existsById(id)){
                debts.add(getDebtById(id).getBody());
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(debts);
    }
}
