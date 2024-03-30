package server.api;

import commons.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.PasswordRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    @Autowired
    private final PasswordRepository repo;

    public PasswordController(PasswordRepository repo) {
        this.repo = repo;
    }

    /**
     * Get request for retrieving the password
     * @return OK - 200 and the password if success
     */
    private ResponseEntity<List<Password>> getPasswords() {
        List<Password> passwords = repo.findAll();

        // return empty
        if(passwords.size()==0){
            return ResponseEntity.ok(passwords);
        }

        // null check
        if(Objects.isNull(passwords.get(0))){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(passwords);
    }

    /**
     * Get request for retrieving the password
     * @return OK - 200 and the password if success, BAD REQUEST - 400 else
     */
    @GetMapping("")
    public ResponseEntity<Password> getPassword() {
        List<Password> passwords = repo.findAll();

        // return empty
        if(passwords.size()==0){
            System.out.println("retrieved no Password");
            return ResponseEntity.badRequest().build();
        }

        // null check
        if(Objects.isNull(passwords.get(0))){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(passwords.get(0));
    }

    /**
     * Adds a new password to the database (only if no other exists yet)
     * @param pass the new password
     * @return OK - 200 and the pass that was added on success,
     *         else if the pass is invalid or a pass exists: BAD REQUEST - 400
     */
    @PostMapping("")
    public ResponseEntity<Password> addPass(@RequestBody Password pass) {
        // Check if password is null or if a pass exists
        if(Objects.isNull(pass) || Objects.requireNonNull(getPasswords().getBody()).size()>0){
            System.out.println("Password: exists or is null");
            return ResponseEntity.badRequest().build();
        }
        repo.save(pass);
        System.out.println("New Password: " + pass.getPassword());
        return ResponseEntity.ok(pass);
    }

    /**
     * Deletes a Password
     * @param id the id of the password to be settled
     * @return OK - 200 and the pass if it is found,
     *         else if the pass is invalid: BAD REQUEST - 400,
     *         and else NOT FOUND - 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Password> deletePass(@PathVariable("id") long id) {
        // check if pass is null
        if(Objects.isNull(id)){return ResponseEntity.badRequest().build();}

        //check if pass exists, and delete
        Optional<Password> password = repo.findById(id);
        if (password.isPresent()) {
            repo.delete(password.get());
            System.out.println("Deleted Pass: " + password.get().getPassword());
            return ResponseEntity.ok(password.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a Password
     * @return OK - 200
     */
    @DeleteMapping("")
    public ResponseEntity<Password> deleteAllPass() {
        List<Password> passwords = getPasswords().getBody();

        assert passwords != null;
        for (Password pass: passwords) {
            deletePass(pass.getPassID());
        }

        return ResponseEntity.ok().build();
    }
}