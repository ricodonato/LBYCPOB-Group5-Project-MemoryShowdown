package ph.edu.dlsu.lbycpob.memorymatch.model;

/**
 * PERSON A (Game Logic) OWNS THIS FILE.
 * Difficulty controls how many cards are on the board (must be even).
 */
public enum Difficulty {
    EASY(8),
    MEDIUM(14),
    HARD(20);

    private final int cardCount;

    Difficulty(int cardCount) {
        this.cardCount = cardCount;
    }

    public int getCardCount() {
        return cardCount;
    }
}
