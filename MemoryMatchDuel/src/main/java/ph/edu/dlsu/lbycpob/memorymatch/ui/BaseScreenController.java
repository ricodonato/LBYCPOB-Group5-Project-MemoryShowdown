package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

// UNDERSTAND: An abstract parent class every screen controller extends, so shared behavior (navigation,
// error display) lives in one place instead of being copy-pasted into each screen.
// DECISION: initialize() was marked final and made to always call onScreenReady() (Template Method
// pattern) instead of letting each subclass override initialize() directly, so every screen is forced
// to go through the same setup entry point and can't accidentally skip it.
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