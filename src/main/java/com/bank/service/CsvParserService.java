package com.bank.service;

import com.bank.model.Transaction;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvParserService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public List<Transaction> parseFile(String filePath) throws IOException, CsvException {
        List<Transaction> transactions = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();

            for (int i = 7; i < rows.size(); i++) {
                String[] row = rows.get(i);

                if (row.length < 9) continue;

                try {
                    LocalDate bookingDate = LocalDate.parse(row[0], DATE_FORMATTER);
                    LocalDate operationDate = LocalDate.parse(row[1], DATE_FORMATTER);
                    String operationType = row[2];
                    double amount = Double.parseDouble(row[3].replace(",", "."));
                    String currency = row[4];
                    String counterparty = row[5];
                    String title = row[7];
                    double balanceAfter = Double.parseDouble(row[8].replace(",", "."));

                    Transaction transaction = new Transaction(
                        bookingDate, operationDate, operationType,
                        amount, currency, counterparty, title, balanceAfter
                    );

                    transactions.add(transaction);
                } catch (Exception e) {
                    System.err.println("Error parsing row " + i + ": " + e.getMessage());
                }
            }
        }

        return transactions;
    }

    public List<Transaction> parseMultipleFiles(List<String> filePaths) throws IOException, CsvException {
        List<Transaction> allTransactions = new ArrayList<>();

        for (String filePath : filePaths) {
            System.out.println("Loading file: " + filePath);
            allTransactions.addAll(parseFile(filePath));
        }

        return allTransactions;
    }
}
