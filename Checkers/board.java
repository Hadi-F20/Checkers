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
 */

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.*;
import Checkers.piece.Piece;

public class board {

    private static Board gameBoard;
    private static volatile Integer movedPiecePosition = null;
    private static volatile Integer movedPieceOldPosition = null;

    public static void main(String args[]) {
        gameBoard = new Board();
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
                gameBoard.checkPieceCollision(movedPiece);
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

        //Check if piece move caused in to move over other players piece
        private void checkPieceCollision(Piece movedPiece) {
            //if piece moved, see if there is enemy piece between prev position and new position
            //if so delete enemy piece
            //if piece can go over another enemy let player move again
            int[] positions = movedPiece.getPositionList();
            int prevPosition = positions[0];
            int currPosition = positions[1];
            if (Math.abs((currPosition / 8) - (prevPosition / 8)) == 1) {
                System.out.println("yes");
            }

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
                    row = currPiece.getPositionList()[1] / 8;
                    col = currPiece.getPositionList()[1] % 8;
                    if (currPiece.getPlayer() == 1) {
                        rowToCheck = row + 1;
                    } else {
                        rowToCheck = row - 1;
                    }
                    if (currBoardState[rowToCheck][col] == null) {
                        int positionToCheck = (rowToCheck * 8) + col;
                        currPiece.addToMoveList(positionToCheck);
                    }

                }
            }
            
        }

        public Piece[][] getBoardState() {
            return this.currBoardState;
        }

        //take a position, move piece in that position to new position
        public void updateBoard(int newRow, int newCol, int oldRow, int oldCol, Piece movedPiece) {
            currBoardState[newRow][newCol] = movedPiece;
            currBoardState[oldRow][oldCol] = null;
            movedPiece.clearMoveList();
            updateMoveList(movedPiece);
            setMovedPieceOldPosition(null);
            setMovedPiecePosition(null);
        }

    }

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
    
}
