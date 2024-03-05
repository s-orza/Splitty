package commons;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class DebtTest {
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
    public void currencyTest(){
        Debt debt = new Debt(amount, currency, debtor, creditor);
        assertEquals(debt.getCurrency(), "EUR");
        debt.setCurrency("USD");
        assertEquals(debt.getCurrency(), "USD");
    }

//    @Test
//    public void debtorTest(){
//        Debt debt = new Debt(amount, currency, debtor, creditor);
//        assertEquals(new Participant("Steve", "steve@gmail.com", "123", "bic"), debt.getDebtor());
//        debt.setDebtor(creditor);
//        assertEquals(new Participant("Alex", "alex@gmail.com", "ABC", "bic2"), debt.getDebtor());
//    }
//
//    @Test
//    public void creditorTest(){
//        Debt debt = new Debt(amount, currency, debtor, creditor);
//        assertEquals(new Participant("Alex", "alex@gmail.com", "ABC", "bic2"), debt.getCreditor());
//        debt.setCreditor(debtor);
//        assertEquals(new Participant("Steve", "steve@gmail.com", "123", "bic"), debt.getCreditor());
//    }

}
