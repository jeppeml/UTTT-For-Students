package dk.easv.gui;

import javafx.scene.control.*;
import dk.easv.bll.bot.IBot;
import dk.easv.bll.game.GameManager;
import dk.easv.bll.game.stats.GameResult;
import dk.easv.dal.DynamicBotClassHandler;
import static dk.easv.gui.util.FontAwesomeHelper.getFontAwesomeIconFromPlayerId;
import static dk.easv.dal.DynamicBotClassHandler.loadBotList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.layout.AnchorPane;

public class AppController implements Initializable {

    public Button btnTrash;
    public Button btnDiamond;
    @FXML
    private TextField txtHumanNameLeft;
    @FXML
    private RadioButton radioRightAI;
    @FXML
    private TextField txtHumanNameRight;
    @FXML
    private RadioButton radioLeftAI;
    @FXML
    private RadioButton radioRightHuman;
    @FXML
    private ToggleGroup toggleLeft;
    @FXML
    private ToggleGroup toggleRight;

    @FXML
    private Button btnStart;
    @FXML
    private ComboBox<IBot> comboBotsRight;
    @FXML
    private ComboBox<IBot> comboBotsLeft;
    @FXML
    private RadioButton radioLeftHuman;
    @FXML
    private Slider sliderSpeed;

    StatsModel statsModel = new StatsModel();
    @FXML
    private AnchorPane anchorMain;
    private final AtomicInteger winsBot1 = new AtomicInteger(0);
    private final AtomicInteger winsBot2 = new AtomicInteger(0);
    private final AtomicInteger ties = new AtomicInteger(0);
    private final AtomicInteger activeSimThreads = new AtomicInteger(0);
    @FXML
    private Button btnStartSim;
    @FXML
    private Slider sliderSim;
    @FXML
    private Label lblBotSpeed;
    @FXML
    private Label lblSimCount;

    private Stage statsWindow  = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<IBot> bots = FXCollections.observableArrayList();
        try {
            DynamicBotClassHandler.writeBotsToTextFile();
            bots = loadBotList();
        }
        catch (Exception ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }

        comboBotsLeft.setButtonCell(new CustomIBotListCell());
        comboBotsLeft.setCellFactory(p -> new CustomIBotListCell());
        comboBotsLeft.setItems(bots);
        comboBotsRight.setButtonCell(new CustomIBotListCell());
        comboBotsRight.setCellFactory(p -> new CustomIBotListCell());
        comboBotsRight.setItems(bots);
        btnDiamond.setGraphic(getFontAwesomeIconFromPlayerId("1"));
        btnTrash.setGraphic(getFontAwesomeIconFromPlayerId("0"));

