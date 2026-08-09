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

    @FXML
    private void initialize() {
        refreshPlayerList();
        refreshMatches();
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
        if (tournamentService.getRegisteredPlayers().size() < 2) {
            showError("Need at least 2 registered players to generate a bracket (currently "
                    + tournamentService.getRegisteredPlayers().size() + ").");
            return;
        }

        hideError();
        SceneManager.get().setTournamentModeActive(true);
        SceneManager.get().switchTo(
                "/fxml/setup.fxml",
                "Memory Match Showdown - Tournament Settings"
        );
    }

    private HBox createMatchRow(TournamentMatch match) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.getStyleClass().add("match-row");

        Label vs = new Label(match.getPlayerA() + "  VS  " + match.getPlayerB());
        vs.getStyleClass().add("match-vs");
        vs.setPrefWidth(260);

        Button playButton = new Button(
                match.isPlayed() ? "Winner: " + match.getWinner() : "PLAY"
        );
        playButton.getStyleClass().addAll("mini-button", "mini-button-green");
        playButton.setDisable(match.isPlayed());

        playButton.setOnAction(event -> startBracketMatch(match));

        row.getChildren().addAll(vs, playButton);
        return row;
    }

    private void startBracketMatch(TournamentMatch match) {
        SceneManager manager = SceneManager.get();

        manager.setActiveTournamentMatch(match);
        manager.setPendingPlayerNames(List.of(match.getPlayerA(), match.getPlayerB()));
        manager.resetRoundNumber();

        manager.getGameEngine().startNewMatch(
                manager.getTournamentDifficulty(),
                manager.getTournamentTheme(),
                manager.getPendingPlayerNames(),
                manager.getTournamentBestOf()
        );

        manager.switchTo("/fxml/game_board.fxml", "Memory Match Showdown - Game");
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

    private void refreshStatus() {
        standingsContainer.getChildren().clear();

        String text = tournamentService.isTournamentComplete()
                ? "Tournament complete!"
                : tournamentService.getCurrentRoundNumber() == 0
                  ? "Waiting for bracket to be generated."
                  : "Currently on Round " + tournamentService.getCurrentRoundNumber();

        Label label = new Label(text);
        label.getStyleClass().add("standing-row");
        standingsContainer.getChildren().add(label);
    }

    @FXML
    private void handleReset() {
        tournamentService.resetTournament();
        refreshPlayerList();
        refreshMatches();
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