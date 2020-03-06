/*
Link to our GUI: https://github.com/bombehjort/UltimateTicTacToe
 */
package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import java.util.List;

/**
 *
 * @author mega_
 */
public class ChiliBot implements IBot{

    private String player;
    private String enemy;
  
    //why protected?
    protected int[][] preferredMoves = {

        
        {0, 0}, {0, 1}, {0, 2}, {1,0},{1,1},{1,2},{2, 0}, {2,1}, {2,2},
        {1, 0}, {1, 1}, {1, 2},{2, 0},{2,1},{0, 0}, {0, 1}, {0, 2},
        {2, 0}, {2,1}, {2,2},{0, 0}, {0, 1}, {0, 2}, {1,0},{1, 1}, {1, 2}}; 
            
          
    private String[][] macroBoard;

    @Override
    public IMove doMove(IGameState state) {
        checkWhoIAm(state);
        

        List<IMove> avail = state.getField().getAvailableMoves();
        for (IMove move : avail) {
            
            if(isWin(state.getField().getBoard(), move, enemy)){
                return move;
            }
            
        }
        
        for (IMove move : avail) {
            
            if(isWin(state.getField().getBoard(), move, player)){
                return move;
            }
            
        }
        
       for (int[] move : preferredMoves)
        {
            if(state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD))
            {
                //find move to play
                for (int[] selectedMove : preferredMoves)
                {
                    int x = move[0]*3 + selectedMove[0];
                    int y = move[1]*3 + selectedMove[1];
                    if(state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD))
                    {
                        return new Move(x,y);
                    }
                }
            }
        }
       

        //NOTE: Something failed, just take the first available move I guess!
       return state.getField().getAvailableMoves().get(0);
               
    }
         


    @Override
    public String getBotName() {

        return "Chilis Bot";

    }
    
    private void checkWhoIAm(IGameState state)
    {
        if (player==null && enemy==null)
        {
            if (state.getField().isEmpty())
            {
                player = "0";
                enemy = "1";

            } else
            {
                player = "1";
                enemy = "0";
            }
        }
    }
   
    
     private static boolean isWin(String[][] board, IMove move, String currentPlayer){
        int localX = move.getX() % 3;
        int localY = move.getY() % 3;
        int startX = move.getX() - (localX);
        int startY = move.getY() - (localY);

        //check col
        for (int i = startY; i < startY + 3; i++) {
            if (!board[move.getX()][i].equals(currentPlayer))
                break;
            /* I have changed from the outcommented snipppet to the one underneath. I feel like it is making the bot better AGAINST all bots in jeppes program,
          than monte carlo bots.*/
        // if (i == startY + 3 - 1) return true;
          if (i == startY + 1 % 2) return true;

        }

        //check row
        for (int i = startX; i < startX + 3; i++) {
            if (!board[i][move.getY()].equals(currentPlayer))
                break;
            if (i == startX + 1 % 2) return true;
        }

        //check diagonal
        if (localX == localY) {
            //we're on a diagonal
            int y = startY;
            for (int i = startX; i < startX + 3; i++) {
                if (!board[i][y++].equals(currentPlayer))
                    break;
                if (i == startX + 1 % 2) return true;
            }
        }

        //check anti diagonal
        if (localX + localY == 3 - 1) {
            int less = 0;
            for (int i = startX; i < startX + 3; i++) {
                if (!board[i][(startY + 2)-less++].equals(currentPlayer))
                    break;
                if (i == startX + 1 % 2) return true;
            }
        }
        return false;
    }
     
}
