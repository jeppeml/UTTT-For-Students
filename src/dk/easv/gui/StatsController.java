package dk.easv.gui;

import javafx.scene.control.ListView;
import dk.easv.bll.game.stats.GameResult;
import dk.easv.bll.game.stats.GameResult.Winner;
import dk.easv.gui.util.FontAwesomeHelper;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StatsController implements Initializable {

    @FXML private Label lblP0Name;
    @FXML private Label lblP1Name;
    @FXML private Label lblP0Stats;
    @FXML private Label lblP1Stats;
    @FXML private ListView<ActiveGame> listActiveGames;
    @FXML private ListView<GameResult> listP0Wins;
    @FXML private ListView<GameResult> listP1Wins;
    @FXML private ProgressBar progressBar;
    @FXML private HBox warningBanner;

    private StatsModel statsModel;
    private final ObservableList<GameResult> p0WinResults = FXCollections.observableArrayList();
    private final ObservableList<GameResult> p1WinResults = FXCollections.observableArrayList();
    private final String[] allPlayerStyles = {"playerTIE", "player0", "player1"};

    private String participantA;
    private String participantB;
    private int aWins, bWins, tieCount, totalGames;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listP0Wins.setItems(p0WinResults);
        listP1Wins.setItems(p1WinResults);
        listP0Wins.setCellFactory(p -> new WinResultCell());
        listP1Wins.setCellFactory(p -> new WinResultCell());
        lblP0Name.getStyleClass().add("player0");
        lblP1Name.getStyleClass().add("player1");
    }

    public void setStatsModel(StatsModel statsModel, Stage stage) {
        this.statsModel = statsModel;
        stage.titleProperty().bind(statsModel.lastSimulationResultsProperty());

        // Bind player names from model
        lblP0Name.textProperty().bind(statsModel.player0NameProperty());
        lblP1Name.textProperty().bind(statsModel.player1NameProperty());

        // Bind progress bar to simulating state
        progressBar.visibleProperty().bind(statsModel.simulatingProperty());
        progressBar.setProgress(-1); // indeterminate

        // Bind active games list
        listActiveGames.setItems(statsModel.getActiveGames());
        listActiveGames.setCellFactory(p -> new ActiveGameCell());

        for (GameResult result : statsModel.getGameResults()) {
            handleNewResult(result);
        }
        updateLabels();

        statsModel.getGameResults().addListener((ListChangeListener<GameResult>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    p0WinResults.clear();
                    p1WinResults.clear();
                    resetCounters();
                }
                if (change.wasAdded()) {
                    for (GameResult result : change.getAddedSubList()) {
                        handleNewResult(result);
                    }
                }
            }
            updateLabels();
        });
    }

    private String getWinnerName(GameResult result) {
        if (result.getWinner() == Winner.player0) return result.getPlayer0();
        if (result.getWinner() == Winner.player1) return result.getPlayer1();
        return null;
    }

    private void handleNewResult(GameResult result) {
        if (participantA == null) {
            participantA = result.getPlayer0();
            participantB = result.getPlayer1();
        }

        totalGames++;
        String winner = getWinnerName(result);
        if (winner == null) {
            tieCount++;
        } else if (winner.equals(participantA)) {
            aWins++;
            p0WinResults.add(result);
        } else if (winner.equals(participantB)) {
            bWins++;
            p1WinResults.add(result);
        }
    }

    private void updateLabels() {
        if (totalGames > 0) {
            lblP0Stats.setText(String.format("%dW / %dL / %dT  (%.1f%%)",
                    aWins, bWins, tieCount, aWins * 100.0 / totalGames));
            lblP1Stats.setText(String.format("%dW / %dL / %dT  (%.1f%%)",
                    bWins, aWins, tieCount, bWins * 100.0 / totalGames));
        } else {
            lblP0Stats.setText("");
            lblP1Stats.setText("");
        }
    }

    private void resetCounters() {
        participantA = null;
        participantB = null;
        aWins = 0;
        bWins = 0;
        tieCount = 0;
        totalGames = 0;
    }

    @FXML
    private void clickClearList(ActionEvent event) {
        statsModel.clear();
    }

    @FXML
    private void clickDismissWarning(ActionEvent event) {
        warningBanner.setVisible(false);
        warningBanner.setManaged(false);
    }

    private class ActiveGameCell extends ListCell<ActiveGame> {
        @Override
        protected void updateItem(ActiveGame item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty && item != null) {
                int turn = item.currentPlayerProperty().get();
                String p0 = item.getPlayer0Name();
                String p1 = item.getPlayer1Name();
                String arrow = "\u25B6 "; // ▶
                String text = turn == 0
                        ? arrow + p0 + "  vs  " + p1
                        : p0 + "  vs  " + arrow + p1;
                setText(text + "   " + item.macroDisplayProperty().get());
            } else {
                setText(null);
            }
        }
    }

    private class WinResultCell extends ListCell<GameResult> {
        @Override
        protected void updateItem(GameResult item, boolean empty) {
            super.updateItem(item, empty);
            this.getStyleClass().removeAll(allPlayerStyles);
            if (!empty && item != null) {
                Node icon;
                String styleClass;
                if (item.getWinner() == Winner.player0) {
                    icon = FontAwesomeHelper.getFontAwesomeIconFromPlayerId("0");
                    styleClass = "player0";
                } else {
                    icon = FontAwesomeHelper.getFontAwesomeIconFromPlayerId("1");
                    styleClass = "player1";
                }
                this.setGraphic(icon);
                this.getStyleClass().add(styleClass);
                this.getStyleClass().add("stat-items");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                this.setText(item.getDate().format(dtf) + "  " +
                        item.getPlayer0() + " vs " + item.getPlayer1());
                this.setContentDisplay(ContentDisplay.RIGHT);
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
