package server.api;


import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;
import server.database.ParticipantRepository;
import commons.Participant;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantRepository participantRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

    }

    @Test
    public void testGetParticipantById_Found() throws Exception {
        Participant participant = new Participant("j", "asdf@f.gg", "123", "123");
        participant.setParticipantID(1L);
        when(participantRepository.existsById(1L)).thenReturn(true);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));

        mockMvc.perform(get("/api/participant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("j"))
                .andExpect(jsonPath("$.email").value("asdf@f.gg"));
    }

    @Test
    public void testGetParticipantById_NotFound() throws Exception {
        when(participantRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(get("/api/participant/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllParticipants() throws Exception {
        List<Participant> participants = Arrays.asList(
                new Participant("a", "a@tudelft.com", "123", "123"),
                new Participant("b", "b@tudelft.com", "123", "123")
        );
        when(participantRepository.findAll()).thenReturn(participants);

        mockMvc.perform(get("/api/participant/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("a"))
                .andExpect(jsonPath("$[1].name").value("b"));
    }

    @Test
    public void testGetParticipantsByIds_AllFound() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Participant> participants = Arrays.asList(
                new Participant("a", "a@tudelft.com", "123", "123"),
                new Participant("b", "b@tudelft.com", "123", "123")
        );

        when(participantRepository.existsById(eq(1L))).thenReturn(true);
        when(participantRepository.existsById(eq(2L))).thenReturn(true);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participants.get(0)));
        when(participantRepository.findById(2L)).thenReturn(Optional.of(participants.get(1)));

        mockMvc.perform(get("/api/participant/{eventId}/list", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].email").value("a@tudelft.com"))
                .andExpect(jsonPath("$[1].email").value("b@tudelft.com"));
    }


    @Test
    public void testGetParticipantsByIds_NotFound() throws Exception {
        List<Long> ids = List.of(3L);
        when(participantRepository.existsById(3L)).thenReturn(false);

        mockMvc.perform(get("/api/participant/{eventId}/list", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateParticipantName_Found() throws Exception {
        Long participantId = 1L;
        String newName = "UpdatedName";
        Participant participant = new Participant("a", "a@tudelft.com", "123", "123");
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        mockMvc.perform(put("/api/participant/{id}/name", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    public void updateParticipantName_NotFound() throws Exception {
        Long participantId = 1L;
        String newName = "UpdatedName";

        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/participant/{id}/name", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newName))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateParticipant_Success() throws Exception {
        Long participantId = 1L;
        Participant updatedParticipant = new Participant("b", "b@tudelft.com", "123", "123");

        when(participantRepository.findById(participantId)).thenReturn(Optional.of(new Participant()));
        when(participantRepository.save(any(Participant.class))).thenReturn(updatedParticipant);

        mockMvc.perform(put("/api/participant/{participantId}", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedParticipant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("b"))
                .andExpect(jsonPath("$.email").value("b@tudelft.com"));
    }

    @Test
    public void updateParticipant_NotFound() throws Exception {
        Long participantId = 1L;
        Participant updatedParticipant = new Participant("b", "b@tudelft.com", "123", "123");
        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/participant/{participantId}", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedParticipant)))
                .andExpect(status().isNotFound());
    }


    // Example test for updating Email
    @Test
    public void updateParticipantEmail_Found() throws Exception {
        Long participantId = 1L;
        String newEmail = "new.email@tudelft.nl";
        Participant participant = new Participant("a", "a@tudelft.com", "123", "123");
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        mockMvc.perform(put("/api/participant/{id}/email", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail));
    }

    @Test
    public void updateParticipantBic_Found() throws Exception {
        Long participantId = 1L;
        String newBic = "45";
        Participant participant = new Participant("a", "a@tudelft.com", "123", "123");
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        mockMvc.perform(put("/api/participant/{id}/bic", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBic))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bic").value(newBic));
    }

    @Test
    public void updateParticipantBic_NotFound() throws Exception {
        Long participantId = 1L;
        String newBic = "45";

        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/participant/{id}/bic", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBic))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateParticipantIban_Found() throws Exception {
        Long participantId = 1L;
        String newIban = "45";
        Participant participant = new Participant("b", "b@tudelft.com", "123", "123");
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        mockMvc.perform(put("/api/participant/{id}/iban", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newIban))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value(newIban));
    }

    @Test
    public void updateParticipantIban_NotFound() throws Exception {
        Long participantId = 1L;
        String newIban = "45";

        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/participant/{id}/iban", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newIban))
                .andExpect(status().isNotFound());
    }


}