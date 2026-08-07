package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.TournamentMatch;

import java.util.List;
import java.util.Map;

/**
 * "allow users to create a tournament that each user can join").
 * PERSON A implements the bracket logic; PERSON B builds the UI against
 * this interface.
 */
public interface TournamentService {

    /** Registers a player into the current tournament lobby. */
    void joinTournament(String playerName);

    /** Returns everyone currently signed up. */
    List<String> getRegisteredPlayers();

    /** Generates a round-robin schedule once enough players have joined (min 2). */
    List<TournamentMatch> generateSchedule();

    /** Records the winner of one scheduled match. */
    void recordMatchWinner(TournamentMatch match, String winnerName);

    /** Returns win counts per player, sorted highest first. */
    Map<String, Integer> getStandings();

    /** Resets the tournament lobby for a new one. */
    void resetTournament();
}
