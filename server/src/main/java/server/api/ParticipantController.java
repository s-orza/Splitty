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

    /**
     * Constructor for the ParticipantController
     * @param repo the repository that holds all participants (of an event)
     */
    public ParticipantController(ParticipantRepository repo) {
        this.repo = repo;
    }

    /**
     * Get request for retrieving one participant by ID
     * @param id the id of the participant to retrieve
     * @return OK - 200 if the id is valid and participant was found and BAD REQUEST - 400 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable("id") Long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Get request for all the participants in the repository
     * @return a response with the list of all participants
     */
    @GetMapping
    public ResponseEntity<List<Participant>> getAllParticipants() {
        List<Participant> participants = repo.findAll();
        return ResponseEntity.ok(participants);
    }

    /**
     * Post request to add a participant to an event
     * @param participant the participant to be added to the
     * @return
     */
    @PostMapping(path = { "", "/{eventId}" })
    public ResponseEntity<List<Participant>> createParticipant(@RequestBody Participant participant) {
        List<Participant> participantList = repo.findAll();
        if (!participantList.contains(participant)){
            repo.saveAndFlush(participant);
        }

        return ResponseEntity.ok(repo.findAll());
    }

    /**
     * Get request for a list of participants given by IDS.
     * @param ids list of participant ids to search for in the database
     * @return OK - 200 response if all participants were found, BAD REQUEST - 400 response if at least one participant
     * is not in the list
     */
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