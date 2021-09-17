package org.cis120.checkers;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class instantiates an instructions window. It explains how to paly
 * checkers.
 */
@SuppressWarnings("serial")
public class Instruct extends JPanel {
    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 200;

    public Instruct() {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        String line1 = "CHECKERS INSTRUCTIONS:";
        char[] l1 = line1.toCharArray();
        g.drawChars(l1, 0, 21, 60, 21);
        String line2 = "Select the piece you want to move, Open moves will appear as green circles";
        char[] l2 = line2.toCharArray();
        g.drawChars(l2, 0, 74, 60, 41);
        String line3 = "To kill a piece, jump over it.";
        char[] l3 = line3.toCharArray();
        g.drawChars(l3, 0, 30, 60, 61);
        String line4 = "If a kill move is possible, you are forced to do it.";
        char[] l4 = line4.toCharArray();
        g.drawChars(l4, 0, 52, 60, 81);
        String line5 = "If a kill is possible after your kill, ";
        line5 = line5.concat("it is still your turn and you can make the next kill.");
        char[] l5 = line5.toCharArray();
        g.drawChars(l5, 0, 92, 60, 101);
        String line6 = "If you reach the opposite end of the board, your piece becomes a king";
        line6 = line6.concat("which can move forward and backward.");
        char[] l6 = line6.toCharArray();
        g.drawChars(l6, 0, 105, 60, 121);
        String line7 = "Game continues until a player has run out of pieces.";
        char[] l7 = line7.toCharArray();
        g.drawChars(l7, 0, 52, 60, 141);
        String line8 = "Good Luck!";
        char[] l8 = line8.toCharArray();
        g.drawChars(l8, 0, 10, 60, 161);
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
    }
}