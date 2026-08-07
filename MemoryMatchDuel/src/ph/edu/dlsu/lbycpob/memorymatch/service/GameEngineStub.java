package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.Card;
import ph.edu.dlsu.lbycpob.memorymatch.model.Difficulty;
import ph.edu.dlsu.lbycpob.memorymatch.model.Player;
import ph.edu.dlsu.lbycpob.memorymatch.model.Theme;

import java.util.ArrayList;
import java.util.List;

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
}
