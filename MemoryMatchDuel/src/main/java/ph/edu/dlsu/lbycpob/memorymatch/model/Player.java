package ph.edu.dlsu.lbycpob.memorymatch.model;

// UNDERSTAND: Tracks one player's live game state — their name, score, current combo streak, how many
// rounds they've won, and how many misses they've made.
public class Player {

    private final String name;
    private int score;
    private int currentCombo;
    private int roundsWon;
    private int missCount;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.currentCombo = 0;
        this.roundsWon = 0;
        this.missCount = 0;
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

    public int getMissCount() {
        return missCount;
    }

    /** Call when this player successfully matches a pair. */
    // UNDERSTAND: registerMatch() adds points when a player finds a pair, plus a bonus for consecutive
    // matches in a row (the "combo").
    // DECISION: The combo bonus was calculated as (currentCombo - 1) * 5 instead of a flat bonus per match,
    // so the reward only kicks in once a player is on a streak, not on their very first match.
    public void registerMatch(int basePoints) {
        currentCombo++;
        int comboBonus = (currentCombo - 1) * 5; // +5 per consecutive match beyond the first
        score += basePoints + comboBonus;
    }

    /** Call when this player flips a non-matching pair; combo resets. */
    // UNDERSTAND: registerMiss() resets the combo to 0 and increases missCount, since a wrong flip breaks
    // the player's streak.
    public void registerMiss() {
        currentCombo = 0;
        missCount++;
    }

    public void registerRoundWin() {
        roundsWon++;
    }

    /**
     * Clears this player's per-round score and combo so a new round can start.
     * roundsWon is intentionally NOT reset — it persists for the whole match.
     */
    // UNDERSTAND: resetRoundStats() clears score, combo, and miss count when a new round starts, but
    // roundsWon is deliberately left untouched.
    // DECISION: roundsWon was excluded from the reset because it needs to persist across an entire
    // best-of-series match, while score/combo/misses only matter within a single round.
    public void resetRoundStats() {
        this.score = 0;
        this.currentCombo = 0;
        this.missCount = 0;
    }
}