package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String author;
    private String content;
    private double money;
    private String currency;
    private LocalDate date;
    private List<String> participants;
    private String type;

    public Expense(String author, String content, double money, String currency,
                   LocalDate date, List<String> participants, String type) {
        this.author = author;
        this.content = content;
        this.money = money;
        this.currency = currency;
        this.date = date;
        this.participants = participants;
        this.type = type;
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

    public LocalDate getDate() {
        return date;
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

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getParticipants() {
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
