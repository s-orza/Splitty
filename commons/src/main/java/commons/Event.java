package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;


import java.util.*;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Column
    long eventID;

    @Column
    String name;
    // Don't know how to set up the connections, need a database anyway

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ParticipantEventRepository",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @Column
    public List<Participant> participants;

    @OneToMany
    @JoinTable(
            name = "ParticipantEventRepository",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @Column
    public List<Debt> debts;

//    @Column
    @OneToMany
    public List<Expense> expenses;
    @Column
    private Date creationDate;
    @Column
    private Date activityDate;

    /**
     * Constructor for an Event object
     * @param name Event's name
     */
    public Event(String name) {

        // Possible to try and generate a random ID that contains letters & numbers (possible venture)
//        checkUniqueness(eventID);


        this.participants = new ArrayList<>();
        this.debts = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.name = name;
        this.creationDate = new Date(System.currentTimeMillis());
        this.activityDate = creationDate;
    }

    /**
     * Empty constructor for testing and more
      */
    public Event() {

    }



//    @Column


    // for the next PUBLIC method, consider this temporary 'database' representation of events
    @Column
    @OneToMany
    private List<Event> eventList = new ArrayList<>();
    private void setUpEventList(){
        eventList.add(new Event());
        eventList.add(new Event());
        eventList.add(new Event());
        eventList.get(0).setEventId(1);
        eventList.get(1).setEventId(2);
        eventList.get(2).setEventId(3);
    }
    /**
     * Checks whether the ID of the event has already been used and whether to generate a new one
     * or not
     * @param eventID ID to be tested for uniqueness
     */
//    private void checkUniqueness(Long eventID){
//        //TODO
//        // Pull from the database a list of all events and compare this ID with all other IDS
//        // If similar to any, generate a new random number
//        setUpEventList();
//        while (eventList.contains(eventID)){
//            eventID = new Random().nextLong();
//        }
//    }



    // Debt Functions

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
        long d = debtor.getParticipantID();
        long c = creditor.getParticipantID();
        // if a debt already exists, modify it
        for (Debt debt: debts) {
            if(debt.getDebtor()==d && debt.getCreditor()==c){
                // if diff currency, return null (add currency conversion later)
                if(!Objects.equals(currency, debt.getCurrency())){return null;}

                //add debts
                debt.setAmount(debt.getAmount() + amount);
                return debt;
            }
            else if(debt.getDebtor()==c && debt.getCreditor()==d){
                // if diff currency, return null (add currency conversion later)
                if(!Objects.equals(currency, debt.getCurrency())){return null;}

                //subtract debts
                debt.setAmount(debt.getAmount() - amount);

                //if cancel out
                if(debt.getAmount() == 0){return (settleDebt(debt));}
                //if cred & debt switch
                if(debt.getAmount() < 0){
                    long oldDebtor = debt.getDebtor();
                    debt.setDebtor(debt.getCreditor());
                    debt.setCreditor(oldDebtor);
                    debt.setAmount(Math.abs(debt.getAmount()));
                }
                return debt;
            }
        }
        Debt res = new Debt(amount, currency, debtor, creditor);
        debts.add(res);
        return res;
    }

    /**
     * adds a new debt if it does not exist yet.
     * If a debt exists between the creditor and debtor, it modifies this debt accordingly
     * @param amount the amount of the debt
     * @param currency the currency of the debt
     * @param debtorID the ID of the debtor who owes
     * @param creditorID the ID of the creditor who is owed
     * @return the debt that was modified or created
     */
    public Debt addDebt(double amount, String currency, long debtorID, long creditorID){
        // if a debt already exists, modify it
        for (Debt debt: debts) {
            if(debt.getDebtor()==debtorID && debt.getCreditor()==creditorID){
                // if diff currency, return null (add currency conversion later)
                if(!Objects.equals(currency, debt.getCurrency())){return null;}

                //add debts
                debt.setAmount(debt.getAmount() + amount);
                return debt;
            }
            else if(debt.getDebtor()==creditorID && debt.getCreditor()==debtorID){
                // if diff currency, return null (add currency conversion later)
                if(!Objects.equals(currency, debt.getCurrency())){return null;}

                //subtract debts
                debt.setAmount(debt.getAmount() - amount);

                //if cancel out
                if(debt.getAmount() == 0){return (settleDebt(debt));}
                //if cred & debt switch
                if(debt.getAmount() < 0){
                    long oldDebtor = debt.getDebtor();
                    debt.setDebtor(debt.getCreditor());
                    debt.setCreditor(oldDebtor);
                    debt.setAmount(Math.abs(debt.getAmount()));
                }
                return debt;
            }
        }
        Debt res = new Debt(amount, currency, debtorID, creditorID);
        debts.add(res);
        return res;
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
                    long oldDebtor = debt.getDebtor();
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
    public List<Debt> clearAllDebts(){
        ArrayList<Debt> result = (ArrayList) getDebts();
        setDebts(new ArrayList<Debt>());
        return result;
    }

    /**
     * returns a list of all the debts a participant has
     * @param debtor the participant who owes
     * @return List of debts a participant owes
     */
    public List<Debt> getDebts(Participant debtor){
        long d = debtor.getParticipantID();
        ArrayList<Debt> result = new ArrayList<>();
        for (Debt debt: debts) {
            if(debt.getDebtor()==d){
                result.add(debt);
            }
        }
        return result;
    }

    /**
     * returns a list of all the debts a participant has
     * @param debtor the participant who owes
     * @return List of debts a participant owes
     */
    public ArrayList<Debt> getDebts(long debtor){
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
        long c = creditor.getParticipantID();
        ArrayList<Debt> result = new ArrayList<>();
        for (Debt credit: debts) {
            if(credit.getCreditor()==c){
                result.add(credit);
            }
        }
        return result;
    }

    /**
     * returns a list of all the credits a participant is owed
     * @param creditor the participant who is owed
     * @return ArrayList of credits a participant is owed
     */
    public ArrayList<Debt> getCredits(long creditor){
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
     * @param participant the participant
     * @return ArrayList of debts the participant is involved in
     */
    public ArrayList<Debt> getDebtsAndCredits(Participant participant){
        long p = participant.getParticipantID();
        ArrayList<Debt> result = new ArrayList<>();
        for (Debt debt: debts) {
            if(debt.getCreditor()==p || debt.getDebtor()==p){
                result.add(debt);
            }
        }
        return result;
    }

    /**
     * returns a list of all the credits and debts a participant is involved in
     * @param participantID the participant's ID
     * @return ArrayList of debts the participant is involved in
     */
    public ArrayList<Debt> getDebtsAndCredits(long participantID){
        ArrayList<Debt> result = new ArrayList<>();
        for (Debt debt: debts) {
            if(debt.getCreditor()==participantID || debt.getDebtor()==participantID){
                result.add(debt);
            }
        }
        return result;
    }

    /**
     * The getter method for the debts
     *
     * @return debts of this event
     **/
    public List<Debt> getDebts() {
        return debts;
    }

    /**
     * The setter method for the debts
     *
     * @param debts The List of Debts to set debts to
     **/
    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    // End Debt Functions

    public void setEventId(long eventID) {
        this.eventID = eventID;
    }

    public long getEventId() {
        return eventID;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void addParticipant(Participant newParticipant){
        participants.add(newParticipant);
    }

    public void removeParticipant(String participantToRemove){
        participants.remove(participantToRemove);
    }

    public void clearParticipantsList(){
        participants = new ArrayList<>();
    }

    public void addListOfParticipants(List<Participant> participantsList) {
        participants.addAll(participantsList);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void removeExpense(Expense expense){
        expenses.remove(expense);
    }

    public void clearExpensesList(){
        expenses = new ArrayList<>();
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventID=" + eventID +
                ", name='" + name + '\'' +
                ", participants=" + participants +
                ", debts=" + debts +
                ", expenses=" + expenses +
                ", creationDate=" + creationDate +
                ", activityDate=" + activityDate +
                ", eventList=" + eventList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (eventID != event.eventID) return false;
        if (!Objects.equals(participants, event.participants)) return false;
        if (!Objects.equals(debts, event.debts)) return false;
        return Objects.equals(expenses, event.expenses);
    }

    @Override
    public int hashCode() {
        int result = (int) (eventID ^ (eventID >>> 32));
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
        result = 31 * result + (debts != null ? debts.hashCode() : 0);
        result = 31 * result + (expenses != null ? expenses.hashCode() : 0);
        return result;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void activity(){
        this.activityDate = new Date(System.currentTimeMillis());
    }

}

