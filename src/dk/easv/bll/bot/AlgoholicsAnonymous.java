package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Introduction
 *
 * The idea was to build a very simple kind of score system/framework for the
 * UTTT bot, so the bot always moves to the board cell with the highest score.
 *
 * Scores are calculated based on the implemented algorithms. To calculate more
 * comprehensive scores, more algorithms can be implemented. It was not possible
 * to implement more algorithms within the given timeframe.
 *
 * Scores can be given and kept for each of the 9 macroboard cells (the overall
 * 3x3 = 9 cells), and for each of the 9 microboard's 9 local cells (9x 3x3 =
 * all 81 cells). Scores given are kept in the two variables macroboardCellScore
 * and microboardCellScore.
 *
 * In the end the scores for the macro- and microboard cells gets added so each
 * of the 81 cells gets its total score, and the bot's best moves are
 * determined. The bot moves to the cell with the highest score on the full 9x9
 * board, as long as the move is in the gamestates list of valid moves. This is
 * done so the bot picks the best move on the board, even when more microboard
 * as avaliable for play. Everytime the bot is asked to make a new move all
 * scores gets re-calculated, based on the new gamestate the bot's doMove method
 * takes as an argument.
 *
 * Why: Small compulsory assignment studying Computer Engineering at EASV (DK).
 * Goal: Have fun, practice Java, get a (very) small introduction to algorithms.
 * State: Yes lots of things could be different, better, improved, refactored,
 * and made more efficient. I also hope you are not afraid of nested for loops.
 * Disclaimer: Use and abuse
 *
 * @author Troels Klein
 *
 */
public class AlgoholicsAnonymous implements IBot {

    // set bot name (very important choice)
    private static final String BOTNAME = "Mogens";

    // initializing some convenient instance variables
    private IGameState state; // current gamestate

    private String opponentPiece;
    private String botPiece;

    // score keeping
    private int[][] macroboardCellScore = new int[3][3]; // 3x3 macroboard
    private int[][][] microboardCellScore = new int[9][3][3]; // 9x 3x3 microboards

    // score settings
    private static final int SCORE_FOR_MACROBOARD_CELLS_WHERE_OPPONENT_CAN_WIN_ON_NEXT_VISIT = -5; // negative score to keep opponent away
    private static final int SCORE_FOR_MACROBOARD_CELLS_WHERE_BOT_CAN_WIN_ON_NEXT_VISIT = 5; // positive score makes cell more attractive to our bot
    private static final int SCORE_FOR_MICROBOARD_CELLS_WHERE_BOT_CAN_GET_TWO_IN_A_ROW_ON_NEXT_VISIT = 2;
    private static final int SCORE_FOR_MICROBOARD_CELLS_WHERE_BOT_CAN_GET_THREE_IN_A_ROW_ON_NEXT_VISIT = 5;
    private static final int SCORE_FOR_MACROBOARD_CELLS_WHERE_BOT_CAN_GET_THREE_IN_A_ROW_ON_NEXT_VISIT = 5;
    // add more score settings for your own algorithms here
    // add more score settings for your own algorithms here
    // add more score settings for your own algorithms here

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     *
     * @return The selected move we want to make.
     */
    @Override
    public IMove doMove(IGameState state) {

        // update instance vars
        this.state = state;
        botPiece = state.getMoveNumber() % 2 + "";
        opponentPiece = botPiece.equals(0) ? "1" : "0";

        // clear scores from previous move
        resetScores();

        // do algorithm stuff        
        macroboardAlgorithms();
        microboardAlgorithms();

        IMove move = getNextMove();

        return move;
    }

    private void macroboardAlgorithms() {

        // implemented algorithm
        scoreMacroboardCellsWherePlayersCanWinMicroboardOnNextVisit();

        // ideas for future implementations:
        // score macrocells by current macroboard state (based on which macro cells are important to who and)
        // give low score to macrocells that are already taken
        // if macroboard is relatively empty try send opponent to less valuable macrocells fx. (0,0) (2,1) and (1,2)
        // add your own agorithm here
        // add your own agorithm here
        // add your own agorithm here
    }

    private void microboardAlgorithms() {

        // implemented algorihtm
        scoreMicroboardCellsWhereBotCanGetTwoInARowOnNextVisit();
        scoreMicroboardCellsWhereBotCanGetThreeInARowOnNextVisit();

        // ideas for future implementations:
        // for each microboard check cells where bot can make a fork (new option to get 3-in-a-row)
        // add your own agorithm here
        // add your own agorithm here
        // add your own agorithm here
    }

