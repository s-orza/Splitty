package server.api;

import java.util.Optional;
import commons.Event;
import commons.Participant;

import commons.ParticipantEvent;
//import org.springframework.http.ResponseEntity;
import commons.ParticipantEventDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.database.ParticipantEventRepository;
import server.database.ParticipantRepository;

@RestController
@RequestMapping("/api/participant/event")
//TODO change path
public class ParticipantEventController {
    private final ParticipantEventRepository repo;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public ParticipantEventController(ParticipantEventRepository repo,
                                      EventRepository eventRepository,
                                      ParticipantRepository participantRepository) {
        this.repo = repo;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * This method adds an entry to the Participant table
     * and also an entry to the participantEvent table
     * @param eventId the id of the event that the participant is in
     * @param participant the participant instance itself
     */
    @PostMapping(path = { "", "/{eventId}" })
    public void createParticipantEvent(@PathVariable(required = false) Long eventId, @RequestBody Participant participant) {
        System.out.println("In ParticipantEvent controller");
        participantRepository.save(participant);
        if (eventId != null) {
            // Assuming you have a method to find an event by its ID
            Optional<Event> event = eventRepository.findById(eventId);

            if (event.isPresent()) {
                repo.save(new ParticipantEvent(event.get().getEventId(), participant.getParticipantID()));
                System.out.println("Saved to database");
//                return ResponseEntity.ok("Saved to database");
            }
            else {
                System.out.println("Provided event not found");
//                return ResponseEntity.ok("Provided event not found");
            }
        }
        System.out.println("No event provided");
//        return ResponseEntity.ok("No event provided");

        // Return the saved participant and a 201 Created status
    }

    /**
     * This method will add only an entry to the participant_event table
     * using an existing participant and an existing event. The participant and the event
     * must exist for the method to work.
     */
    @PostMapping(path = { "", "/" })
    public void createParticipantEvent(@RequestBody ParticipantEventDTO pedto) {

        if (pedto != null) {

            Optional<Event> eventFromDatabase = eventRepository.findById(pedto.getEventId());
            Optional<Participant> participantFromDatabase = participantRepository.findById(pedto.getParticipantId());

            if (eventFromDatabase.isPresent() && participantFromDatabase.isPresent()) {
                repo.save(new ParticipantEvent(eventFromDatabase.get().getEventId(), participantFromDatabase.get().getParticipantID()));
                System.out.println("Saved to database");
//                return ResponseEntity.ok("Saved to database");
            }
            else {
                System.out.println("Provided event not found");
//                return ResponseEntity.ok("Provided event not found");
            }
        }
        System.out.println("No event provided");
//        return ResponseEntity.ok("No event provided");

        // Return the saved participant and a 201 Created status
    }
}