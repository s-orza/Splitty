package server.api;

import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventControllerTest {

    private TestEventRepository repo;
    private EventController sut;

    @BeforeEach
    public void setup() {
        repo = new TestEventRepository();
        sut = new EventController(repo);
    }

    @Test
    void getAll() {
        Event expectedEvent = new Event("name");
        Event expectedEvent2 = new Event("name2");
        List<Event> result = new ArrayList<>();
        result.add(expectedEvent);
        result.add(expectedEvent2);
        sut.addEvent(expectedEvent);
        sut.addEvent(expectedEvent2);
        assertEquals(result,sut.getAll());
    }

    @Test
    void getById() {
        Event expectedEvent = new Event("name");
        Event expectedEvent2 = new Event("name2");
        sut.addEvent(expectedEvent);
        sut.addEvent(expectedEvent2);
        assertEquals(ResponseEntity.ok(expectedEvent),sut.getById(expectedEvent.getEventId()));
    }

    @Test
    void updateEventName() {
        Event expectedEvent = new Event("name");
        sut.addEvent(expectedEvent);
        assertEquals(expectedEvent.getName(),sut.getById(expectedEvent.getEventId()).getBody().getName());
        sut.updateEventName(expectedEvent.getEventId(), "newName");
        assertEquals(sut.getById(expectedEvent.getEventId()).getBody().getName(), "newName");

    }

    @Test
    void removeEventByID() {
        Event expectedEvent = new Event("name");
        sut.addEvent(expectedEvent);
        assertEquals(ResponseEntity.ok(expectedEvent),sut.getById(expectedEvent.getEventId()));
        sut.removeEventByID(expectedEvent.getEventId());
        assertEquals(sut.getAll(), new ArrayList<Event>());
    }

    @Test
    void addEvent() {
        Event expectedEvent = new Event("name");
        List<Event> result = new ArrayList<>();
        result.add(expectedEvent);
        sut.addEvent(expectedEvent);
        assertEquals(result,sut.getAll());
    }

    @Test
    void getByIdParticipant() {
        Event expectedEvent = new Event("name");
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(new Participant());
        expectedEvent.addListOfParticipants(participants);
        sut.addEvent(expectedEvent);
        assertEquals(ResponseEntity.ok(expectedEvent.getParticipants()),
                sut.getByIdParticipant(expectedEvent.getEventId()));
    }
}