package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.TournamentMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TournamentServiceImpl implements TournamentService {

    private final List<String> registeredPlayers = new ArrayList<>();
    private List<TournamentMatch> currentRoundMatches = new ArrayList<>();
    private int currentRound = 0;
    private String champion;

    @Override
    public void joinTournament(String playerName) {
        if (!registeredPlayers.contains(playerName)) {
            registeredPlayers.add(playerName);
        }
    }

    @Override
    public List<String> getRegisteredPlayers() {
        return registeredPlayers;
    }

    @Override
    public void generateBracket() {
        if (registeredPlayers.size() < 2) {
            throw new IllegalStateException(
                    "Need at least 2 registered players to generate a bracket (currently "
                            + registeredPlayers.size() + ").");
        }

        List<String> shuffled = new ArrayList<>(registeredPlayers);
        Collections.shuffle(shuffled);

        int bracketSize = 1;
        while (bracketSize < shuffled.size()) {
            bracketSize *= 2;
        }
        while (shuffled.size() < bracketSize) {
            shuffled.add(null); // bye
        }

        currentRound = 1;
        champion = null;
        currentRoundMatches = new ArrayList<>();
        for (int i = 0; i < shuffled.size(); i += 2) {
            currentRoundMatches.add(new TournamentMatch(shuffled.get(i), shuffled.get(i + 1), currentRound));
        }

        resolveByesAndAdvance();
    }

    @Override
    public List<TournamentMatch> getCurrentRoundMatches() {
        return currentRoundMatches;
    }

    @Override
    public int getCurrentRoundNumber() {
        return currentRound;
    }

    @Override
    public void recordMatchWinner(TournamentMatch match, String winnerName) {
        match.setWinner(winnerName);
        resolveByesAndAdvance();
    }

    private void resolveByesAndAdvance() {
        for (TournamentMatch match : currentRoundMatches) {
            if (match.isBye() && !match.isPlayed()) {
                match.setWinner(match.getPlayerA() != null ? match.getPlayerA() : match.getPlayerB());
            }
        }

        boolean roundComplete = currentRoundMatches.stream().allMatch(TournamentMatch::isPlayed);
        if (!roundComplete) {
            return;
        }

        List<String> winners = currentRoundMatches.stream().map(TournamentMatch::getWinner).toList();

        if (winners.size() == 1) {
            champion = winners.get(0);
            return;
        }

        currentRound++;
        List<TournamentMatch> nextRound = new ArrayList<>();
        for (int i = 0; i < winners.size(); i += 2) {
            nextRound.add(new TournamentMatch(winners.get(i), winners.get(i + 1), currentRound));
        }
        currentRoundMatches = nextRound;

        resolveByesAndAdvance(); // in case the next round is also all byes
    }

    @Override
    public boolean isTournamentComplete() {
        return champion != null;
    }

    @Override
    public String getChampion() {
        return champion;
    }

    @Override
    public void resetTournament() {
        registeredPlayers.clear();
        currentRoundMatches.clear();
        currentRound = 0;
        champion = null;
    }
}