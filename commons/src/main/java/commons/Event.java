package commons;

import jakarta.persistence.*;


import java.util.*;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Column
    long eventId;

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
            name = "DebtEvent",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "debtID")
    )
    @Column
    public List<Debt> debts;

//    @Column
    @OneToMany
    public List<Expense> expenses;
    @Transient
    private List<Tag> tags;


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
        this.tags = new ArrayList<>();
        this.name = name;
        this.creationDate = new Date(System.currentTimeMillis());
        this.activityDate = creationDate;
    }

    /**
     * Empty constructor for testing and more
      */
    public Event() {
        this.participants = new ArrayList<>();
        this.debts = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.name = "Name not set";
        this.creationDate = new Date(System.currentTimeMillis());
        this.activityDate = creationDate;
    }



//    @Column


    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    // for the next PUBLIC method, consider this temporary 'database' representation of events

    // Debt Functions

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
     * The getter method for the debts
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
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setEventId(long eventID) {
        this.eventId = eventID;
    }
    public long getEventId() {
        return eventId;
    }

    public List<Participant> getParticipants() {
        return participants;
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
                "eventID=" + eventId +
                ", name='" + name + '\'' +
                ", participants=" + participants +
                ", debts=" + debts +
                ", expenses=" + expenses +
                ", creationDate=" + creationDate +
                ", activityDate=" + activityDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (eventId != event.eventId) return false;
        if (!Objects.equals(participants, event.participants)) return false;
        if (!Objects.equals(debts, event.debts)) return false;
        return Objects.equals(expenses, event.expenses);
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
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

