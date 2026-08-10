package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.TournamentMatch;

import java.util.List;

// UNDERSTAND: Defines the contract for managing a single-elimination tournament — registering players,
// building the bracket, and recording match winners round by round.
// DECISION: This was kept as its own interface separate from GameEngine because a tournament is a
// higher-level structure (a series of matches between many players) while GameEngine only handles the
// mechanics of one single game/round.
public interface TournamentService {

    void joinTournament(String playerName);

    List<String> getRegisteredPlayers();

    /** Shuffles registered players and builds round 1 of a single-elimination bracket. */
    void generateBracket();

    /** Matches for the round currently being played. */
    List<TournamentMatch> getCurrentRoundMatches();

    /** 1-indexed round number currently being played. */
    int getCurrentRoundNumber();

    /** Records a match's winner; auto-advances to the next round once the round is complete. */
    void recordMatchWinner(TournamentMatch match, String winnerName);

    boolean isTournamentComplete();

    /** Winner's name, or null if not complete yet. */
    String getChampion();

    void resetTournament();
}