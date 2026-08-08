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

import java.util.List;
import java.util.Map;

public class TournamentController {

    @FXML
    private TextField joinField;

    @FXML
    private VBox playersContainer;

    @FXML
    private VBox matchesContainer;

    @FXML
    private VBox standingsContainer;

    @FXML
    private Label errorLabel;

    private final TournamentService tournamentService =
            SceneManager.get()
                    .getTournamentService();

    @FXML
    private void initialize() {

        refreshPlayerList();
        refreshStandings();
    }

    @FXML
    private void handleJoin() {

        String name =
                joinField.getText() == null
                        ? ""
                        : joinField.getText()
                        .trim();

        if (name.isEmpty()) {

            showError(
                    "Enter a player name first."
            );

            return;
        }

        if (name.length() > 18) {

            showError(
                    "Keep tournament names at 18 characters or fewer."
            );

            return;
        }

        tournamentService.joinTournament(
                name
        );

        joinField.clear();

        hideError();

        refreshPlayerList();
        refreshStandings();
    }

    @FXML
    private void handleGenerateSchedule() {

        try {

            List<TournamentMatch> schedule =
                    tournamentService
                            .generateSchedule();

            matchesContainer
                    .getChildren()
                    .clear();

            hideError();

            for (TournamentMatch match :
                    schedule) {

                matchesContainer
                        .getChildren()
                        .add(
                                createMatchRow(match)
                        );
            }

        } catch (IllegalStateException ex) {

            showError(
                    ex.getMessage()
            );
        }
    }

    private HBox createMatchRow(
            TournamentMatch match
    ) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER
        );

        row.getStyleClass()
                .add("match-row");

        Label vs =
                new Label(
                        match.getPlayerA()
                                + "  VS  "
                                + match.getPlayerB()
                );

        vs.getStyleClass()
                .add("match-vs");

        vs.setPrefWidth(260);

        Button playerA =
                new Button(
                        "Win: "
                                + match.getPlayerA()
                );

        playerA.getStyleClass()
                .addAll(
                        "mini-button",
                        "mini-button-green"
                );

        Button playerB =
                new Button(
                        "Win: "
                                + match.getPlayerB()
                );

        playerB.getStyleClass()
                .addAll(
                        "mini-button",
                        "mini-button-blue"
                );

        Runnable disableButtons = () -> {

            playerA.setDisable(true);
            playerB.setDisable(true);
        };

        playerA.setOnAction(
                event -> {

                    if (!match.isPlayed()) {

                        tournamentService
                                .recordMatchWinner(
                                        match,
                                        match.getPlayerA()
                                );

                        disableButtons.run();
                        refreshStandings();
                    }
                }
        );

        playerB.setOnAction(
                event -> {

                    if (!match.isPlayed()) {

                        tournamentService
                                .recordMatchWinner(
                                        match,
                                        match.getPlayerB()
                                );

                        disableButtons.run();
                        refreshStandings();
                    }
                }
        );

        row.getChildren().addAll(
                vs,
                playerA,
                playerB
        );

        return row;
    }

    private void refreshPlayerList() {

        playersContainer
                .getChildren()
                .clear();

        List<String> players =
                tournamentService
                        .getRegisteredPlayers();

        if (players.isEmpty()) {

            Label empty =
                    new Label(
                            "No players have joined yet."
                    );

            empty.getStyleClass()
                    .add("muted-label");

            playersContainer
                    .getChildren()
                    .add(empty);

            return;
        }

        for (String name : players) {

            Label label =
                    new Label(
                            "• " + name
                    );

            label.getStyleClass()
                    .add("player-chip");

            playersContainer
                    .getChildren()
                    .add(label);
        }
    }

    private void refreshStandings() {

        standingsContainer
                .getChildren()
                .clear();

        Map<String, Integer> standings =
                tournamentService
                        .getStandings();

        if (standings.isEmpty()) {

            Label empty =
                    new Label(
                            "Standings appear after players join."
                    );

            empty.getStyleClass()
                    .add("muted-label");

            standingsContainer
                    .getChildren()
                    .add(empty);

            return;
        }

        int rank = 1;

        for (Map.Entry<String, Integer>
                entry : standings.entrySet()) {

            Label label =
                    new Label(
                            "#"
                                    + rank++
                                    + "   "
                                    + entry.getKey()
                                    + "   •   "
                                    + entry.getValue()
                                    + " win(s)"
                    );

            label.getStyleClass()
                    .add("standing-row");

            standingsContainer
                    .getChildren()
                    .add(label);
        }
    }

    @FXML
    private void handleReset() {

        tournamentService
                .resetTournament();

        matchesContainer
                .getChildren()
                .clear();

        refreshPlayerList();
        refreshStandings();
        hideError();
    }

    private void showError(
            String message
    ) {

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

        SceneManager.get().switchTo(
                "/fxml/welcome.fxml",
                "Memory Match Showdown"
        );
    }
}