package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX entry point for
 * Memory Match Showdown.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        SceneManager.get().init(primaryStage);

        SceneManager.get().switchTo(
                "/fxml/welcome.fxml",
                "Memory Match Showdown"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}