package commons;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long expenseId;
    @Column
    private String author;
    @Column
    private String content;
    @Column
    private double money;
    @Column
    private String currency;
    @Column
    private String date;
    @Column
    @ManyToMany
    private List<Participant> participants;
    @Column
    private String type;

    public Expense(String author, String content, double money, String currency,
                   String date, List<Participant> participants, String type) {
        this.author = author;
        this.content = content;
        this.money = money;
        this.currency = currency;
        this.date = date;
        this.participants = participants;
        this.type = type;
    }

    public long getExpenseId() {
        return expenseId;
    }


    public Expense() {

    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public double getMoney() {
        return money;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDate() {
        return date;
    }
    public void setExpenseId(long expenseId) {
        this.expenseId = expenseId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", money=" + money +
                ", currency='" + currency + '\'' +
                ", date=" + date +
                ", participants=" + participants +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(getMoney(), expense.getMoney()) == 0 &&
                Objects.equals(getAuthor(), expense.getAuthor()) && Objects.equals(getContent(),
                expense.getContent()) && Objects.equals(getCurrency(), expense.getCurrency())
                && Objects.equals(getDate(), expense.getDate()) &&
                Objects.equals(getParticipants(), expense.getParticipants()) &&
                Objects.equals(getType(), expense.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuthor(), getContent(), getMoney(), getCurrency(), getDate(),
                getParticipants(), getType());
    }
}
