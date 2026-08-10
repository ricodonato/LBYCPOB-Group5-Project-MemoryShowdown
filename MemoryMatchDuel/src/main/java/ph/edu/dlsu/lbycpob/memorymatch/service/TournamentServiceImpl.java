package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.TournamentMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// UNDERSTAND: Implements the tournament bracket logic — registering players, shuffling them into a
// single-elimination bracket, and advancing round by round until one champion remains.
public class TournamentServiceImpl implements TournamentService {

    private final List<String> registeredPlayers = new ArrayList<>();
    private List<TournamentMatch> currentRoundMatches = new ArrayList<>();
    private int currentRound = 0;
    private String champion;

    // UNDERSTAND: joinTournament() only adds a player if they aren't already registered.
    // DECISION: A contains() check was added before adding, instead of allowing duplicates, so the same
    // player can't accidentally take two bracket slots.
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

    // UNDERSTAND: generateBracket() shuffles the registered players, then pads the list with byes (null
    // slots) until the size is a power of two, so the bracket can be split evenly into matches.
    // DECISION: The bracket size was rounded up to the next power of two instead of requiring an exact
    // power-of-two number of players, because it lets any number of players join, and byes automatically
    // fill the empty slots.
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

    // UNDERSTAND: resolveByesAndAdvance() auto-wins any bye matches, then checks if the whole round is
    // done. If only one winner remains, that player is the champion; otherwise it builds the next round
    // from the current winners.
    // DECISION: This method calls itself again after building the next round (recursion) instead of
    // stopping there, in case the next round is also entirely byes and needs to resolve automatically too.
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