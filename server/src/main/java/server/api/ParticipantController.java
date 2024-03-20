package server.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
     * @return OK - 200 if participant was created
     */
    @PostMapping(path = { "", "/{eventId}" })
    public ResponseEntity<List<Participant>> createParticipant(@RequestBody Participant participant) {
        List<Participant> participantList = repo.findAll();
        if (!participantList.contains(participant)){
            repo.saveAndFlush(participant);
            //TODO
            // Create a return statement with a created responseEntity
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

    /**
     * Put request to update a participants name through their id
     * @param id the ID of the participant's name to update
     * @param newName the new name of the participant
     * @return OK - 200 if participant was found and name successfully replaced and BAD REQUEST - 400 otherwise
     */
    @PutMapping("/{id}/name")
    public ResponseEntity<Participant> updateParticipantName(@PathVariable Long id, @RequestBody String newName) {
        Optional<Participant> participant = repo.findById(id);
        return participant.map(p -> {
            p.setName(newName);
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Put request to update participants email after finding them by id
     * @param id the UID of the participant
     * @param newEmail the new email address of the participant
     * @return OK - 200 if participant was found and email updated successfully and BAD REQUEST - 400 otherwise
     */
    @PutMapping("/{id}/email")
    public ResponseEntity<Participant> updateParticipantEmail(@PathVariable Long id, @RequestBody String newEmail) {
        Optional<Participant> participant = repo.findById(id);
        return participant.map(p -> {
            p.setEmail(newEmail);
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Put request to update the participant BIC after finding them by id
     * @param id the UID to find the participant by
     * @param newBic the bic to replace the old one
     * @return OK - 200 if participant was found and bic replaced and BAD REQUEST - 400 otherwise
     */
    @PutMapping("/{id}/bic")
    public ResponseEntity<Participant> updateParticipantBic(@PathVariable Long id, @RequestBody String newBic) {
        Optional<Participant> participant = repo.findById(id);
        return participant.map(p -> {
            p.setBic(newBic);
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Put request to update the participant IBAN after finding them by id
     * @param id the UID to find the participant by
     * @param newIban the iban replace the old one
     * @return OK - 200 if participant was found and bic replaced and BAD REQUEST - 400 otherwise
     */
    @PutMapping("/{id}/iban")
    public ResponseEntity<Participant> updateParticipantIban(@PathVariable Long id, @RequestBody String newIban) {
        Optional<Participant> participant = repo.findById(id);
        return participant.map(p -> {
            p.setIban(newIban);
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete request to get rid of a participant by their id
     * @param id the UID of the participant to be deleted
     * @return OK - 200 if participant was found and deleted and NOT FOUND - 404 otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParticipantById(@PathVariable Long id) {
        Optional<Participant> participant = repo.findById(id);
        if (participant.isPresent()) {
            repo.delete(participant.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete request to eliminate of a participant by their name
     * (CAREFUL - WAS NOT TESTED IF MULTIPLE PEOPLE ARE SELECTED. USE CAUTIOUSLY)
     * @param name the name of the participant to be deleted
     * @return OK - 200 if participant was found and deleted and NOT FOUND - 404 otherwise
     */
    @DeleteMapping("/")
    public ResponseEntity<?> deleteParticipantByName(@PathVariable String name) {
        List<Participant> participants = repo.findByName(name);
        if (!participants.isEmpty()) {
            repo.deleteAll(participants);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}