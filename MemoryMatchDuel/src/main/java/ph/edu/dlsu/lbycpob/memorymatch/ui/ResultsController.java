package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class NameEntryController {

    @FXML
    private TextField player1Field;

    @FXML
    private TextField player2Field;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {

        List<String> current =
                SceneManager.get()
                        .getPendingPlayerNames();

        if (current.size() >= 2) {

            player1Field.setText(
                    current.get(0)
            );

            player2Field.setText(
                    current.get(1)
            );
        }
    }

    @FXML
    private void handleNext() {

        String p1 =
                clean(player1Field.getText());

        String p2 =
                clean(player2Field.getText());

        if (p1.isEmpty() || p2.isEmpty()) {

            showError(
                    "Enter a name for both players."
            );

            return;
        }

        if (p1.equalsIgnoreCase(p2)) {

            showError(
                    "Player names must be different."
            );

            return;
        }

        if (p1.length() > 18
                || p2.length() > 18) {

            showError(
                    "Keep each player name at 18 characters or fewer."
            );

            return;
        }

        errorLabel.setVisible(false);

        SceneManager.get()
                .setPendingPlayerNames(
                        List.of(p1, p2)
                );

        SceneManager.get().switchTo(
                "/fxml/setup.fxml",
                "Memory Match Showdown - Setup"
        );
    }

    private String clean(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    private void showError(String message) {

        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    @FXML
    private void handleBack() {

        SceneManager.get().switchTo(
                "/fxml/welcome.fxml",
                "Memory Match Showdown"
        );
    }
}