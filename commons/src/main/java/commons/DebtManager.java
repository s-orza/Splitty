package commons;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.Objects;

public class DebtManager {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long debtManagerID;

    private ArrayList<Debt> debts;

    public DebtManager() {
        debts = new ArrayList<Debt>();
    }

    public DebtManager(ArrayList<Debt> debts) {
        this.debts = debts;
    }

    /**
     * The getter method for the debtManagerID attribute
     *
     * @return debtManagerID of this object
     **/
    public long getDebtManagerID() {
        return debtManagerID;
    }

    /**
     * The setter method for the debtManagerID attribute
     *
     * @param debtManagerID The value to set debtManagerID to
     **/
    public void setDebtManagerID(long debtManagerID) {
        this.debtManagerID = debtManagerID;
    }

    /**
     * The getter method for the debts attribute
     *
     * @return debts of this object
     **/
    public ArrayList<Debt> getDebts() {
        return debts;
    }

    /**
     * The setter method for the debts attribute
     *
     * @param debts The value to set debts to
     **/
    public void setDebts(ArrayList<Debt> debts) {
        this.debts = debts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebtManager that = (DebtManager) o;
        return getDebtManagerID() == that.getDebtManagerID() && getDebts().equals(that.getDebts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDebtManagerID(), getDebts());
    }
}
