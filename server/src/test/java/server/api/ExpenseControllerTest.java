package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import server.database.*;
import server.service.ExpenseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseRepository mockExpenseRepo;

    @MockBean
    private ExpenseEventRepository mockExpenseEventRepo;

    @MockBean
    private TagRepository mockTagRepo;

    @Test
    void addExpenseToEvent_BadRequest() throws Exception {
        mockMvc.perform(post("/api/expenses/saved")
                        .param("eventId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addExpenseToEvent_Success() throws Exception {
        Expense expense = new Expense();
        expense.setExpenseId(1L);

        when(mockExpenseRepo.save(any(Expense.class))).thenReturn(expense);
        when(mockExpenseEventRepo.save(any(ExpenseEvent.class))).thenReturn(new ExpenseEvent(1L, 1L));

        mockMvc.perform(post("/api/expenses/saved")
                        .param("eventId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseId").value(expense.getExpenseId()));

        verify(mockExpenseRepo, times(1)).save(any(Expense.class));
        verify(mockExpenseEventRepo, times(1)).save(any(ExpenseEvent.class));
    }

    @Test
    void addTag_WithNullTag_BadRequest() throws Exception {
        mockMvc.perform(post("/api/expenses/tags")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTag_WithTagNullId_NotFound() throws Exception {
        Tag tag = new Tag();

        tag.setId(null);

        mockMvc.perform(post("/api/expenses/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTag_WithExistingTagId_NotFound() throws Exception {
        Tag tag = new Tag(new TagId("TagName", 1L), "Color");
        when(mockTagRepo.existsById(any(TagId.class))).thenReturn(true);

        mockMvc.perform(post("/api/expenses/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTag_WithValidTag_Success() throws Exception {
        Tag tag = new Tag(new TagId("TagName", 1L), "Color");
        when(mockTagRepo.existsById(any(TagId.class))).thenReturn(false);
        when(mockTagRepo.save(any(Tag.class))).thenReturn(tag);

        mockMvc.perform(post("/api/expenses/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.name").value(tag.getId().getName()))
                .andExpect(jsonPath("$.color").value(tag.getColor()));

        verify(mockTagRepo, times(1)).save(any(Tag.class));
    }



    @Test
    void deleteExpById_InvalidParams() throws Exception {
        mockMvc.perform(delete("/api/expenses/")
                        .param("eventId", "-1")
                        .param("expenseId", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteExpById_NotFound() throws Exception {
        mockMvc.perform(delete("/api/expenses/")
                        .param("eventId", "1")
                        .param("expenseId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExpById_ConnectionFail() throws Exception {
        when(mockExpenseRepo.existsById(1L)).thenReturn(true);
        when(mockExpenseRepo.deleteExpenseEventCon(1L, 1L)).thenReturn(0);

        mockMvc.perform(delete("/api/expenses/")
                        .param("eventId", "1")
                        .param("expenseId", "1"))
                .andExpect(status().is(444));
    }

    @Test
    void deleteAllExpensesFromEvent_InvalidParams() throws Exception {
        mockMvc.perform(delete("/api/expenses/allFromEvent")
                        .param("eventId", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAllExpensesFromEvent_Success() throws Exception {
        when(mockExpenseRepo.findAllExpOfAnEvent(1L)).thenReturn(new ArrayList<Expense>());

        mockMvc.perform(delete("/api/expenses/allFromEvent")
                        .param("eventId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTag_NotFound() throws Exception {
        when(mockTagRepo.existsById(any(TagId.class))).thenReturn(false);

        mockMvc.perform(delete("/api/expenses/tags")
                        .param("tagName", "someTag")
                        .param("eventId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTag_Success() throws Exception {
        when(mockTagRepo.existsById(any(TagId.class))).thenReturn(true);
        when(mockExpenseRepo.findAllExpOfAnEvent(1L)).thenReturn(new ArrayList<Expense>());

        mockMvc.perform(delete("/api/expenses/tags")
                        .param("tagName", "someTag")
                        .param("eventId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void updateExpense_InvalidId() throws Exception {
        mockMvc.perform(put("/api/expenses/")
                        .param("expenseId", "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Expense())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateExpense_NotFound() throws Exception {
        long expenseId = 1L;
        when(mockExpenseRepo.existsById(expenseId)).thenReturn(false);

        mockMvc.perform(put("/api/expenses/")
                        .param("expenseId", String.valueOf(expenseId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Expense())))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateExpense_Success() throws Exception {
        long expenseId = 1L;
        Expense expense = new Expense();
        expense.setExpenseId(expenseId);

        when(mockExpenseRepo.existsById(expenseId)).thenReturn(true);
        when(mockExpenseRepo.findById(expenseId)).thenReturn(Optional.of(new Expense()));
        when(mockExpenseRepo.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense savedExpense = invocation.getArgument(0);
            savedExpense.setExpenseId(expenseId);
            return savedExpense;
        });

        mockMvc.perform(put("/api/expenses/")
                        .param("expenseId", String.valueOf(expenseId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(expense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseId").value(expenseId));
    }

    @Test
    void updateTag_NotFound() throws Exception {
        String tagName = "ExistingTag";
        long eventId = 1L;
        when(mockTagRepo.existsById(new TagId(tagName, eventId))).thenReturn(false);

        mockMvc.perform(put("/api/expenses/tags")
                        .param("tagName", tagName)
                        .param("eventId", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Tag())))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateTag_Success() throws Exception {
        String originalTagName = "ExistingTag";
        long eventId = 1L;
        Tag newTag = new Tag(new TagId("NewName", eventId), "NewColor");
        Tag expectedTag = new Tag(new TagId("NewName", eventId), "NewColor");

        when(mockTagRepo.existsById(new TagId(originalTagName, eventId))).thenReturn(true);
        when(mockTagRepo.updateTag(originalTagName, eventId, newTag.getId().getName(), newTag.getColor())).thenReturn(1);
        when(mockTagRepo.getTagByIdFromEvent(newTag.getId().getName(), eventId)).thenReturn(expectedTag);

        mockMvc.perform(put("/api/expenses/tags")
                        .param("tagName", originalTagName)
                        .param("eventId", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newTag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.name").value(newTag.getId().getName()))
                .andExpect(jsonPath("$.color").value(newTag.getColor()));

        verify(mockTagRepo, times(1)).updateTag(originalTagName, eventId, newTag.getId().getName(), newTag.getColor());
        verify(mockTagRepo, times(1)).getTagByIdFromEvent(newTag.getId().getName(), eventId);
    }
}