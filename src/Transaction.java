package src;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static int nextId = 1;
    private int id;
    private String type; // "INCOME" or "EXPENSE"
    private double amount;
    private String category;
    private String description;
    private LocalDate date;

    public Transaction(String type, double amount, String category, String description, LocalDate date) {
        this.id = nextId++;
        this.type = type.toUpperCase();
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public Transaction(String type, double amount, String category, String description) {
        this(type, amount, category, description, LocalDate.now());
    }

    // Getters
    public int getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }

    // Setters
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | $%.2f | %s | %s | %s", 
                           id, type, amount, category, description, 
                           date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public String toCSVString() {
        return String.format("%d,%s,%.2f,%s,%s,%s", 
                           id, type, amount, category, description, 
                           date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public static Transaction fromCSVString(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 6) {
            int id = Integer.parseInt(parts[0]);
            String type = parts[1];
            double amount = Double.parseDouble(parts[2]);
            String category = parts[3];
            String description = parts[4];
            LocalDate date = LocalDate.parse(parts[5]);

            Transaction transaction = new Transaction(type, amount, category, description, date);
            transaction.id = id;
            if (id >= nextId) {
                nextId = id + 1;
            }
            return transaction;
        }
        return null;
    }
}