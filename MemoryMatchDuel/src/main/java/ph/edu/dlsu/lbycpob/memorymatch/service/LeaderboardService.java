package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;

import java.util.List;

// UNDERSTAND: Defines the contract for saving and retrieving persistent player stats and match history.
// DECISION: This was made its own interface (like GameEngine) instead of letting the UI call the JPA
// repositories directly, so the UI team could build the leaderboard screen against
// LeaderboardServiceStub while the backend teammate finished the real database-backed version.
public interface LeaderboardService {

    /** Returns the top players ranked by highest score, descending. */
    List<PlayerEntity> getTopPlayers(int limit);

    /** Records the result of a finished match and updates both players' stats. */
    void recordMatchResult(String winnerName, int winnerScore,
                           String loserName, int loserScore,
                           String difficulty, String theme);

    /** Finds or creates a player by username (called when someone types their name in). */
    PlayerEntity findOrCreatePlayer(String username);
}