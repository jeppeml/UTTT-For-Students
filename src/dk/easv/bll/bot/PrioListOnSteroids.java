package dk.easv.bll.bot;

import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.ArrayList;
import java.util.List;

public class PrioListOnSteroids extends LocalPrioritisedListBot{
    private static final String BOTNAME = "PrioList on Steroids";
    public static void main(String[] args){
        GameState gs = new GameState();
        gs.setMoveNumber(11);
        String[][] board = gs.getField().getBoard();
        board[0][0]="1";
        board[0][2]="1";

        board[3][0]="1";
        board[3][1]="1";

        board[0][6]="1";
        board[1][7]="1";

        board[5][6]="1";
        board[3][8]="1";

        board[0][0]="1";
        board[0][2]="1";

        board[8][8]="1";
        board[6][8]="1";

        for (int x = 0; x < 9; x++) {
            System.out.println();
            for (int y = 0; y < 9; y++) {
                System.out.print(board[y][x]+" ");
            }
        }
        System.out.println(gs.getField().toString());
        List<IMove> moves= new PrioListOnSteroids().getWinningMoves(gs);
        System.out.println(moves);

    }
    @Override
    public IMove doMove(IGameState state) {
    List<IMove> winMoves = getWinningMoves(state);
        if(!winMoves.isEmpty())
                return winMoves.get(0);

        return super.doMove(state);
    }

    private boolean isWinningMove(IGameState state, IMove move, String player){
        String[][] board = state.getField().getBoard();
        boolean isRowWin = true;
        // Row checking
        int startX = move.getX()-(move.getX()%3);
        int endX = startX + 2;
        for (int x = startX; x <= endX; x++) {
            if(x!=move.getX())
                if(!board[x][move.getY()].equals(player))
                    isRowWin = false;
        }

        boolean isColumnWin=true;
        // Column checking
        int startY = move.getY()-(move.getY()%3);
        int endY = startY + 2;
        for (int y = startY; y <= endY; y++) {
            if(y!=move.getY())
                if(!board[move.getX()][y].equals(player))
                    isColumnWin = false;
        }


        boolean isDiagWin = true;

        // Diagonal checking left-top to right-bottom
        if(!(move.getX()==startX && move.getY()==startY))
            if(!board[startX][startY].equals(player))
                isDiagWin=false;
        if(!(move.getX()==startX+1 && move.getY()==startY+1))
            if(!board[startX+1][startY+1].equals(player))
                isDiagWin=false;
        if(!(move.getX()==startX+2 && move.getY()==startY+2))
            if(!board[startX+2][startY+2].equals(player))
                isDiagWin=false;

        boolean isOppositeDiagWin = true;
        // Diagonal checking left-bottom to right-top
        if(!(move.getX()==startX && move.getY()==startY+2))
            if(!board[startX][startY+2].equals(player))
                isOppositeDiagWin=false;
        if(!(move.getX()==startX+1 && move.getY()==startY+1))
            if(!board[startX+1][startY+1].equals(player))
                isOppositeDiagWin=false;
        if(!(move.getX()==startX+2 && move.getY()==startY))
            if(!board[startX+2][startY].equals(player))
                isOppositeDiagWin=false;

        return isColumnWin || isDiagWin || isOppositeDiagWin || isRowWin;
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
