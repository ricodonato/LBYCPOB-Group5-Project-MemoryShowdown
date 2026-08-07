package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.TournamentMatch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PERSON A (Game Logic) OWNS THIS FILE.
 * Simple round-robin: every registered player faces every other player once.
 */
public class TournamentServiceImpl implements TournamentService {

    private final List<String> registeredPlayers = new ArrayList<>();
    private final Map<String, Integer> wins = new LinkedHashMap<>();

    @Override
    public void joinTournament(String playerName) {
        if (!registeredPlayers.contains(playerName)) {
            registeredPlayers.add(playerName);
            wins.put(playerName, 0);
        }
    }

    @Override
    public List<String> getRegisteredPlayers() {
        return registeredPlayers;
    }

    @Override
    public List<TournamentMatch> generateSchedule() {
        if (registeredPlayers.size() < 2) {
            throw new IllegalStateException(
                    "Need at least 2 registered players to generate a tournament schedule (currently "
                            + registeredPlayers.size() + ").");
        }
        List<TournamentMatch> schedule = new ArrayList<>();
        for (int i = 0; i < registeredPlayers.size(); i++) {
            for (int j = i + 1; j < registeredPlayers.size(); j++) {
                schedule.add(new TournamentMatch(registeredPlayers.get(i), registeredPlayers.get(j)));
            }
        }
        return schedule;
    }

    @Override
    public void recordMatchWinner(TournamentMatch match, String winnerName) {
        match.setWinner(winnerName);
        wins.merge(winnerName, 1, Integer::sum);
    }

    @Override
    public Map<String, Integer> getStandings() {
        Map<String, Integer> sorted = new LinkedHashMap<>();
        wins.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }

    @Override
    public void resetTournament() {
        registeredPlayers.clear();
        wins.clear();
    }
}
