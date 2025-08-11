package src;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Category {
    // Predefined expense categories
    public static final List<String> EXPENSE_CATEGORIES = Arrays.asList(
        "Food & Dining",
        "Transportation", 
        "Shopping",
        "Entertainment",
        "Bills & Utilities",
        "Healthcare",
        "Education",
        "Travel",
        "Insurance",
        "Miscellaneous"
    );

    // Predefined income categories
    public static final List<String> INCOME_CATEGORIES = Arrays.asList(
        "Salary",
        "Freelance",
        "Business",
        "Investments",
        "Gifts",
        "Other Income"
    );

    private static List<String> customCategories = new ArrayList<>();

    public static List<String> getExpenseCategories() {
        List<String> allCategories = new ArrayList<>(EXPENSE_CATEGORIES);
        allCategories.addAll(customCategories);
        return allCategories;
    }

    public static List<String> getIncomeCategories() {
        return new ArrayList<>(INCOME_CATEGORIES);
    }

    public static void addCustomCategory(String category) {
        if (!customCategories.contains(category) && 
            !EXPENSE_CATEGORIES.contains(category) && 
            !INCOME_CATEGORIES.contains(category)) {
            customCategories.add(category);
        }
    }

    public static boolean isValidCategory(String category, String type) {
        if (type.equalsIgnoreCase("EXPENSE")) {
            return getExpenseCategories().contains(category);
        } else if (type.equalsIgnoreCase("INCOME")) {
            return getIncomeCategories().contains(category);
        }
        return false;
    }

    public static void displayCategories(String type) {
        System.out.println("\n=== Available Categories ===");
        List<String> categories = type.equalsIgnoreCase("EXPENSE") ? 
                                getExpenseCategories() : getIncomeCategories();

        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, categories.get(i));
        }
    }

    public static String getCategoryByIndex(String type, int index) {
        List<String> categories = type.equalsIgnoreCase("EXPENSE") ? 
                                getExpenseCategories() : getIncomeCategories();

        if (index >= 1 && index <= categories.size()) {
            return categories.get(index - 1);
        }
        return null;
    }
}