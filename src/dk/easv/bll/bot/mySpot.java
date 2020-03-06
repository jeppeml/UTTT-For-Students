/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.bll.bot;

import java.util.List;
import java.util.Random;
import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

/**
 *
 * @author BBran
 * 
 * GitHub repo for GUI: https://github.com/Guruen/UltimateTicTacToe
 * 
 */
public class mySpot implements IBot
{

    private static final String BOTNAME = "Compiling []]]]]......] 42%";
    private String[][] localBoard = new String[9][9];
    private int indexPlus;
    private int indexMinus;
    private String playerID;
/**
 * 
 * 
 * Get playerID from movenumber. Gets cells that is in active board.
 * Checks cells to see if there's 2 if it's playerID in a row/col, 
 * if there is and last spot is available it fills last spot.  
 * Goes for 3 in a row/col. Otherwise it does random available move.
 */
    @Override
    public IMove doMove(IGameState state)
    {

        if (state.getMoveNumber() % 2 == 0)
        {
            playerID = "" + 0;
        } else
        {
            playerID = "" + 1;
        }

        this.localBoard = state.getField().getBoard();

        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (state.getField().isInActiveMicroboard(i, j))
                {
                    if (j == 2 || j == 5 || j == 8)
                    {
                        indexPlus = 0;
                    } else
                    {
                        indexPlus = 1;
                    }
                    if (j == 0 || j == 3 || j == 6)
                    {
                        indexMinus = 0;
                    } else
                    {
                        indexMinus = -1;
                    }

                    if (localBoard[i][j].equals(playerID) && localBoard[i][j + indexPlus].equals(playerID) && localBoard[i][j + indexMinus].equals(IField.EMPTY_FIELD))
                    {
                        int indexMove = j + indexMinus;

                        IMove m = new Move(i, indexMove);
                        if (localBoard[i][indexMove].equals(IField.EMPTY_FIELD))
                        {

                            return m;
                        }
                        break;
                    }

                    if (localBoard[i][j + indexMinus].equals(playerID) && localBoard[i][j].equals(playerID) && localBoard[i][j + indexPlus].equals(IField.EMPTY_FIELD))
                    {
                        int indexMove = j + indexPlus;

                        IMove m = new Move(i, indexMove);
                        if (localBoard[i][indexMove].equals(IField.EMPTY_FIELD))
                        {

                            return m;
                        }
                        break;
                    }

                    if (i == 2 || i == 5 || i == 8)
                    {
                        indexPlus = 0;
                    } else
                    {
                        indexPlus = 1;
                    }
                    if (i == 0 || i == 3 || i == 6)
                    {
                        indexMinus = 0;
                    } else
                    {
                        indexMinus = -1;
                    }

                    
                    if (localBoard[i][j].equals(playerID) && localBoard[i + indexPlus][j].equals(playerID) && localBoard[i + indexMinus][j].equals(IField.EMPTY_FIELD))
                    {
                        int indexMove = i + indexMinus;

                        IMove m = new Move(indexMove, j);

                        if (localBoard[indexMove][j].equals(IField.EMPTY_FIELD))
                        {

                            return m;
                        }
                        break;
                    }

                }

            }
        }

        Random rand = new Random();
        List<IMove> moves = state.getField().getAvailableMoves();

        IMove move = moves.get(rand.nextInt(moves.size()));
        return move;
    }

    @Override
    public String getBotName()
    {
        return BOTNAME;
    }

}
