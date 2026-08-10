package ph.edu.dlsu.lbycpob.memorymatch.model;

// UNDERSTAND: Enum of the three difficulty levels, each carrying how many cards that difficulty uses.
// DECISION: An enum was used instead of plain int constants (e.g. static final int EASY = 8) because it
// bundles the card count with a named, type-safe value — you can't accidentally pass a random int where
// a Difficulty is expected.
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