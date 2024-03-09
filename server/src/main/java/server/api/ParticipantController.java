package server.api;

import java.util.ArrayList;
import java.util.List;

import commons.Participant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;


@RestController
@RequestMapping("/api/participant")
//TODO change path
public class ParticipantController {
    private final ParticipantRepository repo;

    public ParticipantController(ParticipantRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable("id") Long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @GetMapping
    public ResponseEntity<List<Participant>> getAllParticipants() {
        List<Participant> participants = repo.findAll();
        return ResponseEntity.ok(participants);
    }

    @PostMapping(path = { "", "/{eventId}" })
    public ResponseEntity<List<Participant>> createParticipant(@RequestBody Participant participant) {
        List<Participant> participantList = repo.findAll();
        if (!participantList.contains(participant)){
            participantList.add(participant);
        }

        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{eventId}/list")
    public ResponseEntity<List<Participant>> getParticipantsByIds(List<Long> ids){
        List<Participant> participantList = new ArrayList<>();
        for (Long id: ids){
            if (repo.existsById(id)){
                participantList.add(getParticipantById(id).getBody());
            }
            else return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(participantList);
    }

}