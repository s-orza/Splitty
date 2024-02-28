package commons;

import jakarta.persistence.*;

@Entity
@Table(name = "participant_expense")
public class ParticipantExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "expense_id")
    private long expenseId;

    @Column(name = "participant_id")
    private long participantId;

    public ParticipantExpense() {
    }

    public ParticipantExpense(long expenseId, long participantId) {
        this.expenseId = expenseId;
        this.participantId = participantId;
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

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }


}
