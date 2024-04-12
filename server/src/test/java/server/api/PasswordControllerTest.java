package server.api;

import commons.Password;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import server.database.PasswordRepository;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordRepository passwordRepository;

    @Test
    public void getPassword_Success() throws Exception {
        Password password = new Password();
        password.setPassID(1L);
        password.setPassword("123!");

        given(passwordRepository.findAll()).willReturn(Collections.singletonList(password));

        mockMvc.perform(get("/api/password"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'password':'123!'}"));
    }

    @Test
    public void addPass_Success() throws Exception {
        Password password = new Password();
        password.setLength(7);
        password.setPassword("123!");

        given(passwordRepository.findAll()).willReturn(Collections.emptyList());
        given(passwordRepository.save(any(Password.class))).willReturn(password);

        mockMvc.perform(post("/api/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"length\":7}"))
                .andExpect(status().isOk());

        verify(passwordRepository, times(1)).save(any(Password.class));
    }

    @Test
    public void deletePass_Success() throws Exception {
        Password password = new Password();
        password.setPassID(1L);
        password.setPassword("123!");

        given(passwordRepository.findById(1L)).willReturn(Optional.of(password));

        mockMvc.perform(delete("/api/password/{id}", 1L))
                .andExpect(status().isOk());

        verify(passwordRepository, times(1)).delete(any(Password.class));
    }

    // I could not manage to get the deleteAllPass_Success test to work
}