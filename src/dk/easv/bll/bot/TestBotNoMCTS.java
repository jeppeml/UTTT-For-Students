/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.bll.bot;

import dk.easv.bll.game.GameManager;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author fauxtistic
 */
public class TestBotNoMCTS implements IBot {

    private static final String BOT_NAME = "TestBotNoMCTS";
    private Random rand = new Random();
    private String player;
    private String opponent;
    private GameManager gm;
    private IGameState state;

    @Override
    public IMove doMove(IGameState state) {
        this.state = state;
        GameManager gm = new GameManager(state);
        player = gm.getCurrentState().getMoveNumber() % 2 + "";
        opponent = (player.equals("1")) ? "0" : "1";
        List<IMove> possibleMoves = gm.getCurrentState().getField().getAvailableMoves();
        List<WMove> analyzedMoves = analyzeAvailableMoves(possibleMoves);

        WMove wmove = analyzedMoves.get(rand.nextInt(analyzedMoves.size()));
        System.out.println("Hello, I'm player " + player + ". I'm performing move "
                + wmove + " with weight of " + wmove.getWeight() + ". My opponent is "
                + opponent + ".");
        return wmove;

    }

    @Override
    public String getBotName() {
        return BOT_NAME;
    }

    public List<WMove> analyzeAvailableMoves(List<IMove> availableMoves) {
        List<WMove> weightedMoves = new ArrayList<>();

        for (IMove move : availableMoves) {
            WMove wmove = new WMove(move.getX(), move.getY());
            if (moveIsCenterOrDiagonal(wmove)) {
                wmove.setWeight(wmove.getWeight() + 10);
            }

            if (moveGivesMicroboardWin(wmove)) {
                wmove.setWeight(wmove.getWeight() + 30);
            }
            if (moveBlocksOpponentMicroboardWin(wmove)) {
                wmove.setWeight(wmove.getWeight() + 25);
            }
            
            weightedMoves.add(wmove);
        }

        weightedMoves.sort(Comparator.comparing(WMove::getWeight));
        int weight = weightedMoves.get(weightedMoves.size() - 1).getWeight();

        List<WMove> analyzedMoves = new ArrayList<>();
        for (WMove wmove : weightedMoves) {
            if (wmove.getWeight() == weight) {
                analyzedMoves.add(wmove);
            }
        }

        return analyzedMoves;
    }

    public boolean moveIsCenterOrDiagonal(IMove move) {

        boolean isCenterOrDiagonal = false;

        int x = move.getX() % 3;
        int y = move.getY() % 3;

        if ((x == 0 & y == 0) || (x == 2 & y == 0) || (x == 0 & y == 2) || (x == 2 & y == 2) || (x == 1 & y == 1)) {
            isCenterOrDiagonal = true;
        }

        return isCenterOrDiagonal;
    }
        
    public boolean moveGivesMicroboardWin(IMove move) {
        
        boolean givesWin = false;
        
        String[][] board = state.getField().getBoard();
        
        String[][] tempBoard = new String[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    tempBoard[i][j] = board[i][j];
                }
            }
        
        tempBoard[move.getX()][move.getY()] = player;
            
        if (gm.isWin(tempBoard, move, player)) {
            givesWin = true;
        }
        
        return givesWin;
    }
    
    public boolean moveBlocksOpponentMicroboardWin(IMove move) {
        boolean blocksOpponent = false;
        
        String[][] board = state.getField().getBoard();
        
        String[][] tempBoard = new String[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    tempBoard[i][j] = board[i][j];
                }
            }
        
        tempBoard[move.getX()][move.getY()] = opponent;
            
        if (gm.isWin(tempBoard, move, opponent)) {
            blocksOpponent = true;
        }
        
        return blocksOpponent;
    }
            

        
    public boolean moveGivesOpponentWinChance(IMove move) {
        return false; //not implemented yet
    }

    public class WMove extends Move {

        private int weight;

        public WMove(int x, int y) {
            super(x, y);
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

    }

}
