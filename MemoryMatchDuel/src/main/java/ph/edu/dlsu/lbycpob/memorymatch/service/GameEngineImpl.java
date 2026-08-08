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

    // Match-level (best-of-series) state
    private Difficulty matchDifficulty;
    private Theme matchTheme;
    private int roundsToWin = 1;

    @Override
    public void startNewRound(Difficulty difficulty, Theme theme, List<String> playerNames) {
        validatePlayerNames(playerNames);
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
        // Don't allow a 3rd card to flip until checkMatch() resolves the first two.
        if (firstFlipped != null && secondFlipped != null) {
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

        if (board.isFullyMatched()) {
            registerRoundWinner();
        }

        return matched;
    }

    /** Credits the player with the highest score this round with a round win. */
    private void registerRoundWinner() {
        int highestScore = players.stream()
                .mapToInt(Player::getScore)
                .max()
                .orElse(0);

        List<Player> playersAtHighScore = players.stream()
                .filter(p -> p.getScore() == highestScore)
                .toList();

        if (playersAtHighScore.size() == 1) {
            playersAtHighScore.get(0).registerRoundWin();
            return;
        }

        int fewestMisses = playersAtHighScore.stream()
                .mapToInt(Player::getMissCount)
                .min()
                .orElse(0);

        List<Player> tiebreakWinners = playersAtHighScore.stream()
                .filter(p -> p.getMissCount() == fewestMisses)
                .toList();

        if (tiebreakWinners.size() == 1) {
            tiebreakWinners.get(0).registerRoundWin();
        }
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

    @Override
    public void startNewMatch(Difficulty difficulty, Theme theme, List<String> playerNames, int bestOf) {
        if (bestOf < 1) {
            throw new IllegalArgumentException("bestOf must be at least 1");
        }
        validatePlayerNames(playerNames);
        this.matchDifficulty = difficulty;
        this.matchTheme = theme;
        this.roundsToWin = (bestOf / 2) + 1; // e.g. bestOf=3 -> need 2 wins, bestOf=5 -> need 3 wins

        this.players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        startRoundInternal();
    }

    @Override
    public void startNextRound() {
        if (matchDifficulty == null || matchTheme == null || players == null) {
            throw new IllegalStateException("No match in progress — call startNewMatch() first.");
        }
        if (isMatchOver()) {
            throw new IllegalStateException("Match is already over — check getMatchWinner().");
        }
        for (Player p : players) {
            p.resetRoundStats();
        }
        startRoundInternal();
    }

    private void startRoundInternal() {
        this.board = new Board(matchDifficulty, matchTheme);
        this.currentPlayerIndex = 0;
        this.firstFlipped = null;
        this.secondFlipped = null;
    }

    @Override
    public boolean isMatchOver() {
        return getMatchWinner() != null;
    }

    @Override
    public Player getMatchWinner() {
        if (players == null) {
            return null;
        }
        return players.stream()
                .filter(p -> p.getRoundsWon() >= roundsToWin)
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getRoundsToWin() {
        return roundsToWin;
    }

    @Override
    public Difficulty getDifficulty() {
        return matchDifficulty;
    }

    @Override
    public Theme getTheme() {
        return matchTheme;
    }

    @Override
    public Player getLoser() {
        Player winner = getMatchWinner();
        if (winner == null) {
            return null;
        }
        return players.stream()
                .filter(p -> p != winner)
                .findFirst()
                .orElse(null);
    }

    private void validatePlayerNames(List<String> playerNames) {
        if (playerNames == null || playerNames.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 players to start a game.");
        }
        for (String name : playerNames) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Player names must not be null or blank.");
            }
        }
        if (playerNames.stream().map(String::trim).distinct().count() != playerNames.size()) {
            throw new IllegalArgumentException("Player names must be unique.");
        }
    }
}
