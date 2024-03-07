package commons;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    Event event = new Event("123"), event2 = new Event("123");
    List<Participant> participants = new ArrayList<>();
    List<Expense> expensesList = new ArrayList<>();
    @BeforeEach
    void setUp(){
        Participant Gotye=new Participant("Gotye","","","");
        Participant Skillet=new Participant("Skillet","","","");
        participants.add(new Participant("name", "email", "iban", "bic"));
        participants.add(new Participant("name2", "email2", "iban2", "bic2"));
        Expense expense1 = new Expense(Gotye,"Eating-out",15.99,"EUR",
                "2024,2,23",participants,"Papa Johns"),
                expense2 = new Expense(Skillet,"Groceries",99.99,"EUR",
                        "2024,2,23", participants,"from AH - expensive");
        expensesList.add(expense1);
        expensesList.add(expense2);
        event.addExpense(expense1);
        event.addExpense(expense2);
        event.addListOfParticipants(participants);
        event2.addExpense(expense1);
        event2.addExpense(expense2);
        event2.addListOfParticipants(participants);
    }

    @Test
    public void uniqueIDTest(){
            assertNotEquals(event.getEventId(), event2.getEventId());
    }
    @Test
    public void eventConstructorTest(){
        assertNotNull(event);
    }

    @Test
    public void getParticipantsListTest(){
        assertEquals(participants, event.getParticipants());
    }

    @Test
    public void getExpensesListTest(){
        assertEquals(event.getExpenses(), expensesList);
    }

    // Test if different as eventID is different
    @Test
    public void EqualsTest(){
        assertFalse(event.equals(event2));
    }

    @Test
    public void hashCodeTest(){
        assertNotEquals(event.hashCode(), event2.hashCode());
    }

}
