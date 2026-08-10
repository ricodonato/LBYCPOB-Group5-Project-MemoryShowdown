package ph.edu.dlsu.lbycpob.memorymatch.model;

// UNDERSTAND: Represents one matchup in the tournament bracket — two player names, which round it's
// in, and the winner once it's played.
// DECISION: A null playerA/playerB was used to represent a "bye" instead of a separate boolean flag,
// because it naturally falls out of padding an odd bracket with empty slots — isBye() just checks for
// that null.
public class TournamentMatch {

    private final String playerA; // null = bye
    private final String playerB; // null = bye
    private final int round;
    private String winner;

    public TournamentMatch(String playerA, String playerB, int round) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.round = round;
    }

    public String getPlayerA() { return playerA; }
    public String getPlayerB() { return playerB; }
    public int getRound() { return round; }
    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
    // UNDERSTAND: isPlayed() treats "has a winner been set" as the signal that this match is done.
    public boolean isPlayed() { return winner != null; }
    public boolean isBye() { return playerA == null || playerB == null; }
}