package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ph.edu.dlsu.lbycpob.memorymatch.model.Card;
import ph.edu.dlsu.lbycpob.memorymatch.model.Difficulty;
import ph.edu.dlsu.lbycpob.memorymatch.model.Player;
import ph.edu.dlsu.lbycpob.memorymatch.model.Theme;
import ph.edu.dlsu.lbycpob.memorymatch.service.GameEngine;

import java.util.HashMap;
import java.util.Map;

public class GameBoardController extends BaseScreenController {

    @FXML private FlowPane cardGrid;
    @FXML private HBox scorePanelContainer;
    @FXML private Label turnLabel;
    @FXML private Label roundLabel;
    @FXML private Label matchRuleLabel;

    private final GameEngine engine = SceneManager.get().getGameEngine();
    private final Map<Integer, Button> cardButtons = new HashMap<>();
    private final Map<String, PlayerPanel> scorePanels = new HashMap<>();
    private boolean awaitingResolve;

    private Theme activeTheme() {
        return SceneManager.get().isTournamentModeActive()
                ? SceneManager.get().getTournamentTheme()
                : SceneManager.get().getPendingTheme();
    }

    private Difficulty activeDifficulty() {
        return SceneManager.get().isTournamentModeActive()
                ? SceneManager.get().getTournamentDifficulty()
                : SceneManager.get().getPendingDifficulty();
    }

    @Override
    protected void onScreenReady() {
        buildScorePanels();
        buildCardGrid();
        refreshHeader();
        refreshTurnLabel();
    }

    private void buildScorePanels() {
        scorePanelContainer.getChildren().clear();
        scorePanels.clear();

        for (Player player : engine.getPlayers()) {
            Label name = new Label(player.getName());
            name.getStyleClass().add("score-name");

            Label score = new Label("Score  " + player.getScore());
            score.getStyleClass().add("score-value");

            Label wins = new Label("Round wins  " + player.getRoundsWon());
            wins.getStyleClass().add("score-rounds");

            VBox panel = new VBox(4, name, score, wins);
            panel.setAlignment(Pos.CENTER);
            panel.getStyleClass().add("score-panel");

            scorePanels.put(
                    player.getName(),
                    new PlayerPanel(panel, score, wins)
            );

            scorePanelContainer.getChildren().add(panel);
        }
    }

    private void buildCardGrid() {
        cardGrid.getChildren().clear();
        cardButtons.clear();

        Difficulty difficulty = activeDifficulty();

        double cardSize;
        double wrapLength;

        if (difficulty == Difficulty.EASY) {
            cardSize = 105;
            wrapLength = 520;
        } else if (difficulty == Difficulty.MEDIUM) {
            cardSize = 86;
            wrapLength = 600;
        } else {
            cardSize = 74;
            wrapLength = 660;
        }

        cardGrid.setPrefWrapLength(wrapLength);
        cardGrid.setMaxWidth(wrapLength + 60);

        for (Card card : engine.getBoardState()) {

            Button button = new Button("?");

            button.getStyleClass().add("card-button");

            button.setPrefSize(cardSize, cardSize);
            button.setMinSize(cardSize, cardSize);
            button.setMaxSize(cardSize, cardSize);

            button.setOnAction(
                    event -> handleCardClick(card.getId())
            );

            cardButtons.put(card.getId(), button);
            cardGrid.getChildren().add(button);
        }
    }

    private void handleCardClick(int cardId) {

        if (awaitingResolve || engine.isRoundOver()) {
            return;
        }

        Card card = engine.flipCard(cardId);

        if (card == null || !card.isFaceUp()) {
            return;
        }

        Button button = cardButtons.get(cardId);

        button.setText(card.getPairKey());

        if (!button.getStyleClass()
                .contains("card-button-flipped")) {

            button.getStyleClass()
                    .add("card-button-flipped");
        }

        /*
         * Matched cards stay face-up in the model.
         *
         * Therefore, only unresolved face-up cards
         * should be counted before calling checkMatch().
         */
        long unresolvedFaceUp =
                engine.getBoardState()
                        .stream()
                        .filter(c ->
                                c.isFaceUp()
                                        && !c.isMatched())
                        .count();

        if (unresolvedFaceUp == 2) {

            awaitingResolve = true;

            PauseTransition pause =
                    new PauseTransition(
                            Duration.millis(650)
                    );

            pause.setOnFinished(
                    event -> resolveMatchCheck()
            );

            pause.play();
        }
    }

    private void resolveMatchCheck() {

        engine.checkMatch();

        refreshBoardVisuals();
        refreshScorePanels();
        refreshTurnLabel();

        awaitingResolve = false;

        if (engine.isRoundOver()) {

            PauseTransition endPause =
                    new PauseTransition(
                            Duration.millis(450)
                    );

            endPause.setOnFinished(
                    event ->
                            SceneManager.get()
                                    .switchTo(
                                            "/fxml/results.fxml",
                                            "Memory Match Showdown - Results"
                                    )
            );

            endPause.play();
        }
    }

    private void refreshBoardVisuals() {

        for (Card card : engine.getBoardState()) {

            Button button =
                    cardButtons.get(card.getId());

            button.getStyleClass().removeAll(
                    "card-button-flipped",
                    "card-button-matched"
            );

            if (card.isMatched()) {

                button.setText(card.getPairKey());

                button.getStyleClass()
                        .add("card-button-matched");

                button.setDisable(true);

            } else if (card.isFaceUp()) {

                button.setText(card.getPairKey());

                button.getStyleClass()
                        .add("card-button-flipped");

            } else {

                button.setText("?");
                button.setDisable(false);
            }
        }
    }

    private void refreshScorePanels() {

        for (Player player : engine.getPlayers()) {

            PlayerPanel info =
                    scorePanels.get(player.getName());

            if (info != null) {

                info.scoreLabel.setText(
                        "Score  " + player.getScore()
                );

                info.winsLabel.setText(
                        "Round wins  "
                                + player.getRoundsWon()
                );
            }
        }
    }

    private void refreshHeader() {

        int firstTo = engine.getRoundsToWin();

        roundLabel.setText(
                "ROUND "
                        + SceneManager.get()
                        .getCurrentRoundNumber()
        );

        matchRuleLabel.setText(
                "First to "
                        + firstTo
                        + " round win"
                        + (firstTo == 1 ? "" : "s")
                        + "  |  "
                        + activeDifficulty().name()
                        + "  |  "
                        + activeTheme().name()
        );
    }

    private void refreshTurnLabel() {

        for (PlayerPanel panel :
                scorePanels.values()) {

            panel.container
                    .getStyleClass()
                    .remove("current-turn");
        }

        Player current =
                engine.getCurrentPlayer();

        turnLabel.setText(
                current.getName()
                        + "'s turn   •   Combo x"
                        + current.getCurrentCombo()
        );

        PlayerPanel panel =
                scorePanels.get(current.getName());

        if (panel != null) {
            panel.container
                    .getStyleClass()
                    .add("current-turn");
        }
    }

    @FXML
    private void handleQuitMatch() {

        SceneManager.get().setTournamentModeActive(false);
        SceneManager.get().setActiveTournamentMatch(null);

        goToWelcome();
    }

    private record PlayerPanel(
            VBox container,
            Label scoreLabel,
            Label winsLabel
    ) {}
}