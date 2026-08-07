package ph.edu.dlsu.lbycpob.memorymatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.memorymatch.entity.MatchResultEntity;
import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;
import ph.edu.dlsu.lbycpob.memorymatch.repository.MatchResultRepository;
import ph.edu.dlsu.lbycpob.memorymatch.repository.PlayerRepository;

import java.util.List;

/**
 * PERSON C (Backend/DB) OWNS THIS FILE.
 */
@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final PlayerRepository playerRepository;
    private final MatchResultRepository matchResultRepository;

    @Autowired
    public LeaderboardServiceImpl(PlayerRepository playerRepository,
                                  MatchResultRepository matchResultRepository) {
        this.playerRepository = playerRepository;
        this.matchResultRepository = matchResultRepository;
    }

    @Override
    public List<PlayerEntity> getTopPlayers(int limit) {
        return playerRepository.findTop10ByOrderByHighestScoreDesc();
    }

    @Override
    public void recordMatchResult(String winnerName, int winnerScore,
                                   String loserName, int loserScore,
                                   String difficulty, String theme) {
        PlayerEntity winner = findOrCreatePlayer(winnerName);
        PlayerEntity loser = findOrCreatePlayer(loserName);

        winner.setTotalWins(winner.getTotalWins() + 1);
        winner.setTotalMatches(winner.getTotalMatches() + 1);
        winner.setHighestScore(Math.max(winner.getHighestScore(), winnerScore));

        loser.setTotalMatches(loser.getTotalMatches() + 1);
        loser.setHighestScore(Math.max(loser.getHighestScore(), loserScore));

        playerRepository.save(winner);
        playerRepository.save(loser);

        MatchResultEntity result = new MatchResultEntity();
        result.setWinner(winner);
        result.setLoser(loser);
        result.setWinnerScore(winnerScore);
        result.setLoserScore(loserScore);
        result.setDifficulty(difficulty);
        result.setTheme(theme);
        matchResultRepository.save(result);
    }

    @Override
    public PlayerEntity findOrCreatePlayer(String username) {
        return playerRepository.findByUsername(username)
                .orElseGet(() -> playerRepository.save(new PlayerEntity(username)));
    }
}
