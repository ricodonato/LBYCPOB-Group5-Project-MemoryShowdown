package ph.edu.dlsu.lbycpob.memorymatch.ui;


import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import ph.edu.dlsu.lbycpob.memorymatch.model.Difficulty;
import ph.edu.dlsu.lbycpob.memorymatch.model.Theme;
import ph.edu.dlsu.lbycpob.memorymatch.model.TournamentMatch;

import ph.edu.dlsu.lbycpob.memorymatch.service.GameEngine;
import ph.edu.dlsu.lbycpob.memorymatch.service.GameEngineImpl;
import ph.edu.dlsu.lbycpob.memorymatch.service.LeaderboardService;
import ph.edu.dlsu.lbycpob.memorymatch.service.TournamentService;
import ph.edu.dlsu.lbycpob.memorymatch.service.TournamentServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// UNDERSTAND: A singleton class that holds shared state across every screen (which engine/services to
// use, pending player names/settings, current round number) and handles switching from one FXML screen
// to another.
// DECISION: A singleton (private constructor + static get()) was used instead of passing this object
// around through every controller's constructor, because JavaFX creates controller instances itself via
// FXMLLoader — there's no clean way to inject dependencies into them manually, so a single globally
// reachable instance was the simplest way to share state between screens.
public class SceneManager {
    private boolean tournamentModeActive = false;
    private Difficulty tournamentDifficulty = Difficulty.EASY;
    private Theme tournamentTheme = Theme.ANIMALS;
    private int tournamentBestOf = 3;
    private TournamentMatch activeTournamentMatch;

    public boolean isTournamentModeActive() { return tournamentModeActive; }
    public void setTournamentModeActive(boolean active) { this.tournamentModeActive = active; }

    public Difficulty getTournamentDifficulty() { return tournamentDifficulty; }
    public void setTournamentDifficulty(Difficulty difficulty) { this.tournamentDifficulty = difficulty; }

    public Theme getTournamentTheme() { return tournamentTheme; }
    public void setTournamentTheme(Theme theme) { this.tournamentTheme = theme; }

    public int getTournamentBestOf() { return tournamentBestOf; }
    public void setTournamentBestOf(int bestOf) { this.tournamentBestOf = bestOf; }

    public TournamentMatch getActiveTournamentMatch() { return activeTournamentMatch; }
    public void setActiveTournamentMatch(TournamentMatch match) { this.activeTournamentMatch = match; }

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

    // UNDERSTAND: The private constructor creates the real GameEngineImpl and TournamentServiceImpl
    // right away, but leaves leaderboardService as null until MainApp wires up the real Spring bean.
    private SceneManager() {

        this.gameEngine =
                new GameEngineImpl();

        this.tournamentService =
                new TournamentServiceImpl();

        this.leaderboardService = null;
    }

    // UNDERSTAND: get() returns the one shared SceneManager, creating it the first time it's called.
    // DECISION: Lazy initialization (only creating the instance the first time get() is called) was used
    // instead of eagerly creating it as a static field, so GameEngineImpl/TournamentServiceImpl aren't
    // constructed until the app actually needs them.
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

    // UNDERSTAND: switchTo() loads a new FXML file, builds a Scene sized to the screen's visual bounds,
    // attaches the shared stylesheet if it exists, and swaps it into the app's single Stage.
    // DECISION: The scene is resized to Screen.getPrimary().getVisualBounds() every time instead of
    // using a fixed size, so the app fills the user's actual screen resolution rather than looking too
    // small or too large on different monitors.
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