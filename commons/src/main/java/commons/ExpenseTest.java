package commons;
////////////////////////////////////

/**
 * This is just a Dummy Test class, because i needed to populate the event page with some data
 * Later it will be discarded and the proper expense class will be used
 */
///////////////////////////////////



public class ExpenseTest{
    public String author;
    public String description;
    public String date;
    public Double amount;
    public int id;

    public ExpenseTest() {
        this.amount = -1.0;
        this.date = "12-12-2012";
        this.description = "Dinner payment";
        this.author = "Default Name";
        this.id = -1;
    }

    public ExpenseTest(String author, String description, String date, double amount) {
        this.author = author;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.id = (int) Math.round(Math.random()*10000);
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }
}