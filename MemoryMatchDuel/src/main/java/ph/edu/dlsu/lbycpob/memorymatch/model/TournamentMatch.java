package ph.edu.dlsu.lbycpob.memorymatch.model;

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
    public boolean isPlayed() { return winner != null; }
    public boolean isBye() { return playerA == null || playerB == null; }
}