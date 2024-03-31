package Checkers;

//TODO:
/*
  movement works, cant move if there is piece in front
  need to add jumps if piece forward diagonal is enemy and space to jump to is open
  if you have a jump available you have to jump
  add turn tracker, if you moved a piece or did all jumps possible end turn
  if piece reaches end, make it a king so it can jump backwards
    king can jump diagonally forward and backwards
  add score tracker or something

  Some jumps not working, like green pos 14 cant jump if red is in the jumpable square, 21 i think
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

    public static void main(String args[]) {
        gameBoard = new Board();
        currPlayer = 1;
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
            //board state start is first 2 rows as 1 to indicate white and last 2 rows as 2 to indicate red
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
                        newPiece = new Piece(1, (i * 8) + j);
                        currBoardState[i][j] = newPiece;
                        frameBackground.add(newPiece.getCircle(), JLayeredPane.DEFAULT_LAYER);
                    } else if (i > 5) {
                        newPiece = new Piece(2, (i * 8) + j);
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

        private void updateMoveList(Piece movedPiece) {
            int row;
            int col;
            int rowToCheck;
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
                    if (currPlayer == 1) {
                        rowToCheck = row + 1;
                    } else {
                        rowToCheck = row - 1;
                    }
                    if (currBoardState[rowToCheck][col] == null) {
                        int positionToCheck = (rowToCheck * 8) + col;
                        currPiece.addToMoveList(positionToCheck);
                    }
                    checkJumps(currPiece, currPlayer, enemyPlayer, rowToCheck, col);
                }
            }
            
        }


        //IMPLEMENT OTHER PLAYER
        private void checkJumps(Piece currPiece, int player, int enemyPlayer, int rowToCheck, int currCol) {
            if ((rowToCheck + 1 > 7 || rowToCheck - 1 < 0) || (currCol <= 1 || currCol >= 6)) {
                return;
            }
            if (currBoardState[rowToCheck][currCol + 1] == null && currBoardState[rowToCheck][currCol - 1] == null) {
                return;
            }
            if (player == 1) {
                if (currBoardState[rowToCheck][currCol + 1] != null && currBoardState[rowToCheck][currCol + 1].getPlayer() == enemyPlayer && currBoardState[rowToCheck + 1][currCol + 2] == null) {
                    currPiece.addToMoveList(((rowToCheck + 1) * 8) + currCol + 2);
                }
                if (currBoardState[rowToCheck][currCol - 1] != null && currBoardState[rowToCheck][currCol - 1].getPlayer() == enemyPlayer && currBoardState[rowToCheck + 1][currCol - 2] == null) {
                    currPiece.addToMoveList(((rowToCheck + 1) * 8) + currCol - 2);
                }
            } else {
                if (currBoardState[rowToCheck][currCol + 1] != null && currBoardState[rowToCheck][currCol + 1].getPlayer() == enemyPlayer && currBoardState[rowToCheck - 1][currCol + 2] == null) {
                    currPiece.addToMoveList(((rowToCheck - 1) * 8) + currCol + 2);
                }
                if (currBoardState[rowToCheck][currCol - 1] != null && currBoardState[rowToCheck][currCol - 1].getPlayer() == enemyPlayer && currBoardState[rowToCheck - 1][currCol - 2] == null) {
                    currPiece.addToMoveList(((rowToCheck - 1) * 8) + currCol - 2);
                }
            }
            
        }

        private void removeJumpedPiece(int row, int col) {
            JButton pieceToRemove = currBoardState[row][col].getCircle();
            frameBackground.remove((Component) pieceToRemove);
            frameBackground.repaint();
            //pieceToRemove.setEnabled(false);
            //pieceToRemove.setVisible(false);
            currBoardState[row][col] = null;
        }


        public Piece[][] getBoardState() {
            return this.currBoardState;
        }

        //take a position, move piece in that position to new position
        public void updateBoard(int newRow, int newCol, int oldRow, int oldCol, Piece movedPiece) {
            int jumpRow;
            int jumpCol;
            if (!movedPiece.isKing()) {
                jumpRow = currPlayer == 1 ? newRow - 1 : newRow + 1;
                jumpCol = newCol > oldCol ? oldCol + 1 : oldCol - 1;
            } else {
                //NOT IMPLEMENTED YET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
                jumpRow = 0;
                jumpCol = 0;
            }
            if (Math.abs(newRow - oldRow) == 2) {
                removeJumpedPiece(jumpRow, jumpCol);
            }
            currBoardState[newRow][newCol] = movedPiece;
            currBoardState[oldRow][oldCol] = null;
            movedPiece.clearMoveList();
            updateMoveList(movedPiece);
            setMovedPieceOldPosition(null);
            setMovedPiecePosition(null);
            currPlayer = currPlayer == 1 ? 2 : 1;
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
    
}
