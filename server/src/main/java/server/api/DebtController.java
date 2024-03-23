package server.api;

import commons.Debt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.DebtRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/events/{eventId}/debt")
public class DebtController {

    private final DebtRepository repo;

    /**
     *
     * Constructor for the DebtController
     * @param repo the repository that holds all the debts
     * @param eventId the ID of the event to join (for the requestMapping Above)
     */
    public DebtController(DebtRepository repo, @PathVariable("eventId") long eventId) {
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

    /**
     * Adds a new debt to the debt repository
     * @param debt the debt to be added (Must not be Null)
     * @return OK -200 and the debt that was added
     */
    @PostMapping()
    public ResponseEntity<Debt> addDebt(@RequestBody Debt debt) {
        repo.save(debt);
        return ResponseEntity.ok(debt);
    }

    /**
     * Adds a new debt to the debt repository
     * @param debts the List of debts to be added
     * @return the List of debts that were added
     */
    @PostMapping()
    public ResponseEntity<List<Debt>> addListOfDebts(@RequestBody List<Debt> debts) {
        for (Debt debt: debts){
            repo.save(debt);
        }
        return ResponseEntity.ok(debts);
    }

    /**
     * Deletes/settles a Debt by its ID
     * @param id the ID of the debt to be settled
     * @return OK - 200 and the debt of the ID if it is found,
     *         and else NOT FOUND - 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Debt> settleDebtByID(@PathVariable Long id) {
        Optional<Debt> debt = repo.findById(id);
        if (debt.isPresent()) {
            repo.delete(debt.get());
            return ResponseEntity.ok(debt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes/settles a Debt
     * @param debt the debt to be settled
     * @return OK - 200 and the debt if it is found,
     *         and else NOT FOUND - 404
     */
    public ResponseEntity<Debt> settleDebt(Debt debt) {
        if (repo.existsById(debt.getDebtID())) {
            repo.delete(debt);
            return ResponseEntity.ok(debt);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes/settles a list of Debt
     * @param debts the list of debts to be settled
     * @return OK - 200 and the list of debts if they are all found,
     *         and else NOT FOUND - 404
     */
    public ResponseEntity<List<Debt>> settleListOfDebts(List<Debt> debts) {
        List<Debt> result = new ArrayList<>();

        for(Debt debt: debts){
            ResponseEntity<Debt> response = settleDebt(debt);
            if(response.getStatusCode().isError()){return ResponseEntity.notFound().build();}
            result.add(response.getBody());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Deletes/settles a list of Debt by their IDs
     * @param ids the list of ids of the debts to be settled
     * @return OK - 200 and the list of debts if they are all found,
     *         and else NOT FOUND - 404
     */
    public ResponseEntity<List<Debt>> settleListOfDebtsByID(List<Long> ids) {
        List<Debt> result = new ArrayList<>();

        for(Long id: ids){
            ResponseEntity<Debt> response = settleDebtByID(id);
            if(response.getStatusCode().isError()){return ResponseEntity.notFound().build();}
            result.add(response.getBody());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * settles/deletes all debts
     * @return OK - 200 all previous debts
     */
    public ResponseEntity<List<Debt>> settleAll(){
        ResponseEntity<List<Debt>> result = getAllDebts();
        if(Objects.isNull(result)){
            return ResponseEntity.notFound().build();
        }
        settleListOfDebts(Objects.requireNonNull(result.getBody()));
        return result;
    }

    /**
     * replaces all debts with a new list of debts
     * @param debts the new list of debts
     * @return OK - 200 and all previous Debts
     */
    public ResponseEntity<List<Debt>> replaceAll(List<Debt> debts){
        ResponseEntity<List<Debt>> result = settleAll();
        addListOfDebts(debts);
        return result;
    }

    /**
     * replaces a debt with a new one
     * @param oldDebt the debt to be replaced
     * @param newDebt the debt replacing the old one
     * @return OK - 200 and the debt that was replaced if the old debt was found,
     *         NOT FOUND - 404 otherwise
     */
    public ResponseEntity<Debt> replace(Debt oldDebt, Debt newDebt){
        ResponseEntity<Debt> result = settleDebt(oldDebt);
        newDebt.setDebtID(oldDebt.getDebtID());
        addDebt(newDebt);
        return result;
    }
}
