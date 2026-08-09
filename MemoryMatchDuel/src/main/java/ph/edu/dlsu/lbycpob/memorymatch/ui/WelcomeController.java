package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXML;

public class WelcomeController {

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