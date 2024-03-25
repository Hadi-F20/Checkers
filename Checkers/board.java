package Checkers;

//TODO:
/*
  Add piece movement
    swap it with the blank piece it collided with (is pieces x and y between other pieces x and y + width and height)
  Add rules
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.*;
import Checkers.piece.Piece;

public class board {

    public static void main(String args[]) {
        Board gameBoard = new Board();
        while (true) {
            gameBoard.checkPieceMovement();
        }
    }
    public static class Board {
        private final int SQUARE_WIDTH_HEIGHT = 100;
        private final int FRAME_WIDTH = 1000;
        private final int FRAME_HEIGHT = 1000;
        private final int PANEL_WIDTH = 800;
        private final int PANEL_HEIGHT = 800;

        private int[][] currBoardState;
        private JFrame frame;
        private JPanel[][] boardSquares;
        private JLayeredPane frameBackground;
        
        public Board() {
            initializeBoard();
        }

        private void initializeBoard() {
            //initialize board with JButtons as the squares of the board
            //board state start is first 2 rows as 1 to indicate white and last 2 rows as 2 to indicate red
            currBoardState = new int[8][8];
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
                        newPiece = new Piece(1);
                        currBoardState[i][j] = 1;
                        frameBackground.add(newPiece.getCircle(), JLayeredPane.DEFAULT_LAYER);
                    } else if (i > 5) {
                        newPiece = new Piece(2);
                        currBoardState[i][j] = 2;
                        frameBackground.add(newPiece.getCircle(), JLayeredPane.DEFAULT_LAYER);
                    } else {
                        //placeholder pieces to split gridlayout to correct order
                        //use to see if piece can move here, if there is blankSquare button, then no player piece there
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

        //Check if piece has moved to another square, if it did then redraw in that panel
        //if not then place back in its original position so that it doesnt look weird
        public void checkPieceMovement() {
            
        }
    }
}
