package com.branwilliams.bundi.engine;

public enum CardSuite {
    SPADES("Spades"),
    DIAMONDS("Diamonds"),
    HEARTS("Hearts"),
    CLUBS("Clubs");

    public final String displayName;

    CardSuite(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

}
