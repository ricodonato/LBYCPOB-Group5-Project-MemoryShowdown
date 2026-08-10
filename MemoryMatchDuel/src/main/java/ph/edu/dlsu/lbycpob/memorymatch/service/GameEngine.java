package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.Card;
import ph.edu.dlsu.lbycpob.memorymatch.model.Difficulty;
import ph.edu.dlsu.lbycpob.memorymatch.model.Player;
import ph.edu.dlsu.lbycpob.memorymatch.model.Theme;

import java.util.List;

// UNDERSTAND: Defines the contract for anything that can run a memory-match game — starting rounds,
// flipping cards, checking matches, and tracking match-level progress (rounds won, series winner).
// DECISION: An interface was used instead of a single concrete class so the UI layer can be built and
// tested against a fake implementation (GameEngineStub) before the real logic (GameEngineImpl) is done.
public interface GameEngine {

    /** Starts a new round with the given difficulty, theme, and player names. */
    void startNewRound(Difficulty difficulty, Theme theme, List<String> playerNames);

    /** Flip a card by id. Returns the card's current state after flipping. */
    Card flipCard(int cardId);

    /** Call after two cards are face-up to check for a match. Returns true if matched. */
    boolean checkMatch();

    /** Returns the player whose turn it currently is. */
    Player getCurrentPlayer();

    /** Returns all players and their live scores/combo state. */
    List<Player> getPlayers();

    /** True once all pairs on the board are matched. */
    boolean isRoundOver();

    /** Returns all cards currently on the board, for rendering. */
    List<Card> getBoardState();

    /** Returns the player with the highest score once the round is over. */
    Player getWinner();

    // UNDERSTAND: Separate methods exist for round-level state (above) and match-level state (below).
    // DECISION: These were kept as two separate concepts instead of merging them into one "game over" flag,
    // because a best-of-series match can have multiple rounds, each with its own winner, before the match
    // itself is decided.
    void startNewMatch(Difficulty difficulty, Theme theme, List<String> playerNames, int bestOf);

    /**
     * Starts the next round of the current match: same players and settings, board is
     * regenerated, each player's per-round score/combo resets, but roundsWon is kept.
     * Only call this when a match is in progress and isMatchOver() is false.
     */
    void startNextRound();

    /** True once a player has won enough rounds to take the whole match. */
    boolean isMatchOver();

    /** Returns the player who has won the match (by rounds), or null if the match isn't over yet. */
    Player getMatchWinner();

    /** Returns how many round-wins are needed to take the current match. */
    int getRoundsToWin();

    /** Returns the difficulty of the current/last match. */
    Difficulty getDifficulty();

    /** Returns the theme of the current/last match. */
    Theme getTheme();

    /** Returns the player who lost the match (the non-winner), or null if match isn't over. */
    Player getLoser();
}