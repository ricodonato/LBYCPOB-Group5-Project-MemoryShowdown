package ph.edu.dlsu.lbycpob.memorymatch.model;

import java.util.Objects;


public class Card {

    private final int id;
    private final String pairKey;   // cards with the same pairKey match
    private final String theme;     // e.g. "Animals", "Sports", "Food", "Emojis"
    private boolean faceUp;
    private boolean matched;

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

    public boolean matches(Card other) {
        return other != null && this.pairKey.equals(other.pairKey);
    }

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
