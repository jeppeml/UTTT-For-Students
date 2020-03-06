/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import java.util.List;
import java.util.Random;

/**
 * GitHub repository link: https://github.com/snitgaard/Ultimate
 * @author CSnit
 */
public class StinkyBot implements IBot
{

    private String BOTNAME = "StinkyBot";
    Random rand = new Random();
    protected int[][] allMoves = {
        {1,1},
        {0,0}, {0,2}, {2,0}, {2,2},
        {0,1}, {1,2}, {2,1}, {1,1}};
    
    @Override
    public IMove doMove(IGameState state)
    {
        for (int[] move : allMoves)
        {
            if (state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD))
            {
                for (int[] selectedMove : allMoves)
                {
                    int x = move[0] * 3 + selectedMove[0];
                    int y = move[1] * 3 + selectedMove[1];
                    if (state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD))
                    {
                        return new Move(x, y);
                    }
                }
            } else
            {
                    return state.getField().getAvailableMoves().get(0);
            }
        }
        return state.getField().getAvailableMoves().get(0);
    }

    @Override
    public String getBotName()
    {
        return BOTNAME;
    }
}
