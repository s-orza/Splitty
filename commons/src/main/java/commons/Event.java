package commons;

import jakarta.persistence.*;


import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long eventID;
    @OneToMany // Don't know how to set up the connections, need a database anyway
    private List<Participant> participants;
    @OneToMany
    private List<Expense> expenses;

    public Event(long eventID) {
        this.eventID = eventID;
        this.participants = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
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

    @Override
    public String toString() {
        return "Event{" +
                "event_id=" + eventID +
                ", participants=" + participants +
                ", expenses=" + expenses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (eventID != event.eventID) return false;
        if (!Objects.equals(participants, event.participants)) return false;
        return Objects.equals(expenses, event.expenses);
    }

    @Override
    public int hashCode() {
        int result = (int) (eventID ^ (eventID >>> 32));
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
        result = 31 * result + (expenses != null ? expenses.hashCode() : 0);
        return result;
    }
}

