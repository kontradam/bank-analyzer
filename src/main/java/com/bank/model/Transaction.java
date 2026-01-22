package com.bank.model;

import java.time.LocalDate;

public class Transaction {
    private LocalDate bookingDate;
    private LocalDate operationDate;
    private String operationType;
    private double amount;
    private String currency;
    private String counterparty;
    private String title;
    private double balanceAfter;
    private Category category;

    public Transaction(LocalDate bookingDate, LocalDate operationDate, String operationType,
                      double amount, String currency, String counterparty, String title, double balanceAfter) {
        this.bookingDate = bookingDate;
        this.operationDate = operationDate;
        this.operationType = operationType;
        this.amount = amount;
        this.currency = currency;
        this.counterparty = counterparty;
        this.title = title;
        this.balanceAfter = balanceAfter;
    }

    public LocalDate getBookingDate() { return bookingDate; }
    public LocalDate getOperationDate() { return operationDate; }
    public String getOperationType() { return operationType; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCounterparty() { return counterparty; }
    public String getTitle() { return title; }
    public double getBalanceAfter() { return balanceAfter; }
    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

    @Override
    public String toString() {
        return String.format("%s | %.2f %s | %s | %s",
            operationDate, amount, currency, category, title);
    }
}