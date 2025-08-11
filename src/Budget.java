package src;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.YearMonth;

public class Budget {
    private Map<String, Double> categoryBudgets;
    private YearMonth budgetMonth;

    public Budget(YearMonth month) {
        this.budgetMonth = month;
        this.categoryBudgets = new HashMap<>();
    }

    public Budget() {
        this(YearMonth.now());
    }

    public void setBudget(String category, double amount) {
        if (amount >= 0) {
            categoryBudgets.put(category, amount);
        }
    }

    public double getBudget(String category) {
        return categoryBudgets.getOrDefault(category, 0.0);
    }

    public Map<String, Double> getAllBudgets() {
        return new HashMap<>(categoryBudgets);
    }

    public double getTotalBudget() {
        return categoryBudgets.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public YearMonth getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(YearMonth month) {
        this.budgetMonth = month;
    }

    public boolean hasBudget(String category) {
        return categoryBudgets.containsKey(category) && categoryBudgets.get(category) > 0;
    }

    public double getRemainingBudget(String category, double spent) {
        double budget = getBudget(category);
        return Math.max(0, budget - spent);
    }

    public double getBudgetUsagePercentage(String category, double spent) {
        double budget = getBudget(category);
        if (budget == 0) return 0;
        return Math.min(100, (spent / budget) * 100);
    }

    public boolean isOverBudget(String category, double spent) {
        return spent > getBudget(category);
    }

    public boolean isNearBudgetLimit(String category, double spent, double threshold) {
        double budget = getBudget(category);
        if (budget == 0) return false;
        return (spent / budget) >= threshold;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Budget for ").append(budgetMonth).append(":\n");
        for (Map.Entry<String, Double> entry : categoryBudgets.entrySet()) {
            sb.append(String.format("  %s: $%.2f\n", entry.getKey(), entry.getValue()));
        }
        sb.append(String.format("Total Budget: $%.2f\n", getTotalBudget()));
        return sb.toString();
    }

    public String toCSVString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : categoryBudgets.entrySet()) {
            sb.append(budgetMonth).append(",")
              .append(entry.getKey()).append(",")
              .append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}