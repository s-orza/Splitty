package commons;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    Event event = new Event("123"), event2 = new Event("123");
    List<Participant> participants = new ArrayList<>();
    List<Expense> expensesList = new ArrayList<>();

    // Debts
    Participant a;
    Participant b;
    Participant c;

    Debt d1;
    Debt d1copy;
    Debt inv1;
    Debt inv2;
    Debt d2;
    Debt d3;
    Debt d4;

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

        // Debt Setup //

        a = new Participant("Olav","a","iban1","bic1");
        b = new Participant("Constant","b","iban2","bic2");
        c = new Participant("Peters","c","iban3","bic3");

        a.setParticipantID(10);
        b.setParticipantID(20);
        c.setParticipantID(30);

        d1 = new Debt(1.00,"EUR",a,b);
        d1copy = new Debt(1.00,"EUR",a,b);
        inv1 = new Debt(1.00,"EUR",b,a);
        inv2 = new Debt(2.00,"EUR",b,a);
        d2 = new Debt(1.22,"EUR",a,c);
        d3 = new Debt(1.33,"EUR",c,a);
        d4 = new Debt(1.33,"EUR",c,b);


        d1.setDebtID(1);
        d1copy.setDebtID(2);
        inv1.setDebtID(3);
        inv2.setDebtID(4);
        d2.setDebtID(5);
        d3.setDebtID(6);
        d4.setDebtID(6);
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

    // Debt Tests
    @Test
    public void addExistingDebtTest(){
        Event dm = new Event("name");
        dm.addDebt(d1);
        dm.addDebt(d2);

        ArrayList<Debt> debts = new ArrayList<>();
        debts.add(d1);
        debts.add(d2);
        assertEquals(debts,dm.getDebts());

        // breaking even
        dm.addDebt(inv1);
        ArrayList<Debt> debts2 = new ArrayList<>();
        debts2.add(d2);
        assertEquals(debts2,dm.getDebts());

        // going over
        Event dm2 = new Event("name2");
        dm2.addDebt(d1copy);
        dm2.addDebt(d2);
        dm2.addDebt(inv2);

        ArrayList<Debt> debts3 = new ArrayList<>();
        debts3.add(inv1);
        inv1.setDebtID(2);
        debts3.add(d2);

        assertEquals(debts3,dm2.getDebts());
    }
    @Test
    void testNotNull()
    {
        Event ev =new Event();
        assertNotNull(ev);
    }
    @Test
    void getTest()
    {
        Event event1=new Event("Party");
        assertEquals("Party",event1.getName());
    }
    @Test
    void setTest()
    {
        Event event1=new Event("Party");
        List<Tag> tags=new ArrayList<>();
        tags.add(new Tag(new TagId("a",1),"red"));
        event1.setEventId(1);
        event1.setCreationDate(null);
        event1.setParticipants(null);
        event1.setActivityDate(null);
        event1.setExpenses(null);
        event1.setTags(tags);
        event1.setName("ara");
        assertNull(event1.getCreationDate());
        assertNull(event1.getParticipants());
        assertNull(event1.getActivityDate());
        assertNull(event1.getExpenses());
        assertEquals(1,event1.getEventId());
        assertEquals("ara",event1.getName());
        assertEquals("red",event1.getTags().get(0).getColor());

    }
    @Test
    void addDebt1()
    {
        Event event1=new Event("Party");
        Debt debt1=new Debt(1,"EUR",2,3);
        Debt debt2=new Debt(1,"RON",2,3);
        event1.addDebt(debt1);
        assertNull(event1.addDebt(debt2));
    }
    @Test
    void addDebt2()
    {
        Event event1=new Event("Party");
        Debt debt1=new Debt(1,"EUR",2,3);
        Debt debt2=new Debt(1,"EUR",2,3);
        event1.addDebt(debt1);
        assertEquals(2.0,event1.addDebt(debt2).getAmount());
    }
    @Test
    void settleDebt1()
    {
        Event event1=new Event("Party");
        Debt debt1=new Debt(1,"EUR",2,3);
        assertNull(event1.settleDebt(debt1));
    }
    @Test
    void toStringTest()
    {
        Event event1=new Event("Party");
        //the date is a live date so we cannot test this in the usual way like
        //Event{eventID=0, name='Party', participants=[], debts=[], expenses=[],
        // creationDate=Thu Apr 11 00:40:47 CEST 2024, activityDate=Thu Apr 11 00:40:47 CEST 2024}
        assertTrue(event1.toString().length()>20);
    }
    @Test
    void activityTest()
    {
        Event event1=new Event("Party");
        event1.activity();
        assertNotNull(event1.getActivityDate());
    }
    @Test
    void equalsTest()
    {
        Event event1=new Event("Party");
        Event event2=new Event("Party");
        assertEquals(event1,event2);
    }
    @Test
    void hashCodeTest()
    {
        Event event1=new Event("Party");
        Event event2=new Event("Party");
        assertEquals(event1.hashCode(),event2.hashCode());
    }

    @Test
    public void addNewDebtTest(){
        Event dm = new Event("name");
        dm.addDebt(new Debt(1.33,"EUR",c,a));


        List<Debt> debts2 = new ArrayList<>();
        debts2.add(d3);
        //since new debt ahs an ID of 0 w/o the Database
        d3.setDebtID(0);


        assertEquals(debts2,dm.getDebts());
    }


    @Test
    public void settleDebtTest(){
        Event dm = new Event("name");
        dm.addDebt(d1);
        dm.addDebt(d2);

        ArrayList<Debt> debts = new ArrayList<>();
        debts.add(d2);

        dm.settleDebt(d1);

        assertEquals(debts,dm.getDebts());
    }

    @Test
    public void clearAllDebtTest(){
        Event dm = new Event("name");
        dm.addDebt(d1);
        dm.addDebt(d2);

        ArrayList<Debt> debts = new ArrayList<>();
        debts.add(d1);
        debts.add(d2);

        assertEquals(debts,dm.getDebts());
        dm.clearAllDebts();

        assertEquals(new ArrayList<>(),dm.getDebts());
    }

    @Test
    public void getParticipantDebtsTest(){
        Event dm = new Event("name");
        dm.addDebt(d1);
        dm.addDebt(d2);
        dm.addDebt(d4);

        ArrayList<Debt> debts = new ArrayList<>();
        debts.add(d1);
        debts.add(d2);

        assertEquals(debts,dm.getDebts(a));
    }

    @Test
    public void getParticipantCredTest(){
        Event dm = new Event("name");
        dm.addDebt(d1);
        dm.addDebt(d2);
        dm.addDebt(d4);

        ArrayList<Debt> debts = new ArrayList<>();
        debts.add(d1);
        debts.add(d4);

        assertEquals(debts,dm.getCredits(b));
    }

    @Test
    public void getParticipantDebtAndCredTest(){
        Event dm = new Event("name");
        dm.addDebt(d1);
        dm.addDebt(d3);
        dm.addDebt(d4);

        ArrayList<Debt> debts = new ArrayList<>();
        debts.add(d1);
        debts.add(d3);

        assertEquals(debts,dm.getDebtsAndCredits(a));
    }
}
