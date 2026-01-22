package com.bank.service;

import com.bank.model.Category;
import com.bank.model.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

public class ReportService {

    public void generateReport(List<Transaction> transactions, String outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {

            writer.println("Total transactions: " + transactions.size());
            writer.println("Date range: " + getDateRange(transactions));
            writer.println();

            writeCategorySummary(writer, transactions);
            writeMonthlyBreakdown(writer, transactions);
            writeTopExpenses(writer, transactions);
        }
    }

    private String getDateRange(List<Transaction> transactions) {
        if (transactions.isEmpty()) return "N/A";

        LocalDate minDate = transactions.stream()
            .map(Transaction::getOperationDate)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

        LocalDate maxDate = transactions.stream()
            .map(Transaction::getOperationDate)
            .max(LocalDate::compareTo)
            .orElse(LocalDate.now());

        return minDate + " to " + maxDate;
    }

    private void writeCategorySummary(PrintWriter writer, List<Transaction> transactions) {

        Map<Category, Double> categoryTotals = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getAmount() < 0 && t.getCategory() != Category.INTERNAL_TRANSFER) {
            categoryTotals.merge(t.getCategory(), Math.abs(t.getAmount()), Double::sum);
    }
        
    }

        categoryTotals.entrySet().stream()
            .sorted(Map.Entry.<Category, Double>comparingByValue().reversed())
            .forEach(entry -> {
                String line = String.format("%-20s: %10.2f PLN", 
                    entry.getKey().getDisplayName(), entry.getValue());
                writer.println(line);
            });

        double totalExpenses = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        writer.println("----------------------------------------");
        writer.println("TOTAL EXPENSES: " + String.format("%.2f", totalExpenses) + " PLN");
        writer.println();
    }

    private void writeMonthlyBreakdown(PrintWriter writer, List<Transaction> transactions) {

        Map<String, Double> monthlyExpenses = new TreeMap<>();
        Map<String, Double> monthlyIncome = new TreeMap<>();

        for (Transaction t : transactions) {
            String month = t.getOperationDate().getYear() + "-" +
                          String.format("%02d", t.getOperationDate().getMonthValue());

            if (t.getAmount() < 0) {
                monthlyExpenses.merge(month, Math.abs(t.getAmount()), Double::sum);
            } else {
                monthlyIncome.merge(month, t.getAmount(), Double::sum);
            }
        }

        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(monthlyExpenses.keySet());
        allMonths.addAll(monthlyIncome.keySet());

        for (String month : allMonths) {
            double income = monthlyIncome.getOrDefault(month, 0.0);
            double expenses = monthlyExpenses.getOrDefault(month, 0.0);
            double balance = income - expenses;

            String line = String.format("%s | Income: %8.2f | Expenses: %8.2f | Balance: %8.2f PLN",
                month, income, expenses, balance);
            writer.println(line);
        }
        writer.println();
    }

    private void writeTopExpenses(PrintWriter writer, List<Transaction> transactions) {

        transactions.stream()
            .filter(t -> t.getAmount() < 0)
            .sorted(Comparator.comparingDouble(Transaction::getAmount))
            .limit(10)
            .forEach(t -> {
                String title = t.getTitle().length() > 50 ? t.getTitle().substring(0, 50) + "..." : t.getTitle();
                String line = String.format("%s | %.2f PLN | %s | %s",
                    t.getOperationDate(),
                    Math.abs(t.getAmount()),
                    t.getCategory().getDisplayName(),
                    title);
                writer.println(line);
            });

        writer.println();
    }

    public void printSummaryToConsole(List<Transaction> transactions) {

        double totalIncome = transactions.stream()
            .filter(t -> t.getAmount() > 0 && t.getCategory() != Category.INTERNAL_TRANSFER)
            .mapToDouble(Transaction::getAmount)
            .sum();

        double totalExpenses = transactions.stream()
            .filter(t -> t.getAmount() < 0 && t.getCategory() != Category.INTERNAL_TRANSFER)
            .mapToDouble(t -> Math.abs(t.getAmount()))
            .sum();
            
        System.out.println("Total income: " + String.format("%.2f", totalIncome) + " PLN");
        System.out.println("Total expenses: " + String.format("%.2f", totalExpenses) + " PLN");
        System.out.println("Net balance: " + String.format("%.2f", totalIncome - totalExpenses) + " PLN");
    }
}