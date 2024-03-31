package Checkers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;

public class piece {

    /*
     * Fix piece movement when it reaches edge, create instance var that says what direction it can move, change it when it reaches edge
     */


    public static void main(String args[]) {
        return;
    }

    public static class Piece {
        
        private static final int RADIUS = 40;
        //COLOR1 is for player 1 pieces, COLOR2 for player 2
        private static final Color COLOR1 = Color.GREEN;
        private static final Color COLOR2 = Color.RED;

        private int player;
        private CircleButton shape;
        private boolean king;

        //position is index on board: position / 8 = row; position % 8 = col
        //once board uses prevPosition, reset it to currPosition
        private int currPosition;
        private int prevPosition;

        //add list that says which spaces piece can move to using 1 move
        //items in arr will be offset of space based off curr position:
            //ex, piece on 50, item in arr is 42, piece can move to 42 (row = 42 / 8, col = 42 % 8)
        private ArrayList<Integer> moveList;

        /*
         * CONSTRUCTORS
         */
        //create the piece based on the player piece is for
        public Piece(int player, int position) {
            this.player = player;
            this.currPosition = position;
            this.prevPosition = position;
            this.shape = createCircle(player, 50, 50, position);
            this.moveList = new ArrayList<>();
            this.king = false;
            initMoveList(position, player);
        }

        /*
         * PRIVATE METHODS
        */

        //initialize moveList, only peices not in first as last row can move on first turn
        private void initMoveList(int position, int player) {
            if (player == 1 && position > 7) {
                this.moveList.add(8 + position);
            } else if (player == 2 && position < 55) {
                this.moveList.add(-8 + position);
            }
        }

        //create the circle based on which player piece is for
        private CircleButton createCircle(int player, int xCoord, int yCoord, int position) {
            Color colorOfPiece;
            if (player == 1) {
                colorOfPiece = COLOR1;
            } else {
                colorOfPiece = COLOR2;
            }
            CircleButton circle = new CircleButton(xCoord, yCoord, RADIUS, colorOfPiece);
            circle.setOpaque(false);
            circle.setEnabled(false);
            circle.setBorderPainted(false);
            addMouseEventListener(circle);
            return circle;
        }

        //check if move is valid
        private boolean isValidMove(int movePosition) {
            //check if movePosition is in moveList
            return moveList.contains(movePosition);
        }

        private void addMouseEventListener(CircleButton circle) {
            
            circle.addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {
                    //to drag pieces
                    int newXCoord = e.getX() + circle.getX() - 50;
                    int newYCoord = e.getY() + circle.getY() - 50;
                    circle.setBounds(newXCoord, newYCoord, 100, 100);
                }
            });

            circle.addMouseListener(new MouseAdapter() {
                
                private int startXCoord;
                private int startYCoord;
                private int endXCoord;
                private int endYCoord;
                private int pieceOldRow;
                private int pieceOldCol;
                private int pieceNewRow;
                private int pieceNewCol;

                //add check to see if piece can move other direction when at end of board

                @Override
                public void mousePressed(MouseEvent e) {
                    startXCoord = circle.getX();
                    startYCoord = circle.getY();
                    pieceOldRow = startXCoord / 100;
                    pieceOldCol = startYCoord / 100 * 8;
                }


                @Override
                public void mouseReleased(MouseEvent e) {
                    //when released, see if piece ended in different square
                    int player = circle.getPlayer();
                    endXCoord = circle.getX();
                    endYCoord = circle.getY();
                    int xDiff = endXCoord - startXCoord;
                    int yDiff = endYCoord - startYCoord;
                    int yChange = 0;
                    int yChangeCheckHi;
                    int yChangeCheckLo;
                    boolean yChangeCorrect = false;
                    
                    //Check which player piece is for
                    //for p1: move should be downwards, so y must be getting bigger. if yChange > 50 && < 150, valid move
                    //if piece is at last square (y == 700), then can move backwards, so check is same as normal check for other player
                    //same thing but opposite for p2
                    
                    if (startYCoord >= 700 || player == 2) {
                        yChange = -100;
                        yChangeCheckHi = -150;
                        yChangeCheckLo = -50;
                        yChangeCorrect = yDiff > yChangeCheckHi && yDiff < yChangeCheckLo;
                    } else if (startYCoord <= 100 || player == 1) {
                        yChange = 100;
                        yChangeCheckHi = 150;
                        yChangeCheckLo = 50;
                        yChangeCorrect = yDiff < yChangeCheckHi && yDiff > yChangeCheckLo;
                    }
                    pieceNewRow = startXCoord / 100;
                    pieceNewCol = ((startYCoord + yChange) / 100) * 8;
                    if ((xDiff > -50 && xDiff < 50) && (yChangeCorrect) && isValidMove(pieceNewRow + pieceNewCol)) {
                        //change position of piece
                        movePieceOnBoard(yChange, 0);
                    } else {
                        circle.setBounds(startXCoord, startYCoord, 100, 100);
                    }
                }

                //move piece on board
                //implement xChange when it moves over enemy piece
                private void movePieceOnBoard(int yChange, int xChange) {
                    circle.setBounds(startXCoord, startYCoord + yChange, 100, 100);
                    board.setMovedPiecePosition(pieceNewRow + pieceNewCol);
                    board.setMovedPieceOldPosition((startXCoord / 100) + ((startYCoord / 100) * 8));
                    Piece.this.currPosition = pieceNewRow + pieceNewCol;
                    Piece.this.prevPosition = pieceOldRow + pieceOldCol;
                }
            });
        }

        /*
         * PUBLIC METHODS
         */

        //return color of piece
        public int getPlayer() {
            return this.player;
        }

        //get the shape of the piece
        public JButton getCircle() {
            return this.shape;
        }

        //returns int list of prevPosition[0] and currPosition[1]
        public int[] getPositionList() {
            int[] retArr = new int[2];
            retArr[0] = this.prevPosition;
            retArr[1] = this.currPosition;
            return retArr;
        }

        //sets prevPosition to currPosition
        //to be used after board makes its adjustments due to position change to stop it from flagging a change
        public void setPositionToCurrent() {
            this.prevPosition = this.currPosition;
        }

        //add move to moveList
        public void addToMoveList(int spaceCanMoveTo) {
            this.moveList.add(spaceCanMoveTo);
        }

        public void clearMoveList() {
            this.moveList.clear();
        }

        public ArrayList<Integer> getMoveList() {
            return this.moveList;
        }


        /*
         * PRIVATE CLASSES
         */
        private class CircleButton extends JButton {

            private int x, y, radius;
            private Color playerColor;

            public CircleButton(int x, int y, int radius, Color playerColor) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.playerColor = playerColor;
            }

            public int getPlayer() {
                if (playerColor.equals(COLOR1)) {
                    return 1;
                } else {
                    return 2;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(playerColor);
                g.fillOval(x - radius, y - radius, 2*radius, 2*radius);
            }
            
        }
    }
}
