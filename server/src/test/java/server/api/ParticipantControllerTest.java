package server.api;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;
import server.database.ParticipantRepository;
import commons.Participant;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParticipantRepository participantRepository;

    private Participant savedParticipant;

    @BeforeEach
    public void setup() {
        Participant participant = new Participant("Test Participant", "test@example.com", "IBAN", "BIC");
        savedParticipant = participantRepository.save(participant);
    }

    @AfterEach
    public void cleanup() {
        participantRepository.deleteAll();
    }

    @Test
    public void getParticipantById_whenParticipantExists() throws Exception {
        mockMvc.perform(get("/api/participant/{id}", savedParticipant.getParticipantID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Participant"));
    }

    @Test
    public void getParticipantById_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/participant/{id}", -1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getParticipantById_whenParticipantDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/participant/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateParticipantName_whenParticipantExists() throws Exception {
        String newName = "Updated Name";
        mockMvc.perform(put("/api/participant/{id}/name", savedParticipant.getParticipantID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    public void updateParticipantName_whenParticipantDoesNotExist() throws Exception {
        String newName = "Updated Name";
        mockMvc.perform(put("/api/participant/{id}/name", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newName))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateParticipantBic_whenParticipantExists() throws Exception {
        String newBic = "NEWBIC123";
        mockMvc.perform(put("/api/participant/{id}/bic", savedParticipant.getParticipantID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + newBic + "\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bic").value("\"NEWBIC123\""));
    }
    @Test
    public void updateParticipantBic_whenParticipantDoesNotExist() throws Exception {
        String newBic = "NEWBIC123";
        mockMvc.perform(put("/api/participant/{id}/bic", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + newBic + "\""))
                .andExpect(status().isNotFound());
    }
    @Test
    public void updateParticipantIban_whenParticipantExists() throws Exception {
        String newIban = "NEWIBAN123";
        mockMvc.perform(put("/api/participant/{id}/iban", savedParticipant.getParticipantID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + newIban + "\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("\"NEWIBAN123\""));
    }
    @Test
    public void updateParticipantIban_whenParticipantDoesNotExist() throws Exception {
        String newIban = "NEWIBAN123";
        mockMvc.perform(put("/api/participant/{id}/iban", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + newIban + "\""))
                .andExpect(status().isNotFound());
    }
    @Test
    public void updateParticipantEmail_whenParticipantExists() throws Exception {
        String newEmail = "newemail@example.com";
        mockMvc.perform(put("/api/participant/{id}/email", savedParticipant.getParticipantID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + newEmail + "\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("\"newemail@example.com\""));
    }
    @Test
    public void updateParticipantEmail_whenParticipantDoesNotExist() throws Exception {
        String newEmail = "newemail@example.com";
        mockMvc.perform(put("/api/participant/{id}/email", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + newEmail + "\""))
                .andExpect(status().isNotFound());
    }

//    @Test
//    public void deleteParticipantById_whenParticipantExists() throws Exception {
//        mockMvc.perform(delete("/api/participant/{id}", savedParticipant.getParticipantID())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        mockMvc.perform(get("/api/participant/{id}", savedParticipant.getParticipantID())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }

//    @Test
//    public void deleteParticipantByName_whenNameExists() throws Exception {
//        mockMvc.perform(delete("/api/participant/name/{name}", "Test Participant")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/api/participant")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isEmpty());
//    }
}