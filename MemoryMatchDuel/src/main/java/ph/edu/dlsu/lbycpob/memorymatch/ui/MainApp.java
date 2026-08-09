package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ph.edu.dlsu.lbycpob.memorymatch.MemoryShowdownApplication;
import ph.edu.dlsu.lbycpob.memorymatch.service.LeaderboardService;

/**
 * JavaFX entry point for
 * Memory Match Showdown.
 */
public class MainApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {

        // Runs before the UI, off the JavaFX Application Thread —
        // safe place for the slower DB-connecting work Spring does.
        springContext = SpringApplication.run(MemoryShowdownApplication.class);

        LeaderboardService leaderboardService =
                springContext.getBean(LeaderboardService.class);

        SceneManager.get().setLeaderboardService(leaderboardService);
    }

    @Override
    public void start(Stage primaryStage) {

        SceneManager.get().init(primaryStage);

        SceneManager.get().switchTo(
                "/fxml/welcome.fxml",
                "Memory Match Showdown"
        );
    }

    @Override
    public void stop() {

        // Closes the DB connection pool cleanly on app exit.
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}