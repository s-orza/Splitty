package server.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import commons.Participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;


@RestController
@RequestMapping("/api/participant")
//TODO change path
public class ParticipantController {
    @Autowired
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
    public ResponseEntity<Participant> createParticipant(@RequestBody Participant participant) {
        Optional<Participant> existingParticipant = repository.findById(participant.getParticipantID());

        if (existingParticipant.isPresent()) {
            // Participant already exists, handle accordingly
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else {
            // Participant does not exist, save to repository
            Participant savedParticipant = repository.save(participant);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedParticipant);
        }
    }

    public ResponseEntity<List<Participant>> getParticipantsByIds(List<Long> ids){
        List<Participant> participantList = new ArrayList<>();
        for (Long id: ids){
            participantList.add(getById(id).getBody());
        }

        return ResponseEntity.ok(participantList);
    }

}