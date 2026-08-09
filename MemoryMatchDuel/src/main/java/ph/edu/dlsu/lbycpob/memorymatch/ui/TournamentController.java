package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import ph.edu.dlsu.lbycpob.memorymatch.model.TournamentMatch;
import ph.edu.dlsu.lbycpob.memorymatch.service.TournamentService;

import java.util.ArrayList;
import java.util.List;

public class TournamentController {

    @FXML private TextField joinField;
    @FXML private VBox playersContainer;
    @FXML private VBox matchesContainer;
    @FXML private VBox standingsContainer;
    @FXML private Label errorLabel;

    private final TournamentService tournamentService =
            SceneManager.get().getTournamentService();

    // Local history log — rebuilt from scratch each time a bracket is generated
    private final List<String> roundHistory = new ArrayList<>();

    @FXML
    private void initialize() {
        refreshPlayerList();
        refreshMatches();
        refreshHistory();
    }

    @FXML
    private void handleJoin() {
        String name = joinField.getText() == null ? "" : joinField.getText().trim();

        if (name.isEmpty()) {
            showError("Enter a player name first.");
            return;
        }
        if (name.length() > 18) {
            showError("Keep tournament names at 18 characters or fewer.");
            return;
        }

        tournamentService.joinTournament(name);
        joinField.clear();
        hideError();
        refreshPlayerList();
    }

    @FXML
    private void handleGenerateSchedule() {
        try {
            tournamentService.generateBracket();
            roundHistory.clear();
            hideError();
            refreshMatches();
            refreshHistory();
        } catch (IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private HBox createMatchRow(TournamentMatch match) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.getStyleClass().add("match-row");

        Label vs = new Label(match.getPlayerA() + "  VS  " + match.getPlayerB());
        vs.getStyleClass().add("match-vs");
        vs.setPrefWidth(260);

        Button playerA = new Button("Win: " + match.getPlayerA());
        playerA.getStyleClass().addAll("mini-button", "mini-button-green");

        Button playerB = new Button("Win: " + match.getPlayerB());
        playerB.getStyleClass().addAll("mini-button", "mini-button-blue");

        Runnable disableButtons = () -> {
            playerA.setDisable(true);
            playerB.setDisable(true);
        };

        playerA.setOnAction(event -> {
            if (!match.isPlayed()) {
                recordWinner(match, match.getPlayerA());
                disableButtons.run();
            }
        });

        playerB.setOnAction(event -> {
            if (!match.isPlayed()) {
                recordWinner(match, match.getPlayerB());
                disableButtons.run();
            }
        });

        row.getChildren().addAll(vs, playerA, playerB);
        return row;
    }

    private void recordWinner(TournamentMatch match, String winnerName) {
        int roundPlayed = match.getRound();
        String matchup = match.getPlayerA() + " vs " + match.getPlayerB();

        tournamentService.recordMatchWinner(match, winnerName);

        roundHistory.add("R" + roundPlayed + ": " + matchup + "  →  " + winnerName);
        refreshHistory();

        // Round may have auto-advanced (all matches resolved) — refresh the board
        refreshMatches();
    }

    private void refreshMatches() {
        matchesContainer.getChildren().clear();

        if (tournamentService.isTournamentComplete()) {
            Label championLabel = new Label("🏆 Champion: " + tournamentService.getChampion());
            championLabel.getStyleClass().add("match-vs");
            matchesContainer.getChildren().add(championLabel);
            return;
        }

        List<TournamentMatch> current = tournamentService.getCurrentRoundMatches();

        if (current.isEmpty()) {
            Label empty = new Label("Generate a bracket to begin.");
            empty.getStyleClass().add("muted-label");
            matchesContainer.getChildren().add(empty);
            return;
        }

        Label roundLabel = new Label("ROUND " + tournamentService.getCurrentRoundNumber());
        roundLabel.getStyleClass().add("small-heading");
        matchesContainer.getChildren().add(roundLabel);

        for (TournamentMatch match : current) {
            if (!match.isBye()) {
                matchesContainer.getChildren().add(createMatchRow(match));
            }
        }
    }

    private void refreshPlayerList() {
        playersContainer.getChildren().clear();

        List<String> players = tournamentService.getRegisteredPlayers();

        if (players.isEmpty()) {
            Label empty = new Label("No players have joined yet.");
            empty.getStyleClass().add("muted-label");
            playersContainer.getChildren().add(empty);
            return;
        }

        for (String name : players) {
            Label label = new Label("• " + name);
            label.getStyleClass().add("player-chip");
            playersContainer.getChildren().add(label);
        }
    }

    private void refreshHistory() {
        standingsContainer.getChildren().clear();

        if (roundHistory.isEmpty()) {
            Label empty = new Label("Match results will appear here.");
            empty.getStyleClass().add("muted-label");
            standingsContainer.getChildren().add(empty);
            return;
        }

        for (String entry : roundHistory) {
            Label label = new Label(entry);
            label.getStyleClass().add("standing-row");
            standingsContainer.getChildren().add(label);
        }
    }

    @FXML
    private void handleReset() {
        tournamentService.resetTournament();
        roundHistory.clear();
        refreshPlayerList();
        refreshMatches();
        refreshHistory();
        hideError();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @FXML
    private void handleBack() {
        SceneManager.get().switchTo("/fxml/welcome.fxml", "Memory Match Showdown");
    }
}