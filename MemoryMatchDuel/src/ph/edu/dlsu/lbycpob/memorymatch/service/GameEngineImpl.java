package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameEngineImpl implements GameEngine {

    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;
    private Card firstFlipped;
    private Card secondFlipped;

    @Override
    public void startNewRound(Difficulty difficulty, Theme theme, List<String> playerNames) {
        this.board = new Board(difficulty, theme);
        this.players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        this.currentPlayerIndex = 0;
        this.firstFlipped = null;
        this.secondFlipped = null;
    }

    @Override
    public Card flipCard(int cardId) {
        Card card = board.getCardById(cardId);
        if (card == null || card.isMatched() || card.isFaceUp()) {
            return card;
        }
        card.setFaceUp(true);
        if (firstFlipped == null) {
            firstFlipped = card;
        } else if (secondFlipped == null) {
            secondFlipped = card;
        }
        return card;
    }

    @Override
    public boolean checkMatch() {
        if (firstFlipped == null || secondFlipped == null) {
            return false;
        }
        boolean matched = firstFlipped.matches(secondFlipped);
        Player current = getCurrentPlayer();

        if (matched) {
            firstFlipped.setMatched(true);
            secondFlipped.setMatched(true);
            current.registerMatch(10); // base points per match
            // matching player goes again — don't advance turn
        } else {
            firstFlipped.setFaceUp(false);
            secondFlipped.setFaceUp(false);
            current.registerMiss();
            advanceTurn();
        }

        firstFlipped = null;
        secondFlipped = null;
        return matched;
    }

    private void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    @Override
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public boolean isRoundOver() {
        return board.isFullyMatched();
    }

    @Override
    public List<Card> getBoardState() {
        return board.getCards();
    }

    @Override
    public Player getWinner() {
        return players.stream()
                .max(Comparator.comparingInt(Player::getScore))
                .orElse(null);
    }
}
