package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

// UNDERSTAND: Handles the screen where the two players type in their names before a regular (non
// -tournament) match — validates the names and passes them to SceneManager for the next screen.
public class NameEntryController extends BaseScreenController {

    @FXML
    private TextField player1Field;

    @FXML
    private TextField player2Field;

    @FXML
    private Label errorLabel;

    // UNDERSTAND: onScreenReady() pre-fills the text fields with any names already entered, so going
    // "Back" from the setup screen doesn't wipe what the player typed.
    @Override
    protected void onScreenReady() {

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

    // UNDERSTAND: handleNext() runs three validation checks in order — both names filled in, names not
    // identical, and names not too long — showing the first error found and stopping there.
    // DECISION: Each check returns immediately on failure instead of collecting all errors at once,
    // because only one errorLabel exists on screen — there's nowhere to show multiple messages at the
    // same time.
    @FXML
    private void handleNext() {

        String p1 =
                clean(player1Field.getText());

        String p2 =
                clean(player2Field.getText());

        if (p1.isEmpty() || p2.isEmpty()) {

            showError(
                    errorLabel,
                    "Enter a name for both players."
            );

            return;
        }

        if (p1.equalsIgnoreCase(p2)) {

            showError(
                    errorLabel,
                    "Player names must be different."
            );

            return;
        }

        if (p1.length() > 18
                || p2.length() > 18) {

            showError(
                    errorLabel,
                    "Keep each player name at 18 characters or fewer."
            );

            return;
        }

        hideError(errorLabel);

        SceneManager.get()
                .setPendingPlayerNames(
                        List.of(p1, p2)
                );

        SceneManager.get().switchTo(
                "/fxml/setup.fxml",
                "Memory Match Showdown - Setup"
        );
    }

    // UNDERSTAND: clean() turns a null text field value into an empty string and trims whitespace, so
    // the rest of the validation never has to null-check.
    private String clean(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    @FXML
    private void handleBack() {
        goToWelcome();
    }
}