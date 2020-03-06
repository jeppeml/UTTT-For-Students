/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import java.util.List;

/**
 *
 * @author ander
 */
public class PrimusOptimus implements IBot {

    private static final String botName = "Primus Optimus";

    @Override
    public IMove doMove(IGameState state) {

        List<IMove> allAvailableMoves = state.getField().getAvailableMoves();
        return allAvailableMoves.get(0);
    }

    @Override
    public String getBotName() {
        return botName;
    }

    //Link to Github repo: https://github.com/PresidentDungeon/Ultimate-Tick-Tac-Toe
    
}
