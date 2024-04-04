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
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.database.TagRepository;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private TagRepository tagRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Event testEvent;
    private Participant testParticipant;
    private Tag testTag;

    @BeforeEach
    public void setup() {
        testEvent = eventRepository.save(new Event("Sample Event"));
        testParticipant = participantRepository.save(
                new Participant("Sample Participant", "sample@example.com",
                        "SAMPLEIBAN", "SAMPLEBIC"));
        TagId tagId = new TagId("Lunch", testEvent.getEventId());
        testTag = tagRepository.save(new Tag(tagId, "#000000"));

    }

    @AfterEach
    public void cleanup() {
        expenseRepository.deleteAll();
        eventRepository.deleteAll();
        participantRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    public void testAddExpenseToEvent() throws Exception {
        Expense testExpense =
                new Expense(testParticipant, "Lunch", 15.0, "USD",
                "2023-04-01", new ArrayList<>(), "Food");
        mockMvc.perform(post("/api/expenses/saved?eventId=" + testEvent.getEventId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Lunch"));

        assertThat(expenseRepository.findAll()).hasSize(1);
    }

    @Test
    public void testUpdateExpense() throws Exception {
        Expense savedExpense =
                expenseRepository.save(
                        new Expense(testParticipant, "Original",
                        20.0, "EUR", "2023-04-02", null, "Misc"));

        Expense updateInfo = new Expense(
                testParticipant, "Updated", 25.0, "USD",
                "2023-04-03", null, "UpdatedCategory");
        mockMvc.perform(put("/api/expenses/?expenseId=" + savedExpense.getExpenseId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated"));

        Expense updatedExpense = expenseRepository.findById(savedExpense.getExpenseId()).get();
        assertThat(updatedExpense.getContent()).isEqualTo("Updated");
        assertThat(updatedExpense.getMoney()).isEqualTo(25.0);
    }

    @Test
    public void testDeleteExpense() throws Exception {
        Expense expense = new Expense(
                testParticipant, "To be deleted", 10.0,
                "EUR", "2023-04-02", null, "DeleteTest");
        expenseRepository.save(expense);
        mockMvc.perform(post("/api/expenses/saved?eventId=" + testEvent.getEventId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("To be deleted"));

        System.out.println(expense.getExpenseId() + " " + testEvent.getEventId());
        mockMvc.perform(delete("/api/expenses/?eventId=" + testEvent.getEventId()
                        + "&expenseId=" + expense.getExpenseId()))
                .andExpect(status().isOk());

        assertThat(expenseRepository.existsById(expense.getExpenseId())).isFalse();
    }

    @Test
    public void testGetAllExpensesFromEvent() throws Exception {
//        Expense a = new Expense(testParticipant, "Expense 1", 10.0, "EUR", "2023-04-02", null, "Category1");
//        Expense b = new Expense(testParticipant, "Expense 2", 20.0, "USD", "2023-04-03", null, "Category2");

        testAddExpenseToEvent();

        mockMvc.perform(get("/api/expenses/allFromEvent?eventId=" + testEvent.getEventId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetExpenseById() throws Exception {
        Expense savedExpense = expenseRepository.save(
                new Expense(testParticipant, "Dinner", 30.0,
                        "EUR", "2024-02-01", null, "Food"));

        mockMvc.perform(get("/api/expenses/?expenseId=" + savedExpense.getExpenseId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Dinner"));
    }

    @Test
    public void testAddTag() throws Exception {
        Tag newTag = new Tag(new TagId("Coffee", testEvent.getEventId()), "#ffffff");

        mockMvc.perform(post("/api/expenses/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("#ffffff"));

        assertThat(tagRepository.existsById(new TagId("Coffee", testEvent.getEventId()))).isTrue();
    }

    @Test
    public void testUpdateTag() throws Exception {
        Tag newTag = new Tag(new TagId("Coffee", testEvent.getEventId()), "#ffffff");

        mockMvc.perform(post("/api/expenses/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("#ffffff"));

        Tag updatedTag = new Tag(new TagId("Breakfast", testEvent.getEventId()), "#111111");

        mockMvc.perform(put("/api/expenses/tags?tagName=Coffee&eventId=" + testEvent.getEventId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("#111111"));

        Tag fetchedTag = tagRepository.findById(new TagId("Breakfast", testEvent.getEventId())).orElseThrow();
        assertThat(fetchedTag.getColor()).isEqualTo("#111111");
    }

    @Test
    public void testDeleteTag() throws Exception {
        System.out.println(testEvent.getEventId());
        mockMvc.perform(delete("/api/expenses/tags?tagName=Lunch&eventId=" + testEvent.getEventId()))
                .andExpect(status().isOk());

        assertThat(tagRepository.existsById(new TagId("Lunch", testEvent.getEventId()))).isFalse();
    }

    @Test
    public void getExpensePInvolvedInEvent_NotFound() throws Exception {
        long nonExistentEventId = -1;
        long nonExistentAuthorId = -1;

        mockMvc.perform(get("/api/expenses/participantIncluded")
                        .param("eventId", String.valueOf(nonExistentEventId))
                        .param("authorId", String.valueOf(nonExistentAuthorId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllExpenses_Success() throws Exception {
        Expense expense1 = new Expense(
                testParticipant, "Lunch for Team", 50.0,
                "USD", "2024-04-03", Arrays.asList(testParticipant), "Food");
        Expense expense2 = new Expense(
                testParticipant, "Team Coffee Break", 20.0,
                "USD", "2024-04-03", Arrays.asList(testParticipant), "Beverage");
        expenseRepository.save(expense1);
        expenseRepository.save(expense2);

        mockMvc.perform(get("/api/expenses/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[1].content").exists())
                .andExpect(jsonPath("$[0].money").exists())
                .andExpect(jsonPath("$[1].money").exists())
                .andExpect(jsonPath("$[0].type").value("Food"))
                .andExpect(jsonPath("$[1].type").value("Beverage"));
    }

    @Test
    public void getTag_Success() throws Exception {
        mockMvc.perform(get("/api/expenses/tags")
                        .param("tag", testTag.getId().getName())
                        .param("eventId", String.valueOf(testTag.getId().getEventId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value(testTag.getColor()));
    }

    @Test
    public void getTag_NotFound() throws Exception {
        mockMvc.perform(get("/api/expenses/tags")
                        .param("tag", "NonExistentTag")
                        .param("eventId", String.valueOf(testEvent.getEventId())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAllExpensesFromEvent_Success() throws Exception {
        Expense additionalExpense = new Expense(
                testParticipant, "Additional Expense", 20.0, "USD",
                "2023-05-01", new ArrayList<>(), "Additional");
        mockMvc.perform(post("/api/expenses/saved?eventId=" + testEvent.getEventId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(additionalExpense)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/expenses/allFromEvent")
                        .param("eventId", String.valueOf(testEvent.getEventId())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/expenses/allFromEvent?eventId=" + testEvent.getEventId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void deleteAllExpensesFromEvent_BadRequest() throws Exception {

        mockMvc.perform(delete("/api/expenses/allFromEvent")
                        .param("eventId", String.valueOf(-1)))
                .andExpect(status().isBadRequest());
    }
}