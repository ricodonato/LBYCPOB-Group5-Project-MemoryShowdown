package ph.edu.dlsu.lbycpob.memorymatch.model;


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
