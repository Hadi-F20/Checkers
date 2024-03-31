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
        //if jumpList isnt empty then have to take a jump
        private ArrayList<Integer> jumpList;

        /*
         * CONSTRUCTORS
         */
        //create the piece based on the player piece is for
        public Piece(int player, int position) {
            this.player = player;
            this.king = false;
            this.currPosition = position;
            this.prevPosition = position;
            this.shape = createCircle(player, 50, 50);
            this.moveList = new ArrayList<>();
            this.jumpList = new ArrayList<>();
            initMoveList(position, player);
        }

        /*
         * PRIVATE METHODS
        */

        //initialize moveList, only peices not in first as last row can move on first turn
        private void initMoveList(int position, int player) {
            if (player == 1 && position < 55) {
                if (position >= 48 && position < 55) {
                    this.moveList.add(-7 + position);
                }
                if (position > 48 && position <= 55) {
                    this.moveList.add(-9 + position);
                }
            } else if (player == 2 && position > 7) {
                if (position >= 8 && position < 15) {
                    this.moveList.add(9 + position);
                }
                if (position > 8 && position <= 15) {
                    this.moveList.add(7 + position);
                }           
            }
        }

        //create the circle based on which player piece is for
        private CircleButton createCircle(int player, int xCoord, int yCoord) {
            Color colorOfPiece;
            if (player == 1) {
                colorOfPiece = COLOR1;
            } else {
                colorOfPiece = COLOR2;
            }
            CircleButton circle = new CircleButton(xCoord, yCoord, RADIUS, colorOfPiece);
            if (this.isKing()) {
                circle.setText("King");
                circle.setForeground(Color.ORANGE);
            }
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

        private boolean isValidJump(int movePosition) {
            return jumpList.contains(movePosition);
        }

        private void addMouseEventListener(CircleButton circle) {
            
            circle.addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {
                    //to drag pieces
                    if (Piece.this.player != board.getCurrPlayer()) {
                        return;
                    }
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
                private int player;

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
                    player = Piece.this.getPlayer();
                    endXCoord = circle.getX();
                    endYCoord = circle.getY();
                    int xDiff = endXCoord - startXCoord;
                    int yDiff = endYCoord - startYCoord;
                    int yChange = (int) Math.round(yDiff / 100.0) * 100;
                    int xChange = (int) Math.round(xDiff / 100.0) * 100;
                    pieceNewRow = (startXCoord + xChange) / 100;
                    pieceNewCol = ((startYCoord + yChange) / 100) * 8;
                    if (isValidJump(pieceNewRow + pieceNewCol)) {
                        movePieceOnBoard(yChange, xChange);
                    } else if (!board.isJumpAvailable() && isValidMove(pieceNewRow + pieceNewCol)) {
                        //change position of piece
                        movePieceOnBoard(yChange, xChange);
                    } else {
                        circle.setBounds(startXCoord, startYCoord, 100, 100);
                    }
                }

                //move piece on board based on coord change
                private void movePieceOnBoard(int yChange, int xChange) {
                    circle.setBounds(startXCoord + xChange, startYCoord + yChange, 100, 100);
                    board.setMovedPiecePosition(pieceNewRow + pieceNewCol);
                    board.setMovedPieceOldPosition((startXCoord / 100) + ((startYCoord / 100) * 8));
                    Piece.this.currPosition = pieceNewRow + pieceNewCol;
                    Piece.this.prevPosition = pieceOldRow + pieceOldCol;
                    if (!Piece.this.isKing()) {
                        if ((player == 1 && (pieceNewRow + pieceNewCol) < 8) || (player == 2 && (pieceNewRow + pieceNewCol) > 55)) {
                            setPieceToKing();
                        }
                    }          
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

        public void addToJumpList(int jumpPosition) {
            this.jumpList.add(jumpPosition);
        }

        public void clearMoveList() {
            this.moveList.clear();
            this.jumpList.clear();
        }

        public ArrayList<Integer> getMoveList() {
            return this.moveList;
        }

        public boolean isKing() {
            return this.king;
        }

        public boolean isJumpListEmpty() {
            return this.jumpList.isEmpty();
        }

        public void setPieceToKing() {
            this.king = true; 
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

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(playerColor);
                g.fillOval(x - radius, y - radius, 2*radius, 2*radius);
            }
            
        }
    }
}