    /**
     * Checks all cells on all microboards for if bot can get two-in-a-row on
     * next visit. It must also be possible to get 3-in-a-row later. A score is
     * given to related cells.
     */
    private void scoreMicroboardCellsWhereBotCanGetTwoInARowOnNextVisit() {

        int countPlayerPieces;
        int countAvailablePieces;
        int countMicroboard = 0;
        int countCheck;

        // lookup table
        IMove[][] boardCheckCellCoordinates = getBoardCheckCellCoordinatesTable();

        // for each microboard
        for (String[][] microboard : getAllMicroboards()) {

            // reset counters
            countCheck = 0;

            // run all 8 checks per board => 3 rows, 3 cols, 2 diagonals
            for (String[] checks : prepareBoardForCheck(microboard)) {

                // reset counters
                countPlayerPieces = 0;
                countAvailablePieces = 0;

                // iterate through the 3 cells in each check
                for (String cell : checks) {

                    // count if bots piece is in cell
                    if (cell.equals(botPiece)) {
                        countPlayerPieces++;
                    }

                    // count if cell is empty
                    if (cell.equals(IField.EMPTY_FIELD) || cell.equals(IField.AVAILABLE_FIELD)) {
                        countAvailablePieces++;
                    }
                }

                // if 2-in-a-row is possible in this check
                if (countPlayerPieces == 1 && countAvailablePieces == 2) {

                    // give score to related cells for this check
                    for (IMove cell : boardCheckCellCoordinates[countCheck]) {
                        microboardCellScore[countMicroboard][cell.getX()][cell.getY()] += SCORE_FOR_MICROBOARD_CELLS_WHERE_BOT_CAN_GET_TWO_IN_A_ROW_ON_NEXT_VISIT;
                    }
                }
                countCheck++;
            }
            countMicroboard++;
        }
    }

    private void scoreMicroboardCellsWhereBotCanGetThreeInARowOnNextVisit() {

        int countPlayerPieces;
        int countAvailablePieces;
        int countMicroboard = 0;
        int countCheck;

        // lookup table
        IMove[][] boardCheckCellCoordinates = getBoardCheckCellCoordinatesTable();

        // for each microboard
        for (String[][] microboard : getAllMicroboards()) {

            // reset counters
            countCheck = 0;

            // run all 8 checks per board => 3 rows, 3 cols, 2 diagonals
            for (String[] checks : prepareBoardForCheck(microboard)) {

                // reset counters
                countPlayerPieces = 0;
                countAvailablePieces = 0;

                // iterate through the 3 cells in each check
                for (String cell : checks) {

                    // count if bots piece is in cell
                    if (cell.equals(botPiece)) {
                        countPlayerPieces++;
                    }

                    // count if cell is empty
                    if (cell.equals(IField.EMPTY_FIELD) || cell.equals(IField.AVAILABLE_FIELD)) {
                        countAvailablePieces++;
                    }
                }

                // if 3-in-a-row is possible in this check
                if (countPlayerPieces == 2 && countAvailablePieces == 1) {

                    // give score to related cells for this check
                    for (IMove cell : boardCheckCellCoordinates[countCheck]) {
                        microboardCellScore[countMicroboard][cell.getX()][cell.getY()] += SCORE_FOR_MICROBOARD_CELLS_WHERE_BOT_CAN_GET_THREE_IN_A_ROW_ON_NEXT_VISIT;
                    }
                }
                countCheck++;
            }
            countMicroboard++;
        }
    }

    /**
     * Sets a score for macroboard cells where the related microboard can be won
     * by opponent on next visit.
     */
    private void scoreMacroboardCellsWherePlayersCanWinMicroboardOnNextVisit() {

        // helper vars
        int microboardNum = 0;
        int macroboardCellX;
        int macroboardCellY;

        // for each microboard
        for (String[][] microboard : getAllMicroboards()) {

            // determine which macroboard cell current microboard relates to
            macroboardCellX = microboardNum % 3;
            macroboardCellY = microboardNum / 3;
            microboardNum++;

            // update score for this macroboard cell if opponent can win on next visit
            if (canPlayerWinBoardOnNextVisit(microboard, opponentPiece)) {
                macroboardCellScore[macroboardCellX][macroboardCellY] += SCORE_FOR_MACROBOARD_CELLS_WHERE_OPPONENT_CAN_WIN_ON_NEXT_VISIT;
            }

            // update score for this macroboard cell if bot can win on next visit
            if (canPlayerWinBoardOnNextVisit(microboard, botPiece)) {

                // update score for this macroboard cell
                macroboardCellScore[macroboardCellX][macroboardCellY] += SCORE_FOR_MACROBOARD_CELLS_WHERE_BOT_CAN_WIN_ON_NEXT_VISIT;
            }
        }
    }

