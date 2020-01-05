package com.branwilliams.bundi.engine;

public class Card {

    private CardRank rank;

    private CardSuit suite;

    public Card(CardRank rank, CardSuit suite) {
        this.rank = rank;
        this.suite = suite;
    }

    public CardRank getRank() {
        return rank;
    }

    public void setRank(CardRank rank) {
        this.rank = rank;
    }

    public CardSuit getSuite() {
        return suite;
    }

    public void setSuite(CardSuit suite) {
        this.suite = suite;
    }

    @Override
    public String toString() {
        return rank + " of " + suite;
    }
}
