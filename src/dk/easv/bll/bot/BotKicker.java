/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.bll.bot;
// https://github.com/TriggerStorm/TickleTackleTow
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import dk.easv.bll.game.IGameState;
import dk.easv.bll.game.GameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import dk.easv.bll.field.IField;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Alan
 */
public class BotKicker implements IBot {
   
    private static final String BOTNAME = "BotKicker";
    private String ourBot;
    private String opponentBot;
    int us;
    int them;
    int macroX;
    int macroY;
    boolean noActiveboard;
    boolean hasEmptyPlace;
    List<IMove> winningMoves;
    List<IMove> blockingMoves;
    IMove randonMove;
    Move emptyPlace;
    Random random;
    String[][] activeboard;
    String[][] microboard;

    
  
    public IMove doMove(IGameState state) { 
        
// Decide which player the BOt is
        if (state.getMoveNumber() == 0) {
            ourBot = "0";
            opponentBot = "1";
        }
        if (state.getMoveNumber() == 1) {
            ourBot = "1";
            opponentBot = "0";
        }
// Intialise variables
        noActiveboard = true;
        winningMoves = new ArrayList<>();
        blockingMoves = new ArrayList<>();
        random = new Random();
        microboard = state.getField().getBoard();
 
        String[][] activeboard = getActiveMicroboard(state);
        if (noActiveboard == false) {  // If there is an active microboard, check active microboard for a winning microboard
            checkForPossibleWin(state, activeboard);

            if (winningMoves.size() > 0) {
                return winningMoves.get(0);
            }
            if (blockingMoves.size() > 0) {
                return blockingMoves.get(0);
            }
        }

        if (noActiveboard == true) {  // If there is no active microboard, check macroboard for a winning microboard
            for (int WinThenBlock = 0; WinThenBlock < 2; WinThenBlock++) {
                for ( macroY = 0; macroY < 3; macroY++) {
                    for (int macroX = 0; macroX < 3; macroX++) {
                        for (int activeY = 0; activeY < 3; activeY++) {
                            for (int activeX = 0; activeX < 3; activeX++) {
                                activeboard[activeX][activeY] = microboard[(macroX) + activeX][(macroY) + activeY];
                            }
                        }
                        checkForPossibleWin(state, activeboard);
                        if (WinThenBlock == 0) {
                            if (winningMoves.size() > 0) {
                                return winningMoves.get(0);
                            }
                        }
                
                        if (WinThenBlock == 0) {
                            if (blockingMoves.size() > 0) {
                                return blockingMoves.get(0);
                            }
                        }
                    }
                }   
            }
        }        
        // If there is no chance of winning or blocking, do a random move
        IMove randomMove = randomMove(state);
        return randomMove; 
    }
    
    
    
    
    public String[][] getActiveMicroboard(IGameState state) {  // Returns the 3x3 active microboard "activeboard"
        activeboard = new String[3][3];
//  Search the macroboard
        for (int startY = 0; startY < 3; startY++) {
            for (int startX = 0; startX < 3; startX++) {
                if (state.getField().isInActiveMicroboard(startX * 3, startY * 3)) {  //  Check if in active microboard
                    macroX = startX;  //  Remember macroboard X loaction
                    macroY = startY;  //  Remember macroboard Y loaction
                    for (int activeY = 0; activeY < 3; activeY++) {
                        for (int activeX = 0; activeX < 3; activeX++) {
                            activeboard[activeX][activeY] = microboard[(startX * 3) + activeX][(startY * 3) + activeY];  //  Copy active part of board to activeboard
                        }
                    }
                    noActiveboard = false;  // If active board found
                    return activeboard;
                }
            }
        }
        noActiveboard = true;  // If no active board found
        return null; // This result NEVER used
    }

    public void checkForPossibleWin(IGameState state, String[][] activeboard) {
    //  Goes through every column, row and diagonal
        IMove emptyPlace = randomMove(state);
        int x;
        int y;

        // Check vertically
        for (x = 0; x < 3; x++) {
            hasEmptyPlace = false;
            us = 0;
            them = 0;
            for (y = 0; y < 3; y++) {
                testWinningMove(x, y);  //  Tests each column
            }
        }

        // Check horizontically
        for (y = 0; y < 3; y++) {
            hasEmptyPlace = false;
            us = 0;
            them = 0;
            for (x = 0; x < 3; x++) {
                testWinningMove(x, y);  //  Tests each row
            }
        }

        // Check diagonally
        hasEmptyPlace = false;
        us = 0;
        them = 0;
        for (int d = 0; d < 3; d++) {
            x = d;
            y = d;
            testWinningMove(x, y);  //  Tests first diagonal
        }
        hasEmptyPlace = false;;
        
        us = 0;
        them = 0;
        for (int d = 2; d >= 0; d--) {
            x = d;
            y = 2-d;
            testWinningMove(x,y);  //  Tests second diagonal
        }
    }

    public void testWinningMove(int x, int y) {
    //  Check to see if there are two of one type and an empty space, to either win or block    
        if (activeboard[x][y].equals(ourBot)) {
            us++;  //  Counts our bots total in given column, row or diagonal
        }
        if (activeboard[x][y].equals(opponentBot)) {
            them++;  //  Counts oppositions bots total in given column, row or diagonal
        }
        if (activeboard[x][y].equals(".")) {  //  Makes sure there's a free space
            hasEmptyPlace = true;
            emptyPlace = new Move((macroX * 3) + x, (macroY * 3) + y);
        }
        if (us > 1 && (hasEmptyPlace == true)) {  // If we have 2, with a free space
            winningMoves.add(emptyPlace);  //  Add a winning move to winningMoves list
        }
        if (them > 1 && (hasEmptyPlace == true)) {  // If they have 2, with a free space
            blockingMoves.add(emptyPlace);  //  Add a blocking move to blockingMoves list
        }
    }

    
    public IMove randomMove(IGameState state) {
    //  If there are no winning or blocking moves, do a random valid move
        List<IMove> validMoves = state.getField().getAvailableMoves();
        int randomInt = random.nextInt(validMoves.size());
        return new Move(validMoves.get(randomInt).getX(), validMoves.get(randomInt).getY());
    }
    
    
    public String getBotName() {
        return BOTNAME;
    }
}