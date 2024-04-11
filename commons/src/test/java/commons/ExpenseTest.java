package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpenseTest {
    Expense expense;
    @BeforeEach
    void setUp() {
        List<Participant> participants=new ArrayList<>();
        participants.add(new Participant("Alex","a","b","c"));
        participants.add(new Participant("Mirela","a","b","c"));
        participants.add(new Participant("Gigi","a","b","c"));
        expense=new Expense(new Participant("Alex","a","b","c"),
                "some water",23.5,"EUR",
                "22,2,2024",participants,"Drinks");
    }

    @Test
    void getAuthor() {
        assertEquals(new Participant("Alex","a","b","c"), expense.getAuthor());
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
    void testNotNull()
    {
        Expense expenseNotNull=new Expense();
        assertNotNull(expenseNotNull);
    }
    @Test
    void testData()
    {
        Participant p1=new Participant("Alex","a","b","c");
        Participant p2=new Participant("Bob","a","b","c");
        List<Participant> list=new ArrayList<>();
        list.add(p2);
        Expense expense1=new Expense(p1,
                "some water",23.5,"EUR",
                "22,2,2024",new ArrayList<>(),"Drinks");
        expense1.setExpenseId(1);
        expense1.setAuthor(p2);
        expense1.setContent("mici");
        expense1.setMoney(10.0);
        expense1.setCurrency("RON");
        expense1.setDate("20-02-2024");
        expense1.setParticipants(list);
        expense1.setType("Food");

        assertEquals(1,expense1.getExpenseId());
        assertEquals(p2,expense1.getAuthor());
        assertEquals("mici",expense1.getContent());
        assertEquals(10.0,expense1.getMoney());
        assertEquals("RON",expense1.getCurrency());
        assertEquals("20-02-2024",expense1.getDate());
        assertEquals(list,expense1.getParticipants());
        assertEquals("Food",expense1.getType());
    }
    @Test
    void testToString()
    {
        assertEquals("Expense{0, author='Alex', content='some water', money=23.5, currency='EUR'" +
                ", date=22,2,2024, participants=[Alex, Mirela, Gigi], type='Drinks'}",expense.toString());
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
        Expense expense2=new Expense(new Participant("Alex","a","b","c"),
                "some water",23.5,"EUR",
                        "22,2,2024",people,"Drinks");
        assertEquals(expense,expense2);
    }

    @Test
    void testHashCode() {
        List<Participant> people=new ArrayList<>();
        people.add(new Participant("Alex","a","b","c"));
        people.add(new Participant("Mirela","a","b","c"));
        people.add(new Participant("Gigi","a","b","c"));
        Expense expense2=new Expense(new Participant("Alex","a","b","c"),
                "some water",23.5,"EUR",
                "22,2,2024",people,"Drinks");
        assertEquals(expense.hashCode(),expense2.hashCode());
    }
}