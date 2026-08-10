package ph.edu.dlsu.lbycpob.memorymatch.model;

import java.util.Objects;

// UNDERSTAND: Represents one card on the board — its id, the symbol it needs to match on (pairKey),
// its theme, and whether it's currently face up or already matched.
// DECISION: A String pairKey was used instead of comparing two Card objects directly because it lets
// any two cards sharing the same symbol count as a match, without needing a reference to each other.
public class Card {

    private final int id;
    private final String pairKey;   // cards with the same pairKey match
    private final String theme;     // e.g. "Animals", "Sports", "Food", "Emojis"
    private boolean faceUp;
    private boolean matched;

    // UNDERSTAND: The constructor always starts a card face-down and unmatched, since every card begins
    // hidden when a new board is generated.
    public Card(int id, String pairKey, String theme) {
        this.id = id;
        this.pairKey = pairKey;
        this.theme = theme;
        this.faceUp = false;
        this.matched = false;
    }

    public int getId() {
        return id;
    }

    public String getPairKey() {
        return pairKey;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    // UNDERSTAND: matches() checks if this card's pairKey equals another card's pairKey.
    // DECISION: A null check was added before comparing pairKeys because calling .equals() on a null
    // "other" card would throw a NullPointerException instead of just returning false.
    public boolean matches(Card other) {
        return other != null && this.pairKey.equals(other.pairKey);
    }

    // UNDERSTAND: equals() and hashCode() were overridden to compare cards by id only.
    // DECISION: id was used instead of the default object reference because Board needs to find a specific
    // card by id (e.g. in getCardById) and treat two Card objects with the same id as the same card.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}