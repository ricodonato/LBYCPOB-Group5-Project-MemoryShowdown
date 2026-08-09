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

public class ResultsController {
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

    @FXML
    private void initialize() {

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

        SceneManager.get().switchTo(
                "/fxml/welcome.fxml",
                "Memory Match Showdown"
        );
    }
}