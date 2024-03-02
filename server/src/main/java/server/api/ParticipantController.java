package server.api;

import commons.Participant;

import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/participant")
//TODO change path
public class ParticipantController {
    private final ParticipantRepository repo;

    public ParticipantController(ParticipantRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = { "", "/{participantId}" })
    public Optional<Participant> getParticipant(@PathVariable long participantId) {
        return repo.findById(participantId);
    }

    @PostMapping(path = { "", "/{eventId}" })
    public Participant createParticipant(@RequestBody Participant participant) {
        repo.save(participant);
        return participant;
    }
}