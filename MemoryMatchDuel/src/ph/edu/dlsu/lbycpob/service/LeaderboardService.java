package ph.edu.dlsu.lbycpob.service;

import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;

import java.util.List;

/**
 * SHARED CONTRACT between PERSON B (UI) and PERSON C (Backend/DB).
 * PERSON C implements this against the real database.
 * PERSON B codes the leaderboard screen against this interface —
 * use LeaderboardServiceStub while the real DB version isn't ready yet.
 */
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
