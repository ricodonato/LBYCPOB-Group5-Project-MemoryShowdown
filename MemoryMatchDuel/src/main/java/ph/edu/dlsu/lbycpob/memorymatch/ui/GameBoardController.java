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

// UNDERSTAND: Controls the actual gameplay screen — renders the card grid and score panels, handles
// card clicks, and drives the pause/reveal/re-flip animation timing around GameEngine's turn logic.
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

    // UNDERSTAND: activeTheme()/activeDifficulty() read from either the tournament settings or the
    // regular match settings, depending on which mode is active.
    // DECISION: These were written as small helper methods instead of repeating the isTournamentModeActive()
    // check everywhere they're needed, so the board always renders using the correct settings no matter
    // which mode started the match.
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

    // UNDERSTAND: buildScorePanels() creates one small panel (name, score, round wins) per player and
    // stores a reference to each panel's labels so they can be updated later without rebuilding the UI.
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

    // UNDERSTAND: buildCardGrid() creates one Button per card on the board, sizing them based on
    // difficulty so a HARD board's 20 cards still fit reasonably on screen.
    // DECISION: cardSize/wrapLength were hardcoded per difficulty instead of computed from the card
    // count with a formula, because there are only 3 fixed difficulties — a lookup table was simpler and
    // easier to visually tune than a formula.
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

        // UNDERSTAND: cardFontSize scales down with cardSize instead of relying on the CSS's fixed
        // 32px .card-button font size. cardPadding is pinned to a fixed px value instead of JavaFX's
        // default em-based Button padding, which otherwise grows/shrinks proportionally with font size.
        // DECISION: On MEDIUM/HARD, buttons shrink to 86px/74px, but a 32px emoji glyph (plus border +
        // padding) doesn't fit in a box that small — JavaFX truncates the overflowing Label to an
        // ellipsis ("..."). Scaling the font relative to cardSize fixed this for the resting/matched
        // state, but .card-button-flipped bumps -fx-border-width to 4px (vs the base/matched 3px),
        // eating a few more pixels on every side — enough to push a borderline-sized glyph back into
        // ellipsis while a matched card (still at 3px border) rendered fine. Sizing the font with extra
        // headroom (0.28 instead of 0.34) and using fixed padding keeps every card state — resting,
        // flipped, and matched — comfortably inside the button regardless of border width.
        double cardFontSize = Math.min(30, cardSize * 0.28);
        String cardFontStyle = "-fx-font-size: " + cardFontSize + "px; -fx-padding: 4;";

        for (Card card : engine.getBoardState()) {

            Button button = new Button("?");

            button.getStyleClass().add("card-button");

            button.setPrefSize(cardSize, cardSize);
            button.setMinSize(cardSize, cardSize);
            button.setMaxSize(cardSize, cardSize);

            // Inline style overrides the CSS's fixed font-size per button so it scales with cardSize.
            button.setStyle(cardFontStyle);

            button.setOnAction(
                    event -> handleCardClick(card.getId())
            );

            cardButtons.put(card.getId(), button);
            cardGrid.getChildren().add(button);
        }
    }

    // UNDERSTAND: handleCardClick() flips the clicked card via the engine, updates that one button's
    // look, and once exactly two unresolved cards are face up, pauses briefly (so the player can see
    // both cards) before calling resolveMatchCheck().
    // DECISION: A PauseTransition delay was added before resolving the match instead of calling
    // checkMatch() immediately, so the player actually gets to see both flipped cards before a mismatch
    // flips them back down — resolving instantly would make mismatches invisible.
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

    // UNDERSTAND: resolveMatchCheck() actually calls the engine's checkMatch(), refreshes the visuals
    // and turn label, then — if the round just ended — pauses again before navigating to the results
    // screen.
    // DECISION: A second short pause was added before switching to results, instead of navigating
    // immediately, so the player briefly sees the final matched card on the board rather than the screen
    // jumping away the instant the last pair is found.
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

    // UNDERSTAND: refreshBoardVisuals() loops through every card and syncs each button's text/style to
    // match the card's current state (matched, face up, or hidden).
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

    // UNDERSTAND: refreshTurnLabel() clears the "current-turn" highlight from every score panel, then
    // re-adds it to whichever player's turn it currently is, and updates the turn/combo text.
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

    // UNDERSTAND: A small private record used purely to bundle a player's panel + its score/wins labels
    // together, so they can all be looked up from one map entry.
    // DECISION: A record was used instead of a full class with getters/constructor written out by hand,
    // since this is just a simple, immutable data holder with no extra behavior.
    private record PlayerPanel(
            VBox container,
            Label scoreLabel,
            Label winsLabel
    ) {}
}