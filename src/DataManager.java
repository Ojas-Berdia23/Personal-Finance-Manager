package src;

import java.io.*;
import java.time.YearMonth;
import java.util.*;

public class DataManager {
    private static final String DATA_DIR = "data/";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "transactions.csv";
    private static final String BUDGETS_FILE = DATA_DIR + "budgets.csv";
    private static final String GOALS_FILE = DATA_DIR + "goals.csv";

    public DataManager() {
        createDataDirectory();
    }

    private void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Transaction data management
    public void saveTransactions(List<Transaction> transactions) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            writer.println("ID,Type,Amount,Category,Description,Date");
            for (Transaction transaction : transactions) {
                writer.println(transaction.toCSVString());
            }
            System.out.println("Transactions saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);

        if (!file.exists()) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }

                Transaction transaction = Transaction.fromCSVString(line);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }

            if (!transactions.isEmpty()) {
                System.out.println("Loaded " + transactions.size() + " transactions.");
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }

        return transactions;
    }

    // Budget data management
    public void saveBudget(Budget budget) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BUDGETS_FILE))) {
            writer.println("Month,Category,Amount");
            writer.print(budget.toCSVString());
            System.out.println("Budget saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving budget: " + e.getMessage());
        }
    }

    public Budget loadBudget() {
        Budget budget = new Budget();
        File file = new File(BUDGETS_FILE);

        if (!file.exists()) {
            return budget;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    YearMonth month = YearMonth.parse(parts[0]);
                    String category = parts[1];
                    double amount = Double.parseDouble(parts[2]);

                    budget.setBudgetMonth(month);
                    budget.setBudget(category, amount);
                }
            }

            if (!budget.getAllBudgets().isEmpty()) {
                System.out.println("Budget loaded successfully.");
            }
        } catch (IOException e) {
            System.err.println("❌ Error loading budget: " + e.getMessage());
        }

        return budget;
    }

    // Savings goals data management
    public void saveSavingsGoals(List<SavingsGoal> goals) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GOALS_FILE))) {
            writer.println("ID,Name,TargetAmount,CurrentAmount,StartDate,TargetDate,Description");
            for (SavingsGoal goal : goals) {
                writer.println(goal.toCSVString());
            }
            System.out.println("Savings goals saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving savings goals: " + e.getMessage());
        }
    }

    public List<SavingsGoal> loadSavingsGoals() {
        List<SavingsGoal> goals = new ArrayList<>();
        File file = new File(GOALS_FILE);

        if (!file.exists()) {
            return goals;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }

                SavingsGoal goal = SavingsGoal.fromCSVString(line);
                if (goal != null) {
                    goals.add(goal);
                }
            }

            if (!goals.isEmpty()) {
                System.out.println("Loaded " + goals.size() + " savings goals.");
            }
        } catch (IOException e) {
            System.err.println("Error loading savings goals: " + e.getMessage());
        }

        return goals;
    }

    // Export functionality
    public void exportTransactionsToCSV(List<Transaction> transactions, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            writer.println("ID,Type,Amount,Category,Description,Date");
            for (Transaction transaction : transactions) {
                writer.println(transaction.toCSVString());
            }
            System.out.println("Transactions exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting transactions: " + e.getMessage());
        }
    }

    // Backup functionality
    public void createBackup() {
        String timestamp = java.time.LocalDateTime.now()
                          .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupDir = DATA_DIR + "backup_" + timestamp + "/";

        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            copyFile(TRANSACTIONS_FILE, backupDir + "transactions.csv");
            copyFile(BUDGETS_FILE, backupDir + "budgets.csv");
            copyFile(GOALS_FILE, backupDir + "goals.csv");
            System.out.println("Backup created: " + backupDir);
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }

    private void copyFile(String source, String destination) throws IOException {
        File sourceFile = new File(source);
        if (!sourceFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
             PrintWriter writer = new PrintWriter(new FileWriter(destination))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        }
    }

    public boolean hasExistingData() {
        File transFile = new File(TRANSACTIONS_FILE);
        File budgetFile = new File(BUDGETS_FILE);
        File goalsFile = new File(GOALS_FILE);

        return (transFile.exists() && transFile.length() > 0) ||
               (budgetFile.exists() && budgetFile.length() > 0) ||
               (goalsFile.exists() && goalsFile.length() > 0);
    }
}