package ph.edu.dlsu.lbycpob.memorymatch.model;

/**
 * PERSON A (Game Logic) OWNS THIS FILE.
 * In-memory representation of a player during an active match
 * (not the same as the database entity Person C will build).
 */
public class Player {

    private final String name;
    private int score;
    private int currentCombo;
    private int roundsWon;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.currentCombo = 0;
        this.roundsWon = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentCombo() {
        return currentCombo;
    }

    public int getRoundsWon() {
        return roundsWon;
    }

    /** Call when this player successfully matches a pair. */
    public void registerMatch(int basePoints) {
        currentCombo++;
        int comboBonus = (currentCombo - 1) * 5; // +5 per consecutive match beyond the first
        score += basePoints + comboBonus;
    }

    /** Call when this player flips a non-matching pair; combo resets. */
    public void registerMiss() {
        currentCombo = 0;
    }

    public void registerRoundWin() {
        roundsWon++;
    }
}
