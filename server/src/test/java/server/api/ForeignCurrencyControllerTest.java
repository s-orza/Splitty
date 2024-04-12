package server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import server.service.CurrencyService;

@SpringBootTest
@AutoConfigureMockMvc
public class ForeignCurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    public void getExchangeRate_Success() throws Exception {
        String date = "2023-10-01";
        String from = "USD";
        String to = "EUR";
        double expectedRate = 0.85;

        when(currencyService.getExchangeRateAndUpdateCacheFile(anyString(), anyString(), anyString())).thenReturn(expectedRate);

        mockMvc.perform(get("/api/foreignCurrencies/{date}?from={from}&to={to}", date, from, to))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(String.valueOf(expectedRate)));
    }

    @Test
    public void getExchangeRate_InternalServerError() throws Exception {
        String date = "2023-10-01";
        String from = "USD";
        String to = "EUR";

        when(currencyService.getExchangeRateAndUpdateCacheFile(anyString(), anyString(), anyString())).thenReturn(-1.0);

        mockMvc.perform(get("/api/foreignCurrencies/{date}?from={from}&to={to}", date, from, to))
                .andExpect(status().isInternalServerError());
    }
}
