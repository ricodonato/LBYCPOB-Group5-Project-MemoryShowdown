package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.Card;
import ph.edu.dlsu.lbycpob.memorymatch.model.Difficulty;
import ph.edu.dlsu.lbycpob.memorymatch.model.Player;
import ph.edu.dlsu.lbycpob.memorymatch.model.Theme;

import java.util.ArrayList;
import java.util.List;

// UNDERSTAND: A fake, simplified stand-in for GameEngine that returns canned/predictable values instead
// of real game logic.
// DECISION: A stub class was used instead of testing directly against GameEngineImpl so the UI screens
// could be built and clicked through before the real scoring/turn logic was finished, without waiting
// on each other.
public class GameEngineStub implements GameEngine {

    private final List<Card> fakeCards = new ArrayList<>();
    private final List<Player> fakePlayers = new ArrayList<>();

    @Override
    public void startNewRound(Difficulty difficulty, Theme theme, List<String> playerNames) {
        fakeCards.clear();
        List<String> symbols = theme.getSymbols();
        for (int i = 0; i < difficulty.getCardCount(); i++) {
            fakeCards.add(new Card(i, symbols.get((i / 2) % symbols.size()), theme.name()));
        }
        fakePlayers.clear();
        for (String name : playerNames) {
            fakePlayers.add(new Player(name));
        }
    }

    @Override
    public Card flipCard(int cardId) {
        return fakeCards.stream().filter(c -> c.getId() == cardId).findFirst().orElse(null);
    }

    // UNDERSTAND: checkMatch() always returns true, since the stub isn't meant to simulate real win/loss
    // conditions — just give the UI something to render.
    @Override
    public boolean checkMatch() {
        return true;
    }

    @Override
    public Player getCurrentPlayer() {
        return fakePlayers.isEmpty() ? new Player("Player 1") : fakePlayers.get(0);
    }

    @Override
    public List<Player> getPlayers() {
        return fakePlayers;
    }

    @Override
    public boolean isRoundOver() {
        return false;
    }

    @Override
    public List<Card> getBoardState() {
        return fakeCards;
    }

    @Override
    public Player getWinner() {
        return fakePlayers.isEmpty() ? null : fakePlayers.get(0);
    }

    @Override
    public void startNewMatch(Difficulty difficulty, Theme theme, List<String> playerNames, int bestOf) {
        // Fake: just start a single round, ignore bestOf.
        startNewRound(difficulty, theme, playerNames);
    }

    @Override
    public void startNextRound() {
        // No-op for the stub — nothing to advance.
    }

    // UNDERSTAND: isMatchOver()/getMatchWinner() are effectively switched off (return false/null), since
    // the stub doesn't simulate a real best-of-series outcome.
    @Override
    public boolean isMatchOver() {
        return false;
    }

    @Override
    public Player getMatchWinner() {
        return null;
    }

    @Override
    public int getRoundsToWin() {
        return 1;
    }

    @Override
    public Difficulty getDifficulty() {
        return null;
    }

    @Override
    public Theme getTheme() {
        return null;
    }

    @Override
    public Player getLoser() {
        return null;
    }
}