package ph.edu.dlsu.lbycpob.memorymatch.ui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;
import ph.edu.dlsu.lbycpob.memorymatch.service.LeaderboardService;

import java.util.List;

public class LeaderboardController extends BaseScreenController {

    @FXML
    private VBox rowsContainer;

    @FXML
    private Label connectionLabel;

    @Override
    protected void onScreenReady() {

        rowsContainer.getChildren().clear();

        LeaderboardService service =
                SceneManager.get()
                        .getLeaderboardService();

        if (service == null) {

            connectionLabel.setText(
                    "Leaderboard backend is not connected on this branch yet."
            );

            connectionLabel.setVisible(true);
            connectionLabel.setManaged(true);

            return;
        }

        connectionLabel.setVisible(false);
        connectionLabel.setManaged(false);

        List<PlayerEntity> topPlayers =
                service.getTopPlayers(10);

        if (topPlayers.isEmpty()) {

            rowsContainer.getChildren()
                    .add(
                            new Label(
                                    "No recorded matches yet."
                            )
                    );

            return;
        }

        int rank = 1;

        for (PlayerEntity player :
                topPlayers) {

            HBox row =
                    new HBox(20);

            row.setAlignment(
                    Pos.CENTER_LEFT
            );

            row.getStyleClass()
                    .add(
                            "leaderboard-row"
                    );

            Label rankLabel =
                    new Label(
                            "#" + rank++
                    );

            rankLabel.getStyleClass()
                    .add(
                            "leaderboard-rank"
                    );

            Label nameLabel =
                    new Label(
                            player.getUsername()
                    );

            nameLabel.getStyleClass()
                    .add(
                            "leaderboard-name"
                    );

            nameLabel.setPrefWidth(180);

            Label stats =
                    new Label(
                            "Best "
                                    + player.getHighestScore()
                                    + "   •   "
                                    + player.getTotalWins()
                                    + " wins   •   "
                                    + player.getTotalMatches()
                                    + " matches"
                    );

            stats.getStyleClass()
                    .add(
                            "leaderboard-score"
                    );

            row.getChildren().addAll(
                    rankLabel,
                    nameLabel,
                    stats
            );

            rowsContainer
                    .getChildren()
                    .add(row);
        }
    }

    @FXML
    private void handleBack() {
        goToWelcome();
    }
}
