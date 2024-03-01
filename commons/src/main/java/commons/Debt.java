package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Debt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long debtID;

    private double amount;
    private Participant debtor;
    private Participant creditor;

    public Debt(double amount, Participant debtor, Participant creditor){
        this.amount = amount;
        this.debtor = debtor;
        this.creditor = creditor;
    }

    /**
     * The getter method for the debtID attribute
     *
     * @return debtID of this object
     **/
    public long getDebtID() {
        return debtID;
    }

    /**
     * The setter method for the debtID attribute
     *
     * @param debtID The value to set debtID to
     **/
    public void setDebtID(long debtID) {
        this.debtID = debtID;
    }

    /**
     * The getter method for the amount attribute
     *
     * @return amount of this object
     **/
    public double getAmount() {
        return amount;
    }

    /**
     * The setter method for the amount attribute
     *
     * @param amount The value to set amount to
     **/
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * The getter method for the debtor attribute
     *
     * @return debtor of this object
     **/
    public Participant getDebtor() {
        return debtor;
    }

    /**
     * The setter method for the debtor attribute
     *
     * @param debtor The value to set debtor to
     **/
    public void setDebtor(Participant debtor) {
        this.debtor = debtor;
    }

    /**
     * The getter method for the creditor attribute
     *
     * @return creditor of this object
     **/
    public Participant getCreditor() {
        return creditor;
    }

    /**
     * The setter method for the creditor attribute
     *
     * @param creditor The value to set creditor to
     **/
    public void setCreditor(Participant creditor) {
        this.creditor = creditor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return getDebtID() == debt.getDebtID() &&
                Double.compare(debt.getAmount(),getAmount()) == 0 &&
                getDebtor().equals(debt.getDebtor()) &&
                getCreditor().equals(debt.getCreditor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDebtID(), getAmount(), getDebtor(), getCreditor());
    }
}
