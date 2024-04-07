package server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.database.ParticipantEventRepository;
import commons.Participant;
import commons.Event;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ParticipantEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantEventRepository participantEventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Participant testParticipant;
    private Event testEvent;

    @BeforeEach
    public void setup() {
        testEvent = eventRepository.save(new Event("Test Event"));
        testParticipant = participantRepository.save(
                new Participant("Test Participant", "test@participant.com",
                        "TESTIBAN123", "TESTBIC123"));
    }

    @AfterEach
    public void cleanup() {
        participantEventRepository.deleteAll();
        participantRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    public void testCreateParticipantEvent() throws Exception {
        mockMvc.perform(post("/api/participant/event/" + testEvent.getEventId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testParticipant)))
                .andExpect(status().isOk())
                .andExpect(content().string("Saved to database"));
    }

    @Test
    public void testCreateParticipantEventWithNonExistentEvent() throws Exception {
        mockMvc.perform(post("/api/participant/event/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testParticipant)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetParticipantsOfEvent() throws Exception {
        testCreateParticipantEvent();

        mockMvc.perform(get("/api/participant/event/" +
                        testEvent.getEventId() + "/allParticipants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("Test Participant"))
                .andExpect(jsonPath("$.[0].email").value("test@participant.com"));
    }

    @Test
    public void testDeleteParticipantEvent() throws Exception {
        testCreateParticipantEvent();

        mockMvc.perform(delete("/api/participant/event/" +
                        testEvent.getEventId() + "/" + testParticipant.getParticipantID()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/participant/event/" + testEvent.getEventId() + "/allParticipants"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void testGetEventsOfParticipantWithEvents() throws Exception {

        testCreateParticipantEvent();

        mockMvc.perform(get("/api/participant/event/getEvents/" + testParticipant.getParticipantID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].name").value("Test Event"));
    }

    @Test
    public void testGetEventsOfParticipantWithNoEvents() throws Exception {

        Participant newParticipant = participantRepository.save(
                new Participant("Another Participant", "another@participant.com",
                        "IBAN", "BIC"));

        mockMvc.perform(get("/api/participant/event/getEvents/" + newParticipant.getParticipantID()))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void testGetEventsOfParticipantInvalidParticipantId() throws Exception {

        mockMvc.perform(get("/api/participant/event/getEvents/543655"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

}
