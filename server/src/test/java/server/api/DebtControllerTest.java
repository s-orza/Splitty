package server.api;

import commons.*;
import server.service.DebtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import server.database.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DebtControllerTest {
    @Mock
    private DebtRepository mockRepo;

    @Mock
    private DebtService mockDebtService;

    private DebtController debtController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        debtController = new DebtController(mockRepo, mockDebtService);
    }

    @Test
    void getDebtById_Success() {
        // Given
        long validId = 1L;
        Debt expectedDebt = new Debt();
        when(mockRepo.findById(validId)).thenReturn(Optional.of(expectedDebt));
        ResponseEntity<Debt> response = debtController.getDebtById(validId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDebt, response.getBody());
        verify(mockRepo).findById(validId);
    }

    @Test
    void getDebtById_InvalidId() {
        long invalidId = -1L;
        ResponseEntity<Debt> response = debtController.getDebtById(invalidId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(mockRepo, never()).findById(anyLong());
    }

    @Test
    void getDebtById_DoesNotExist() {
        long validIdButNoDebt = 2L;
        when(mockRepo.findById(validIdButNoDebt)).thenReturn(Optional.empty());


        ResponseEntity<Debt> response = debtController.getDebtById(validIdButNoDebt);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(mockRepo).findById(validIdButNoDebt);
    }

    @Test
    void getAllDebts_NotEmptyList() {

        Debt debt1 = new Debt();
        Debt debt2 = new Debt();
        List<Debt> expectedDebts = Arrays.asList(debt1, debt2);
        when(mockRepo.findAll()).thenReturn(expectedDebts);

        ResponseEntity<List<Debt>> response = debtController.getAllDebts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDebts, response.getBody());
        verify(mockRepo).findAll();
    }

    @Test
    void getListOfDebts_Success() {
        List<Long> ids = Arrays.asList(1L, 2L);
        Debt debt1 = new Debt();
        Debt debt2 = new Debt();
        when(mockRepo.existsById(1L)).thenReturn(true);
        when(mockRepo.existsById(2L)).thenReturn(true);
        when(mockRepo.findById(1L)).thenReturn(Optional.of(debt1));
        when(mockRepo.findById(2L)).thenReturn(Optional.of(debt2));

        ResponseEntity<List<Debt>> response = debtController.getListOfDebts(ids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(mockRepo, times(1)).existsById(1L);
        verify(mockRepo, times(1)).existsById(2L);
    }

    @Test
    void getListOfDebts_BadRequest() {
        List<Long> idsWithInvalid = Arrays.asList(-1L, 1L);
        ResponseEntity<List<Debt>> response = debtController.getListOfDebts(idsWithInvalid);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getListOfDebts_NotFound() {
        List<Long> idsWithNonExistent = Arrays.asList(1L, 3L);
        Debt debt1 = new Debt();
        when(mockRepo.existsById(1L)).thenReturn(true);
        when(mockRepo.existsById(3L)).thenReturn(false);
        when(mockRepo.findById(1L)).thenReturn(Optional.of(debt1));

        ResponseEntity<List<Debt>> response = debtController.getListOfDebts(idsWithNonExistent);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(mockRepo).existsById(1L);
        verify(mockRepo).existsById(3L);
    }

    @Test
    void getListOfDebts_IdDoesNotExists() {
        List<Long> ids = Arrays.asList(1L, 3L);
        when(mockRepo.existsById(1L)).thenReturn(true);
        when(mockRepo.existsById(3L)).thenReturn(false);

        Debt debt1 = new Debt();
        when(mockRepo.findById(1L)).thenReturn(Optional.of(debt1));
        when(mockRepo.findById(3L)).thenReturn(Optional.empty());

        ResponseEntity<List<Debt>> response = debtController.getListOfDebts(ids);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(mockRepo, times(1)).existsById(1L);
        verify(mockRepo, times(1)).existsById(3L);
    }


    @Test
    void addDebt_Success() {
        long eventId = 1L;
        String date = "2024-04-10";
        Debt validDebt = new Debt();
        doNothing().when(mockDebtService).saveDebtToEvent(anyLong(), any(Debt.class), anyString());

        ResponseEntity<Debt> response = debtController.addDebt(eventId, date, validDebt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validDebt, response.getBody());
        verify(mockDebtService, times(1)).saveDebtToEvent(eventId, validDebt, date);
    }

    @Test
    void addDebt_ReturnsBadRequest_WhenDebtIsNull() {
        long eventId = 1L;
        String date = "2024-04-10";

        ResponseEntity<Debt> response = debtController.addDebt(eventId, date, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(mockDebtService, never()).saveDebtToEvent(anyLong(), any(Debt.class), anyString());
    }

    @Test
    void addListOfDebts_ReturnsBadRequest_WhenListIsNull() {
        ResponseEntity<List<Debt>> response = debtController.addListOfDebts(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void settleDebtByID_IdNull() {
        ResponseEntity<Debt> response = debtController.settleDebtByID(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void settleDebtByID_DoesNotExist() {
        when(mockRepo.findById(anyLong())).thenReturn(Optional.empty());
        ResponseEntity<Debt> response = debtController.settleDebtByID(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void settleDebtByID_Success() {
        Debt debt = new Debt();
        when(mockRepo.findById(anyLong())).thenReturn(Optional.of(debt));
        ResponseEntity<Debt> response = debtController.settleDebtByID(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockRepo, times(1)).delete(debt);
    }

    @Test
    void settleDebt_DebtIsNull() {
        ResponseEntity<Debt> response = debtController.settleDebt(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void settleDebt_DebtDoesNotExist() {
        Debt debt = new Debt();
        debt.setDebtID(1L);
        when(mockRepo.existsById(anyLong())).thenReturn(false);
        ResponseEntity<Debt> response = debtController.settleDebt(debt);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void settleListOfDebts_ListIsNull() {
        ResponseEntity<List<Debt>> response = debtController.settleListOfDebts(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void settleListOfDebts_Success() {
        Debt debt1 = new Debt();
        debt1.setDebtID(1L);
        Debt debt2 = new Debt();
        debt2.setDebtID(2L);
        List<Debt> debts = Arrays.asList(debt1, debt2);
        when(mockRepo.existsById(debt1.getDebtID())).thenReturn(true);
        when(mockRepo.existsById(debt2.getDebtID())).thenReturn(true);
        ResponseEntity<List<Debt>> response = debtController.settleListOfDebts(debts);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockRepo, times(1)).delete(debt1);
        verify(mockRepo, times(1)).delete(debt2);
    }

}
