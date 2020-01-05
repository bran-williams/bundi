package com.branwilliams.bundi.engine;

public class Card {

    private CardRank rank;

    private CardSuite suite;

    public Card(CardRank rank, CardSuite suite) {
        this.rank = rank;
        this.suite = suite;
    }

    public CardRank getRank() {
        return rank;
    }

    public void setRank(CardRank rank) {
        this.rank = rank;
    }

    public CardSuite getSuite() {
        return suite;
    }

    public void setSuite(CardSuite suite) {
        this.suite = suite;
    }

    @Override
    public String toString() {
        return rank + " of " + suite;
    }
}
