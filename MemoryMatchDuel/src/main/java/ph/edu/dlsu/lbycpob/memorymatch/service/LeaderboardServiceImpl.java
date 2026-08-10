package ph.edu.dlsu.lbycpob.memorymatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.memorymatch.entity.MatchResultEntity;
import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;
import ph.edu.dlsu.lbycpob.memorymatch.repository.MatchResultRepository;
import ph.edu.dlsu.lbycpob.memorymatch.repository.PlayerRepository;
import org.springframework.data.domain.PageRequest;

import java.util.List;

// UNDERSTAND: The real, database-backed implementation of LeaderboardService — talks to the two JPA
// repositories to read and write player/match data.
// DECISION: @Service + constructor @Autowired was used instead of manually creating this object, so
// Spring Boot can inject the repository dependencies automatically and manage this as a singleton bean.
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
        return playerRepository.findAllByOrderByHighestScoreDesc(PageRequest.of(0, limit));
    }

    // UNDERSTAND: recordMatchResult() looks up (or creates) both players, updates their running totals,
    // saves them, then saves a new MatchResultEntity row for this match.
    // DECISION: findOrCreatePlayer() is called for both winner and loser instead of assuming they
    // already exist in the database, so a brand-new player who's never played before doesn't cause a
    // lookup failure.
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

    // UNDERSTAND: findOrCreatePlayer() returns the existing PlayerEntity for a username, or creates and
    // saves a brand-new one if it doesn't exist yet.
    // DECISION: Optional.orElseGet() was used instead of an if/else null-check, because it only runs the
    // "create a new player" logic when actually needed, rather than always constructing one upfront.
    @Override
    public PlayerEntity findOrCreatePlayer(String username) {
        return playerRepository.findByUsername(username)
                .orElseGet(() -> playerRepository.save(new PlayerEntity(username)));
    }
}