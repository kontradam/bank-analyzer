package com.bank;

import com.bank.model.Transaction;
import com.bank.service.CategorizationService;
import com.bank.service.CsvParserService;
import com.bank.service.ReportService;

import java.util.ArrayList;
import java.util.List;

public class BankAnalyzerApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar bank-analyzer.jar <csv_file1> [csv_file2] ...");
            System.out.println("Example: java -jar bank-analyzer.jar transactions1.csv transactions2.csv");
            return;
        }

        try {
            CsvParserService csvParser = new CsvParserService();
            CategorizationService categorizer = new CategorizationService();
            ReportService reportService = new ReportService();

            List<String> filePaths = new ArrayList<>();
            for (String arg : args) {
                filePaths.add(arg);
            }

            System.out.println("Files to process: " + filePaths.size());
            System.out.println();

            List<Transaction> transactions = csvParser.parseMultipleFiles(filePaths);
            System.out.println("Loaded transactions: " + transactions.size());

            categorizer.categorizeTransactions(transactions);
            System.out.println("Categorization complete.");

            reportService.printSummaryToConsole(transactions);

            String reportPath = "bank_analysis_report.txt";
            reportService.generateReport(transactions, reportPath);
            System.out.println("Full report saved to: " + reportPath);

            System.out.println("\nAnalysis complete");

        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
        }
    }
}