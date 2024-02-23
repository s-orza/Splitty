package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long eventID;
    private List<Person> participants;
    private List<Expense> expenses;
    private class Expense{
        private int amountPayed;
        private String payerName, title;
    }

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

    public List<Person> getParticipants() {
        return participants;
    }

    public void addParticipant(Person newParticipant){
        participants.add(newParticipant);
    }

    public void addListOfParticipants(List<Person> participantsList) {
        participants.addAll(participantsList);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public Expense getLastExpense(){
        return expenses.get(expenses.size() - 1);
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
