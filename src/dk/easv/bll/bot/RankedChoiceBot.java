/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.bll.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

/**
 *
 * @author Kacper Bujko, Michael lumby, Kasper Mathiesen, Antoni Bujko.
 * link for github: https://github.com/scupak/UltimateTicTacToe 
 */
public class RankedChoiceBot implements IBot
{
    private static final String BOTNAME = "The Borg";
    private IMove lastmove;
    private List<IMove> possibleMoves;
    private List<IMove> MicroBoardSpaces;
    private Random ran;
    private boolean randomMode = false;
    private IGameState gamestate;
    private long starttime;
    private long endtime;
    private int FirstChoicedMove = 4;
    private int SecondChoicedMove = 0;
    private int ThirdChoicedMove = 6;
    private int FourthChoicedMove = 2;
    private int FifthChoicedMove = 8;
    private int SixthChoicedMove = 3;
    private int SeventhChoicedMove = 7;
    private int EigthChoicedMove = 1;
    private int NinthChoicedMove = 5;
   

    @Override
    public IMove doMove(IGameState state)
    {
        randomMode = false;
        starttime = System.currentTimeMillis();
        ran = new Random();
        possibleMoves = state.getField().getAvailableMoves();
        gamestate = state;
        getAvailableMovesMicroBoard();
        checkIfGoingFirst();
        checkIfPossibleWinOnMicroBoard();
        endtime = System.currentTimeMillis() - starttime;
        
        return lastmove;

        
    }

    public IMove getLastMove() {
       return lastmove;
    }
    
    /**
     * Checks if there is a possible win on the board.
     */
    private void checkIfPossibleWinOnMicroBoard()
    {
        boolean centreIsAvailable = false;
        boolean secondIsAvailable = false;
        boolean thirdIsAvailable = false;
        boolean fourthIsAvailable = false;
        boolean fifthIsAvailable = false;
        boolean sixthIsAvailable = false;
        boolean seventhIsAvailable = false;
        boolean eigthIsAvailable = false;
        boolean ninthIsAvailable = false;
        
        for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(FirstChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(FirstChoicedMove).getY() == move.getY())
            {
                centreIsAvailable = true;
                
            }
            
        }
        
         for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(SecondChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(SecondChoicedMove).getY() == move.getY())
            {
               secondIsAvailable = true;
                
            }
            
        }
         for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(ThirdChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(ThirdChoicedMove).getY() == move.getY())
            {
               thirdIsAvailable = true;
                
            }
            
        }
        for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(FourthChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(FourthChoicedMove).getY() == move.getY())
            {
               fourthIsAvailable = true;
                
            }
            
        }
        for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(FifthChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(FifthChoicedMove).getY() == move.getY())
            {
               fifthIsAvailable = true;
                
            }
            
        } 
        for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(SixthChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(SixthChoicedMove).getY() == move.getY())
            {
               sixthIsAvailable = true;
                
            }
            
        }
        for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(SeventhChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(SeventhChoicedMove).getY() == move.getY())
            {
               seventhIsAvailable = true;
                
            }
            
        } 
        for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(EigthChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(EigthChoicedMove).getY() == move.getY())
            {
               eigthIsAvailable = true;
                
            }
            
        }
        for (IMove move : possibleMoves)
        {
            if (MicroBoardSpaces.get(NinthChoicedMove).getX() == move.getX() && MicroBoardSpaces.get(NinthChoicedMove).getY() == move.getY())
            {
               ninthIsAvailable = true;
                
            }
            
        }
        if (randomMode == true)
        {
            lastmove = possibleMoves.get(ran.nextInt(possibleMoves.size()));
        }
        else if (centreIsAvailable == true)
        {
            lastmove = MicroBoardSpaces.get(FirstChoicedMove);
        }
        else if (secondIsAvailable == true)
        {
            /* FirstChoicedMove = 5;
             SecondChoicedMove = 1;
            ThirdChoicedMove = 7;
            FourthChoicedMove = 3;
            FifthChoicedMove = 2;
            SixthChoicedMove = 8;
            SeventhChoicedMove = 6;
            EigthChoicedMove = 0;
            NinthChoicedMove = 4;*/
            lastmove = MicroBoardSpaces.get(SecondChoicedMove);
            
        }
        else if (thirdIsAvailable == true)
        {
            lastmove = MicroBoardSpaces.get(ThirdChoicedMove);
            
        }
        else if (fourthIsAvailable == true)
        {
            lastmove = MicroBoardSpaces.get(FourthChoicedMove);
            
        }
        else if (fifthIsAvailable == true)
        {
            lastmove = MicroBoardSpaces.get(FifthChoicedMove);
            
        }
        else if (sixthIsAvailable == true)
        {
            lastmove = MicroBoardSpaces.get(SixthChoicedMove);
            
        }
        else if (seventhIsAvailable == true)
        {
            lastmove = MicroBoardSpaces.get(SeventhChoicedMove);
            
        }
        else if (eigthIsAvailable == true)
        {
            lastmove = MicroBoardSpaces.get(EigthChoicedMove);
            
        }
        else if (ninthIsAvailable == true)
        {
            lastmove = MicroBoardSpaces.get(NinthChoicedMove);
            
        }
        else
        {
            lastmove = possibleMoves.get(ran.nextInt(possibleMoves.size()));
            
        }
        
                
       
    }
    
    public void getAvailableMovesMicroBoard() {
        
        MicroBoardSpaces = new ArrayList<>();
        
        for (int x = 0; x < gamestate.getField().getBoard().length; x++) {
            
            for (int y = 0; y < gamestate.getField().getBoard().length; y++) {
                
              
               
             if( gamestate.getField().isInActiveMicroboard(x, y)){
             
                 MicroBoardSpaces.add(new Move(x, y));
             
             }
                
            }
            
    }
        
        
        
        
        

    }
    
    private void checkIfGoingFirst()
    {
       
        if (gamestate.getMoveNumber() == 0)
        {
             FirstChoicedMove = 40;
             SecondChoicedMove = 30;
             ThirdChoicedMove = 48;
             FourthChoicedMove = 32;
             FifthChoicedMove = 50;
             SixthChoicedMove = 39;
             SeventhChoicedMove = 49;
             EigthChoicedMove = 31;
             NinthChoicedMove = 41;
        }
        else if (FirstChoicedMove == 40)
        {
            FirstChoicedMove = 4;
            SecondChoicedMove = 0;
            ThirdChoicedMove = 6;
            FourthChoicedMove = 2;
            FifthChoicedMove = 8;
            SixthChoicedMove = 3;
            SeventhChoicedMove = 7;
            EigthChoicedMove = 1;
            NinthChoicedMove = 5;
        }
        else if (gamestate.getMoveNumber() == 1 && FirstChoicedMove != 40)
        {
            FirstChoicedMove = 5;
            SecondChoicedMove = 4;
            ThirdChoicedMove = 3;
            FourthChoicedMove = 1;
            FifthChoicedMove = 7;
            SixthChoicedMove = 0;
            SeventhChoicedMove = 8;
            EigthChoicedMove = 2;
            NinthChoicedMove = 6;
        }
        else if (gamestate.getMoveNumber() > 1 && FirstChoicedMove == 5)
        {
            randomMode = true;
        }
        
       
    }

    @Override
    public String getBotName() {
       return BOTNAME;
    }
}
