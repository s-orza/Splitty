package server.api;

import commons.Event;
import commons.Participant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;

import java.util.Collection;

@RestController
@RequestMapping("/api/participant")
//TODO change path
public class ParticipantController {
    private final ParticipantRepository repository;

    public ParticipantController(ParticipantRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable("id") long id) {
        if (id < 0 || !repository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repository.findById(id).get());
    }

    @PostMapping(path = { "", "/{eventId}" })
    public Participant createParticipant(@RequestBody Participant participant) {
        if (repository.findBy(participant)){

        }
    }
}