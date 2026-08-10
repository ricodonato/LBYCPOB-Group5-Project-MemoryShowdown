package ph.edu.dlsu.lbycpob.memorymatch.entity;

import jakarta.persistence.*;

// UNDERSTAND: A JPA entity that records the outcome of one finished match — who won, who lost, both
// scores, and the difficulty/theme played — as a row in the "match_results" table.
// DECISION: winner and loser were stored as @ManyToOne references to PlayerEntity instead of plain
// String names, so the database keeps a real relationship between a match result and the actual player
// row, instead of a loose text copy that could drift out of sync (e.g. after a username change).
@Entity
@Table(name = "match_results")
public class MatchResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "winner_id", nullable = false)
    private PlayerEntity winner;

    @ManyToOne
    @JoinColumn(name = "loser_id", nullable = false)
    private PlayerEntity loser;

    private int winnerScore;
    private int loserScore;
    private String difficulty;
    private String theme;

    public MatchResultEntity() {
        // required by JPA — don't call this directly
    }

    public Long getId() { return id; }

    public PlayerEntity getWinner() { return winner; }
    public void setWinner(PlayerEntity winner) { this.winner = winner; }

    public PlayerEntity getLoser() { return loser; }
    public void setLoser(PlayerEntity loser) { this.loser = loser; }

    public int getWinnerScore() { return winnerScore; }
    public void setWinnerScore(int winnerScore) { this.winnerScore = winnerScore; }

    public int getLoserScore() { return loserScore; }
    public void setLoserScore(int loserScore) { this.loserScore = loserScore; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}