    private void scoreMacroboardCellsWhereBotCanWinMacroboardCellOnNextVisit() {

        // lookup table
        IMove[][] boardCheckCellCoordinates = getBoardCheckCellCoordinatesTable();

        int countBotPieces;
        int countAvailablePieces;

        // run all 8 checks per board => 3 rows, 3 cols, 2 diagonals
        for (String[] checks : prepareBoardForCheck(state.getField().getMacroboard())) {

            // reset counters
            countBotPieces = 0;
            countAvailablePieces = 0;

            // iterate through the 3 cells in each check
            for (String cell : checks) {

                // count if players piece is in cell
                if (cell.equals(botPiece)) {
                    countBotPieces++;
                }

                // count if cell is empty
                if (cell.equals(IField.EMPTY_FIELD) || cell.equals(IField.AVAILABLE_FIELD) || cell.equals("-1")) {
                    countAvailablePieces++;
                }
            }

            // if 3-in-a-row is possible next time
            if (countBotPieces == 2 && countAvailablePieces == 1) {

                // give score to related cells for this check
                for (IMove cell : boardCheckCellCoordinates[0]) {
                    macroboardCellScore[cell.getX()][cell.getY()] += SCORE_FOR_MACROBOARD_CELLS_WHERE_BOT_CAN_GET_THREE_IN_A_ROW_ON_NEXT_VISIT;
                }
            }
        }
    }

    /**
     * Checks if player (opponent or bot) can win a microboard on next visit
     *
     * @param board is a 3x3 board
     * @return true/false
     */
    private boolean canPlayerWinBoardOnNextVisit(String[][] board, String playerPiece) {

        int countPlayerPieces;
        int countAvailablePieces;

        // run all 8 checks per board => 3 rows, 3 cols, 2 diagonals
        for (String[] checks : prepareBoardForCheck(board)) {

            // reset counters
            countPlayerPieces = 0;
            countAvailablePieces = 0;

            // iterate through the 3 cells in each check
            for (String cell : checks) {

                // count if players piece is in cell
                if (cell.equals(playerPiece)) {
                    countPlayerPieces++;
                }

                // count if cell is empty
                if (cell.equals(IField.EMPTY_FIELD) || cell.equals(IField.AVAILABLE_FIELD) || cell.equals("-1")) {
                    countAvailablePieces++;
                }
            }

            // if 3-in-a-row is possible next time
            if (countPlayerPieces == 2 && countAvailablePieces == 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Takes the 9x9 board and converts it to an array of 2d array microboards
     *
     * @returns a list of all 9 3x3 microboards
     */
    private String[][][] getAllMicroboards() {

        // initialize 3d array to hold microboards (array of 2d arrays)
        String[][][] microboardList = new String[9][3][3];

        int microboardNum = 0;
        // iterate through 9 3x3 microboards
        for (int n = 0; n < 9; n += 3) {
            for (int m = 0; m < 9; m += 3) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        microboardList[microboardNum][j][i] = state.getField().getBoard()[j + m][i + n];
                    }
                }
                microboardNum++;
            }
        }

        return microboardList;
    }

