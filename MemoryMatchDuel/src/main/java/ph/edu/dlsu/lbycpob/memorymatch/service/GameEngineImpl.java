package ph.edu.dlsu.lbycpob.memorymatch.service;

import ph.edu.dlsu.lbycpob.memorymatch.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// UNDERSTAND: This is the real implementation of GameEngine — it holds the actual board, players, and
// turn state, and applies the rules for flipping, matching, and scoring.
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

    // UNDERSTAND: startNewRound() validates the player names first, then builds a fresh Board and a new
    // list of Player objects, and resets whose turn it is.
    // DECISION: validatePlayerNames() was pulled out into its own private method instead of inlining the
    // checks, because it's reused by both startNewRound() and startNewMatch().
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

    // UNDERSTAND: flipCard() only allows a card to be flipped if it isn't already matched or face up, and
    // blocks a 3rd flip while two cards are already waiting to be checked.
    // DECISION: firstFlipped/secondFlipped were used as two separate fields instead of a list, since the
    // game rule only ever needs exactly two cards flipped at a time before resolving them.
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

    // UNDERSTAND: checkMatch() compares the two flipped cards. If they match, both stay face up and the
    // current player scores and goes again. If not, both flip back down and the turn passes to the next
    // player.
    // DECISION: The matching player is allowed to keep their turn (no call to advanceTurn() in the matched
    // branch) because that's the standard memory-game rule — finding a pair earns you another turn.
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
    // UNDERSTAND: registerRoundWinner() finds whoever has the highest score once the board is fully matched
    // and gives them a round win.
    // DECISION: If more than one player is tied for the highest score, no one is given the round win
    // instead of picking one arbitrarily, because a tie shouldn't unfairly favor either player.
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
        }
        // If more than one player is at the highest score, it's a genuine tie —
        // no round win is awarded, matching what the results screen displays.
    }

    // UNDERSTAND: advanceTurn() moves currentPlayerIndex to the next player, wrapping back to 0 after the
    // last player.
    // DECISION: The modulo operator (%) was used instead of an if-check for "reached the last player"
    // because it naturally wraps the index around for any number of players.
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

    // UNDERSTAND: startNewMatch() sets up a best-of-N series — it calculates roundsToWin from the bestOf
    // value, creates the players, then starts the first round.
    // DECISION: roundsToWin was calculated as (bestOf / 2) + 1 instead of asking for it directly, so the
    // caller only has to think in terms of "best of 3 / best of 5" rather than doing that math themselves.
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

    // UNDERSTAND: startNextRound() resets each player's per-round stats (but not roundsWon) and generates a
    // new board, so the series can continue.
    // DECISION: An IllegalStateException is thrown if called with no match in progress or after the match
    // is already over, so a UI bug can't accidentally start a "round 6" of a match that already ended.
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

    // UNDERSTAND: getMatchWinner() and getLoser() figure out who has won or lost the whole series based on
    // roundsWon compared to roundsToWin.
    // DECISION: getLoser() was implemented by filtering for "the player who isn't the winner" instead of
    // hardcoding index 0/1, so it still works correctly if more than 2 players are ever supported later.
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