package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

// UNDERSTAND: A fake, in-memory stand-in for LeaderboardService that returns hardcoded players instead
// of hitting a real database.
// DECISION: This stub was used instead of waiting on the real Spring/DB-backed implementation, so the
// UI teammate could build and test the leaderboard screen before the backend teammate's part was ready.
public class LeaderboardServiceStub implements LeaderboardService {

    @Override
    public List<PlayerEntity> getTopPlayers(int limit) {
        List<PlayerEntity> fake = new ArrayList<>();
        String[] names = {"Matthew", "Rico", "Drew"};
        int[] scores = {320, 275, 190};
        for (int i = 0; i < names.length; i++) {
            PlayerEntity p = new PlayerEntity(names[i]);
            p.setHighestScore(scores[i]);
            p.setTotalWins(3 - i);
            p.setTotalMatches(5);
            fake.add(p);
        }
        return fake;
    }

    // UNDERSTAND: recordMatchResult() doesn't actually save anything here — it just prints to the
    // console so the caller can be tested without a real database.
    @Override
    public void recordMatchResult(String winnerName, int winnerScore,
                                  String loserName, int loserScore,
                                  String difficulty, String theme) {
        // no-op for UI testing
        System.out.println("[STUB] " + winnerName + " beat " + loserName
                + " (" + winnerScore + " - " + loserScore + ")");
    }

    @Override
    public PlayerEntity findOrCreatePlayer(String username) {
        return new PlayerEntity(username);
    }
}