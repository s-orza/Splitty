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

    public DebtController(DebtRepository repo) {
        this.repo = repo;
    }
}
