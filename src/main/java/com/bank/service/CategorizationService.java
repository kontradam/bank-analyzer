package com.bank.service;

import com.bank.model.Category;
import com.bank.model.Transaction;

import java.util.List;

public class CategorizationService {

    public void categorizeTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            Category category = detectCategory(transaction);
            transaction.setCategory(category);
        }
    }

    private Category detectCategory(Transaction transaction) {
        String title = transaction.getTitle().toUpperCase();
        String counterparty = transaction.getCounterparty().toUpperCase();
        String operationType = transaction.getOperationType();
        double amount = transaction.getAmount();

        if (amount > 0 && title.contains("WYNAGRODZENIE")) {
            return Category.WYNAGRODZENIE;
        }

        if (amount > 0 && (title.contains("GODZINY") || title.contains("DODATKI"))) {
            return Category.WYNAGRODZENIE;
        }

        if (isFood(title)) return Category.JEDZENIE;
        if (isTransport(title)) return Category.TRANSPORT;
        if (isEntertainment(title)) return Category.ROZRYWKA;
        if (isBill(counterparty, title)) return Category.RACHUNKI;
        if (isHealth(title)) return Category.ZDROWIE;
        if (isShopping(title)) return Category.ZAKUPY;
        if (operationType.contains("Przelewy")) {
            if (isInternalTransfer(transaction)) {
                return Category.INTERNAL_TRANSFER;
            }
            return Category.PRZELEWY;
        }

        return Category.INNE;
    }

    private boolean isFood(String title) {
        String[] keywords = {
            "MCDONALDS", "KFC", "BURGER", "PIZZA", "KEBAB", "KABAB",
            "RESTAUR", "BAR", "CAFE", "PIJALNIA", "THAI", "ASIAN",
            "PYSZNE.PL", "ZABKA", "STOKROTKA", "BIEDRONKA"
        };
        return containsAny(title, keywords);
    }

    private boolean isTransport(String title) {
        String[] keywords = {
            "BOLT", "UBER", "MPK", "PKP", "ORLEN", "PARKING",
            "JAKDOJADE", "METROPOLIA GZM"
        };
        return containsAny(title, keywords);
    }

    private boolean isEntertainment(String title) {
        String[] keywords = {
            "CINEMA", "KINO", "THEATER", "TEATR", "NETFLIX", "SPOTIFY"
        };
        return containsAny(title, keywords);
    }

    private boolean isBill(String counterparty, String title) {
        String[] keywords = {
            "PGE", "PGNIG", "MPWIK", "NETIA", "ORANGE", "PLAY",
            "T-MOBILE", "PLUS", "FUNDUSZ REMONTOWY", "CZYNSZ"
        };
        return containsAny(counterparty, keywords) || containsAny(title, keywords);
    }

    private boolean isHealth(String title) {
        String[] keywords = {
            "APTEKA", "PHARMACY", "LEKARZ", "PRZYCHODNIA", "SZPITAL"
        };
        return containsAny(title, keywords);
    }

    private boolean isShopping(String title) {
        String[] keywords = {
            "ALLEGRO", "AMAZON", "MEDIA MARKT", "DECATHLON",
            "H&M", "ZARA", "RESERVED"
        };
        return containsAny(title, keywords);
    }

    private boolean isInternalTransfer(Transaction transaction) {
        String title = transaction.getTitle().toUpperCase();
        String counterparty = transaction.getCounterparty().toUpperCase();

        String[] keywords = {"WLASNE", "OSZCZEDNOSCIOWE", "PRZELEW WEWNETRZNY"};

        for (String keyword : keywords) {
            if (counterparty.contains(keyword) || title.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(String text, String[] keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
}
