package ph.edu.dlsu.lbycpob.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.Card;
import ph.edu.dlsu.lbycpob.memorymatch.model.Difficulty;
import ph.edu.dlsu.lbycpob.memorymatch.model.Player;
import ph.edu.dlsu.lbycpob.memorymatch.model.Theme;

import java.util.List;

/**
 * SHARED CONTRACT — agree on this together before splitting up.
 * PERSON A implements this. PERSON B (UI) codes against this interface only,
 * so the UI can be built before the real logic is finished (use GameEngineStub
 * in the meantime).
 */
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
}
