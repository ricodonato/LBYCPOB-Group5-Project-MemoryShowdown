package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Shared base class for every JavaFX screen controller.
 *
 * Every controller in this app was independently repeating two things:
 *   1. Navigating back to the Welcome screen the same way.
 *   2. Showing and hiding an inline error Label the same way.
 *
 * This class pulls both up into one place. It also uses the Template
 * Method pattern for screen setup: implementing JavaFX's Initializable
 * interface here means initialize() always runs first and always
 * delegates to onScreenReady(), which every subclass is forced to
 * provide its own version of. Implementing Initializable at this level
 * (instead of relying on JavaFX's "magic method name" convention) means
 * FXMLLoader will find and call it correctly no matter how deep the
 * subclass sits in the hierarchy.
 */
public abstract class BaseScreenController implements Initializable {

    protected final SceneManager scene = SceneManager.get();

    @Override
    public final void initialize(URL location, ResourceBundle resources) {
        onScreenReady();
    }

    /**
     * Called once the FXML fields for this screen have been injected.
     * Each screen implements this with whatever setup it needs.
     * A screen with nothing to set up (like WelcomeController) can
     * leave the method body empty.
     */
    protected abstract void onScreenReady();

    /** Common "go back to the main menu" navigation used by several screens. */
    protected void goToWelcome() {
        scene.switchTo("/fxml/welcome.fxml", "Memory Match Showdown");
    }

    /** Shared inline-error display, used by any screen that has an error Label. */
    protected void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /** Shared inline-error hiding, paired with showError above. */
    protected void hideError(Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
