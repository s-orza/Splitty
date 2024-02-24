package commons;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    Event event;
    @BeforeEach
    void setUp(){
        List<Person> personList = new ArrayList<>();
        List<Expense> expensesList = new ArrayList<>();
        personList.add(new Person("Anne", "Claude"));
        personList.add(new Person("Mauricio", "MoveitMoveit"));
        List<String> participants = new ArrayList<>();
        participants.add("Somebody");
        participants.add("That");
        participants.add("I");
        participants.add("Used");
        participants.add("To");
        participants.add("Know");
        Expense expense1 = new Expense("Gotye","Eating-out",15.99,"EUR",
                LocalDate.of(2024,2,23),participants,"Papa Johns"),
            expense2 = new Expense("Skillet","Grocceries",99.99,"EUR",
                LocalDate.of(2024,2,20),participants,"from AH - expensive");
        expensesList.add(expense1);
        expensesList.add(expense2);
        event = new Event();
        event.addExpense(expense1);
        event.addExpense(expense2);
        event.addListOfParticipants(personList);
    }

    @Test
    public void eventConstructorTest(){
        assertNotNull(event);
    }

    @Test
    public void EventIDUniquenessTest(){
        assertNotEquals(new Random().longs(), event.getEventID());
    }

    @Test
    public void getExpensesListTest(){
        List<String> participants = new ArrayList<>();
        participants.add("Somebody");
        participants.add("That");
        participants.add("I");
        participants.add("Used");
        participants.add("To");
        participants.add("Know");
        Expense expense1 = new Expense("Gotye","Eating-out",15.99,"EUR",
                LocalDate.of(2024,2,23),participants,"Papa Johns"),
                expense2 = new Expense("Skillet","Grocceries",99.99,"EUR",
                        LocalDate.of(2024,2,20),participants,"from AH - expensive");
        List<Expense> expensesList = new ArrayList<>();
        expensesList.add(expense1);
        expensesList.add(expense2);
        assertEquals(event.getExpenses(), expensesList);
    }

}
