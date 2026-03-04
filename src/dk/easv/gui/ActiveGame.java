package dk.easv.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActiveGame {
    private final String player0Name;
    private final String player1Name;
    private final IntegerProperty currentPlayer = new SimpleIntegerProperty(0);
    private final StringProperty macroDisplay = new SimpleStringProperty("");
    private final String[][] macroCells = {
            {"-1", "-1", "-1"}, {"-1", "-1", "-1"}, {"-1", "-1", "-1"}};
    private final String[][] boardCells = new String[9][9];

    public ActiveGame(String player0Name, String player1Name) {
        this.player0Name = player0Name;
        this.player1Name = player1Name;
        for (String[] row : boardCells) java.util.Arrays.fill(row, ".");
    }

    public String getPlayer0Name() { return player0Name; }
    public String getPlayer1Name() { return player1Name; }
    public IntegerProperty currentPlayerProperty() { return currentPlayer; }
    public StringProperty macroDisplayProperty() { return macroDisplay; }
    public String[][] getMacroCells() { return macroCells; }
    public String[][] getBoardCells() { return boardCells; }

    public void updateFrom(int currentPlayer, String[][] board, String[][] macroboard) {
        this.currentPlayer.set(currentPlayer);
        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++)
                this.boardCells[x][y] = board[x][y];
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                this.macroCells[x][y] = macroboard[x][y];
        // Toggle macroDisplay to fire property change for ObservableList extractors
        this.macroDisplay.set(String.valueOf(System.nanoTime()));
    }
}
