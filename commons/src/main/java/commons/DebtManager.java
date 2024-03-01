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

    /**
     * makes a new empty DebtManager
     */
    public DebtManager() {
        debts = new ArrayList<Debt>();
    }

    /**
     * makes a new DebtManager based on a list of debts
     * @param debts list of debts to make manager with
     */
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
     * adds a new debt if it does not exist yet.
     * If a debt exists between the creditor and debtor, it modifies this debt accordingly
     * @param amount the amount of the debt
     * @param currency the currency of the debt
     * @param debtor the debtor who owes
     * @param creditor the creditor who is owed
     * @return the debt that was modified or created
     */
    public Debt addDebt(double amount, String currency, Participant debtor, Participant creditor){
        // if a debt already exists, modify it
        for (Debt debt: debts) {
            if(debt.getDebtor()==debtor && debt.getCreditor()==creditor){
                // if diff currency, return null (add currency conversion later)
                if(!Objects.equals(currency, debt.getCurrency())){return null;}

                //add debts
                debt.setAmount(debt.getAmount() + amount);
                return debt;
            }
            else if(debt.getDebtor()==creditor && debt.getCreditor()==debtor){
                // if diff currency, return null (add currency conversion later)
                if(!Objects.equals(currency, debt.getCurrency())){return null;}

                //subtract debts
                debt.setAmount(debt.getAmount() - amount);
                return debt;
            }
        }

        return new Debt(amount, currency, debtor, creditor);
    }

    /**
     * settles a debt by removing it
     * @param debt debt to be settled
     * @return the settled debt, or null if the debt does not exist
     */
    public Debt settleDebt(Debt debt){
        if(!debts.remove(debt)){
            return null;
        }
        return debt;
    }

    /**
     * returns a list of all the debts a participant has
     * @param debtor the participant who owes
     * @return Arraylist of debts a participant owes
     */
    public ArrayList<Debt> getDebts(Participant debtor){
        ArrayList<Debt> result = new ArrayList<>();
        for (Debt debt: debts) {
            if(debt.getDebtor()==debtor){
                result.add(debt);
            }
        }
        return result;
    }

    /**
     * returns a list of all the credits a participant is owed
     * @param creditor the participant who is owed
     * @return ArrayList of credits a participant is owed
     */
    public ArrayList<Debt> getCredits(Participant creditor){
        ArrayList<Debt> result = new ArrayList<>();
        for (Debt credit: debts) {
            if(credit.getCreditor()==creditor){
                result.add(credit);
            }
        }
        return result;
    }

    /**
     * returns a list of all the credits and debts a participant is involved in
     * @param p the participant
     * @return ArrayList of debts the participant is involved in
     */
    public ArrayList<Debt> getDebtsAndCredits(Participant p){
        ArrayList<Debt> result = new ArrayList<>();
        for (Debt debt: debts) {
            if(debt.getCreditor()==p || debt.getDebtor()==p){
                result.add(debt);
            }
        }
        return result;
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
