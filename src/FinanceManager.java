package src;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class FinanceManager {
    private List<Transaction> transactions;
    private List<SavingsGoal> savingsGoals;
    private Budget currentBudget;
    private DataManager dataManager;
    private Scanner scanner;
    private ReportGenerator reportGenerator;

    public FinanceManager() {
        this.transactions = new ArrayList<>();
        this.savingsGoals = new ArrayList<>();
        this.currentBudget = new Budget();
        this.dataManager = new DataManager();
        this.scanner = new Scanner(System.in);

        loadAllData();
        this.reportGenerator = new ReportGenerator(transactions, currentBudget);
    }

    public static void main(String[] args) {
        FinanceManager manager = new FinanceManager();
        manager.run();
    }

    public void run() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    PERSONAL FINANCE MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));

        if (dataManager.hasExistingData()) {
            System.out.println("Welcome back! Your data has been loaded successfully.");
        } else {
            System.out.println("Welcome! This appears to be your first time using the system.");
        }

        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice (1-8): ");

            switch (choice) {
                case 1:
                    addTransactionMenu();
                    break;
                case 2:
                    viewTransactionsMenu();
                    break;
                case 3:
                    budgetManagementMenu();
                    break;
                case 4:
                    reportsMenu();
                    break;
                case 5:
                    savingsGoalsMenu();
                    break;
                case 6:
                    settingsMenu();
                    break;
                case 7:
                    System.out.println("\nSaving all data...");
                    saveAllData();
                    System.out.println("Data saved successfully!");
                    break;
                case 8:
                    System.out.println("\nSaving data before exit...");
                    saveAllData();
                    System.out.println("\nThank you for using Personal Finance Manager!");
                    System.out.println("Remember to review your spending regularly for better financial health!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 8) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void displayMainMenu() {
        reportGenerator.generateQuickSummary();

        System.out.println("\n" + "=".repeat(40));
        System.out.println("              MAIN MENU");
        System.out.println("=".repeat(40));
        System.out.println("1. Add Transaction");
        System.out.println("2. View Transactions");
        System.out.println("3. Budget Management");
        System.out.println("4. Reports & Analytics");
        System.out.println("5. Savings Goals");
        System.out.println("6. Settings & Export");
        System.out.println("7. Save Data");
        System.out.println("8. Exit");
        System.out.println("=".repeat(40));
    }

    private void addTransactionMenu() {
        System.out.println("\n=== ADD TRANSACTION ===");
        System.out.println("1. Income");
        System.out.println("2. Expense");

        int choice = getIntInput("Choose type (1-2): ");
        String type = choice == 1 ? "INCOME" : "EXPENSE";

        double amount = getDoubleInput("Enter amount: $");
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        Category.displayCategories(type);
        int categoryChoice = getIntInput("Select category: ");
        String category = Category.getCategoryByIndex(type, categoryChoice);

        if (category == null) {
            System.out.println("Invalid category selection.");
            return;
        }

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Date (YYYY-MM-DD or press Enter for today): ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date = LocalDate.now();

        if (!dateInput.isEmpty()) {
            try {
                date = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Using today's date.");
            }
        }

        Transaction transaction = new Transaction(type, amount, category, description, date);
        transactions.add(transaction);

        System.out.println("Transaction added successfully!");
        System.out.println(transaction);

        // Check budget if it's an expense
        if (type.equals("EXPENSE") && currentBudget.hasBudget(category)) {
            double spent = getMonthlySpending(category, YearMonth.from(date));
            double budget = currentBudget.getBudget(category);
            double remaining = budget - spent;

            System.out.printf("\n%s budget: $%.2f remaining this month\n", 
                            category, remaining);

            if (currentBudget.isNearBudgetLimit(category, spent, 0.8)) {
                System.out.println("Warning: You've used 80% of your budget for " + category);
            }

            if (currentBudget.isOverBudget(category, spent)) {
                System.out.println("Alert: You've exceeded your budget for " + category);
            }
        }
    }

    private void viewTransactionsMenu() {
        if (transactions.isEmpty()) {
            System.out.println("\nNo transactions found. Add some transactions first!");
            return;
        }

        System.out.println("\n=== VIEW TRANSACTIONS ===");
        System.out.println("1. All Transactions");
        System.out.println("2. This Month");
        System.out.println("3. Income Only");
        System.out.println("4. Expenses Only");
        System.out.println("5. By Category");

        int choice = getIntInput("Choose view (1-5): ");
        List<Transaction> toDisplay = new ArrayList<>();

        switch (choice) {
            case 1:
                toDisplay = new ArrayList<>(transactions);
                break;
            case 2:
                YearMonth currentMonth = YearMonth.now();
                toDisplay = transactions.stream()
                    .filter(t -> YearMonth.from(t.getDate()).equals(currentMonth))
                    .collect(java.util.stream.Collectors.toList());
                break;
            case 3:
                toDisplay = transactions.stream()
                    .filter(t -> t.getType().equals("INCOME"))
                    .collect(java.util.stream.Collectors.toList());
                break;
            case 4:
                toDisplay = transactions.stream()
                    .filter(t -> t.getType().equals("EXPENSE"))
                    .collect(java.util.stream.Collectors.toList());
                break;
            case 5:
                System.out.print("Enter category name: ");
                String category = scanner.nextLine();
                toDisplay = transactions.stream()
                    .filter(t -> t.getCategory().equalsIgnoreCase(category))
                    .collect(java.util.stream.Collectors.toList());
                break;
        }

        if (toDisplay.isEmpty()) {
            System.out.println("\nNo transactions found for your selection.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-5s %-8s %-10s %-20s %-25s %-12s\n", 
                        "ID", "TYPE", "AMOUNT", "CATEGORY", "DESCRIPTION", "DATE");
        System.out.println("=".repeat(80));

        toDisplay.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        for (Transaction t : toDisplay) {
            System.out.printf("%-5d %-8s $%-9.2f %-20s %-25s %-12s\n",
                            t.getId(), t.getType(), t.getAmount(),
                            t.getCategory(), 
                            t.getDescription().length() > 24 ? 
                                t.getDescription().substring(0, 21) + "..." : t.getDescription(),
                            t.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        double total = toDisplay.stream().mapToDouble(
            t -> t.getType().equals("INCOME") ? t.getAmount() : -t.getAmount()
        ).sum();

        System.out.println("=".repeat(80));
        System.out.printf("Total: $%.2f (%d transactions)\n", Math.abs(total), toDisplay.size());
    }

    private void budgetManagementMenu() {
        System.out.println("\n=== BUDGET MANAGEMENT ===");
        System.out.println("1. Set Category Budget");
        System.out.println("2. View Current Budget");
        System.out.println("3. Budget vs Spending Report");

        int choice = getIntInput("Choose option (1-3): ");

        switch (choice) {
            case 1:
                setBudget();
                break;
            case 2:
                viewBudget();
                break;
            case 3:
                budgetReport();
                break;
        }
    }

    private void setBudget() {
        Category.displayCategories("EXPENSE");
        int categoryChoice = getIntInput("Select category: ");
        String category = Category.getCategoryByIndex("EXPENSE", categoryChoice);

        if (category == null) {
            System.out.println("Invalid category selection.");
            return;
        }

        double amount = getDoubleInput("Enter budget amount for " + category + ": $");
        if (amount < 0) {
            System.out.println("Budget amount cannot be negative.");
            return;
        }

        currentBudget.setBudget(category, amount);
        System.out.printf("Budget set: %s = $%.2f\n", category, amount);

        // Show current spending for this category
        YearMonth currentMonth = YearMonth.now();
        double spent = getMonthlySpending(category, currentMonth);
        double remaining = amount - spent;

        System.out.printf("Current spending this month: $%.2f\n", spent);
        System.out.printf("Remaining budget: $%.2f\n", remaining);
    }

    private void viewBudget() {
        if (currentBudget.getAllBudgets().isEmpty()) {
            System.out.println("\nNo budgets set. Set some budgets first!");
            return;
        }

        System.out.println("\n=== CURRENT BUDGET ===");
        System.out.println(currentBudget);

        YearMonth currentMonth = YearMonth.now();
        System.out.println("\n--- BUDGET STATUS ---");

        for (Map.Entry<String, Double> entry : currentBudget.getAllBudgets().entrySet()) {
            String category = entry.getKey();
            double budget = entry.getValue();
            double spent = getMonthlySpending(category, currentMonth);
            double percentage = budget > 0 ? (spent / budget) * 100 : 0;

            String status = spent <= budget ? "✅" : "🚨";
            System.out.printf("%s %-20s: $%.2f / $%.2f (%.1f%%)\n", 
                            status, category, spent, budget, percentage);
        }
    }

    private void budgetReport() {
        YearMonth currentMonth = YearMonth.now();
        List<Transaction> monthlyTransactions = transactions.stream()
            .filter(t -> YearMonth.from(t.getDate()).equals(currentMonth))
            .collect(java.util.stream.Collectors.toList());

        reportGenerator.generateMonthlyReport(currentMonth);
    }

    private void reportsMenu() {
        if (transactions.isEmpty()) {
            System.out.println("\n📭 No transactions found. Add some transactions first!");
            return;
        }

        System.out.println("\n=== REPORTS & ANALYTICS ===");
        System.out.println("1. Monthly Report");
        System.out.println("2. Yearly Report");
        System.out.println("3. Quick Summary");

        int choice = getIntInput("Choose report (1-3): ");

        switch (choice) {
            case 1:
                System.out.print("Enter month (YYYY-MM) or press Enter for current month: ");
                String monthInput = scanner.nextLine().trim();
                YearMonth month = YearMonth.now();

                if (!monthInput.isEmpty()) {
                    try {
                        month = YearMonth.parse(monthInput);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid format. Using current month.");
                    }
                }

                reportGenerator.generateMonthlyReport(month);
                break;

            case 2:
                int year = getIntInput("Enter year (or 0 for current year): ");
                if (year == 0) year = LocalDate.now().getYear();

                reportGenerator.generateYearlyReport(year);
                break;

            case 3:
                reportGenerator.generateQuickSummary();
                break;
        }
    }

    private void savingsGoalsMenu() {
        System.out.println("\n=== SAVINGS GOALS ===");
        System.out.println("1. Create New Goal");
        System.out.println("2. View All Goals");
        System.out.println("3. Update Goal Progress");
        System.out.println("4. Delete Goal");

        int choice = getIntInput("Choose option (1-4): ");

        switch (choice) {
            case 1:
                createSavingsGoal();
                break;
            case 2:
                viewSavingsGoals();
                break;
            case 3:
                updateGoalProgress();
                break;
            case 4:
                deleteGoal();
                break;
        }
    }

    private void createSavingsGoal() {
        System.out.print("Goal name: ");
        String name = scanner.nextLine();

        double targetAmount = getDoubleInput("Target amount: $");
        if (targetAmount <= 0) {
            System.out.println("Target amount must be positive.");
            return;
        }

        System.out.print("Target date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();

        try {
            LocalDate targetDate = LocalDate.parse(dateInput);
            if (targetDate.isBefore(LocalDate.now())) {
                System.out.println("Target date is in the past. Goal created anyway.");
            }

            System.out.print("Description (optional): ");
            String description = scanner.nextLine();

            SavingsGoal goal = new SavingsGoal(name, targetAmount, targetDate, description);
            savingsGoals.add(goal);

            System.out.println("Savings goal created!");
            System.out.println(goal);

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Goal not created.");
        }
    }

    private void viewSavingsGoals() {
        if (savingsGoals.isEmpty()) {
            System.out.println("\nNo savings goals found. Create some goals first!");
            return;
        }

        System.out.println("\n=== YOUR SAVINGS GOALS ===");
        for (SavingsGoal goal : savingsGoals) {
            System.out.println("\n" + "-".repeat(50));
            System.out.println(goal);

            if (goal.isGoalAchieved()) {
                System.out.println("Congratulations! Goal achieved!");
            } else if (goal.getDaysRemaining() < 0) {
                System.out.println("Goal deadline has passed.");
            }
        }
    }

    private void updateGoalProgress() {
        if (savingsGoals.isEmpty()) {
            System.out.println("\nNo savings goals found.");
            return;
        }

        viewSavingsGoals();
        int goalId = getIntInput("\nEnter goal ID to update: ");

        SavingsGoal goal = savingsGoals.stream()
            .filter(g -> g.getId() == goalId)
            .findFirst().orElse(null);

        if (goal == null) {
            System.out.println("Goal not found.");
            return;
        }

        System.out.printf("Current amount: $%.2f\n", goal.getCurrentAmount());
        double amount = getDoubleInput("Add to savings: $");

        if (amount > 0) {
            goal.addToSavings(amount);
            System.out.println("Goal updated!");
            System.out.println(goal);

            if (goal.isGoalAchieved()) {
                System.out.println("\nCONGRATULATIONS! ");
                System.out.println("You've achieved your savings goal: " + goal.getName());
            }
        } else {
            System.out.println("Amount must be positive.");
        }
    }

    private void deleteGoal() {
        if (savingsGoals.isEmpty()) {
            System.out.println("\nNo savings goals found.");
            return;
        }

        viewSavingsGoals();
        int goalId = getIntInput("\nEnter goal ID to delete: ");

        boolean removed = savingsGoals.removeIf(g -> g.getId() == goalId);
        if (removed) {
            System.out.println("Goal deleted successfully.");
        } else {
            System.out.println("Goal not found.");
        }
    }

    private void settingsMenu() {
        System.out.println("\n=== SETTINGS & EXPORT ===");
        System.out.println("1. Export Transactions to CSV");
        System.out.println("2. Create Data Backup");
        System.out.println("3. Add Custom Category");
        System.out.println("4. View Statistics");

        int choice = getIntInput("Choose option (1-4): ");

        switch (choice) {
            case 1:
                exportData();
                break;
            case 2:
                dataManager.createBackup();
                break;
            case 3:
                addCustomCategory();
                break;
            case 4:
                showStatistics();
                break;
        }
    }

    private void exportData() {
        if (transactions.isEmpty()) {
            System.out.println("\n📭 No transactions to export.");
            return;
        }

        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String filename = "transactions_export_" + timestamp + ".csv";

        dataManager.exportTransactionsToCSV(transactions, filename);
    }

    private void addCustomCategory() {
        System.out.print("Enter custom category name: ");
        String category = scanner.nextLine().trim();

        if (!category.isEmpty()) {
            Category.addCustomCategory(category);
            System.out.println("Custom category added: " + category);
        } else {
            System.out.println("Category name cannot be empty.");
        }
    }

    private void showStatistics() {
        if (transactions.isEmpty()) {
            System.out.println("\nNo data available for statistics.");
            return;
        }

        System.out.println("\n=== STATISTICS ===");
        System.out.printf("Total Transactions: %d\n", transactions.size());

        long incomeCount = transactions.stream().filter(t -> t.getType().equals("INCOME")).count();
        long expenseCount = transactions.stream().filter(t -> t.getType().equals("EXPENSE")).count();

        System.out.printf("Income Transactions: %d\n", incomeCount);
        System.out.printf("Expense Transactions: %d\n", expenseCount);
        System.out.printf("Savings Goals: %d\n", savingsGoals.size());
        System.out.printf("Budget Categories: %d\n", currentBudget.getAllBudgets().size());

        if (!transactions.isEmpty()) {
            LocalDate earliestDate = transactions.stream()
                .min(Comparator.comparing(Transaction::getDate))
                .get().getDate();

            LocalDate latestDate = transactions.stream()
                .max(Comparator.comparing(Transaction::getDate))
                .get().getDate();

            System.out.printf("Data Range: %s to %s\n", 
                            earliestDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            latestDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    // Utility methods
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    private double getMonthlySpending(String category, YearMonth month) {
        return transactions.stream()
            .filter(t -> t.getType().equals("EXPENSE"))
            .filter(t -> t.getCategory().equals(category))
            .filter(t -> YearMonth.from(t.getDate()).equals(month))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    private void loadAllData() {
        System.out.println("\nLoading data...");
        transactions = dataManager.loadTransactions();
        currentBudget = dataManager.loadBudget();
        savingsGoals = dataManager.loadSavingsGoals();
    }

    private void saveAllData() {
        dataManager.saveTransactions(transactions);
        dataManager.saveBudget(currentBudget);
        dataManager.saveSavingsGoals(savingsGoals);
    }
}