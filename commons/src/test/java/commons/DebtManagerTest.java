package commons;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class DebtManagerTest {

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
    public void addExistingDebtTest(){
//        System.out.println(d1);
//        System.out.println(d1copy);
//        System.out.println(inv1);
//        System.out.println(inv2);
//        System.out.println(d2);
//        System.out.println(d3);
//        System.out.println(d4);
        DebtManager dm = new DebtManager();
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
        DebtManager dm2 = new DebtManager();
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
    public void addNewDebtTest(){
        DebtManager dm = new DebtManager();
        dm.addDebt(1.33,"EUR",c,a);


        ArrayList<Debt> debts2 = new ArrayList<>();
        debts2.add(d3);
        //since new debt ahs an ID of 0 w/o the Database
        d3.setDebtID(0);


        assertEquals(debts2,dm.getDebts());
    }


    @Test
    public void settleDebtTest(){
        DebtManager dm = new DebtManager();
        dm.addDebt(d1);
        dm.addDebt(d2);

        ArrayList<Debt> debts = new ArrayList<>();
        debts.add(d2);

        dm.settleDebt(d1);

        assertEquals(debts,dm.getDebts());
    }

    @Test
    public void clearAllDebtTest(){
        DebtManager dm = new DebtManager();
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
        DebtManager dm = new DebtManager();
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
        DebtManager dm = new DebtManager();
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
        DebtManager dm = new DebtManager();
        dm.addDebt(d1);
        dm.addDebt(d3);
        dm.addDebt(d4);

        ArrayList<Debt> debts = new ArrayList<>();
        debts.add(d1);
        debts.add(d3);

        assertEquals(debts,dm.getDebtsAndCredits(a));
    }
}
