/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.gui;

import dk.easv.bll.game.stats.GameResult;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jeppjleemoritzled
 */
public class StatsModel {
    private final ObservableList<GameResult> gameResults =
            FXCollections.observableArrayList();

    private final ObservableList<ActiveGame> activeGames =
            FXCollections.observableArrayList(
                    game -> new javafx.beans.Observable[] {
                            game.currentPlayerProperty(), game.macroDisplayProperty() });

    public ObservableList<ActiveGame> getActiveGames() { return activeGames; }

    private final StringProperty lastSimulationResults =
            new SimpleStringProperty("");

    private final StringProperty player0Name = new SimpleStringProperty("Player 0");
    private final StringProperty player1Name = new SimpleStringProperty("Player 1");
    private final BooleanProperty simulating = new SimpleBooleanProperty(false);

    public StringProperty player0NameProperty() { return player0Name; }
    public StringProperty player1NameProperty() { return player1Name; }
    public BooleanProperty simulatingProperty() { return simulating; }

    public void setPlayerNames(String p0, String p1) {
        player0Name.set(p0);
        player1Name.set(p1);
    }

    public StringProperty lastSimulationResultsProperty() {
        return lastSimulationResults;
    }
    
    public String getLastSimulationResults() {
        return lastSimulationResults.get();
    }

    public void setLastSimulationResults(String lastSimulationResults) {
        this.lastSimulationResults.set(lastSimulationResults);
    }
    
    public ObservableList<GameResult> getGameResults(){
        return gameResults;
    }
    
    public synchronized void addGameResult(GameResult gr) {
        gameResults.add(gr);
    }

    public void clear() {
        gameResults.clear();
        lastSimulationResults.set("Simulating...");
    }
    
    
}
