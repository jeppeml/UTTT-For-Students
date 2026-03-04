package dk.easv.gui;

import javafx.scene.control.ListView;
import dk.easv.bll.game.stats.GameResult;
import dk.easv.bll.game.stats.GameResult.Winner;
import dk.easv.gui.util.FontAwesomeHelper;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StatsController implements Initializable {

    @FXML private Label lblP0Name;
    @FXML private Label lblP1Name;
    @FXML private Label lblP0Stats;
    @FXML private Label lblP1Stats;
    @FXML private Label lblCurrentGames;
    @FXML private FlowPane flowActiveGames;
    @FXML private ListView<GameResult> listP0Wins;
    @FXML private ListView<GameResult> listP1Wins;
    @FXML private ProgressBar progressBar;
    @FXML private HBox warningBanner;

    private final Map<ActiveGame, Node> cardMap = new HashMap<>();

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

        // Active games FlowPane: add/remove cards when list changes
        for (ActiveGame game : statsModel.getActiveGames()) {
            Node card = createCard(game);
            cardMap.put(game, card);
            flowActiveGames.getChildren().add(card);
        }
        statsModel.getActiveGames().addListener((ListChangeListener<ActiveGame>) change -> {
            while (change.next()) {
                for (ActiveGame game : change.getRemoved()) {
                    Node card = cardMap.remove(game);
                    if (card != null) flowActiveGames.getChildren().remove(card);
                }
                for (ActiveGame game : change.getAddedSubList()) {
                    Node card = createCard(game);
                    cardMap.put(game, card);
                    flowActiveGames.getChildren().add(card);
                }
            }
        });

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

    private static final String[] CELL_STYLES = {"macro-cell-p0", "macro-cell-p1", "macro-cell-tie", "macro-cell-open"};

    private Node createCard(ActiveGame game) {
        VBox card = new VBox(2);
        card.getStyleClass().add("active-game-card");
        card.setAlignment(Pos.CENTER);

        Label nameTop = new Label(game.getPlayer0Name());
        nameTop.getStyleClass().add("card-name-p0");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("macro-grid");
        grid.setHgap(2);
        grid.setVgap(2);
        Region[][] cells = new Region[3][3];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Region cell = new Region();
                cell.getStyleClass().add("macro-cell");
                cell.setPrefSize(18, 18);
                cell.setMinSize(18, 18);
                cells[x][y] = cell;
                grid.add(cell, x, y);
            }
        }

        Label nameBottom = new Label(game.getPlayer1Name());
        nameBottom.getStyleClass().add("card-name-p1");

        card.getChildren().addAll(nameTop, grid, nameBottom);

        Runnable updater = () -> updateCardContent(game, cells, nameTop, nameBottom);
        updater.run();

        game.macroDisplayProperty().addListener((obs, old, val) -> updater.run());
        game.currentPlayerProperty().addListener((obs, old, val) -> updater.run());

        return card;
    }

    private void updateCardContent(ActiveGame game, Region[][] cells, Label nameTop, Label nameBottom) {
        String[][] macro = game.getMacroCells();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                cells[x][y].getStyleClass().removeAll(CELL_STYLES);
                String val = macro[x][y];
                if ("0".equals(val)) cells[x][y].getStyleClass().add("macro-cell-p0");
                else if ("1".equals(val)) cells[x][y].getStyleClass().add("macro-cell-p1");
                else if ("TIE".equals(val)) cells[x][y].getStyleClass().add("macro-cell-tie");
                else cells[x][y].getStyleClass().add("macro-cell-open");
            }
        }
        int turn = game.currentPlayerProperty().get();
        String arrow = "\u25B6 ";
        nameTop.setText(turn == 0 ? arrow + game.getPlayer0Name() : game.getPlayer0Name());
        nameBottom.setText(turn == 1 ? arrow + game.getPlayer1Name() : game.getPlayer1Name());
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
