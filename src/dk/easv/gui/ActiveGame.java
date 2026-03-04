package dk.easv.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActiveGame {
    private final String player0Name;
    private final String player1Name;
    private final IntegerProperty currentPlayer = new SimpleIntegerProperty(0);
    private final StringProperty macroDisplay = new SimpleStringProperty("\u00B7\u00B7\u00B7/\u00B7\u00B7\u00B7/\u00B7\u00B7\u00B7");

    public ActiveGame(String player0Name, String player1Name) {
        this.player0Name = player0Name;
        this.player1Name = player1Name;
    }

    public String getPlayer0Name() { return player0Name; }
    public String getPlayer1Name() { return player1Name; }
    public IntegerProperty currentPlayerProperty() { return currentPlayer; }
    public StringProperty macroDisplayProperty() { return macroDisplay; }

    public void updateFrom(int currentPlayer, String[][] macroboard) {
        this.currentPlayer.set(currentPlayer);
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 3; y++) {
            if (y > 0) sb.append("/");
            for (int x = 0; x < 3; x++) {
                String cell = macroboard[x][y];
                switch (cell) {
                    case "0":   sb.append("\u25CF"); break; // ●
                    case "1":   sb.append("\u25C6"); break; // ◆
                    case "TIE": sb.append("="); break;
                    default:    sb.append("\u00B7"); break;  // ·
                }
            }
        }
        this.macroDisplay.set(sb.toString());
    }
}
