package ph.edu.dlsu.lbycpob.memorymatch.entity;

import jakarta.persistence.*;

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

    protected MatchResultEntity() {
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