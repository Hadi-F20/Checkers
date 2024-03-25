package Checkers;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class piece {

    public static void main(String args[]) {
        
    }

    public static class Piece {
        
        private static final int RADIUS = 40;
        //color1 is for player 1 pieces, color2 for player 2
        private static final Color color1 = Color.GREEN;
        private static final Color color2 = Color.RED;

        private int player;
        private CircleButton shape;
        private int position;
        
        //create the piece based on the player piece is for
        public Piece(int player) {
            this.player = player;
            this.shape = createCircle(player, 50, 50);
        }

        //create the circle based on which player piece is for
        private CircleButton createCircle(int player, int xCoord, int yCoord) {
            Color colorOfPiece;
            if (player == 1) {
                colorOfPiece = color1;
            } else {
                colorOfPiece = color2;
            }
            CircleButton circle = new CircleButton(xCoord, yCoord, RADIUS, colorOfPiece);
            circle.setOpaque(false);
            circle.setEnabled(false);
            circle.setBorderPainted(false);
            addMouseEventListener(circle);
            return circle;
        }

        private void addMouseEventListener(CircleButton circle) {
            circle.addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {
                    //to drag pieces
                    Point newLocation = e.getLocationOnScreen();
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

                //add check to see if piece can move other direction when at end of board

                @Override
                public void mousePressed(MouseEvent e) {
                    startXCoord = circle.getX();
                    startYCoord = circle.getY();
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
                    //for p1: move should be downwards, so y must be getting bigger. if yChange > 50 && < 200, valid move
                    //if piece is at last square (y == 700), then can move backwards, so check is same as normal check for other player
                    
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
                    
                    if ((xDiff > -50 && xDiff < 50) && (yChangeCorrect)) {
                        circle.setBounds(startXCoord, startYCoord + yChange, 100, 100);
                    } else {
                        circle.setBounds(startXCoord, startYCoord, 100, 100);
                    }
                }
            });
        }

        //return color of piece
        public int getPlayer() {
            return this.player;
        }

        //get the shape of the piece
        public JButton getCircle() {
            return this.shape;
        }

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
                if (playerColor.equals(color1)) {
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
