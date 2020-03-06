/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import java.util.List;
import java.util.Random;

/**
 *
 * @author FCAI
 */
public class GLHF implements IBot {

    Random rand = new Random();
    private static final String Available_Field = ".";
    private String Player;
    int[] playBoard;

    @Override
    public IMove doMove(IGameState state) {
        Player = state.getMoveNumber() % 2 + "";
        List<IMove> moves = state.getField().getAvailableMoves();
        String[][] board = state.getField().getMacroboard();

        if (!isBoardFull(board)) {
            playBoard = getMacroBoard(moves);
            if (canStayBoard(playBoard, moves)) {
                IMove m = new Move(playBoard[0] * 3 + playBoard[0], playBoard[1] * 3 + playBoard[1]);
                return m;
            } else {
                if (canCorner(board, moves)) {
                    return playCorner(moves, playBoard);
                } else if (canConnect(board, moves)) {
                    return connect(board, moves);
                } else {
                    return moves.get(rand.nextInt(moves.size()));
                }
            }
        }
        return moves.get(rand.nextInt(moves.size()));
    }

    private boolean isBoardFull(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].equals(Available_Field)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] getMacroBoard(List<IMove> moves) {
        IMove m = moves.get(0);
        int x = m.getX() / 3;
        int y = m.getY() / 3;

        return new int[]{x, y};
    }

    private boolean canStayBoard(int[] macro, List<IMove> moves) {
        for (IMove move : moves) {
            if (macro[0] * 3 + macro[0] == move.getX() && macro[1] * 3 + macro[1] == move.getY()) {
                return true;
            }
        }
        return false;
    }

    private IMove connect(String[][] board, List<IMove> moves) {
        if (board[0][0].equals(Player) && board[1][0].equals(Player)) {
            int[] m = {2 + 3 * playBoard[0], 0 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(2 + 3 * playBoard[0], 0 + 3 * playBoard[1]);
            }
        }
        if (board[1][0].equals(Player) && board[2][0].equals(Player)) {
            int[] m = {0 + 3 * playBoard[0], 0 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(0 + 3 * playBoard[0], 0 + 3 * playBoard[1]);
            }
        }
        if (board[0][0].equals(Player) && board[2][0].equals(Player)) {
            int[] m = {1 + 3 * playBoard[0], 0 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(1 + 3 * playBoard[0], 0 + 3 * playBoard[1]);
            }
        }
        if (board[0][1].equals(Player) && board[1][1].equals(Player)) {
            int[] m = {2 + 3 * playBoard[0], 1 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(2 + 3 * playBoard[0], 1 + 3 * playBoard[1]);
            }
        }
        if (board[1][1].equals(Player) && board[2][1].equals(Player)) {
            int[] m = {0 + 3 * playBoard[0], 1 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(0 + 3 * playBoard[0], 1 + 3 * playBoard[1]);
            }
        }
        if (board[0][1].equals(Player) && board[2][1].equals(Player)) {
            int[] m = {1 + 3 * playBoard[0], 1 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(1 + 3 * playBoard[0], 1 + 3 * playBoard[1]);
            }
        }
        if (board[0][2].equals(Player) && board[1][2].equals(Player)) {
            int[] m = {2 + 3 * playBoard[0], 2 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(2 + 3 * playBoard[0], 2 + 3 * playBoard[1]);
            }
        }
        if (board[1][2].equals(Player) && board[2][2].equals(Player)) {
            int[] m = {0 + 3 * playBoard[0], 2 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(0 + 3 * playBoard[0], 2 + 3 * playBoard[1]);
            }
        }
        if (board[0][2].equals(Player) && board[2][2].equals(Player)) {
            int[] m = {1 + 3 * playBoard[0], 2 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(1 + 3 * playBoard[0], 2 + 3 * playBoard[1]);
            }
        }
        if (board[0][0].equals(Player) && board[1][1].equals(Player)) {
            int[] m = {2 + 3 * playBoard[0], 2 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(2 + 3 * playBoard[0], 2 + 3 * playBoard[1]);
            }
        }
        if (board[0][0].equals(Player) && board[2][2].equals(Player)) {
            int[] m = {1 + 3 * playBoard[0], 1 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(1 + 3 * playBoard[0], 1 + 3 * playBoard[1]);
            }
        }
        if (board[1][1].equals(Player) && board[2][2].equals(Player)) {
            int[] m = {0 + 3 * playBoard[0], 0 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(0 + 3 * playBoard[0], 0 + 3 * playBoard[1]);
            }
        }
        if (board[2][0].equals(Player) && board[1][1].equals(Player)) {
            int[] m = {0 + 3 * playBoard[0], 2 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(0 + 3 * playBoard[0], 2 + 3 * playBoard[1]);
            }
        }
        if (board[1][1].equals(Player) && board[0][2].equals(Player)) {
            int[] m = {2 + 3 * playBoard[0], 0 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(2 + 3 * playBoard[0], 0 + 3 * playBoard[1]);
            }
        }
        if (board[2][0].equals(Player) && board[0][2].equals(Player)) {
            int[] m = {1 + 3 * playBoard[0], 1 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(1 + 3 * playBoard[0], 1 + 3 * playBoard[1]);
            }
        }
        if (board[0][0].equals(Player) && board[0][1].equals(Player)) {
            int[] m = {0 + 3 * playBoard[0], 2 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(0 + 3 * playBoard[0], 2 + 3 * playBoard[1]);
            }
        }
        if (board[0][0].equals(Player) && board[0][2].equals(Player)) {
            int[] m = {0 + 3 * playBoard[0], 1 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(0 + 3 * playBoard[0], 1 + 3 * playBoard[1]);
            }
        }
        if (board[0][3].equals(Player) && board[0][1].equals(Player)) {
            int[] m = {0 + 3 * playBoard[0], 0 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(0 + 3 * playBoard[0], 0 + 3 * playBoard[1]);
            }
        }
        if (board[1][0].equals(Player) && board[1][1].equals(Player)) {
            int[] m = {1 + 3 * playBoard[0], 2 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(1 + 3 * playBoard[0], 2 + 3 * playBoard[1]);
            }
        }
        if (board[1][0].equals(Player) && board[1][2].equals(Player)) {
            int[] m = {1 + 3 * playBoard[0], 1 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(1 + 3 * playBoard[0], 1 + 3 * playBoard[1]);
            }
        }
        if (board[1][1].equals(Player) && board[1][2].equals(Player)) {
            int[] m = {1 + 3 * playBoard[0], 0 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(1 + 3 * playBoard[0], 0 + 3 * playBoard[1]);
            }
        }
        if (board[2][0].equals(Player) && board[2][1].equals(Player)) {
            int[] m = {2 + 3 * playBoard[0], 2 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(2 + 3 * playBoard[0], 2 + 3 * playBoard[1]);
            }
        }
        if (board[2][0].equals(Player) && board[2][2].equals(Player)) {
            int[] m = {2 + 3 * playBoard[0], 1 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(2 + 3 * playBoard[0], 1 + 3 * playBoard[1]);
            }
        }
        if (board[2][1].equals(Player) && board[2][2].equals(Player)) {
            int[] m = {2 + 3 * playBoard[0], 0 + 3 * playBoard[1]};

            if (haveMove(moves, m)) {
                return new Move(2 + 3 * playBoard[0], 0 + 3 * playBoard[1]);
            }
        }
        return moves.get(rand.nextInt(moves.size()));
    }

    private IMove playCorner(List<IMove> moves, int[] playBoard) {
        int[] corner1 = {0, 0};
        int[] corner2 = {2, 0};
        int[] corner3 = {0, 2};
        int[] corner4 = {2, 2};

        for (IMove move : moves) {
            if (move.getX() == corner1[0] + 3 * playBoard[0] && move.getY() == corner1[1] + 3 * playBoard[1]) {
                return move;
            } else if (move.getX() == corner2[0] + 3 * playBoard[0] && move.getY() == corner2[1] + 3 * playBoard[1]) {
                return move;
            } else if (move.getX() == corner3[0] + 3 * playBoard[0] && move.getY() == corner3[1] + 3 * playBoard[1]) {
                return move;
            } else if (move.getX() == corner4[0] + 3 * playBoard[0] && move.getY() == corner4[1] + 3 * playBoard[1]) {
                return move;
            }
        }
        return moves.get(rand.nextInt(moves.size()));
    }

    private boolean canCorner(String[][] board, List<IMove> moves) {
        if (board[0][0].equals(Available_Field) || board[2][0].equals(Available_Field) || board[0][2].equals(Available_Field) || board[2][2].equals(Available_Field)) {
            int playedFields = 0;

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j].equals(Player)) {
                        playedFields++;
                    }
                }
            }
            if (playedFields < 2) {
                return true;
            }
        }
        return false;
    }

    private boolean canConnect(String[][] board, List<IMove> moves) {
        if (board[0][0].equals(Player) && board[1][1].equals(Player) || board[0][0].equals(Player) && board[2][2].equals(Player)
                || board[1][1].equals(Player) && board[2][2].equals(Player) || board[2][0].equals(Player) && board[1][1].equals(Player) || board[0][2].equals(Player) && board[1][1].equals(Player) || board[2][0].equals(Player) && board[0][2].equals(Player)
                || board[0][0].equals(Player) && board[1][0].equals(Player) || board[1][0].equals(Player) && board[2][0].equals(Player) || board[0][0].equals(Player) && board[2][0].equals(Player)
                || board[0][1].equals(Player) && board[1][1].equals(Player) || board[1][1].equals(Player) && board[2][1].equals(Player) || board[0][1].equals(Player) && board[2][1].equals(Player)
                || board[0][2].equals(Player) && board[1][2].equals(Player) || board[1][2].equals(Player) && board[2][2].equals(Player) || board[0][2].equals(Player) && board[2][2].equals(Player)) {
            return true;
        }
        return false;
    }

    private boolean haveMove(List<IMove> moves, int[] playMove) {
        for (IMove move : moves) {
            if (move.getX() == playMove[0] && move.getY() == playMove[1]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getBotName() {
        return "GLHF";
    }

}
