package server.api;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import commons.ParticipantEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.database.ParticipantEventRepository;
import commons.Participant;
import commons.Event;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ParticipantEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantEventRepository participantEventRepository;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private ParticipantRepository participantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createParticipantEvent_Success() throws Exception {
        Long eventId = 1L;
        Participant participant =                 new Participant("a", "a@tudelft.com", "123", "123");
        participant.setParticipantID(1L);
        Event event = new Event("Event");
        event.setEventId(eventId);

        ParticipantEvent participantEvent = new ParticipantEvent(event.getEventId(), participant.getParticipantID());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.save(Mockito.<Participant>any())).thenReturn(participant);

        when(participantEventRepository.save(Mockito.any(ParticipantEvent.class))).thenReturn(participantEvent);

        mockMvc.perform(post("/api/participant/event/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participant)))
                .andExpect(status().isOk());

        verify(participantEventRepository).save(refEq(new ParticipantEvent(eventId, participant.getParticipantID())));
    }


    @Test
    public void createParticipantEvent_NotFound() throws Exception {
        Long eventId = 1L;
        Participant participant = new Participant("b", "b@tudelft.com", "123", "123");

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/participant/event/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participant)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getParticipantsOfEvent_Success() throws Exception {
        long eventId = 1L;
        List<Long> participantIds = Arrays.asList(1L, 2L);
        List<Participant> participants = Arrays.asList(
                new Participant("a", "a@tudelft.com", "123", "123"),
                new Participant("b", "b@tudelft.com", "123", "123")
        );

        when(participantEventRepository.findParticipantIdsByEventId(anyLong())).thenReturn(participantIds);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participants.get(0)));
        when(participantRepository.findById(2L)).thenReturn(Optional.of(participants.get(1)));

        mockMvc.perform(get("/api/participant/event/{eventId}/allParticipants", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(participants.size()))
                .andExpect(jsonPath("$[0].name").value("a"))
                .andExpect(jsonPath("$[1].name").value("b"));
    }

    @Test
    public void getEventsOfParticipant_Success() throws Exception {
        long participantId = 1L;
        List<Long> eventIds = Arrays.asList(10L, 20L);
        List<Event> events = Arrays.asList(
                new Event("Event 1"),
                new Event("Event 2")
        );

        when(participantEventRepository.findEventIdsByParticipantId(anyLong())).thenReturn(eventIds);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(events.get(0)));
        when(eventRepository.findById(20L)).thenReturn(Optional.of(events.get(1)));

        mockMvc.perform(get("/api/participant/event/getEvents/{participantId}", participantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(events.size()))
                .andExpect(jsonPath("$[0].name").value("Event 1"))
                .andExpect(jsonPath("$[1].name").value("Event 2"));
    }
}
