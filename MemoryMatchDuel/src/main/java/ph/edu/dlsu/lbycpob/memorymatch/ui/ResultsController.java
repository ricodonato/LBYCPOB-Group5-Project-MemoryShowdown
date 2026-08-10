package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import ph.edu.dlsu.lbycpob.memorymatch.model.TournamentMatch;
import ph.edu.dlsu.lbycpob.memorymatch.model.Player;
import ph.edu.dlsu.lbycpob.memorymatch.service.GameEngine;
import ph.edu.dlsu.lbycpob.memorymatch.service.LeaderboardService;

import java.util.Comparator;
import java.util.List;

// UNDERSTAND: Controls the results screen shown after every round — displays who won the round, or if
// the whole match/series just finished, shows the champion and records the result to the leaderboard.
public class ResultsController extends BaseScreenController {
    private boolean cameFromTournament;

    @FXML
    private Label resultTitleLabel;

    @FXML
    private Label winnerLabel;

    @FXML
    private Label roundScoreLabel;

    @FXML
    private Label seriesScoreLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button nextRoundButton;

    @FXML
    private Button newMatchButton;

    private final GameEngine engine =
            SceneManager.get()
                    .getGameEngine();

    // UNDERSTAND: onScreenReady() figures out this round's winner by sorting players by score and
    // checking if exactly one player is at the top (a tie means no round winner), builds the score/wins
    // summary strings, then branches into two very different layouts depending on whether the whole
    // match is over or just this round.
    // DECISION: atHighest (a count of how many players share the top score) was used instead of just
    // comparing the top two scores, so this logic still works correctly if more than 2 players are ever
    // added later.
    @Override
    protected void onScreenReady() {

        List<Player> players =
                engine.getPlayers();

        List<Player> byScore =
                players.stream()
                        .sorted(
                                Comparator
                                        .comparingInt(
                                                Player::getScore
                                        )
                                        .reversed()
                        )
                        .toList();

        int highest =
                byScore.isEmpty()
                        ? 0
                        : byScore.get(0)
                          .getScore();

        long atHighest =
                players.stream()
                        .filter(
                                p ->
                                        p.getScore()
                                                == highest
                        )
                        .count();

        Player roundWinner =
                atHighest == 1
                        && !byScore.isEmpty()
                        ? byScore.get(0)
                        : null;

        roundScoreLabel.setText(
                players.stream()
                        .map(
                                p ->
                                        p.getName()
                                                + "  "
                                                + p.getScore()
                                                + " pts"
                        )
                        .reduce(
                                (a, b) ->
                                        a
                                                + "     |     "
                                                + b
                        )
                        .orElse("")
        );

        seriesScoreLabel.setText(
                players.stream()
                        .map(
                                p ->
                                        p.getName()
                                                + "  "
                                                + p.getRoundsWon()
                                                + " round win"
                                                + (
                                                p.getRoundsWon() == 1
                                                        ? ""
                                                        : "s"
                                        )
                        )
                        .reduce(
                                (a, b) ->
                                        a
                                                + "     |     "
                                                + b
                        )
                        .orElse("")
        );

        // UNDERSTAND: When the whole match is over, this block records the final result to the
        // leaderboard and, if this match came from a tournament bracket, reports the winner back to the
        // TournamentService so the bracket can advance.
        // DECISION: The leaderboard write and tournament-advance calls are both guarded by
        // "leaderboardService != null" / "activeMatch != null" checks instead of assuming they're always
        // present, since a match can be played without a leaderboard connected, and without being part
        // of a tournament.
        if (engine.isMatchOver()) {

            Player champion =
                    engine.getMatchWinner();

            Player runnerUp =
                    engine.getLoser();

            LeaderboardService leaderboardService =
                    SceneManager.get()
                            .getLeaderboardService();

            if (leaderboardService != null
                    && champion != null
                    && runnerUp != null) {

                leaderboardService.recordMatchResult(
                        champion.getName(),
                        champion.getScore(),
                        runnerUp.getName(),
                        runnerUp.getScore(),
                        engine.getDifficulty().name(),
                        engine.getTheme().name()
                );

                TournamentMatch activeMatch = SceneManager.get().getActiveTournamentMatch();
                cameFromTournament = activeMatch != null;

                if (activeMatch != null && champion != null) {
                    SceneManager.get().getTournamentService()
                            .recordMatchWinner(activeMatch, champion.getName());
                    SceneManager.get().setActiveTournamentMatch(null);
                }
            }

            resultTitleLabel.setText(
                    "MATCH COMPLETE"
            );

            winnerLabel.setText(
                    champion == null
                            ? "Match finished"
                            : "🏆 "
                              + champion.getName()
                              + " wins the showdown!"
            );

            statusLabel.setText(
                    cameFromTournament
                            ? "Tournament bracket match complete"
                            : "Best-of-" + SceneManager.get().getPendingBestOf() + " series complete"
            );

            nextRoundButton.setVisible(false);
            nextRoundButton.setManaged(false);

            newMatchButton.setText(cameFromTournament ? "Back to Tournament" : "New Match");
            newMatchButton.setVisible(true);
            newMatchButton.setManaged(true);

        } else {

            resultTitleLabel.setText(
                    "ROUND "
                            + SceneManager.get()
                            .getCurrentRoundNumber()
                            + " COMPLETE"
            );

            if (roundWinner == null) {

                winnerLabel.setText(
                        "🤝 Tied round - no round win awarded"
                );

                statusLabel.setText(
                        "Replay with a fresh board to break the tie."
                );

                nextRoundButton.setText(
                        "Replay Round"
                );

            } else {

                winnerLabel.setText(
                        "⭐ "
                                + roundWinner.getName()
                                + " takes the round!"
                );

                statusLabel.setText(
                        "First to "
                                + engine.getRoundsToWin()
                                + " round wins takes the match."
                );

                nextRoundButton.setText(
                        "Next Round"
                );
            }

            nextRoundButton.setVisible(true);
            nextRoundButton.setManaged(true);

            newMatchButton.setVisible(false);
            newMatchButton.setManaged(false);
        }
    }

    @FXML
    private void handleNextRound() {

        engine.startNextRound();

        SceneManager.get()
                .advanceRoundNumber();

        SceneManager.get().switchTo(
                "/fxml/game_board.fxml",
                "Memory Match Showdown - Game"
        );
    }

    // UNDERSTAND: handleNewMatch() routes back to either the tournament screen or the regular setup
    // screen, depending on whether this finished match was a bracket match.
    @FXML
    private void handleNewMatch() {
        if (cameFromTournament) {
            SceneManager.get().switchTo("/fxml/tournament.fxml", "Memory Match Showdown - Tournament");
        } else {
            SceneManager.get().switchTo("/fxml/setup.fxml", "Memory Match Showdown - Setup");
        }
    }

    @FXML
    private void handleLeaderboard() {

        SceneManager.get().switchTo(
                "/fxml/leaderboard.fxml",
                "Memory Match Showdown - Leaderboard"
        );
    }

    @FXML
    private void handleMainMenu() {
        goToWelcome();
    }
}