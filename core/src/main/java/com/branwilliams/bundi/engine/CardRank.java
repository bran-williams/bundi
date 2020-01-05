package com.branwilliams.bundi.engine;

/**
 * This represents the rank of a standard 3 card deck.
 *
 * */
public enum CardRank {
    LOW_ACE("Ace"),
    TWO("Two"),
    THREE("Three"),
    FOUR("Four"),
    FIVE("Five"),
    SIX("Six"),
    SEVEN("Seven"),
    EIGHT("Eight"),
    NINE("Nine"),
    TEN("Ten"),
    JACK("Jack"),
    QUEEN("Queen"),
    KING("King"),
    HIGH_ACE("Ace");
//    Ace, King, Queen, Jack, 10, 9, 8, 7, 6, 5, 4, 3, 2, Ace;

    public final String displayName;

    CardRank(String displayName) {
        this.displayName = displayName;
    }

    public boolean hasHigherRank(CardRank rank) {
        return this.ordinal() > rank.ordinal();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
