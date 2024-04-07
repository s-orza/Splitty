package server.api;

import commons.Password;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.database.PasswordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordRepository passwordRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        passwordRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        passwordRepository.deleteAll();
    }

    @Test
    public void testAddPassword() throws Exception {
        Password password = new Password(10);

        mockMvc.perform(post("/api/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length").value(10));

        assert passwordRepository.findAll().size() == 1;
    }

    @Test
    public void testGetPassword() throws Exception {
        Password password = new Password(8);
        passwordRepository.save(password);

        mockMvc.perform(get("/api/password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length").value(8));
    }

    @Test
    public void testDeletePasswordById() throws Exception {
        Password password = passwordRepository.save(new Password(8));

        mockMvc.perform(delete("/api/password/" + password.getPassID()))
                .andExpect(status().isOk());

        assert passwordRepository.findAll().isEmpty();
    }

    @Test
    public void testDeleteAllPasswords() throws Exception {
        passwordRepository.save(new Password(8));
        passwordRepository.save(new Password(10));

        mockMvc.perform(delete("/api/password"))
                .andExpect(status().isOk());

        assert passwordRepository.findAll().isEmpty();
    }
}
