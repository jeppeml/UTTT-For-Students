package dk.easv.bll.bot;

import dk.easv.bll.bot.IBot;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomBotOnSteroids implements IBot {
    private static final String BOTNAME = "Random on Steroids";
    Random rand = new Random();

    @Override
    public IMove doMove(IGameState state) {
        List<IMove> winMoves = getWinningMoves(state);
        if(!winMoves.isEmpty())
            return winMoves.get(0);
        List<IMove> moves = state.getField().getAvailableMoves();
        if (moves.size() > 0) {
            return moves.get(rand.nextInt(moves.size())); /* get random move from available moves */
        }

        return null;

    }


    // Simplified version of checking if win. Check the GameManager class to see another similar solution
    private boolean isWinningMove(IGameState state, IMove move, String player){
        // Clones the array and all values to a new array, so we don't mess with the game
        String[][] board = Arrays.stream(state.getField().getBoard()).map(String[]::clone).toArray(String[][]::new);

        //Places the player in the game. Sort of a simulation.
        board[move.getX()][move.getY()] = player;

        int startX = move.getX()-(move.getX()%3);
        if(board[startX][move.getY()].equals(player))
            if (board[startX][move.getY()].equals(board[startX+1][move.getY()]) &&
                    board[startX+1][move.getY()].equals(board[startX+2][move.getY()]))
                return true;

        int startY = move.getY()-(move.getY()%3);
        if(board[move.getX()][startY].equals(player))
            if (board[move.getX()][startY].equals(board[move.getX()][startY+1]) &&
                    board[move.getX()][startY+1].equals(board[move.getX()][startY+2]))
        return true;


        if(board[startX][startY].equals(player))
            if (board[startX][startY].equals(board[startX+1][startY+1]) &&
                    board[startX+1][startY+1].equals(board[startX+2][startY+2]))
                return true;

        if(board[startX][startY+2].equals(player))
            if (board[startX][startY+2].equals(board[startX+1][startY+1]) &&
                    board[startX+1][startY+1].equals(board[startX+2][startY]))
                return true;

        return false;
    }
    // Compile a list of all available winning moves
    private List<IMove> getWinningMoves(IGameState state){
        String player = "1";
        if(state.getMoveNumber()%2==0)
            player="0";

        List<IMove> avail = state.getField().getAvailableMoves();

        List<IMove> winningMoves = new ArrayList<>();
        for (IMove move:avail) {
            if(isWinningMove(state,move,player))
                winningMoves.add(move);
        }
        return winningMoves;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
