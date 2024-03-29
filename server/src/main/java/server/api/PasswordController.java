package server.api;

import commons.Debt;
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
     * @return OK - 200 and the password if success, else NOT FOUND - 404
     */
    @GetMapping("")
    public ResponseEntity<Password> getPass() {
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
    public ResponseEntity<Password> addPass(@RequestBody Password pass) {
        // Check if password is null or if a pass exists
        if(Objects.isNull(pass) || getPass().getStatusCode().is2xxSuccessful()){
            return ResponseEntity.badRequest().build();
        }
        System.out.println("Password: " + pass.getPassword());
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
            return ResponseEntity.ok(password.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    /**
//     * Authenticates a string against the password
//     * @return OK - 200 and whether the string is correct,
//     *         else NOT FOUND - 404 if the pass/string were invalid
//     */
//    @GetMapping("")
//    public ResponseEntity<Boolean> authenticateString(String pass) {
//        // check for null or error
//        if(Objects.isNull(pass) || getPass().getStatusCode().isError()){
//            return ResponseEntity.badRequest().build();
//        }
//
//        // retrieve pass and check if null
//        Password realPass = getPass().getBody();
//        if(Objects.isNull(realPass) || Objects.isNull(realPass.getPassword())){
//            return ResponseEntity.badRequest().build();
//        }
//
//        // check passwords
//        if(pass.equals(realPass.getPassword())){
//            return ResponseEntity.ok(true);
//        } else {
//            return ResponseEntity.ok(false);
//        }
//    }


}