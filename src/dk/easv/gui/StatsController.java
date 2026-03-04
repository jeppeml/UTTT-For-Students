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

    private static final String[] MICRO_STYLES = {"micro-cell-p0", "micro-cell-p1", "micro-cell-tie", "micro-cell-empty"};
    private static final String[] SECTION_STYLES = {"section-p0", "section-p1", "section-tie"};

    private Node createCard(ActiveGame game) {
        VBox card = new VBox(2);
        card.getStyleClass().add("active-game-card");
        card.setAlignment(Pos.CENTER);

        Label nameTop = new Label(game.getPlayer0Name());
        nameTop.getStyleClass().add("card-name-p0");

        GridPane outerGrid = new GridPane();
        outerGrid.setHgap(2);
        outerGrid.setVgap(2);
        outerGrid.getStyleClass().add("outer-macro-grid");

        Region[][] cells = new Region[9][9];
        GridPane[][] sections = new GridPane[3][3];
        for (int my = 0; my < 3; my++) {
            for (int mx = 0; mx < 3; mx++) {
                GridPane section = new GridPane();
                section.setHgap(1);
                section.setVgap(1);
                section.getStyleClass().add("micro-section");
                sections[mx][my] = section;
                for (int cy = 0; cy < 3; cy++) {
                    for (int cx = 0; cx < 3; cx++) {
                        Region cell = new Region();
                        cell.getStyleClass().add("micro-cell");
                        cell.setPrefSize(5, 5);
                        cell.setMinSize(5, 5);
                        cells[mx * 3 + cx][my * 3 + cy] = cell;
                        section.add(cell, cx, cy);
                    }
                }
                outerGrid.add(section, mx, my);
            }
        }

        Label nameBottom = new Label(game.getPlayer1Name());
        nameBottom.getStyleClass().add("card-name-p1");

        card.getChildren().addAll(nameTop, outerGrid, nameBottom);

        Runnable updater = () -> updateCardContent(game, cells, sections, nameTop, nameBottom);
        updater.run();

        game.macroDisplayProperty().addListener((obs, old, val) -> updater.run());
        game.currentPlayerProperty().addListener((obs, old, val) -> updater.run());

        return card;
    }

    private void updateCardContent(ActiveGame game, Region[][] cells, GridPane[][] sections,
                                   Label nameTop, Label nameBottom) {
        String[][] board = game.getBoardCells();
        String[][] macro = game.getMacroCells();
        for (int my = 0; my < 3; my++) {
            for (int mx = 0; mx < 3; mx++) {
                String mval = macro[mx][my];
                boolean won = "0".equals(mval) || "1".equals(mval) || "TIE".equals(mval);
                sections[mx][my].getStyleClass().removeAll(SECTION_STYLES);
                if ("0".equals(mval)) sections[mx][my].getStyleClass().add("section-p0");
                else if ("1".equals(mval)) sections[mx][my].getStyleClass().add("section-p1");
                else if ("TIE".equals(mval)) sections[mx][my].getStyleClass().add("section-tie");
                for (int cy = 0; cy < 3; cy++) {
                    for (int cx = 0; cx < 3; cx++) {
                        Region cell = cells[mx * 3 + cx][my * 3 + cy];
                        cell.getStyleClass().removeAll(MICRO_STYLES);
                        if (won) {
                            cell.getStyleClass().add(
                                "0".equals(mval) ? "micro-cell-p0" :
                                "1".equals(mval) ? "micro-cell-p1" : "micro-cell-tie");
                        } else {
                            String val = board[mx * 3 + cx][my * 3 + cy];
                            if ("0".equals(val)) cell.getStyleClass().add("micro-cell-p0");
                            else if ("1".equals(val)) cell.getStyleClass().add("micro-cell-p1");
                            else cell.getStyleClass().add("micro-cell-empty");
                        }
                    }
                }
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
