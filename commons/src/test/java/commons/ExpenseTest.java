package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {
    Expense expense;
    @BeforeEach
    void setUp() {
        List<Participant> participants=new ArrayList<>();
        participants.add(new Participant("Alex","a","b","c"));
        participants.add(new Participant("Mirela","a","b","c"));
        participants.add(new Participant("Gigi","a","b","c"));
        expense=new Expense("Alex","some water",23.5,"EUR",
                "22,2,2024",participants,"Drinks");
    }

    @Test
    void getAuthor() {
        assertEquals("Alex", expense.getAuthor());
    }

    @Test
    void getContent() {
        assertEquals("some water", expense.getContent());
    }

    @Test
    void getMoney() {
        assertEquals(23.5, expense.getMoney());
    }

    @Test
    void getCurrency() {
        assertEquals("EUR", expense.getCurrency());
    }

    @Test
    void getDate() {
        String date="22,2,2024";
        assertEquals(date,expense.getDate());
    }

    @Test
    void getParticipants() {
        List<Participant> people=new ArrayList<>();
        people.add(new Participant("Alex","a","b","c"));
        people.add(new Participant("Mirela","a","b","c"));
        people.add(new Participant("Gigi","a","b","c"));
        assertEquals(people,
                expense.getParticipants());
    }

    @Test
    void getType() {
        assertEquals("Drinks", expense.getType());
    }

    @Test
    void testEquals() {
        List<Participant> people=new ArrayList<>();
        people.add(new Participant("Alex","a","b","c"));
        people.add(new Participant("Mirela","a","b","c"));
        people.add(new Participant("Gigi","a","b","c"));
        Expense expense2=new Expense("Alex","some water",23.5,"EUR",
                        "22,2,2024",people,"Drinks");
        assertEquals(expense,expense2);
    }

    @Test
    void testHashCode() {
        List<Participant> people=new ArrayList<>();
        people.add(new Participant("Alex","a","b","c"));
        people.add(new Participant("Mirela","a","b","c"));
        people.add(new Participant("Gigi","a","b","c"));
        Expense expense2=new Expense("Alex","some water",23.5,"EUR",
                "22,2,2024",people,"Drinks");
        assertEquals(expense.hashCode(),expense2.hashCode());
    }
}