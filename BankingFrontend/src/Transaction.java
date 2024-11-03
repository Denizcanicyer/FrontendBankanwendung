
import java.time.LocalDate;


public class Transaction {


    private int id;
    private String type; //
    private double amount;
    private String category;


    public Transaction() {
    }

    public Transaction(int id, String type, double amount, String category) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;

    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", type=" + type + ", amount=" + amount + ", category=" + category + "}";
    }
}
