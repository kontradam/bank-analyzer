package com.bank.model;

public enum Category {
    JEDZENIE("Jedzenie"),
    TRANSPORT("Transport"),
    RACHUNKI("Rachunki"),
    ROZRYWKA("Rozrywka"),
    ZAKUPY("Zakupy"),
    ZDROWIE("Zdrowie"),
    WYNAGRODZENIE("Wynagrodzenie"),
    INNE_PRZYCHODY("Inne przychody"),
    PRZELEWY("Przelewy"),
    INTERNAL_TRANSFER("Transfer wewnetrzny"),
    INNE("Inne");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}