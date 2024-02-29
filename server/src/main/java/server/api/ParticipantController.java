package server.api;

import commons.Participant;

import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/participant")
//TODO change path
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

    @PostMapping(path = { "", "/{eventId}" })
    public Participant createParticipant(@RequestBody Participant participant) {

        repo.save(participant);

        return participant;
    }


}