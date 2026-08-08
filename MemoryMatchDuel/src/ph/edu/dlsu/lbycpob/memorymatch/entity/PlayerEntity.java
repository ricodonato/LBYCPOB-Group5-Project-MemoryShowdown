package ph.edu.dlsu.lbycpob.memorymatch.entity;

import jakarta.persistence.*;

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