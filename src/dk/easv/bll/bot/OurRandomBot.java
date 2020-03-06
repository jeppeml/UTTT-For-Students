// Link to Github Repository
//https://github.com/mrbacky/UltimateTicTacToe
package dk.easv.bll.bot;

import java.util.Random;
import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;


public class OurRandomBot implements IBot {
     Random random1 = new Random();

    int[][] preferredMoves = {
        {random1.nextInt(3), random1.nextInt(3)}, {random1.nextInt(3), random1.nextInt(3)}, {random1.nextInt(3), random1.nextInt(3)}, {random1.nextInt(3), random1.nextInt(3)}, //Corners ordered across
        {random1.nextInt(3), random1.nextInt(3)}, {random1.nextInt(3), random1.nextInt(3)}, {random1.nextInt(3), random1.nextInt(3)}, {random1.nextInt(3), random1.nextInt(3)}, //Outer Middles ordered across
        {random1.nextInt(3), random1.nextInt(3)}};

    private static final String BOTNAME = "Local Prio ListBot";

    @Override
    public IMove doMove(IGameState state) {

        //Find macroboard to play in
        for (int[] move : preferredMoves) {
            if (state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD)) {
                //find move to play
                for (int[] selectedMove : preferredMoves) {
                    int x = move[0] * 3 + selectedMove[0];
                    int y = move[1] * 3 + selectedMove[1];
                    if (state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD)) {
                        return new Move(x, y);
                    }
                }
            }
        }

        //NOTE: Something failed, just take the first available move I guess!
        return state.getField().getAvailableMoves().get(0);
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}


