package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import ph.edu.dlsu.lbycpob.memorymatch.model.Difficulty;
import ph.edu.dlsu.lbycpob.memorymatch.model.Theme;

import ph.edu.dlsu.lbycpob.memorymatch.service.GameEngine;
import ph.edu.dlsu.lbycpob.memorymatch.service.GameEngineImpl;
import ph.edu.dlsu.lbycpob.memorymatch.service.LeaderboardService;
import ph.edu.dlsu.lbycpob.memorymatch.service.TournamentService;
import ph.edu.dlsu.lbycpob.memorymatch.service.TournamentServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UI-owned application navigator
 * and shared UI state.
 *
 * The UI uses the real Group 5
 * GameEngineImpl so the screens
 * reflect the current best-of-series
 * game logic.
 *
 * LeaderboardService stays nullable
 * until the backend is connected to
 * the JavaFX lifecycle.
 */
public class SceneManager {

    private static SceneManager instance;

    private Stage stage;

    private final GameEngine gameEngine;
    private final TournamentService tournamentService;

    private LeaderboardService leaderboardService;

    private List<String> pendingPlayerNames =
            new ArrayList<>();

    private Difficulty pendingDifficulty =
            Difficulty.EASY;

    private Theme pendingTheme =
            Theme.ANIMALS;

    private int pendingBestOf = 3;
    private int currentRoundNumber = 1;

    private SceneManager() {

        this.gameEngine =
                new GameEngineImpl();

        this.tournamentService =
                new TournamentServiceImpl();

        this.leaderboardService = null;
    }

    public static SceneManager get() {

        if (instance == null) {
            instance = new SceneManager();
        }

        return instance;
    }

    public void init(Stage stage) {

        this.stage = stage;

        stage.setMinWidth(1000);
        stage.setMinHeight(700);
    }

    public void switchTo(
            String fxmlPath,
            String title
    ) {

        if (stage == null) {
            throw new IllegalStateException(
                    "SceneManager has not been initialized with a Stage."
            );
        }

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            SceneManager.class
                                    .getResource(fxmlPath)
                    );

            Parent root =
                    loader.load();

            Rectangle2D bounds =
                    Screen.getPrimary()
                            .getVisualBounds();

            double width =
                    Math.max(
                            1000,
                            bounds.getWidth()
                    );

            double height =
                    Math.max(
                            700,
                            bounds.getHeight()
                    );

            Scene scene =
                    new Scene(
                            root,
                            width,
                            height
                    );

            var css =
                    SceneManager.class
                            .getResource(
                                    "/css/cartoon-theme.css"
                            );

            if (css != null) {
                scene.getStylesheets()
                        .add(
                                css.toExternalForm()
                        );
            }

            stage.setTitle(title);
            stage.setScene(scene);

            stage.setX(
                    bounds.getMinX()
            );

            stage.setY(
                    bounds.getMinY()
            );

            stage.setWidth(
                    bounds.getWidth()
            );

            stage.setHeight(
                    bounds.getHeight()
            );

            stage.show();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load screen: "
                            + fxmlPath,
                    e
            );
        }
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public TournamentService
    getTournamentService() {
        return tournamentService;
    }

    public LeaderboardService
    getLeaderboardService() {
        return leaderboardService;
    }

    /**
     * Backend teammate can call this
     * once a Spring-managed service
     * becomes available.
     */
    public void setLeaderboardService(
            LeaderboardService leaderboardService
    ) {
        this.leaderboardService =
                leaderboardService;
    }

    public List<String>
    getPendingPlayerNames() {

        return pendingPlayerNames;
    }

    public void setPendingPlayerNames(
            List<String> names
    ) {

        this.pendingPlayerNames =
                new ArrayList<>(names);
    }

    public Difficulty
    getPendingDifficulty() {

        return pendingDifficulty;
    }

    public void setPendingDifficulty(
            Difficulty difficulty
    ) {

        this.pendingDifficulty =
                difficulty;
    }

    public Theme getPendingTheme() {
        return pendingTheme;
    }

    public void setPendingTheme(
            Theme theme
    ) {

        this.pendingTheme =
                theme;
    }

    public int getPendingBestOf() {
        return pendingBestOf;
    }

    public void setPendingBestOf(
            int pendingBestOf
    ) {

        this.pendingBestOf =
                pendingBestOf;
    }

    public int getCurrentRoundNumber() {
        return currentRoundNumber;
    }

    public void resetRoundNumber() {
        currentRoundNumber = 1;
    }

    public void advanceRoundNumber() {
        currentRoundNumber++;
    }
}