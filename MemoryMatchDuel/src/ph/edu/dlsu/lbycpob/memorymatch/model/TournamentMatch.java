package ph.edu.dlsu.lbycpob.memorymatch.model;

/**
 * PERSON A (Game Logic) OWNS THIS FILE.
 * One head-to-head pairing within a tournament round-robin.
 */
public class TournamentMatch {

    private final String playerA;
    private final String playerB;
    private String winner; // null until played

    public TournamentMatch(String playerA, String playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public String getPlayerA() {
        return playerA;
    }

    public String getPlayerB() {
        return playerB;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean isPlayed() {
        return winner != null;
    }
}
