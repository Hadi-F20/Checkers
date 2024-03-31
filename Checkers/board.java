package Checkers;

//TODO:
/*
  Add square highlighting indicating which squares current piece can move to
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.*;

import Checkers.piece.Piece;

public class board {

    private static Board gameBoard;
    private static volatile Integer movedPiecePosition = null;
    private static volatile Integer movedPieceOldPosition = null;
    private static int currPlayer;
    private static int player1PieceCount;
    private static int player2PieceCount;

    //Check if jump is available, if so can only do that move
    private static boolean jumpAvailable = false;

    public static void main(String args[]) {
        gameBoard = new Board();
        currPlayer = 1;
        player1PieceCount = 16;
        player2PieceCount = 16;
        while (true) {
            //loop through boardState to check collisions
            Piece[][] boardState = gameBoard.getBoardState();
            //need to get piece from original position
            if (movedPieceOldPosition != null && movedPiecePosition != null) {
                int pieceNewRow = movedPiecePosition / 8;
                int pieceNewCol = movedPiecePosition % 8;
                int pieceOldRow = movedPieceOldPosition / 8;
                int pieceOldCol = movedPieceOldPosition % 8;
                Piece movedPiece = boardState[pieceOldRow][pieceOldCol];
                gameBoard.updateBoard(pieceNewRow, pieceNewCol, pieceOldRow, pieceOldCol, movedPiece);          
            }
            if (player1PieceCount == 0 || player2PieceCount == 0) {
                break;
            }
        }
        
    }

    public static class Board {
        private final int SQUARE_WIDTH_HEIGHT = 100;
        private final int FRAME_WIDTH = 1000;
        private final int FRAME_HEIGHT = 1000;
        private final int PANEL_WIDTH = 800;
        private final int PANEL_HEIGHT = 800;

        private Piece[][] currBoardState;
        private JFrame frame;
        private JPanel[][] boardSquares;
        private JLayeredPane frameBackground;
        
        public Board() {
            initializeBoard();
        }

        private void initializeBoard() {
            //initialize board with JButtons as the squares of the board
            //board state start is first 2 rows are player 2 and last 2 are player 1
            currBoardState = new Piece[8][8];
            boardSquares = new JPanel[8][8];
            frame = new JFrame("Checkers");
            frameBackground = new JLayeredPane();
            frameBackground.setLayout(new GridLayout(8, 8));
            frameBackground.setBounds(100, 100, PANEL_WIDTH, PANEL_HEIGHT);
            
            frame.add(frameBackground);
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    boardSquares[i][j] = new JPanel();
                    int squareXPos = SQUARE_WIDTH_HEIGHT * (j + 1);
                    int squareYPos = SQUARE_WIDTH_HEIGHT * (i + 1);
                    Piece newPiece;
                    if ((i + j) % 2 == 0) {
                        boardSquares[i][j].setBackground(Color.WHITE);
                    } else {
                        boardSquares[i][j].setBackground(Color.BLACK);
                    }
                    if (i < 2) {
                        //Constructor, new Piece(player, position)
                        newPiece = new Piece(2, (i * 8) + j);
                        currBoardState[i][j] = newPiece;
                        frameBackground.add(newPiece.getCircle(), JLayeredPane.DEFAULT_LAYER);
                    } else if (i > 5) {
                        newPiece = new Piece(1, (i * 8) + j);
                        currBoardState[i][j] = newPiece;
                        frameBackground.add(newPiece.getCircle(), JLayeredPane.DEFAULT_LAYER);
                    } else {
                        //placeholder pieces to split gridlayout to correct order
                        JButton blankSquare = new JButton();
                        blankSquare.setBounds(squareXPos, squareYPos, 100, 100);
                        blankSquare.setEnabled(false);
                        blankSquare.setVisible(false);
                        frameBackground.add(blankSquare, JLayeredPane.DEFAULT_LAYER);
                    }
                    boardSquares[i][j].setBounds(squareXPos, squareYPos, 100, 100);
                    
                    frame.add(boardSquares[i][j]);
                    
                }
            }
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            frame.setLayout(null);
            frame.setBackground(Color.GRAY);
            frame.setVisible(true);
        }

        //update moveList of each piece based on the current move
        private void updateMoveList(Piece movedPiece) {
            int row;
            int col;
            Piece currPiece;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    currPiece = currBoardState[i][j];
                    if (currPiece == null) {
                        continue;
                    }
                    currPiece.clearMoveList();
                    int currPlayer = currPiece.getPlayer();
                    int currPosition = currPiece.getPositionList()[1];
                    int enemyPlayer = currPlayer == 1 ? 2 : 1;
                    row = currPosition / 8;
                    col = currPosition % 8;
                    if (currPiece.isKing()) {
                        //check both directions if piece is king
                        moveCheck(col, row - 1, currPiece);
                        moveCheck(col, row + 1, currPiece);
                        checkJumps(currPiece, currPlayer, enemyPlayer, row + 1, col);
                        checkJumps(currPiece, currPlayer, enemyPlayer, row - 1, col);
                    } else if (currPlayer == 1) {
                        moveCheck(col, row - 1, currPiece);
                        checkJumps(currPiece, currPlayer, enemyPlayer, row - 1, col);
                    } else {
                        moveCheck(col, row + 1, currPiece);
                        checkJumps(currPiece, currPlayer, enemyPlayer, row + 1, col);
                    }
                    jumpAvailable = jumpAvailable || !currPiece.isJumpListEmpty();
                }
            }
        }

        //check which basic moves are valid
        private void moveCheck(int col, int row, Piece currPiece) {
            if (col < 7 && (row >= 0 && row < 8) && currBoardState[row][col + 1] == null) {
                int positionToAdd = (row * 8) + (col + 1);
                currPiece.addToMoveList(positionToAdd);
            }
            if (col > 0 && (row >= 0 && row < 8) && currBoardState[row][col - 1] == null) {
                int positionToAdd = (row * 8) + (col - 1);
                currPiece.addToMoveList(positionToAdd);
            }
        }

        //check if piece can jump
        private void checkJumps(Piece currPiece, int player, int enemyPlayer, int rowToCheck, int currCol) {
            if ((rowToCheck + 1 > 7 || rowToCheck - 1 < 0)) {
                return;
            }
            if ((currCol + 1 >= 8 || currBoardState[rowToCheck][currCol + 1] == null) && (currCol - 1 < 0 || currBoardState[rowToCheck][currCol - 1] == null)) {
                return;
            }
            if (player == 1 || currPiece.isKing()) {
                if (currCol < 6 && currBoardState[rowToCheck][currCol + 1] != null && currBoardState[rowToCheck][currCol + 1].getPlayer() == enemyPlayer && currBoardState[rowToCheck - 1][currCol + 2] == null) {
                    currPiece.addToJumpList(((rowToCheck - 1) * 8) + currCol + 2);
                }
                if (currCol > 1 && currBoardState[rowToCheck][currCol - 1] != null && currBoardState[rowToCheck][currCol - 1].getPlayer() == enemyPlayer && currBoardState[rowToCheck - 1][currCol - 2] == null) {
                    currPiece.addToJumpList(((rowToCheck - 1) * 8) + currCol - 2);
                }
            }
            if (player == 2 || currPiece.isKing()) {
                if (currCol < 6 && currBoardState[rowToCheck][currCol + 1] != null && currBoardState[rowToCheck][currCol + 1].getPlayer() == enemyPlayer && currBoardState[rowToCheck + 1][currCol + 2] == null) {
                    currPiece.addToJumpList(((rowToCheck + 1) * 8) + currCol + 2);
                }
                if (currCol > 1 && currBoardState[rowToCheck][currCol - 1] != null && currBoardState[rowToCheck][currCol - 1].getPlayer() == enemyPlayer && currBoardState[rowToCheck + 1][currCol - 2] == null) {
                    currPiece.addToJumpList(((rowToCheck + 1) * 8) + currCol - 2);
                }
            }
        }

        //remove the piece current piece jumped over
        private void removeJumpedPiece(int row, int col) {
            Piece pieceToRemove = currBoardState[row][col];
            JButton circleToRemove = pieceToRemove.getCircle();
            if (pieceToRemove.getPlayer() == 1) {
                board.player1PieceCount--;
            } else {
                board.player2PieceCount--;
            }
            frameBackground.remove((Component) circleToRemove);
            frameBackground.repaint();
            currBoardState[row][col] = null;
        }

        //check if there are any jumps available for player
        private boolean checkJumpsForPlayer(int player) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece currPiece = currBoardState[i][j];
                    if (currPiece == null) {continue;}
                    if (currPiece.getPlayer() == player) {
                        if (!currPiece.isJumpListEmpty()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        //check if jump available for piece
        private boolean noJumpsForPiece(Piece currPiece) {
            return currPiece.isJumpListEmpty();
        }

        //take a position, move piece in that position to new position
        public void updateBoard(int newRow, int newCol, int oldRow, int oldCol, Piece movedPiece) {
            int jumpRow = newRow > oldRow ? oldRow + 1 : oldRow - 1;
            int jumpCol = newCol > oldCol ? oldCol + 1 : oldCol - 1;
            if (Math.abs(newRow - oldRow) == 2) {
                removeJumpedPiece(jumpRow, jumpCol);
            }
            currBoardState[newRow][newCol] = movedPiece;
            currBoardState[oldRow][oldCol] = null;
            movedPiece.clearMoveList();
            updateMoveList(movedPiece);
            if (Math.abs(newRow - oldRow) == 1 || noJumpsForPiece(movedPiece)) {
                currPlayer = currPlayer == 1 ? 2 : 1;
                setJumpAvailableToFalse();
            }
            setMovedPieceOldPosition(null);
            setMovedPiecePosition(null);
            jumpAvailable = checkJumpsForPlayer(currPlayer);
        }

        public Piece[][] getBoardState() {
            return this.currBoardState;
        }
    }


    /*
     * Setters and Getters
     */
    public int getMovedPiecePosition() {
        return board.movedPiecePosition;
    }

    public static void setMovedPiecePosition(Integer position) {
        board.movedPiecePosition = position;
    }

    public static void setMovedPieceOldPosition(Integer position) {
        board.movedPieceOldPosition = position;
    }

    public static Integer getMovePieceOldPositon() {
        return board.movedPieceOldPosition;
    }

    public static Integer getMovedPiecePositon() {
        return board.movedPiecePosition;
    }

    public static int getCurrPlayer() {
        return board.currPlayer;
    }

    public static boolean isJumpAvailable() {
        return board.jumpAvailable;
    }
    
    public static void setJumpAvailableToFalse() {
        board.jumpAvailable = false;
    }
}
