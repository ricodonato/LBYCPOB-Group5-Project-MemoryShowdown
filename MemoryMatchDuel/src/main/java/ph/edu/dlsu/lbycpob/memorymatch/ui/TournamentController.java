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

// UNDERSTAND: Controls the tournament screen — lets players join, generates the bracket, shows the
// current round's matches as clickable rows, and shows the champion once the bracket is finished.
public class TournamentController extends BaseScreenController {

    @FXML private TextField joinField;
    @FXML private VBox playersContainer;
    @FXML private VBox matchesContainer;
    @FXML private VBox standingsContainer;
    @FXML private Label errorLabel;

    private final TournamentService tournamentService =
            SceneManager.get().getTournamentService();

    @Override
    protected void onScreenReady() {
        refreshPlayerList();
        refreshMatches();
    }

    @FXML
    private void handleJoin() {
        String name = joinField.getText() == null ? "" : joinField.getText().trim();

        if (name.isEmpty()) {
            showError(errorLabel, "Enter a player name first.");
            return;
        }
        if (name.length() > 18) {
            showError(errorLabel, "Keep tournament names at 18 characters or fewer.");
            return;
        }

        tournamentService.joinTournament(name);
        joinField.clear();
        hideError(errorLabel);
        refreshPlayerList();
    }

    // UNDERSTAND: handleGenerateSchedule() checks there are at least 2 players and an even number of
    // them before switching to the setup screen to pick tournament settings.
    // DECISION: The even-player check was added on the UI side (in addition to whatever TournamentService
    // itself does) so the player gets a clear, specific error message here instead of a generic exception
    // bubbling up from the service layer.
    @FXML
    private void handleGenerateSchedule() {
        int count = tournamentService.getRegisteredPlayers().size();

        if (count < 2) {
            showError(errorLabel, "Need at least 2 registered players to generate a bracket (currently " + count + ").");
            return;
        }

        if (count % 2 != 0) {
            showError(errorLabel, "Bracket needs an even number of players (currently " + count + "). Add one more or remove one.");
            return;
        }

        hideError(errorLabel);
        SceneManager.get().setTournamentModeActive(true);
        SceneManager.get().switchTo("/fxml/setup.fxml", "Memory Match Showdown - Tournament Settings");
    }

    // UNDERSTAND: createMatchRow() builds one row showing "PlayerA VS PlayerB" and a button that either
    // says "PLAY" or, once played, shows the winner and disables itself.
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

    // UNDERSTAND: startBracketMatch() stores which bracket match is being played, loads both player
    // names into the game engine using the tournament's chosen difficulty/theme/best-of, then jumps to
    // the game board.
    // DECISION: setActiveTournamentMatch(match) is stored on SceneManager before switching screens, so
    // that later, when ResultsController sees the match finish, it knows which bracket match to record
    // the winner against.
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

    // UNDERSTAND: refreshMatches() shows the champion banner if the tournament is done, a "generate a
    // bracket" prompt if nothing's been generated yet, or the current round's non-bye matches otherwise.
    // DECISION: Bye matches are filtered out of the displayed rows (if (!match.isBye())) because a bye
    // is auto-resolved by the service already — showing it as a clickable "PLAY" match would be
    // confusing since there's no real opponent to play against.
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
        hideError(errorLabel);
    }

    @FXML
    private void handleBack() {
        goToWelcome();
    }
}