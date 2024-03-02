package commons;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Objects;
import java.lang.Math;

public class DebtManager {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long debtManagerID;

    @OneToMany
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

                //if cancel out
                if(debt.getAmount() == 0){return (settleDebt(debt));}
                //if cred & debt switch
                if(debt.getAmount() < 0){
                    Participant oldDebtor = debt.getDebtor();
                    debt.setDebtor(debt.getCreditor());
                    debt.setCreditor(oldDebtor);
                    debt.setAmount(Math.abs(debt.getAmount()));
                }
                return debt;
            }
        }
        Debt d = new Debt(amount, currency, debtor, creditor);
        debts.add(d);
        return d;
    }

    /**
     * adds a new debt if it does not exist yet.
     * If a debt exists between the creditor and debtor, it modifies this debt accordingly
     * @param d the debt to be added
     * @return the debt that was modified or added
     */
    public Debt addDebt(Debt d){
        // if a debt already exists, modify it
        for (Debt debt: debts) {
            if(debt.getDebtor()==d.getDebtor() && debt.getCreditor()==d.getCreditor()){
                // if diff currency, return null (add currency conversion later)
                if(!Objects.equals(d.getCurrency(), debt.getCurrency())){return null;}

                //add debts
                debt.setAmount(debt.getAmount() + d.getAmount());
                return debt;
            }
            else if(debt.getDebtor()==d.getCreditor() && debt.getCreditor()==d.getDebtor()){
                // if diff currency, return null (add currency conversion later)
                if(!Objects.equals(d.getCurrency(), debt.getCurrency())){return null;}

                //subtract debts
                debt.setAmount(debt.getAmount() - d.getAmount());

                //if cancel out
                if(debt.getAmount() == 0){return (settleDebt(debt));}
                //if cred & debt switch
                if(debt.getAmount() < 0){
                    Participant oldDebtor = debt.getDebtor();
                    debt.setDebtor(debt.getCreditor());
                    debt.setCreditor(oldDebtor);
                    debt.setAmount(Math.abs(debt.getAmount()));
                }
                return debt;
            }
        }
        debts.add(d);
        return d;
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
     * Resets all the debts by removing them
     * @return the list of previous debts that was removed
     */
    public ArrayList<Debt> clearAllDebts(){
        ArrayList<Debt> result = getDebts();
        setDebts(new ArrayList<Debt>());
        return result;
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
