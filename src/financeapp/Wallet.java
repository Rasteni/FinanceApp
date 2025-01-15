package financeapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Wallet implements Serializable {
    private Map<String, Double> incomeByCategory;
    private Map<String, Double> expenseByCategory;
    private Map<String, Double> budgetByCategory;

    public Wallet() {
        this.incomeByCategory = new HashMap<>();
        this.expenseByCategory = new HashMap<>();
        this.budgetByCategory = new HashMap<>();
    }

    public void addIncome(String category, double amount) {
        incomeByCategory.put(category, incomeByCategory.getOrDefault(category, 0.0) + amount);
    }

    public void addExpense(String category, double amount) {
        expenseByCategory.put(category, expenseByCategory.getOrDefault(category, 0.0) + amount);
    }

    public void setBudget(String category, double amount) {
        budgetByCategory.put(category, amount);
    }

    public double getTotalIncome() {
        return incomeByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalExpense() {
        return expenseByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    public Map<String, Double> getIncomeByCategory() {
        return incomeByCategory;
    }

    public Map<String, Double> getExpenseByCategory() {
        return expenseByCategory;
    }

    public Map<String, Double> getBudgetByCategory() {
        return budgetByCategory;
    }
}