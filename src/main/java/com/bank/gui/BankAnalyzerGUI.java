package com.bank.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.bank.model.Transaction;
import com.bank.service.CsvParserService;
import com.bank.service.CategorizationService;
import java.io.File;
import java.util.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class BankAnalyzerGUI extends Application {

    private static final String CURRENCY = "PLN";
    private static final String NO_CATEGORY = "N/A";
    private static final String INCOME_LABEL = "Income";
    private static final String EXPENSES_LABEL = "Expenses";
    
    private TableView<Transaction> transactionTable;
    private Label summaryLabel;
    private Label statsLabel;
    private BarChart<String, Number> expenseChart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bank CSV Analyzer");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Button loadButton = new Button("Load CSV Files");
        loadButton.setOnAction(e -> loadFiles(primaryStage));

        statsLabel = new Label("Income: 0.00 " + CURRENCY + " | Expenses: 0.00 " + CURRENCY);
        statsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.getChildren().addAll(loadButton, statsLabel);

        summaryLabel = new Label("No files loaded");
        transactionTable = createTransactionTable();

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(transactionTable, createExpenseChart());
        splitPane.setDividerPositions(0.6);

        root.setTop(topBar);
        root.setCenter(splitPane);
        root.setBottom(summaryLabel);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFiles(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV Files");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null && !files.isEmpty()) {
            processFiles(files);
        }
    }

    private void processFiles(List<File> files) {
        try {
            List<Transaction> allTransactions = loadTransactions(files);
            categorizeTransactions(allTransactions);
            updateView(allTransactions, files.size());
        } catch (Exception e) {
            summaryLabel.setText("Error: " + e.getMessage());
        }
    }

    private List<Transaction> loadTransactions(List<File> files) throws Exception {
        CsvParserService parser = new CsvParserService();
        List<Transaction> allTransactions = new ArrayList<>();
        
        for (File file : files) {
            List<Transaction> transactions = parser.parseFile(file.getAbsolutePath());
            allTransactions.addAll(transactions);
        }
        
        return allTransactions;
    }

    private void categorizeTransactions(List<Transaction> transactions) {
        CategorizationService categorizer = new CategorizationService();
        categorizer.categorizeTransactions(transactions);
    }

    private void updateView(List<Transaction> transactions, int fileCount) {
        transactionTable.getItems().clear();
        transactionTable.getItems().addAll(transactions);
        
        summaryLabel.setText(String.format(
            "Loaded %d transactions from %d file(s)", 
            transactions.size(), 
            fileCount
        ));

        updateStatistics(transactions);
        updateChart(transactions);
    }

    private void updateStatistics(List<Transaction> transactions) {
        double totalIncome = 0;
        double totalExpenses = 0;
        
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                totalIncome += t.getAmount();
            } else {
                totalExpenses += Math.abs(t.getAmount());
            }
        }
        
        statsLabel.setText(String.format(
            "Income: %.2f %s | Expenses: %.2f %s",
            totalIncome, CURRENCY, totalExpenses, CURRENCY
        ));
    }

    private void updateChart(List<Transaction> transactions) {
        Map<String, Double> categoryData = aggregateByCategory(transactions);
        
        expenseChart.getData().clear();
        
        XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
        categorySeries.setName("Categories");
        
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            categorySeries.getData().add(
                new XYChart.Data<>(entry.getKey(), entry.getValue())
            );
        }
        
        double totalIncome = calculateTotalIncome(transactions);
        double totalExpenses = calculateTotalExpenses(transactions);
        
        XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
        totalSeries.setName("Totals");
        totalSeries.getData().add(new XYChart.Data<>(INCOME_LABEL, totalIncome));
        totalSeries.getData().add(new XYChart.Data<>(EXPENSES_LABEL, totalExpenses));
        
        expenseChart.getData().addAll(categorySeries, totalSeries);
    }

    private Map<String, Double> aggregateByCategory(List<Transaction> transactions) {
        Map<String, Double> categoryData = new HashMap<>();
        
        for (Transaction t : transactions) {
            if (t.getCategory() != null) {
                String category = t.getCategory().getDisplayName();
                double amount = Math.abs(t.getAmount());
                categoryData.put(category, 
                    categoryData.getOrDefault(category, 0.0) + amount
                );
            }
        }
        
        return categoryData;
    }

    private double calculateTotalIncome(List<Transaction> transactions) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                total += t.getAmount();
            }
        }
        return total;
    }

    private double calculateTotalExpenses(List<Transaction> transactions) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) {
                total += Math.abs(t.getAmount());
            }
        }
        return total;
    }

    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>();
        
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getOperationDate().toString()
            )
        );
        
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                formatAmount(data.getValue())
            )
        );
        
        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCategory() != null ? 
                data.getValue().getCategory().getDisplayName() : NO_CATEGORY
            )
        );
        
        TableColumn<Transaction, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTitle()
            )
        );
        
        table.getColumns().addAll(dateCol, amountCol, categoryCol, titleCol);
        return table;
    }

    private String formatAmount(Transaction transaction) {
        return String.format("%.2f %s", 
            transaction.getAmount(), 
            transaction.getCurrency()
        );
    }

    private BarChart<String, Number> createExpenseChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (" + CURRENCY + ")");
        
        expenseChart = new BarChart<>(xAxis, yAxis);
        expenseChart.setTitle("Income & Expenses by Category");
        expenseChart.setLegendVisible(true);
        
        return expenseChart;
    }
}