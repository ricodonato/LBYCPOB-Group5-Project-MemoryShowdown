package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;

import java.util.ArrayList;
import java.util.List;


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
