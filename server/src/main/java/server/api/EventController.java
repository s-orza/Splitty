package server.api;

import commons.Event;
import commons.Participant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.EventRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("api/events")
public class EventController {


    private final EventRepository repo;

    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    //endpoint with all events in it
    @GetMapping(path = { "", "/" })
    public List<Event> getAll() {
        return repo.findAll();
    }

    private Map<Object, Consumer<Event>> listeners = new HashMap<>();

    @GetMapping("/updates" )
    public DeferredResult<ResponseEntity<Event>> getUpdatesEvents() {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Event>>(5000L, noContent);
        var key = new Object();
        listeners.put(key, e -> {
            res.setResult(ResponseEntity.ok(e));
        });
        res.onCompletion(() -> {
            listeners.remove(key);
        });
        return res;
    }


    //endpoint with an event with a specific id in it
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> eventOptional = repo.findById(id);
        if (eventOptional.isPresent()) {
            return ResponseEntity.ok(eventOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //endpoint for put method to change the name of an event
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEventName(@PathVariable("id") long id, @RequestBody String newName) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Event event = repo.findById(id).get();
        event.setName(newName);
        repo.save(event);

        return ResponseEntity.ok().build();
    }

    //endpoint with an event with specific id in it to be deleted
    @DeleteMapping("/{id}")
    public ResponseEntity<Event> removeEventByID (@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //endpoint to an event to
    @PostMapping(path = { "", "/" })
    public Event addEvent(@RequestBody Event event) {
        listeners.forEach((k, l) -> l.accept(event));
        repo.save(event);
        return event;

    }

    //endpoint to get a list of participants from an event
    @GetMapping("/participants/{id}")
    public ResponseEntity<List<Participant>> getByIdParticipant(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get().getParticipants());
    }


}
