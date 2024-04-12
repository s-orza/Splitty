package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import server.service.DebtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import server.database.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DebtControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DebtRepository mockRepo;

    @MockBean
    private DebtService mockDebtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        debtController = new DebtController(mockRepo, mockDebtService);
    }

    @Test
    void getDebtById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/events/debts/{id}", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDebtById_NotFound() throws Exception {
        long invalidId = 2L;
        when(mockRepo.findById(invalidId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/events/debts/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDebtById_Found() throws Exception {
        long validId = 1L;
        Debt expectedDebt = new Debt(100, "USD", 1L, 2L);
        when(mockRepo.findById(validId)).thenReturn(Optional.of(expectedDebt));

        mockMvc.perform(get("/api/events/debts/{id}", validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(expectedDebt.getAmount()))
                .andExpect(jsonPath("$.currency").value(expectedDebt.getCurrency()));
    }

    @Test
    void getAllDebts_ReturnsAllDebts() throws Exception {
        List<Debt> expectedDebts = Arrays.asList(new Debt(100, "USD", 1L, 2L), new Debt(200, "EUR", 3L, 4L));
        when(mockRepo.findAll()).thenReturn(expectedDebts);

        mockMvc.perform(get("/api/events/debts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedDebts.size()));
    }

    @Test
    void getListOfDebts_NullIds() throws Exception {
        mockMvc.perform(get("/api/events/debts/all").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getListOfDebts_InvalidIdInList() throws Exception {
        mockMvc.perform(post("/api/events/debts/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, -2, 3]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addDebt_Success() throws Exception {
        Debt debt = new Debt(100, "USD", 1L, 2L);
        long eventId = 1L;
        String date = "2022-01-01";

        mockMvc.perform(post("/api/events/debts")
                        .param("eventId", String.valueOf(eventId))
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debt)))
                .andExpect(status().isOk());

    }


    @Test
    public void addListOfDebts_Success() throws Exception {
        List<Debt> debts = Arrays.asList(new Debt(100, "USD", 1L, 2L), new Debt(200, "EUR", 3L, 4L));

        mockMvc.perform(post("/api/events/debts/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debts)))
                .andExpect(status().isOk());
    }

    @Test
    public void addListOfDebts_NullList() throws Exception {
        mockMvc.perform(post("/api/events/debts/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void settleDebtByID_Found() throws Exception {
        long id = 1L;
        long eventId = 1L;
        Debt debt = new Debt(100, "USD", 1L, 2L);
        when(mockRepo.findById(id)).thenReturn(Optional.of(debt));

        mockMvc.perform(delete("/api/events/debts/{id}", id)
                        .param("eventId", String.valueOf(eventId)))
                .andExpect(status().isOk());
    }

    @Test
    public void settleDebtByID_NotFound() throws Exception {
        long id = 1L;
        long eventId = 1L;
        when(mockRepo.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/events/debts/{id}", id)
                        .param("eventId", String.valueOf(eventId)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void settleDebt_Success() throws Exception {
        long eventId = 1L;
        Debt existingDebt = new Debt(100, "USD", 1L, 2L);
        existingDebt.setDebtID(1L);
        when(mockRepo.existsById(existingDebt.getDebtID())).thenReturn(true);
        when(mockRepo.findById(existingDebt.getDebtID())).thenReturn(Optional.of(existingDebt));

        doAnswer(invocation -> {
            Debt debt = invocation.getArgument(1);
            return true;
        }).when(mockDebtService).deleteDebt(eq(eventId), any(Debt.class));


        mockMvc.perform(delete("/api/events/debts")
                        .param("eventId", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingDebt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debtID").value(existingDebt.getDebtID()))
                .andExpect(jsonPath("$.amount").value(existingDebt.getAmount()))
                .andExpect(jsonPath("$.currency").value(existingDebt.getCurrency()));
    }

    @Test
    public void settleDebt_NotFound() throws Exception {
        long eventId = 1L;
        Debt debt = new Debt(100, "USD", 1L, 2L);
        when(mockRepo.existsById(debt.getDebtID())).thenReturn(false);

        mockMvc.perform(delete("/api/events/debts/")
                        .param("eventId", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debt)))
                .andExpect(status().isNotFound());
    }

    @Test
    void settleDebtByID_WithValidId() throws Exception {
        long validId = 1L;
        Debt expectedDebt = new Debt(100, "USD", 1L, 2L);
        expectedDebt.setDebtID(validId);
        when(mockRepo.findById(validId)).thenReturn(Optional.of(expectedDebt));

        mockMvc.perform(delete("/api/events/debts/{id}/noEvent", validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debtID").value(expectedDebt.getDebtID()))
                .andExpect(jsonPath("$.amount").value(expectedDebt.getAmount()));

        verify(mockRepo).delete(expectedDebt);
    }
    @Test
    void settleDebt_NoEvent() throws Exception {
        Debt validDebt = new Debt(100, "USD", 1L, 2L);
        validDebt.setDebtID(1L);
        when(mockRepo.existsById(validDebt.getDebtID())).thenReturn(true);

        mockMvc.perform(delete("/api/events/debts/noEvent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDebt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debtID").value(validDebt.getDebtID()));

        verify(mockRepo).delete(validDebt);
    }

    @Test
    void settleDebt_BadRequest() throws Exception {
        mockMvc.perform(delete("/api/events/debts/noEvent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void settleDebt_NotFoundNoEvent() throws Exception {
        Debt nonExistingDebt = new Debt(200, "EUR", 3L, 4L);
        nonExistingDebt.setDebtID(2L);
        when(mockRepo.existsById(nonExistingDebt.getDebtID())).thenReturn(false);

        mockMvc.perform(delete("/api/events/debts/noEvent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingDebt)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getListOfDebts_AllIdsValid_ReturnsAllDebts() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Debt> expectedDebts = Arrays.asList(
                new Debt(100, "USD", 1L, 2L),
                new Debt(200, "EUR", 3L, 4L)
        );
        for (int i = 0; i < ids.size(); i++) {
            when(mockRepo.existsById(ids.get(i))).thenReturn(true);
            when(mockRepo.findById(ids.get(i))).thenReturn(Optional.of(expectedDebts.get(i)));
        }

        mockMvc.perform(get("/api/events/debts/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedDebts.size()))
                .andExpect(jsonPath("$[0].amount").value(expectedDebts.get(0).getAmount()))
                .andExpect(jsonPath("$[1].amount").value(expectedDebts.get(1).getAmount()));
    }

    @Test
    void getListOfDebts_NotFoundIdInList_ReturnsNotFound() throws Exception {
        List<Long> ids = Arrays.asList(1L, 4L);
        Debt existingDebt = new Debt(100, "USD", 1L, 2L);
        when(mockRepo.existsById(1L)).thenReturn(true);
        when(mockRepo.existsById(4L)).thenReturn(false);
        when(mockRepo.findById(1L)).thenReturn(Optional.of(existingDebt));

        mockMvc.perform(get("/api/events/debts/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNotFound());
    }

}
