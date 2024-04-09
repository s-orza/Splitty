package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import server.database.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DebtControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent;
    private Participant testParticipant;
    private Participant testCreditor;

    private Debt testDebt;


    @BeforeEach
    public void setup() {
        testEvent =
                eventRepository.save(new Event("Sample Event"));
        testParticipant =
                participantRepository.save(new Participant("Debtor Participant",
                        "debtor@example.com", "DEBTORIBAN", "DEBTORBIC"));
        testCreditor =
                participantRepository.save(new Participant("Creditor Participant",
                        "creditor@example.com", "CREDITORIBAN", "CREDITORBIC"));
        testDebt =
                debtRepository.save(new Debt(100, "USD", testParticipant.getParticipantID(),
                        testCreditor.getParticipantID()));
    }

    @AfterEach
    public void cleanup() {
        debtRepository.deleteAll();
        participantRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    public void getDebtById_Success() throws Exception {
        Debt savedDebt = debtRepository.findById(testDebt.getDebtID()).get();
        mockMvc.perform(get("/api/events/debts/" + savedDebt.getDebtID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debtID").value(savedDebt.getDebtID()))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    public void getAllDebts_Success() throws Exception {
        mockMvc.perform(get("/api/events/debts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").isNotEmpty());
    }


    @Test
    public void getListOfDebts_Success() throws Exception {
        Participant debtor = participantRepository.save(
                new Participant("Debtor", "debtor@example.com", "DEBTORIBAN", "DEBTORBIC"));
        Participant creditor = participantRepository.save(
                new Participant("Creditor", "creditor@example.com", "CREDITORIBAN", "CREDITORBIC"));
        Debt debt1 = debtRepository.save(new Debt(100, "USD", debtor.getParticipantID(), creditor.getParticipantID()));
        Debt debt2 = debtRepository.save(new Debt(200, "EUR", debtor.getParticipantID(), creditor.getParticipantID()));

        List<Long> ids = Arrays.asList(debt1.getDebtID(), debt2.getDebtID());

        mockMvc.perform(get("/api/events/debts/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].debtID").value(debt1.getDebtID()))
                .andExpect(jsonPath("$[1].debtID").value(debt2.getDebtID()));
    }

    @Test
    public void getListOfDebts_InvalidId() throws Exception {
        List<Long> ids = Arrays.asList(-1L);

        mockMvc.perform(get("/api/events/debts/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addListOfDebts_Success() throws Exception {
        Participant debtor = participantRepository.save(
                new Participant("Debtor Participant", "debtor@example.com", "DEBTORIBAN", "DEBTORBIC"));
        Participant creditor = participantRepository.save(
                new Participant("Creditor Participant", "creditor@example.com", "CREDITORIBAN", "CREDITORBIC"));

        List<Debt> debts = Arrays.asList(
                new Debt(100, "USD", debtor.getParticipantID(), creditor.getParticipantID()),
                new Debt(200, "EUR", debtor.getParticipantID(), creditor.getParticipantID())
        );

        mockMvc.perform(post("/api/events/debts/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debts)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[1].amount").value(200));
    }

    @Test
    public void testSettleDebtSuccess() throws Exception {
        mockMvc.perform(delete("/api/events/debts/noEvent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDebt)))
                .andExpect(status().isOk());

        assertThat(debtRepository.existsById(testDebt.getDebtID())).isFalse();
    }

    @Test
    public void testSettleDebtNotFound() throws Exception {

        Participant debtor = participantRepository.save(
                new Participant("Debtor", "debtor@example.com", "DEBTORIBAN", "DEBTORBIC"));
        Participant creditor = participantRepository.save(
                new Participant("Creditor", "creditor@example.com", "CREDITORIBAN", "CREDITORBIC"));


        Debt testDebt = new Debt(100.0, "USD", debtor.getParticipantID(), creditor.getParticipantID());
        testDebt.setDebtID(9999L);

        mockMvc.perform(delete("/api/events/debts/noEvent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDebt)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSettleDebtBadRequest() throws Exception {
        mockMvc.perform(delete("/api/events/debts/noEvent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

}
