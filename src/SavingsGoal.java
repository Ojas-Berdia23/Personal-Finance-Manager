package src;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SavingsGoal {
    private static int nextId = 1;
    private int id;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private LocalDate startDate;
    private LocalDate targetDate;
    private String description;

    public SavingsGoal(String name, double targetAmount, LocalDate targetDate, String description) {
        this.id = nextId++;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
        this.startDate = LocalDate.now();
        this.targetDate = targetDate;
        this.description = description;
    }

    public SavingsGoal(String name, double targetAmount, LocalDate targetDate) {
        this(name, targetAmount, targetDate, "");
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getTargetDate() { return targetDate; }
    public String getDescription() { return description; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = Math.max(0, currentAmount); }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public void setDescription(String description) { this.description = description; }

    // Business methods
    public void addToSavings(double amount) {
        if (amount > 0) {
            this.currentAmount += amount;
        }
    }

    public double getRemainingAmount() {
        return Math.max(0, targetAmount - currentAmount);
    }

    public double getProgressPercentage() {
        if (targetAmount == 0) return 0;
        return Math.min(100, (currentAmount / targetAmount) * 100);
    }

    public boolean isGoalAchieved() {
        return currentAmount >= targetAmount;
    }

    public long getDaysRemaining() {
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }

    public long getTotalDays() {
        return ChronoUnit.DAYS.between(startDate, targetDate);
    }

    public double getRequiredMonthlySavings() {
        long daysRemaining = getDaysRemaining();
        if (daysRemaining <= 0) return getRemainingAmount();

        double monthsRemaining = daysRemaining / 30.0;
        return getRemainingAmount() / monthsRemaining;
    }

    public double getRequiredWeeklySavings() {
        long daysRemaining = getDaysRemaining();
        if (daysRemaining <= 0) return getRemainingAmount();

        double weeksRemaining = daysRemaining / 7.0;
        return getRemainingAmount() / weeksRemaining;
    }

    public String getTimeRemainingText() {
        long days = getDaysRemaining();
        if (days < 0) {
            return "Goal deadline passed";
        } else if (days == 0) {
            return "Goal deadline is today";
        } else if (days == 1) {
            return "1 day remaining";
        } else if (days < 30) {
            return days + " days remaining";
        } else if (days < 365) {
            long months = days / 30;
            return months + " month" + (months > 1 ? "s" : "") + " remaining";
        } else {
            long years = days / 365;
            long remainingMonths = (days % 365) / 30;
            return years + " year" + (years > 1 ? "s" : "") + 
                   (remainingMonths > 0 ? ", " + remainingMonths + " month" + 
                    (remainingMonths > 1 ? "s" : "") : "") + " remaining";
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Goal: %s\n" +
            "  Target: $%.2f | Current: $%.2f (%.1f%%)\n" +
            "  Remaining: $%.2f\n" +
            "  %s\n" +
            "  Monthly savings needed: $%.2f",
            name, targetAmount, currentAmount, getProgressPercentage(),
            getRemainingAmount(), getTimeRemainingText(), getRequiredMonthlySavings()
        );
    }

    public String toCSVString() {
        return String.format("%d,%s,%.2f,%.2f,%s,%s,%s",
                           id, name, targetAmount, currentAmount,
                           startDate, targetDate, description.replace(",", ";"));
    }

    public static SavingsGoal fromCSVString(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 6) {
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            double targetAmount = Double.parseDouble(parts[2]);
            double currentAmount = Double.parseDouble(parts[3]);
            LocalDate startDate = LocalDate.parse(parts[4]);
            LocalDate targetDate = LocalDate.parse(parts[5]);
            String description = parts.length > 6 ? parts[6].replace(";", ",") : "";

            SavingsGoal goal = new SavingsGoal(name, targetAmount, targetDate, description);
            goal.id = id;
            goal.currentAmount = currentAmount;
            goal.startDate = startDate;

            if (id >= nextId) {
                nextId = id + 1;
            }
            return goal;
        }
        return null;
    }
}