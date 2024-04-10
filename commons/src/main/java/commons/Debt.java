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
    private String currency;
    private long debtor;
    private long creditor;

    public Debt(double amount, String currency, Participant debtor, Participant creditor){
        this.amount = amount;
        this.currency = currency;
        this.debtor = debtor.getParticipantID();
        this.creditor = creditor.getParticipantID();
    }

    public Debt() {
    }

    public Debt(double amount, String currency, long debtorID, long creditorID){
        this.amount = amount;
        this.currency = currency;
        this.debtor = debtorID;
        this.creditor = creditorID;
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
     * The getter method for the currency attribute
     *
     * @return currency of this object
     **/
    public String getCurrency() {
        return currency;
    }

    /**
     * The setter method for the currency attribute
     *
     * @param currency The value to set currency to
     **/
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * The getter method for the debtor attribute
     *
     * @return debtor of this object
     **/
    public long getDebtor() {
        return debtor;
    }


    /**
     * The setter method for the debtor attribute
     *
     * @param debtorID The value to set debtor to
     **/
    public void setDebtor(long debtorID) {
        this.debtor = debtorID;
    }

    /**
     * The getter method for the creditor attribute
     *
     * @return creditor of this object
     **/
    public long getCreditor() {
        return creditor;
    }

    /**
     * The setter method for the creditor attribute
     *
     * @param creditorID The value to set creditor to
     **/
    public void setCreditor(long creditorID) {
        this.creditor = creditorID;
    }

    @Override
    public String toString() {
        return "Debt{" +
                "debtID=" + debtID +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", debtor=" + debtor +
                ", creditor=" + creditor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return getDebtID() == debt.getDebtID() &&
                Double.compare(debt.getAmount(), getAmount()) == 0 &&
                getDebtor() == debt.getDebtor() &&
                getCreditor() == debt.getCreditor() &&
                Objects.equals(getCurrency(), debt.getCurrency());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDebtID(), getAmount(), getCurrency(), getDebtor(), getCreditor());
    }
}
