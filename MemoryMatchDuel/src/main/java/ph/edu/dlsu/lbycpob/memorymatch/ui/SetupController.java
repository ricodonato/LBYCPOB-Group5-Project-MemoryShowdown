package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

import ph.edu.dlsu.lbycpob.memorymatch.model.Difficulty;
import ph.edu.dlsu.lbycpob.memorymatch.model.Theme;

// UNDERSTAND: Handles the screen where difficulty, theme, and best-of length are chosen before a match
// starts, using toggle buttons instead of dropdowns.
public class SetupController extends BaseScreenController {

    @FXML
    private ToggleButton easyToggle;

    @FXML
    private ToggleButton mediumToggle;

    @FXML
    private ToggleButton hardToggle;

    @FXML
    private ToggleButton animalsToggle;

    @FXML
    private ToggleButton sportsToggle;

    @FXML
    private ToggleButton foodToggle;

    @FXML
    private ToggleButton emojisToggle;

    @FXML
    private ToggleButton bestOf1Toggle;

    @FXML
    private ToggleButton bestOf3Toggle;

    @FXML
    private ToggleButton bestOf5Toggle;

    @FXML
    private Label playersLabel;

    // UNDERSTAND: onScreenReady() shows a different heading depending on whether this setup screen was
    // reached from a normal match or from a tournament bracket match.
    @Override
    protected void onScreenReady() {
        if (SceneManager.get().isTournamentModeActive()) {
            playersLabel.setText("Choose the match settings for this tournament.");
        } else {
            playersLabel.setText(
                    String.join("  VS  ", SceneManager.get().getPendingPlayerNames())
            );
        }
    }

    // UNDERSTAND: handleStart() reads which toggle is selected for difficulty, theme, and best-of,
    // defaulting to EASY / ANIMALS / best-of-3 if nothing was picked, then either starts a tournament
    // bracket match or a regular match depending on the current mode.
    // DECISION: Each toggle group was checked with a series of if-statements instead of a switch on an
    // enum, since ToggleButtons don't carry their own enum value — this was the simplest way to map a
    // selected button back to a Difficulty/Theme/int.
    @FXML
    private void handleStart() {

        Difficulty difficulty = Difficulty.EASY;
        if (mediumToggle.isSelected()) difficulty = Difficulty.MEDIUM;
        if (hardToggle.isSelected()) difficulty = Difficulty.HARD;

        Theme theme = Theme.ANIMALS;
        if (sportsToggle.isSelected()) theme = Theme.SPORTS;
        if (foodToggle.isSelected()) theme = Theme.FOOD;
        if (emojisToggle.isSelected()) theme = Theme.EMOJIS;

        int bestOf = 3;
        if (bestOf1Toggle.isSelected()) bestOf = 1;
        if (bestOf5Toggle.isSelected()) bestOf = 5;

        SceneManager manager = SceneManager.get();

        if (manager.isTournamentModeActive()) {
            manager.setTournamentDifficulty(difficulty);
            manager.setTournamentTheme(theme);
            manager.setTournamentBestOf(bestOf);

            manager.getTournamentService().generateBracket();

            manager.switchTo("/fxml/tournament.fxml", "Memory Match Showdown - Tournament");
            return;
        }

        manager.setPendingDifficulty(difficulty);
        manager.setPendingTheme(theme);
        manager.setPendingBestOf(bestOf);
        manager.resetRoundNumber();

        manager.getGameEngine().startNewMatch(
                difficulty, theme, manager.getPendingPlayerNames(), bestOf
        );

        manager.switchTo("/fxml/game_board.fxml", "Memory Match Showdown - Game");
    }

    @FXML
    private void handleBack() {

        SceneManager.get().switchTo(
                "/fxml/name_entry.fxml",
                "Memory Match Showdown - Players"
        );
    }
}