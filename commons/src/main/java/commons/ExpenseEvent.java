package commons;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_event")
public class ExpenseEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "expense_id")
    private long expenseId;

    @Column(name = "event_id")
    private long eventId;

    public ExpenseEvent() {
    }

    public ExpenseEvent(long expenseId, long eventId) {
        this.expenseId = expenseId;
        this.eventId = eventId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(long expenseId) {
        this.expenseId = expenseId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setParticipantId(long eventId) {
        this.eventId = eventId;
    }


}
