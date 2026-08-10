package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXML;

// UNDERSTAND: Controls the very first screen — three buttons that route the player to Play,
// Leaderboard, or Tournament.
// DECISION: setTournamentModeActive(false) is explicitly called in handlePlay() instead of assuming the
// flag defaults correctly, so a normal 1v1 match can't accidentally inherit "tournament mode" left on
// from a previous session.
public class WelcomeController extends BaseScreenController {

    @Override
    protected void onScreenReady() {
        // Nothing to set up when the Welcome screen loads.
    }

    @FXML
    private void handlePlay() {

        SceneManager.get().setTournamentModeActive(false);

        SceneManager.get().switchTo(
                "/fxml/name_entry.fxml",
                "Memory Match Showdown - Players"
        );
    }

    @FXML
    private void handleLeaderboard() {

        SceneManager.get().switchTo(
                "/fxml/leaderboard.fxml",
                "Memory Match Showdown - Leaderboard"
        );
    }

    @FXML
    private void handleTournament() {

        SceneManager.get().switchTo(
                "/fxml/tournament.fxml",
                "Memory Match Showdown - Tournament"
        );
    }
}