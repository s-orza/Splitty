package server.api;

import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api/events")
public class ParticipantController {


    private final ParticipantRepository repo;

    public ParticipantController(ParticipantRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = { "", "/" })
    public List<Participant> getAll() {
        System.out.println(new ArrayList<>(repo.findAll()));
        return new ArrayList<>(repo.findAll());
    }
    @PostMapping(path = { "", "/" })
    public List<Participant> createEvent() {
        System.out.println(new ArrayList<>(repo.findAll()));
        return new ArrayList<>(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());

    }
}