package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.List;
import java.util.Random;

/**
 *
 * @author mjl
 */
public interface IBot {

    /**
     * Makes a turn. Implement this method to make your dk.easv.bll.bot do something.
     *
     * @param state the current dk.easv.bll.game state
     * @return The column where the turn was made.
     */
    IMove doMove(IGameState state);

    String getBotName();

    /**
     * Override and return true for bots that communicate over the network (e.g. REST clients).
     * Network bots are exempt from the per-move time limit because latency is outside
     * their control. A warning is printed to stderr if a network bot responds slowly.
     */
    default boolean isNetworkBot() { return false; }

}
