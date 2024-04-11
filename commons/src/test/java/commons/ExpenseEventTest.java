package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseEventTest {
    @Test
    void testNotNull()
    {
        ExpenseEvent expenseEvent =new ExpenseEvent();
        assertNotNull(expenseEvent);
    }
    @Test
    void getTest() {
        ExpenseEvent expenseEvent=new ExpenseEvent(1,2);
        expenseEvent.setId(0);
        assertEquals(0,expenseEvent.getId());
        assertEquals(1,expenseEvent.getExpenseId());
        assertEquals(2,expenseEvent.getEventId());
    }

    @Test
    void setTests() {
        ExpenseEvent expenseEvent=new ExpenseEvent(1,2);
        expenseEvent.setId(0);
        expenseEvent.setExpenseId(3);
        expenseEvent.setEventId(4);
        assertEquals(0,expenseEvent.getId());
        assertEquals(3,expenseEvent.getExpenseId());
        assertEquals(4,expenseEvent.getEventId());
    }
}