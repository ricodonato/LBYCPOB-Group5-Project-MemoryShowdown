package ph.edu.dlsu.lbycpob.memorymatch.entity;

import jakarta.persistence.*;

// UNDERSTAND: A JPA entity that maps a "players" database table — stores a player's lifetime stats
// (wins, matches played, highest score) so they persist between app runs, unlike the in-memory Player
// class used during a live game.
// DECISION: This was kept as a separate class from model.Player instead of reusing it, because Player
// tracks live, per-round game state while PlayerEntity tracks permanent, saved stats — mixing the two
// would tie the database schema to in-game logic that changes often.
@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private int totalWins;
    private int totalMatches;
    private int highestScore;

    // UNDERSTAND: A no-arg protected constructor is required by JPA so it can build objects via
    // reflection when loading rows from the database.
    protected PlayerEntity() {
        // required by JPA — don't call this directly
    }

    public PlayerEntity(String username) {
        this.username = username;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }

    public int getTotalWins() { return totalWins; }
    public void setTotalWins(int totalWins) { this.totalWins = totalWins; }

    public int getTotalMatches() { return totalMatches; }
    public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }

    public int getHighestScore() { return highestScore; }
    public void setHighestScore(int highestScore) { this.highestScore = highestScore; }
}