package com.bank;

import com.bank.model.Category;
import com.bank.model.Transaction;
import com.bank.service.CategorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategorizationServiceTest {

    private CategorizationService service;

    @BeforeEach
    void setUp() {
        service = new CategorizationService();
    }

    @Test
    void shouldCategorizeMcDonaldsAsFood() {
        Transaction transaction = new Transaction(
            LocalDate.now(), LocalDate.now(), "Card payment",
            -58.20, "PLN", "", "MCDONALDS 180 LUBLIN", 1000.0
        );

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        service.categorizeTransactions(transactions);

        assertEquals(Category.JEDZENIE, transaction.getCategory());
    }

    @Test
    void shouldCategorizeBoltAsTransport() {
        Transaction transaction = new Transaction(
            LocalDate.now(), LocalDate.now(), "Card payment",
            -15.29, "PLN", "", "BOLT.EU/O/2601030749 Warsaw", 1000.0
        );

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        service.categorizeTransactions(transactions);

        assertEquals(Category.TRANSPORT, transaction.getCategory());
    }

    @Test
    void shouldCategorizeCinemaAsEntertainment() {
        Transaction transaction = new Transaction(
            LocalDate.now(), LocalDate.now(), "Card payment",
            -93.80, "PLN", "", "CINEMA CITY LUBLIN PLAZA", 1000.0
        );

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        service.categorizeTransactions(transactions);

        assertEquals(Category.ROZRYWKA, transaction.getCategory());
    }

    @Test
    void shouldCategorizePGEAsBill() {
        Transaction transaction = new Transaction(
            LocalDate.now(), LocalDate.now(), "Outgoing transfer",
            -950.83, "PLN", "PGE", "Payment for electricity", 1000.0
        );

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        service.categorizeTransactions(transactions);

        assertEquals(Category.RACHUNKI, transaction.getCategory());
    }

    @Test
    void shouldCategorizePharmacyAsHealth() {
        Transaction transaction = new Transaction(
            LocalDate.now(), LocalDate.now(), "Card payment",
            -51.40, "PLN", "", "APTEKA ESKUL Lublin", 1000.0
        );

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        service.categorizeTransactions(transactions);

        assertEquals(Category.ZDROWIE, transaction.getCategory());
    }

    @Test
    void shouldCategorizeSalaryAsIncome() {
        Transaction transaction = new Transaction(
            LocalDate.now(), LocalDate.now(), "Incoming transfer",
            4603.22, "PLN", "EMPLOYER", "WYNAGRODZENIE ZA MC 01/2026", 5000.0
        );

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        service.categorizeTransactions(transactions);

        assertEquals(Category.WYNAGRODZENIE, transaction.getCategory());
    }

    @Test
    void shouldCategorizeUnknownAsOther() {
        Transaction transaction = new Transaction(
            LocalDate.now(), LocalDate.now(), "Other",
            -100.0, "PLN", "UNKNOWN", "Some random payment", 1000.0
        );

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        service.categorizeTransactions(transactions);

        assertEquals(Category.INNE, transaction.getCategory());
    }
}
