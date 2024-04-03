package server.api;

import commons.Debt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.DebtRepository;
import server.service.DebtService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/events/debts")//do not put here the {eventId}, it causes an error
public class DebtController {

    private final DebtRepository repo;
    private final DebtService debtService;

    /**
     * Constructor for the DebtController
     *
     * @param repo        the repository that holds all the debts
     * @param debtService the debt service
     */
    public DebtController(DebtRepository repo, DebtService debtService) {
        this.repo = repo;
        this.debtService = debtService;
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
        if (Objects.isNull(id) ||id < 0) {return ResponseEntity.badRequest().build();}

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
     *         else if an id in the list is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @GetMapping("all")
    public ResponseEntity<List<Debt>> getListOfDebts(@RequestBody List<Long> ids){
        List<Debt> debts = new ArrayList<>();

        //check if list is null
        if(Objects.isNull(ids)){return ResponseEntity.badRequest().build();}

        for (Long id: ids){
            //check if ID is null or invalid
            if (Objects.isNull(id) ||id < 0) {return ResponseEntity.badRequest().build();}

            if (repo.existsById(id)){
                debts.add(getDebtById(id).getBody());
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(debts);
    }

    /**
     * Adds a new debt to the debt repository or updates an old one (See debt service)
     * @param debt the debt to be added (Must not be Null)
     * @return OK - 200 and the debt that was added on success,
     *         else if the debt is invalid: BAD REQUEST - 400,
     */
    @PostMapping("")
    public ResponseEntity<Debt> addDebt(@RequestParam("eventId") long eventId,
                                        @RequestParam("date") String date,
                                        @RequestBody Debt debt) {
        // Check if debt is null (this debt has the id 0)
        if(Objects.isNull(debt)){
            return ResponseEntity.badRequest().build();
        }
        //post or put the values of the debt, (this debt usually has the debtId 0)
        //we need the date in case of a debt update
        debtService.saveDebtToEvent(eventId,debt, date);
        return ResponseEntity.ok(debt);
    }

    /**
     * Adds a new debt to the debt repository
     * @param debts the List of debts to be added
     * @return the List of debts that were added on success,
     *         else if a debt in the list is invalid: BAD REQUEST - 400,
     */
    @PostMapping("all")
    public ResponseEntity<List<Debt>> addListOfDebts(@RequestBody List<Debt> debts) {
        // check if list is null
        if(Objects.isNull(debts)){return ResponseEntity.badRequest().build();}

        for (Debt debt: debts){
            // Check if debt is null or exists on DB
            if(Objects.isNull(debt) || getDebtById(debt.getDebtID()).getStatusCode().is2xxSuccessful()){
                return ResponseEntity.badRequest().build();
            }
            repo.save(debt);
        }
        return ResponseEntity.ok(debts);
    }


    /**
     * Deletes/settles a Debt by its ID
     * @param id the ID of the debt to be settled
     * @return OK - 200 and the debt of the ID if it is found,
     *         else if the id is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Debt> settleDebtByID(@PathVariable Long id) {
        // check if id is null
        if(Objects.isNull(id)){return ResponseEntity.badRequest().build();}

        // check if the debt exists on the db
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
     *         else if the debt is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @DeleteMapping("")
    public ResponseEntity<Debt> settleDebt(@RequestBody Debt debt) {
        // check if debt is null
        if(Objects.isNull(debt)){return ResponseEntity.badRequest().build();}

        //check if debt exists
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
     *         else if a debt is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @DeleteMapping("d")
    public ResponseEntity<List<Debt>> settleListOfDebts(@RequestBody List<Debt> debts) {
        // check if list is null
        if(Objects.isNull(debts)){return ResponseEntity.badRequest().build();}
        List<Debt> result = new ArrayList<>();

        for(Debt debt: debts){
            // check if debt is null
            if(Objects.isNull(debt)){return ResponseEntity.badRequest().build();}
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
     *         else if an ID is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @DeleteMapping("dd")
    public ResponseEntity<List<Debt>> settleListOfDebtsByID(@RequestBody List<Long> ids) {
        // check if list is null
        if(Objects.isNull(ids)){return ResponseEntity.badRequest().build();}
        List<Debt> result = new ArrayList<>();

        for(Long id: ids){
            // check if id is null
            if(Objects.isNull(id)){return ResponseEntity.badRequest().build();}

            ResponseEntity<Debt> response = settleDebtByID(id);
            if(response.getStatusCode().isError()){return ResponseEntity.notFound().build();}
            result.add(response.getBody());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * settles/deletes all debts
     * @return OK - 200 and all previous debts if any debts exist,
     *         and else NOT FOUND - 404
     */
    @DeleteMapping("/all")
    public ResponseEntity<List<Debt>> settleAll(){  //this function is dangerous!
        ResponseEntity<List<Debt>> result = getAllDebts();
        // if no debts existed, return notFound
        if(Objects.isNull(result)){
            return ResponseEntity.notFound().build();
        }

        settleListOfDebts(Objects.requireNonNull(result.getBody()));
        return result;
    }

    /**
     * replaces all debts with a new list of debts
     * @param debts the new list of debts
     * @return OK - 200 and all previous Debts if any debts exist,
     *         else if a debt is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @RequestMapping("")
    public ResponseEntity<List<Debt>> replaceAll(@RequestBody List<Debt> debts){
        // check if list is null
        if(Objects.isNull(debts)){return ResponseEntity.badRequest().build();}

        ResponseEntity<List<Debt>> result = settleAll();

        // adds debts and checks if they are valid, and whether they exist already
        addListOfDebts(debts);
        return result;
    }

    /*
     * replaces a debt with a new one
     * @param oldDebt the debt to be replaced
     * @param newDebt the debt replacing the old one
     * @return OK - 200 and the debt that was replaced if the old debt was found,
     *         else if a debt is invalid: BAD REQUEST - 400,
     *         NOT FOUND - 404 otherwise
     */
    /* This function cannot have 2 RequestBody, this is why I commented it.
    @RequestMapping("")
    public ResponseEntity<Debt> replace(Debt oldDebt, Debt newDebt){
        // check if a debt is null
        if(Objects.isNull(oldDebt) || Objects.isNull(newDebt)){return ResponseEntity.badRequest().build();}

        // settleDebt checks if debt exists
        ResponseEntity<Debt> result = settleDebt(oldDebt);
        newDebt.setDebtID(oldDebt.getDebtID());
        addDebt(newDebt);
        return result;
    }
    */
}
