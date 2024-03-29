package server.api;

import commons.Debt;
import commons.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.PasswordRepository;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/quotes")
public class PasswordController {

    @Autowired
    private final PasswordRepository repo;

    public PasswordController(PasswordRepository repo) {
        this.repo = repo;
    }

    /**
     * Get request for retrieving the password
     * @return OK - 200 and the password if success, else NOT FOUND - 404
     */
    @GetMapping("")
    private ResponseEntity<Password> getPass() {
        List<Password> passwords = repo.findAll();

        // null check
        if(Objects.isNull(passwords.get(0))){
            return ResponseEntity.notFound().build();
        }
        Password pass = passwords.get(0);
        return ResponseEntity.ok(pass);
    }

    /**
     * Adds a new password to the database (only if no other exists yet)
     * @param pass the new password
     * @return OK - 200 and the pass that was added on success,
     *         else if the pass is invalid or a pass exists: BAD REQUEST - 400,
     */
    @PostMapping("")
    private ResponseEntity<Password> addPass(@RequestBody Password pass) {
        // Check if debt is null or if a pass exists
        if(Objects.isNull(pass) || getPass().getStatusCode().is2xxSuccessful()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(pass);
    }

    /**
     * Deletes a Password
     * @param pass the password to be settled
     * @return OK - 200 and the pass if it is found,
     *         else if the pass is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @DeleteMapping("")
    public ResponseEntity<Password> deletePass(@RequestBody Password pass) {
        // check if pass is null
        if(Objects.isNull(pass)){return ResponseEntity.badRequest().build();}

        //check if pass exists, and delete
        if (repo.existsById(pass.getDebtID())) {
            repo.delete(pass);
            return ResponseEntity.ok(pass);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}