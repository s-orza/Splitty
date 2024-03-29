package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.PasswordRepository;
import server.database.QuoteRepository;

import java.util.Random;

@RestController
@RequestMapping("/api/quotes")
public class PasswordController {

    @Autowired
    private final PasswordRepository repo;

    public PasswordController(PasswordRepository repo) {
        this.repo = repo;
    }
}