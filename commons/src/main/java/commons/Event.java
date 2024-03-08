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
    private long eventId;

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

    @Column
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
        this.participants = new ArrayList<>();
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
    private void checkUniqueness(Long eventID){
        //TODO
        // Pull from the database a list of all events and compare this ID with all other IDS
        // If similar to any, generate a new random number
        setUpEventList();
        while (eventList.contains(eventID)){
            eventID = new Random().nextLong();
        }
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
                "event_id= " + eventId +
                ", participants= " + participants +
                ", expenses= " + expenses +
                ", name= " + name +
                ", creatinDate= " + creationDate +
                ", activityDate= " +activityDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (eventId != event.eventId) return false;
        if (!Objects.equals(participants, event.participants)) return false;
        return Objects.equals(expenses, event.expenses);
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
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

