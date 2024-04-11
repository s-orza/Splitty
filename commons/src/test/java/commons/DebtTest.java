package commons;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DebtTest {
    private Debt d;
    private double amount;
    private String currency;
    private Participant debtor;
    private Participant creditor;

    @BeforeEach
    void setUp(){
        amount = 420.69;
        currency = "EUR";
        debtor = new Participant("Steve", "steve@gmail.com", "123", "bic");
        creditor = new Participant("Alex", "alex@gmail.com", "ABC", "bic2");
        d=new Debt(2.0,"EUR",1,2);
    }

    @Test
    public void debtConstructorTest(){
        assertNotNull(new Debt(amount, currency, debtor, creditor));
    }

    @Test
    public void amountTest(){
        Debt debt = new Debt(amount, currency, debtor, creditor);
        assertEquals(debt.getAmount(), 420.69);
        debt.setAmount(10.00);
        assertEquals(debt.getAmount(), 10.00);
    }
    @Test
    void testNotNull()
    {
        Debt debt =new Debt();
        assertNotNull(debt);
        assertNotNull(d);
    }
    @Test
    void setTest()
    {
        d.setCreditor(3);
        d.setDebtor(4);
        assertEquals(3,d.getCreditor());
        assertEquals(4,d.getDebtor());
    }
    @Test
    void testToString()
    {
        String x=d.toString();
        assertEquals("Debt{debtID=0, amount=2.0, currency='EUR', debtor=1, creditor=2}",d.toString());
    }
    @Test
    public void currencyTest(){
        Debt debt = new Debt(amount, currency, debtor, creditor);
        assertEquals(debt.getCurrency(), "EUR");
        debt.setCurrency("USD");
        assertEquals(debt.getCurrency(), "USD");
    }
    @Test
    void testHashcode()
    {
        Debt d2=new Debt(2.0,"EUR",1,2);
        assertEquals(d.hashCode(),d2.hashCode());
    }

}