    /**
     * Converts a 3x3 micro- or macroboard to a 8x3 2d array to ease checking.
     *
     * 8 because it checks the 3 rows, 3 cols, and 2 diagonals of the 3x3 board
     * 3 because each check consists of 3 cell pieces
     *
     * @param board a 3x3 board - either macro- or microboard
     * @return an array of the 8x 3 cell pieces that should be checked
     */
    private String[][] prepareBoardForCheck(String[][] board) {

        // initialize array
        String[][] checkArr = new String[8][3];

        // horizontal checks
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                checkArr[i][j] = board[j][i];
            }
        }

        // vertical checks
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                checkArr[i + 3][j] = board[i][j];
            }
        }

        // first diagonal check
        for (int i = 0; i < 3; i++) {
            checkArr[6][i] = board[i][i];
        }

        // second diagonal check
        for (int i = 0; i < 3; i++) {
            checkArr[7][i] = board[i][2 - i];
        }

        return checkArr;
    }

    /**
     * Gets a 2d array of the coordinates of the 3 cells in each of the 8 checks
     * performed when checking a microboard
     *
     * @return
     */
    private IMove[][] getBoardCheckCellCoordinatesTable() {

        // Array of cell coordinates for all 8 possible checks
        IMove[][] checks = new IMove[8][3];

        // horizontal checks
        for (int i = 0; i < 3; i++) { // 3 checks
            for (int j = 0; j < 3; j++) { // 3 cells
                checks[i][j] = new Move(j, i);
            }
        }

        // vertical checks
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                checks[i + 3][j] = new Move(i, j);
            }
        }

        // first diagonal check
        for (int i = 0; i < 3; i++) {
            checks[6][i] = new Move(i, i);
        }

        // second diagonal check
        for (int i = 0; i < 3; i++) {
            checks[7][i] = new Move(i, 2 - i);
        }

        // return final cell coordinates for all 8 possible checks on microboard
        return checks;
    }

    /**
     * Returns first valid move with highest score.
     *
     * The move is determined by the coordinates with best total score in
     * gameboard.
     *
     * Combines the score for each of the 9 macroboard cells, with the score of
     * its corresponding 9 microboard cells, those that would send the opponent
     * to that macroboard cell.
     *
     * Then picks first valid move with highest score.
     *
     * @return first valid move with highest score.
     */
    private IMove getNextMove() {

        // add macroboard cell scores to all related microboard cells
        addMacroboardCellScoresToRelatedMicroboardCells();

        // get map of all board moves sorted by highest score first
        Map<IMove, Integer> allMovesSortedByScore = getAllMovesSortedByScore();

        // iterate through moves starting with highest score
        for (Map.Entry<IMove, Integer> entry : allMovesSortedByScore.entrySet()) {
            IMove move = entry.getKey();
            Integer score = entry.getValue();

            // if move is in avaliable moves
            for (IMove availableMove : state.getField().getAvailableMoves()) {
                if (availableMove.getX() == move.getX() && availableMove.getY() == move.getY()) {
                    // highest scoring move that is valid
                    return move;
                }
            }
        }

        // oops! no valid move was found? should not happen, but if it does pick a random valid move
        Random rand = new Random();
        IMove randomMove = state.getField().getAvailableMoves().get(rand.nextInt(state.getField().getAvailableMoves().size()));

        return randomMove;
    }

    /**
     * Score all board moves
     *
     * @return Map with all board moves and respective scores
     */
    private Map<IMove, Integer> getAllMovesSortedByScore() {

        // empty map that holds moves as keys and score as values,
        // because keys in maps must be unique
        Map<IMove, Integer> allMoves = new HashMap<>();

        // helper variables
        int boardX;
        int boardY;

        // put each score and move in map
        for (int n = 0; n < 9; n++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    boardX = j + (n % 3) * 3;
                    boardY = i + (n / 3) * 3;

                    allMoves.put(new Move(boardX, boardY), microboardCellScore[n][j][i]);
                }
            }
        }

        // sort move map by values descending (highest scores first)
        LinkedHashMap<IMove, Integer> allMovesSortedByScore = new LinkedHashMap<>();
        allMoves.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> allMovesSortedByScore.put(x.getKey(), x.getValue()));

        return allMovesSortedByScore;
    }

    /**
     * Combine scores of macro- and microboard cells, by adding each macrobaord
     * cell score to the related microboard cell.
     *
     * Fx. the score of macroboard cell (0,0) is added to the score of each
     * microboard cell at (0,0) thereby affecting the cell by taking into
     * account which microboard the opponent gets sent to next.
     */
    private void addMacroboardCellScoresToRelatedMicroboardCells() {

        for (int n = 0; n < 9; n++) { // for each microboard
            for (int i = 0; i < 3; i++) { // for each microboard cell
                for (int j = 0; j < 3; j++) {
                    // add macroboard cell score to each related microboard cell
                    microboardCellScore[n][j][i] += macroboardCellScore[j][i];
                }
            }
        }
    }

    /**
     * Reset scores for macro- and microboard cells
     */
    private void resetScores() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                macroboardCellScore[i][j] = 0;
            }
        }

        for (int n = 0; n < 9; n++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    microboardCellScore[n][i][j] = 0;
                }
            }
        }
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