        radioLeftAI.selectedProperty().addListener((observable, oldValue, newValue) -> comboBotsLeft.setDisable(!newValue));
        radioLeftHuman.selectedProperty().addListener((observable, oldValue, newValue) -> txtHumanNameLeft.setDisable(!newValue));
        radioRightAI.selectedProperty().addListener((observable, oldValue, newValue) -> comboBotsRight.setDisable(!newValue));
        radioRightHuman.selectedProperty().addListener((observable, oldValue, newValue) -> txtHumanNameRight.setDisable(!newValue));
        comboBotsLeft.getSelectionModel().selectFirst();
        comboBotsLeft.setDisable(true);
        comboBotsRight.getSelectionModel().selectFirst();
        comboBotsRight.setDisable(true);
        btnStartSim.setGraphic(de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory.get()
                .createIcon(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.EXTERNAL_LINK, "16"));
        sliderSpeed.valueProperty().addListener((obs, oldVal, newVal) ->
                lblBotSpeed.setText("Bot Move Delay: " + Math.round(newVal.doubleValue()) + "ms"));
        lblBotSpeed.setText("Bot Move Delay: " + Math.round(sliderSpeed.getValue()) + "ms");
        sliderSim.valueProperty().addListener((obs, oldVal, newVal) ->
                lblSimCount.setText(Math.round(newVal.doubleValue()) + ""));
        lblSimCount.setText(Math.round(sliderSim.getValue()) + "");
    }

    @FXML
    private void clickOpenStats(ActionEvent event) throws IOException {
        openStatsWindow();
    }
    
    private void openStatsWindow() throws IOException {
        if(statsWindow==null)
        {
            statsWindow = new Stage();
            statsWindow.initModality(Modality.WINDOW_MODAL);
            FXMLLoader fxLoader = new FXMLLoader(
                    getClass().getResource("Stats.fxml"));

            Parent root = fxLoader.load();

            StatsController controller
                    = ((StatsController) fxLoader.getController());

            Scene scene = new Scene(root);
            statsWindow.setScene(scene);

            controller.setStatsModel(statsModel, statsWindow);
            statsWindow.setOnCloseRequest(
                    (obs)-> statsWindow=null
            );
            statsWindow.showAndWait();
        }
        else
        {
            statsWindow.toFront();
        }
    }

    private void startSimulation(long amountOfSimulations) {
        int multiCores = Runtime.getRuntime().availableProcessors();
        winsBot1.set(0);
        winsBot2.set(0);
        ties.set(0);
        long perThread = amountOfSimulations / multiCores;
        long remainder = amountOfSimulations % multiCores;
        activeSimThreads.set(multiCores);
        statsModel.simulatingProperty().set(true);
        for (int i = 0; i < multiCores; i++) {
            long count = perThread + (i < remainder ? 1 : 0);
            Thread t = new Thread(
                    new Simulator(count,
                        this.comboBotsLeft.getValue().getClass(),
                        this.comboBotsRight.getValue().getClass()));
            t.setDaemon(true);
            t.start();
        }
    }

    @FXML
    private void clickSelector(ActionEvent event) {
        if(toggleLeft.getSelectedToggle()==radioLeftAI &&
                toggleRight.getSelectedToggle()== radioRightAI) {
            btnStartSim.setDisable(false);
            sliderSim.setDisable(false);
        } else {
            btnStartSim.setDisable(true);
            sliderSim.setDisable(true);
        }
    }
    
    private class Simulator implements Runnable{
        private final long amountOfSimulations;
        private IBot bot1;
        private IBot bot2;
        private final String name1;
        private final String name2;
        public Simulator(
                long amountOfSimulations,
                Class<? extends IBot> b1,
                Class<? extends IBot> b2) {

            this.amountOfSimulations=amountOfSimulations;
            try {
                this.bot1 = b1.getDeclaredConstructor().newInstance();
                this.bot2 = b2.getDeclaredConstructor().newInstance();
            }
            catch (Exception ex) {
                throw new RuntimeException("Failed to instantiate bots for simulation", ex);
            }
            if (bot1.getBotName().equals(bot2.getBotName())) {
                name1 = bot1.getBotName() + " #1";
                name2 = bot2.getBotName() + " #2";
            } else {
                name1 = bot1.getBotName();
                name2 = bot2.getBotName();
            }
        }
        
        @Override
        public void run() {
            long firstHalf = amountOfSimulations / 2;
            long secondHalf = amountOfSimulations - firstHalf;
            for (int i = 0; i < firstHalf; i++) {
                BoardModel model = new BoardModel(bot1, bot2);
                ActiveGame ag = new ActiveGame(name1, name2);
                Platform.runLater(() -> statsModel.getActiveGames().add(ag));
                int currentPlayer = 0;
                while (model.getGameOverState() == GameManager.GameOverState.Active
                         && model.getGameState().getField().getAvailableMoves().size()>0) {
                    currentPlayer = model.getCurrentPlayer();
                    Boolean valid = model.doMove();
                    if (!valid) {
                        // Bot forfeits: opponent wins
                        int opponent = (model.getCurrentPlayer() + 1) % 2;
                        model.forceGameOver(opponent);
                        currentPlayer = opponent;
                        break;
                    }
                    updateActiveGame(ag, model);
                }
                Platform.runLater(() -> statsModel.getActiveGames().remove(ag));
                // There is a tie
                if (model.getGameOverState().equals(GameManager.GameOverState.Tie)) {
                    this.addGameResult(
                            new GameResult(
                                    name1,
                                    name2,
                                    GameResult.Winner.tie));
                    ties.incrementAndGet();
                }
                else { // There is a winner
                    GameResult.Winner winResult;
                    if(currentPlayer==0) {
                        winsBot1.incrementAndGet();
                        winResult = GameResult.Winner.player0;
                    }
                    else {
                        winsBot2.incrementAndGet();
                        winResult = GameResult.Winner.player1;
                    }

                    this.addGameResult(
                            new GameResult(
                                    name1,
                                    name2,
                                    winResult));
                }
                updateTitle();

            }
            for (int i = 0; i < secondHalf; i++) {
                BoardModel model = new BoardModel(bot2, bot1);
                ActiveGame ag = new ActiveGame(name2, name1);
                Platform.runLater(() -> statsModel.getActiveGames().add(ag));
                int currentPlayer = 0;
                while (model.getGameOverState() == GameManager.GameOverState.Active
                         && model.getGameState().getField().getAvailableMoves().size()>0) {
                    currentPlayer = model.getCurrentPlayer();
                    Boolean valid = model.doMove();
                    if (!valid) {
                        int opponent = (model.getCurrentPlayer() + 1) % 2;
                        model.forceGameOver(opponent);
                        currentPlayer = opponent;
                        break;
                    }
                    updateActiveGame(ag, model);
                }
                Platform.runLater(() -> statsModel.getActiveGames().remove(ag));
                // There is a tie
                if (model.getGameOverState().equals(GameManager.GameOverState.Tie)) {
                    this.addGameResult(
                            new GameResult(
                                    name2,
                                    name1,
                                    GameResult.Winner.tie));
                    ties.incrementAndGet();
                }
                else { // There is a winner
                    GameResult.Winner winResult;
                    if(currentPlayer==0) {
                        winsBot2.incrementAndGet();
                        winResult = GameResult.Winner.player0;
                    }
                    else {
                        winsBot1.incrementAndGet();
                        winResult = GameResult.Winner.player1;
                    }

                    this.addGameResult(
                            new GameResult(
                                    name2,
                                    name1,
                                    winResult));
                }
                updateTitle();

            }
            if (activeSimThreads.decrementAndGet() == 0) {
                Platform.runLater(() -> statsModel.simulatingProperty().set(false));
            }
        }
        private void updateActiveGame(ActiveGame ag, BoardModel model) {
            String[][] macro = model.getMacroboard();
            String[][] copy = new String[3][3];
            for (int a = 0; a < 3; a++)
                System.arraycopy(macro[a], 0, copy[a], 0, 3);
            int cp = model.getCurrentPlayer();
            Platform.runLater(() -> ag.updateFrom(cp, copy));
        }

        private void updateTitle() {
            Platform.runLater(() ->
                statsModel.setLastSimulationResults(name1 + " vs " +
                        name2 + " | " +
                        "w/w/t " + winsBot1.get() + "/" +
                        winsBot2.get() + "/" + ties.get()));
        }
        private void addGameResult(GameResult gameResult) {
            Platform.runLater(()->
                statsModel.addGameResult(gameResult));
        }
    }
    private class CustomIBotListCell extends ListCell<IBot> {

        @Override
        protected void updateItem(IBot item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty && item != null) {
                setText(item.getBotName());
            }
            else {
                setText(null);
            }
        }
    }

    @FXML
    private void clickStartSimulation(ActionEvent event) throws IOException {
        String n0 = comboBotsLeft.getValue().getBotName();
        String n1 = comboBotsRight.getValue().getBotName();
        if (n0.equals(n1)) {
            statsModel.setPlayerNames(n0 + " #1", n1 + " #2");
        } else {
            statsModel.setPlayerNames(n0, n1);
        }
        statsModel.clear();
        startSimulation(Math.round(sliderSim.getValue()));
        openStatsWindow();
    }

    @FXML
    public void clickStart(ActionEvent actionEvent) throws IOException {
        Stage primaryStage = new Stage();
            primaryStage.initModality(Modality.WINDOW_MODAL);
            FXMLLoader fxLoader = new FXMLLoader(getClass().getResource("UTTTGame.fxml"));

            Parent root = fxLoader.load();

            UTTTGameController controller = ((UTTTGameController) fxLoader.getController());

            if (toggleLeft.getSelectedToggle().equals(radioLeftAI)
                    && toggleRight.getSelectedToggle().equals(radioRightAI)) {
                controller.setupGame(comboBotsLeft.getSelectionModel().getSelectedItem(), comboBotsRight.getSelectionModel().getSelectedItem());
                primaryStage.setTitle(
                        comboBotsLeft.getSelectionModel().getSelectedItem().getBotName()
                        + " vs "
                        + comboBotsRight.getSelectionModel().getSelectedItem().getBotName());
            }
            else if (toggleLeft.getSelectedToggle().equals(radioLeftHuman)
                    && toggleRight.getSelectedToggle().equals(radioRightAI)) {
                controller.setupGame(txtHumanNameLeft.getText(), comboBotsRight.getSelectionModel().getSelectedItem());
                primaryStage.setTitle(
                        txtHumanNameLeft.getText()
                        + " vs "
                        + comboBotsRight.getSelectionModel().getSelectedItem().getBotName());
            }
            else if (toggleLeft.getSelectedToggle().equals(radioLeftAI)
                    && toggleRight.getSelectedToggle().equals(radioRightHuman)) {
                controller.setupGame(comboBotsLeft.getSelectionModel().getSelectedItem(), txtHumanNameRight.getText());
                primaryStage.setTitle(
                        comboBotsLeft.getSelectionModel().getSelectedItem().getBotName()
                        + " vs "
                        + txtHumanNameRight.getText());
            }
            else if (toggleLeft.getSelectedToggle().equals(radioLeftHuman)
                    && toggleRight.getSelectedToggle().equals(radioRightHuman)) {
                controller.setupGame(txtHumanNameLeft.getText(), txtHumanNameRight.getText());
                primaryStage.setTitle(
                        txtHumanNameLeft.getText()
                        + " vs "
                        + txtHumanNameRight.getText());
            }
            controller.setSpeed(sliderSpeed.getValue());
            controller.startGame();
            controller.setStatsModel(statsModel);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.showAndWait();
    }
}
