package server.api;

import java.util.ArrayList;
import java.util.Optional;
import commons.Event;
import commons.Participant;
import java.util.List;
import commons.ParticipantEvent;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.database.ParticipantEventRepository;
import server.database.ParticipantRepository;

@RestController
@RequestMapping("/api/participant/event")
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
     * This method correctly creates a participant and the connection with the event
     * @param eventId the id of the event that the participant is in
     * @param participant the participant instance itself
     */
    @PostMapping(path = {  "/{eventId}" })
    public ResponseEntity<String> createParticipantEvent(@PathVariable Long eventId,
                                       @RequestBody Participant participant) {
        Participant p=participantRepository.save(participant);
        if (eventId != null) {
            // Assuming you have a method to find an event by its ID
            Optional<Event> event = eventRepository.findById(eventId);

            if (event.isPresent()) {
                repo.save(new ParticipantEvent(event.get().getEventId(),p.getParticipantID()));
                System.out.println("Saved to database");
                return ResponseEntity.ok("Saved to database");
            }
            else {
                System.out.println("Provided event not found");
                return ResponseEntity.notFound().build();
            }
        }
        System.out.println("No event provided");
        return ResponseEntity.badRequest().body("eventId was null");

    }

    @Transactional
    @MessageMapping("participant/event/{eventId}")
    public Participant ParticipantMessage(@DestinationVariable @NonNull Long eventId,
                                          @Payload Participant participant) {
        createParticipantEvent(eventId, participant);
        return participant;
    }

    @Transactional
    @MessageMapping("participant/{eventId}")
    public Participant RemoveParticipantMessage(@DestinationVariable @NonNull Long eventId,
                                                @Payload Participant participant) {
        deleteParticipantEvent(eventId, participant.getParticipantID());
        return participant;
    }

    /**
     * This works!
     * @param eventId the event
     * @return a list with participants.
     */
    @GetMapping(path = {"/{eventId}/allParticipants" })
    public ResponseEntity<List<Participant>> getParticipantsOfEvent(@PathVariable long eventId) {

        List<Long> participantIds = repo.findParticipantIdsByEventId(eventId);
        List<Participant> participants = new ArrayList<>();
        for(long id : participantIds){
            Participant toAdd = participantRepository.findById(id).get();
            participants.add(toAdd);
        }
        return ResponseEntity.ok(participants);
    }

    @GetMapping(path = {"/getEvents/{participantId}" })
    public ResponseEntity<List<Event>> getEventsOfParticipant(@PathVariable long participantId) {

        List<Long> eventIds = repo.findEventIdsByParticipantId(participantId);
        System.out.println(eventIds);
        List<Event> events = new ArrayList<>();

        for(long id : eventIds){
            System.out.println("in for loop");
            Event toAdd = eventRepository.findById(id).get();
            events.add(toAdd);
        }
        System.out.println("Events: " + events);
        return ResponseEntity.ok(events);
    }

    /**
     * This method gets the id of the event as the path variable
     * and also the participant id as a parameter.
     * @param participantId the
     */
    @DeleteMapping(path = {"/{eventId}/{participantId}" })
    public void deleteParticipantEvent(@PathVariable long eventId,
                                       @PathVariable long participantId) {
        repo.deleteParticipantEvent(eventId, participantId);
    }

}