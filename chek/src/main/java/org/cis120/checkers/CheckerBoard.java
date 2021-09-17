package org.cis120.checkers;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class instantiates a TicTacToe object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 * 
 * In a Model-View-Controller framework, GameBoard stores the model as a field
 * and acts as both the controller (with a MouseListener) and the view (with
 * its paintComponent method and the status JLabel).
 */
@SuppressWarnings("serial")
public class CheckerBoard extends JPanel {

    private Checkers chek; // model for the game
    private JLabel status; // current status text

    // Game constants
    public static final int BOARD_WIDTH = 304;
    public static final int BOARD_HEIGHT = 304;

    /**
     * Initializes the game board.
     */
    public CheckerBoard(JLabel statusInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        chek = new Checkers(); // initializes model for the game
        status = statusInit; // initializes the status JLabel

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();

                // updates the model given the coordinates of the mouseclick
                chek.selectSquare(p.x / 38, p.y / 38);

                updateStatus(); // updates the status JLabel
                repaint(); // repaints the game board
            }
        });
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        chek.reset();
        status.setText("Black's Turn");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        if (chek.getCurrentPlayer()) {
            if (chek.getKillTurn()) {
                if (chek.getMultiTurn()) {
                    status.setText("Black Multi Kill!");
                } else {
                    status.setText("Black Kill Turn");
                }
            } else {
                status.setText("Black's Turn");
            }
        } else {
            if (chek.getKillTurn()) {
                if (chek.getMultiTurn()) {
                    status.setText("Red Multi Kill!");
                } else {
                    status.setText("Red Kill Turn");
                }
            } else {
                status.setText("Red's Turn");
            }
        }

        int winner = chek.checkWinner();
        if (winner == 1) {
            status.setText("Black wins!!!");
        } else if (winner == 2) {
            status.setText("Red wins!!!");
        }
    }

    public void save() {
        chek.saveGame();
    }

    public void load() {
        chek.loadGame();
    }

    public void undo() {
        chek.undo();
        repaint();
    }

    /**
     * Draws the game board.
     * 
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draws board grid
        int sqWidth = BOARD_WIDTH / 8;
        int sqHeight = BOARD_HEIGHT / 8;
        Color dark = new Color(92, 82, 31);
        Color light = new Color(143, 128, 53);
        BoardSquare[][] b = chek.getBoardCopy();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                BoardSquare bt = b[j][i];
                if (!bt.isValid()) {
                    g.setColor(light);
                } else {
                    g.setColor(dark);
                }
                int x = j * sqWidth;
                int y = i * sqHeight;
                g.fillRect(x, y, sqWidth, sqHeight);
                if (bt.hasChecker()) {
                    if (bt.isBlack()) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.RED);
                    }
                    g.fillOval(x + 9, y + 9, 20, 20);
                    if (bt.isKing()) {
                        g.setColor(Color.YELLOW);
                        int[] xarr = new int[] { x + 13, x + 13, x + 16, x + 19, x + 22, x + 25,
                            x + 25 };
                        int[] yarr = new int[] { y + 14, y + 24, y + 18, y + 24, y + 18, y + 24,
                            y + 14 };
                        g.fillPolygon(xarr, yarr, 7);
                    }
                }
            }
        }
        // Draws available moves if there are any
        Point[] moves = chek.getMoves();
        g.setColor(Color.GREEN);
        for (int i = 0; i < moves.length; i++) {
            Point p = moves[i];
            if (p != null) {
                int x = (int) Math.round(p.getX());
                int y = (int) Math.round(p.getY());
                x = x * sqWidth + 9;
                y = y * sqHeight + 9;
                g.drawOval(x, y, 20, 20);
            }
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}