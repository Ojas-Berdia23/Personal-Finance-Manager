package src;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {
    private List<Transaction> transactions;
    private Budget currentBudget;

    public ReportGenerator(List<Transaction> transactions, Budget budget) {
        this.transactions = transactions;
        this.currentBudget = budget;
    }

    public void generateMonthlyReport(YearMonth month) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         MONTHLY FINANCIAL REPORT");
        System.out.println("              " + month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        System.out.println("=".repeat(50));

        List<Transaction> monthlyTransactions = getTransactionsForMonth(month);

        double totalIncome = calculateTotalIncome(monthlyTransactions);
        double totalExpenses = calculateTotalExpenses(monthlyTransactions);
        double netSavings = totalIncome - totalExpenses;

        System.out.printf("Total Income:     $%,10.2f\n", totalIncome);
        System.out.printf("Total Expenses:   $%,10.2f\n", totalExpenses);
        System.out.println("-".repeat(30));
        System.out.printf("Net Savings:      $%,10.2f\n", netSavings);

        if (netSavings < 0) {
            System.out.println("⚠️  Warning: You spent more than you earned this month!");
        }

        generateCategoryBreakdown(monthlyTransactions);
        generateBudgetComparison(monthlyTransactions, month);
    }

    public void generateYearlyReport(int year) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         YEARLY FINANCIAL REPORT");
        System.out.println("                   " + year);
        System.out.println("=".repeat(50));

        List<Transaction> yearlyTransactions = getTransactionsForYear(year);

        double totalIncome = calculateTotalIncome(yearlyTransactions);
        double totalExpenses = calculateTotalExpenses(yearlyTransactions);
        double netSavings = totalIncome - totalExpenses;

        System.out.printf("Total Income:     $%,10.2f\n", totalIncome);
        System.out.printf("Total Expenses:   $%,10.2f\n", totalExpenses);
        System.out.println("-".repeat(30));
        System.out.printf("Net Savings:      $%,10.2f\n", netSavings);

        if (totalIncome > 0) {
            double savingsRate = (netSavings / totalIncome) * 100;
            System.out.printf("Savings Rate:     %10.1f%%\n", savingsRate);
        }

        generateMonthlyTrends(yearlyTransactions, year);
        generateCategoryBreakdown(yearlyTransactions);
    }

    private void generateCategoryBreakdown(List<Transaction> transactions) {
        System.out.println("\n--- EXPENSE BREAKDOWN BY CATEGORY ---");

        Map<String, Double> expensesByCategory = transactions.stream()
            .filter(t -> t.getType().equals("EXPENSE"))
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));

        double totalExpenses = expensesByCategory.values().stream()
                                               .mapToDouble(Double::doubleValue).sum();

        expensesByCategory.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> {
                double percentage = totalExpenses > 0 ? (entry.getValue() / totalExpenses) * 100 : 0;
                System.out.printf("%-20s $%,8.2f (%5.1f%%)\n", 
                                entry.getKey(), entry.getValue(), percentage);
            });
    }

    private void generateBudgetComparison(List<Transaction> transactions, YearMonth month) {
        if (currentBudget == null || !currentBudget.getBudgetMonth().equals(month)) {
            return;
        }

        System.out.println("\n--- BUDGET vs ACTUAL SPENDING ---");

        Map<String, Double> actualSpending = transactions.stream()
            .filter(t -> t.getType().equals("EXPENSE"))
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));

        for (Map.Entry<String, Double> budget : currentBudget.getAllBudgets().entrySet()) {
            String category = budget.getKey();
            double budgeted = budget.getValue();
            double actual = actualSpending.getOrDefault(category, 0.0);
            double remaining = budgeted - actual;
            double percentage = budgeted > 0 ? (actual / budgeted) * 100 : 0;

            String status = remaining >= 0 ? "✓" : "⚠️";
            System.out.printf("%-20s %s Budget: $%,7.2f | Spent: $%,7.2f | Remaining: $%,7.2f (%5.1f%%)\n",
                            category, status, budgeted, actual, remaining, percentage);
        }
    }

    private void generateMonthlyTrends(List<Transaction> transactions, int year) {
        System.out.println("\n--- MONTHLY TRENDS ---");

        Map<YearMonth, Double> monthlyIncome = new HashMap<>();
        Map<YearMonth, Double> monthlyExpenses = new HashMap<>();

        for (Transaction t : transactions) {
            YearMonth month = YearMonth.from(t.getDate());
            if (t.getType().equals("INCOME")) {
                monthlyIncome.merge(month, t.getAmount(), Double::sum);
            } else {
                monthlyExpenses.merge(month, t.getAmount(), Double::sum);
            }
        }

        System.out.printf("%-10s %12s %12s %12s\n", "Month", "Income", "Expenses", "Savings");
        System.out.println("-".repeat(50));

        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);
            double income = monthlyIncome.getOrDefault(ym, 0.0);
            double expenses = monthlyExpenses.getOrDefault(ym, 0.0);
            double savings = income - expenses;

            if (income > 0 || expenses > 0) {
                System.out.printf("%-10s $%,10.2f $%,10.2f $%,10.2f\n",
                                ym.format(DateTimeFormatter.ofPattern("MMM yyyy")),
                                income, expenses, savings);
            }
        }
    }

    private List<Transaction> getTransactionsForMonth(YearMonth month) {
        return transactions.stream()
            .filter(t -> YearMonth.from(t.getDate()).equals(month))
            .collect(Collectors.toList());
    }

    private List<Transaction> getTransactionsForYear(int year) {
        return transactions.stream()
            .filter(t -> t.getDate().getYear() == year)
            .collect(Collectors.toList());
    }

    private double calculateTotalIncome(List<Transaction> transactions) {
        return transactions.stream()
            .filter(t -> t.getType().equals("INCOME"))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    private double calculateTotalExpenses(List<Transaction> transactions) {
        return transactions.stream()
            .filter(t -> t.getType().equals("EXPENSE"))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    public void generateQuickSummary() {
        YearMonth currentMonth = YearMonth.now();
        List<Transaction> monthlyTransactions = getTransactionsForMonth(currentMonth);

        double monthlyIncome = calculateTotalIncome(monthlyTransactions);
        double monthlyExpenses = calculateTotalExpenses(monthlyTransactions);
        double currentBalance = monthlyIncome - monthlyExpenses;

        System.out.println("\n=== CURRENT MONTH SUMMARY ===");
        System.out.printf("Income:   $%,8.2f\n", monthlyIncome);
        System.out.printf("Expenses: $%,8.2f\n", monthlyExpenses);
        System.out.printf("Balance:  $%,8.2f\n", currentBalance);

        if (currentBalance < 0) {
            System.out.println("💡 Tip: Consider reviewing your expenses to improve your financial situation.");
        }
    }
}