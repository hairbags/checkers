package org.cis120.checkers;

import java.io.File;
import java.awt.*;
import java.io.*;

import java.util.*;

public class Checkers {

    private BoardSquare[][] board;
    private int numTurns;
    private boolean player1;
    private boolean gameOver;
    private ArrayList<Point> openMoves;
    private boolean killTurn;
    private boolean multiKTurn;
    private Point selectedSquare;
    private LinkedList<BoardSquare[][]> pastBoard;
    private LinkedList<String[][]> pastBoardS;
    private LinkedList<Boolean> pastTurn;
    private LinkedList<Boolean> pastKTurn;

    /**
     * Constructor sets up game state.
     */
    public Checkers() {
        reset();
    }

    /**
     * reset (re-)sets the game state to start a new game.
     */
    public void reset() {
        board = new BoardSquare[8][8];
        openMoves = new ArrayList();
        killTurn = false;
        multiKTurn = false;
        selectedSquare = new Point();
        boolean alternator = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (alternator) {
                    BoardSquare b = new BoardSquare(true);
                    board[j][i] = b;
                    if (i < 3) {
                        b.moveTo(true, false);
                    }
                    if (i > 4) {
                        b.moveTo(false, false);
                    }
                } else {
                    board[j][i] = new BoardSquare(false);
                }
                alternator = !alternator;
            }
            alternator = !alternator;
        }
        numTurns = 0;
        player1 = true;
        gameOver = false;
        pastBoard = new LinkedList();
        pastTurn = new LinkedList();
        pastKTurn = new LinkedList();
        pastBoardS = new LinkedList();
        pastBoard.add(getBoardCopy());
        pastTurn.add(player1);
        pastKTurn.add(killTurn);
    }

    /**
     * This method returns an array of all open moves.
     * 
     * @return an array of Points for all open moves.
     */

    public Point[] getMoves() {
        int size = openMoves.size();
        Point[] ret = new Point[size];
        for (int i = 0; i < size; i++) {
            ret[i] = openMoves.get(i);
        }
        return ret;
    }

    /**
     * helper method checks if a given point is on the checkerboard.
     *
     * @param p Point to be checked
     * @return true if point is on the board
     */

    private boolean inBounds(Point p) {
        int x = (int) Math.round(p.getX());
        int y = (int) Math.round(p.getY());
        return !(x > 7 || x < 0 || y < 0 || y > 7);
    }

    /**
     * Method checks if a kill is possible from the start point in the specified
     * direction.
     * 
     * @return boolean true if kill is possible
     */

    private boolean checkKill(Point start, Point direction) {
        if (!inBounds(start)) {
            throw new IllegalArgumentException("Checker is outside board.");
        }
        int x = (int) Math.round(start.getX());
        int y = (int) Math.round(start.getY());
        int xd = (int) Math.round(direction.getX()) + x;
        int yd = (int) Math.round(direction.getY()) + y;
        int xe = (int) Math.round(direction.getX()) * 2 + x;
        int ye = (int) Math.round(direction.getY()) * 2 + y;
        BoardSquare b = board[x][y];
        if (!b.hasChecker() || b.isBlack() != player1 || gameOver || !b.isValid()) {
            throw new IllegalArgumentException("you cant do this shit");
        }
        Point dier = new Point(xd, yd);
        if (!inBounds(dier)) {
            return false;
        }
        BoardSquare bd = board[xd][yd];
        Point end = new Point(xe, ye);
        if (!inBounds(end)) {
            return false;
        }
        BoardSquare be = board[xe][ye];
        if (!bd.hasChecker() || bd.isBlack() == b.isBlack() || be.hasChecker()) {
            return false;
        }
        return true;
    }

    /**
     * killList gives an ArrayList of all possible kill moves in a given turn.
     * if there is at least one kill move, the player going must execute it.
     * if there are multiple moves player may choose between them.
     * 
     * @return ArrayList of tuples of points, the first point is the killer
     *         Checker's location on the board, the second point is the direction of
     *         the kill.
     */

    private boolean killPossible() {
        boolean isBlack = player1;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                BoardSquare b = board[j][i];
                if (b.isValid() && b.hasChecker() && b.isBlack() == isBlack) {
                    Point start = new Point(j, i);
                    Point upRt = new Point(1, 1);
                    Point upLt = new Point(-1, 1);
                    Point downRt = new Point(1, -1);
                    Point downLt = new Point(-1, -1);
                    if (b.isBlack() || b.isKing()) {
                        if (checkKill(start, upRt)) {
                            killTurn = true;
                            return true;
                        }
                        if (checkKill(start, upLt)) {
                            killTurn = true;
                            return true;
                        }
                    }
                    if (!b.isBlack() || b.isKing()) {
                        if (checkKill(start, downRt)) {
                            killTurn = true;
                            return true;
                        }
                        if (checkKill(start, downLt)) {
                            killTurn = true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * availableMoves gives an ArrayList of all points that a given checker
     * can move to.
     *
     * @param x x value of checker's position
     * @param y y value of checker's position
     * @return ArrayList of available squares for the given checker.
     * @throws IllegalArgumentExcpetion if method is called on a square that does
     *                                  not have a checker in it or is light.
     */

    private ArrayList<Point> availableMoves(int x, int y) {
        Point curr = new Point(x, y);
        BoardSquare b = board[x][y];
        if (!inBounds(curr) || !b.isValid()) {
            throw new IllegalArgumentException("Checker is outside board or light.");
        }
        ArrayList<Point> ret = new ArrayList();
        if (!b.hasChecker() || b.isBlack() != player1 || gameOver || !b.isValid()) {
            return ret;
        }
        int[] testX = { x + 1, x - 1, x - 1, x + 1 };
        int[] testY = { y + 1, y + 1, y - 1, y - 1 };
        Point[] testPs = { new Point(x + 1, y + 1), new Point(x - 1, y + 1),
            new Point(x - 1, y - 1), new Point(x + 1, y - 1) };
        if (b.isBlack() || b.isKing()) {
            if (inBounds(testPs[0])) {
                BoardSquare bt = board[testX[0]][testY[0]];
                if (!bt.hasChecker()) {
                    ret.add(testPs[0]);
                }
            }
            if (inBounds(testPs[1])) {
                BoardSquare bt = board[testX[1]][testY[1]];
                if (!bt.hasChecker()) {
                    ret.add(testPs[1]);
                }
            }
        }
        if (!b.isBlack() || b.isKing()) {
            if (inBounds(testPs[2])) {
                BoardSquare bt = board[testX[2]][testY[2]];
                if (!bt.hasChecker()) {
                    ret.add(testPs[2]);
                }
            }
            if (inBounds(testPs[3])) {
                BoardSquare bt = board[testX[3]][testY[3]];
                if (!bt.hasChecker()) {
                    ret.add(testPs[3]);
                }
            }
        }
        return ret;
    }

    private ArrayList<Point> availableKills(int x, int y) {
        Point start = new Point(x, y);
        BoardSquare b = board[x][y];
        if (!inBounds(start) || !b.isValid()) {
            throw new IllegalArgumentException("Checker is outside board or light.");
        }
        ArrayList<Point> list = new ArrayList();
        if (!b.hasChecker() || b.isBlack() != player1 || gameOver || !b.isValid()) {
            return list;
        }
        Point upRt = new Point(1, 1);
        Point upLt = new Point(-1, 1);
        Point downRt = new Point(1, -1);
        Point downLt = new Point(-1, -1);
        if (b.isBlack() || b.isKing()) {
            if (checkKill(start, upRt)) {
                list.add(new Point(x + 2, y + 2));
            }
            if (checkKill(start, upLt)) {
                list.add(new Point(x - 2, y + 2));
            }
        }
        if (!b.isBlack() || b.isKing()) {
            if (checkKill(start, downRt)) {
                list.add(new Point(x + 2, y - 2));
            }
            if (checkKill(start, downLt)) {
                list.add(new Point(x - 2, y - 2));
            }
        }
        return list;
    }

    /**
     * checkWinner checks whether the game has reached a win condition.
     *
     * @return 0 if nobody has won yet, 1 if black has won, and 2 if red
     *         has won
     * @throws Exception if somehow there are no pieces on the board.
     */
    public int checkWinner() {
        int bcount = 0;
        int rcount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                BoardSquare b = getCell(j, i);
                if (b.isBlack() && b.hasChecker()) {
                    bcount += 1;
                }
                if (!b.isBlack() && b.hasChecker()) {
                    rcount += 1;
                }
            }
        }
        if (rcount == 0 && bcount == 0) {
            System.out.println("how the fuck are there no pieces");
        }
        if (rcount == 0) {
            return 1;
        }
        if (bcount == 0) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * Make sure piece is moved to the boardsquare before calling this method.
     * Checks if Checker in new square should become a King. If it should become
     * one, the square is turned into a king
     * 
     * @return True if checker needs to become a King.
     * @throws IllegalArgumentException if there is no Checker in the square.
     */

    private boolean kingCheck(Point p) {
        int x = (int) Math.round(p.getX());
        int y = (int) Math.round(p.getY());
        BoardSquare b = board[x][y];
        if (b.isBlack() && y == 7) {
            b.kingIt();
            if (multiKTurn) {
                multiKTurn = false;
                killTurn = false;
            }
            return true;
        }
        if (!b.isBlack() && y == 0) {
            b.kingIt();
            if (multiKTurn) {
                multiKTurn = false;
                killTurn = false;
            }
            return true;
        }
        if (!b.hasChecker()) {
            throw new IllegalArgumentException("Add the fuckin checker first");
        }
        return false;
    }

    /**
     * Method called to move one Checker from one position to the next.
     * Should be used only for normal movements, not kill moves.
     * Assumes that invariants are maintained and movement is valid.
     *
     * @param old  Point from which piece is moving.
     * @param newP Point to which piece is moving.
     * @throws IllegalMoveException if it is a killTurn.
     */

    private void movePiece(Point old, Point newP) {
        if (killTurn) {
            System.out.println("cant move piece on kill turn");
        }
        int x = (int) Math.round(old.getX());
        int y = (int) Math.round(old.getY());
        int xn = (int) Math.round(newP.getX());
        int yn = (int) Math.round(newP.getY());
        BoardSquare bold = board[x][y];
        boolean black = bold.isBlack();
        boolean king = bold.isKing();
        BoardSquare bnew = board[xn][yn];
        board[x][y].vacate();
        board[xn][yn].moveTo(black, king);
        Point pnew = new Point(xn, yn);
        kingCheck(pnew);
        advanceTurn();
    }

    /**
     * Method called to move one Checker from one position to the next and
     * kill the piece in between. Returns array of all possibilities for multi
     * kills.
     * Should be used only for kill moves.
     * Assumes that invariants are maintained and movement is valid.
     * 
     * @param old  Point from which piece is killing.
     * @param newP Point where piece will end up.
     * @return ArrayList of all possible multiple kill paths after completing
     *         killMove.
     */

    private void killMove(Point old, Point newP) {
        int x = (int) Math.round(old.getX());
        int y = (int) Math.round(old.getY());
        int xn = (int) Math.round(newP.getX());
        int yn = (int) Math.round(newP.getY());
        Point dir = new Point((xn - x) / 2, (yn - y) / 2);
        BoardSquare bold = board[x][y];
        boolean black = bold.isBlack();
        boolean king = bold.isKing();
        BoardSquare bkill = board[x + (int) Math.round(dir.getX())][y
                + (int) Math.round(dir.getY())];
        BoardSquare bnew = board[xn][yn];
        board[x][y].vacate();
        bkill.vacate();
        board[xn][yn].moveTo(black, king);
        if (kingCheck(newP)) {
            advanceTurn();
            selectedSquare = newP;
            return;
        }
        ArrayList<Point> ret = availableKills(xn, yn);

        if (ret.isEmpty()) {
            multiKTurn = false;
            advanceTurn();
        } else {
            multiKTurn = true;
            advanceTurn();
            openMoves.clear();
            openMoves.addAll(ret);
            selectedSquare = newP;
        }
    }

    /**
     * printGameState prints the current game state
     * for debugging.
     */
    public void printGameState() {
        System.out.println("\n\nTurn " + numTurns + ":\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j]);
                if (j < 2) {
                    System.out.print(" | ");
                }
            }
            if (i < 2) {
                System.out.println("\n---------");
            }
        }
    }

    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     *
     * @param x x value of cell
     * @param y value of cell
     * @return the BoardSquare at the coordinates
     */
    public BoardSquare getCell(int x, int y) {
        return board[x][y];
    }

    /**
     * This method saves the game to a .csv file, does not save if game is
     * over.
     * 
     * 
     * @throws IllegalArgumentException if game is over. UI method
     *                                  needs to catch this exception and send error
     *                                  message to user
     */

    public void saveGame() throws IllegalArgumentException {
        if (gameOver) {
            throw new IllegalArgumentException("can't save finished game");
        }
        File file = new File("files/leaderboard.csv");
        try {
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            String firstLine = String.valueOf(numTurns) + "," + String.valueOf(player1)
                    + "," + String.valueOf(gameOver) + "," + String.valueOf(killTurn)
                    + "," + String.valueOf(multiKTurn);
            bw.write(firstLine);
            bw.newLine();
            ListIterator<BoardSquare[][]> iterB = pastBoard.listIterator(pastBoard.size());
            ListIterator<Boolean> iterT = pastTurn.listIterator(pastTurn.size());
            ListIterator<Boolean> iterK = pastKTurn.listIterator(pastKTurn.size());
            while (iterB.hasNext()) {
                BoardSquare[][] curBoard = iterB.next();
                boolean turnstate = iterT.next();
                boolean kstate = iterK.next();
                bw.write(String.valueOf(turnstate));
                bw.write(",");
                bw.write(String.valueOf(kstate));
                bw.newLine();
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        String data = null;
                        BoardSquare b = curBoard[j][i];
                        if (!b.isValid()) {
                            data = "NO";
                        } else {
                            if (!b.hasChecker()) {
                                data = "EMPTY";
                            }
                            if (b.hasChecker() && b.isBlack()) {
                                data = "BLACK";
                            }
                            if (b.hasChecker() && !b.isBlack()) {
                                data = "RED";
                            }
                            if (b.isKing()) {
                                data = data.concat("K");
                            }
                            if (j != 7) {
                                data = data.concat(",");
                            }
                        }
                        bw.write(data);
                        if (j == 7) {
                            bw.newLine();
                        }
                        bw.flush();
                    }
                }
            }
            bw.flush();
        } catch (IOException e2) {
        }
    }

    public class IllegalMoveException extends Exception {
        public IllegalMoveException(String errormsg) {
            super(errormsg);
        }
    }

    /**
     * This method is a helper function to loadGame(). It decodes code Strings from
     * saveGame() into a boardsquare with the desired state.
     * assumes invariants are maintained.
     * 
     * @param msg String of codeword to tell this method what kind of BoardSquare to
     *            make.
     * @return BoardSquare with state corresponding to input.
     */

    private BoardSquare stringToSquare(String msg) {
        if (msg == "NO") {
            return new BoardSquare(false);
        }
        if (msg == "EMPTY") {
            return new BoardSquare(true);
        }
        BoardSquare b = new BoardSquare(true);
        if (msg == "BLACK") {
            b.moveTo(true, false);
        }
        if (msg == "RED") {
            b.moveTo(false, false);
        }
        if (msg == "BLACKK") {
            b.moveTo(true, true);
        }
        if (msg == "REDK") {
            b.moveTo(false, true);
        }
        return b;
    }

    /**
     * This method loads the game from the .csv savefile. It clears the old memory
     * of moves and writes in the turns from the savefile. Game can be undone
     * normally.
     * 
     */

    public void loadGame() {
        reset();
        pastTurn.clear();
        pastKTurn.clear();
        pastBoard.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("files/leaderboard.csv"));
            String firstLine = reader.readLine();
            String shit[] = firstLine.split(",");
            numTurns = Integer.parseInt(shit[0]);
            player1 = Boolean.parseBoolean(shit[1]);
            gameOver = Boolean.parseBoolean(shit[2]);
            killTurn = Boolean.parseBoolean(shit[3]);
            multiKTurn = Boolean.parseBoolean(shit[4]);
            BoardSquare[][] bload = new BoardSquare[8][8];
            for (int k = 0; k < numTurns + 1; k++) {
                try {
                    boolean player1load = Boolean.parseBoolean(reader.readLine());
                    boolean kTload = true;
                    for (int i = 0; i < 8; i++) {
                        String row = reader.readLine();
                        String[] col = row.split(",");
                        for (int j = 0; j < 8; j++) {
                            bload[j][i] = stringToSquare(col[j]);
                        }
                    }
                    pastBoard.add(bload);
                    pastTurn.add(player1load);
                    pastKTurn.add(kTload);
                } catch (IOException e2) {
                }
            }
            BoardSquare[][] virgin = new BoardSquare[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    BoardSquare b1 = bload[j][i];
                    boolean aV = b1.isValid();
                    boolean chek = b1.hasChecker();
                    boolean black = b1.isBlack();
                    boolean king = b1.isKing();
                    BoardSquare b2 = new BoardSquare(aV);
                    if (aV) {
                        b2.moveTo(black, king);
                    }
                    virgin[j][i] = b2;
                }
            }
            board = virgin;
        } catch (IOException e) {
        }
    }

    /**
     * This method undoes the last turn. Does nothing if game is over.
     * 
     * 
     */

    public BoardSquare[][] undo() {
        BoardSquare[][] btest = new BoardSquare[8][8];
        if (numTurns > 0 && !gameOver) {
            numTurns -= 1;
            pastBoard.removeLast();
            pastTurn.removeLast();
            pastKTurn.removeLast();
            btest = pastBoard.peekLast();
            board = btest;
            player1 = pastTurn.peekLast();
            killTurn = pastKTurn.peekLast();
        }
        return btest;
    }

    /**
     * public void undo2() {
     * if (numTurns > 0 && !gameOver) {
     * numTurns -= 1;
     * // TODO switched up undo
     * pastBoardS.removeLast();
     * pastTurn.removeLast();
     * String[][] newb = pastBoardS.peek();
     * for (int i = 0; i < 8; i++) {
     * for (int j = 0; j < 8; j++) {
     * String Square = newb[j][i];
     * BoardSquare b = stringToSquare(Square);
     * board[j][i] = b;
     * }
     * }
     * player1 = pastTurn.peek();
     * if (killPossible()) {
     * killTurn = true;
     * }
     * }
     * }
     */

    public BoardSquare[][] getBoardCopy() {
        if (board == null) {
            System.out.println("where the fuck is the board");
        }
        BoardSquare[][] virgin = new BoardSquare[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                BoardSquare b1 = board[j][i];
                boolean aV = b1.isValid();
                boolean chek = b1.hasChecker();
                boolean black = b1.isBlack();
                boolean king = b1.isKing();
                BoardSquare b2 = new BoardSquare(aV);
                if (aV && chek) {
                    b2.moveTo(black, king);
                }
                virgin[j][i] = b2;
            }
        }
        return virgin;
    }

    /**
     * This method records the current turn and adds it to the LinkedList
     * of past turns. Updates state with new turn
     * 
     * 
     */

    /**
     * 
     * private void recordTurn() {
     * String[][] record = new String[8][8];
     * for (int i = 0; i < 8; i++) {
     * for (int j = 0; j < 8; j++) {
     * String data = null;
     * BoardSquare b = board[j][i];
     * if (!b.isValid()) {
     * data = "NO";
     * }
     * if (!b.hasChecker()) {
     * data = "EMPTY";
     * }
     * if (b.hasChecker() && b.isBlack()) {
     * data = "BLACK";
     * }
     * if (b.hasChecker() && !b.isBlack()) {
     * data = "RED";
     * }
     * if (b.isKing()) {
     * data = data.concat("K");
     * }
     * record[j][i] = data;
     * }
     * }
     * pastBoardS.add(record);
     * pastTurn.add(player1);
     * }
     */

    private void advanceTurn() {
        BoardSquare[][] currboard = getBoardCopy();
        pastBoard.add(currboard);
        pastTurn.add(player1);
        pastKTurn.add(killTurn);
        numTurns++;
        openMoves.clear();
        int x = (int) Math.round(selectedSquare.getX());
        int y = (int) Math.round(selectedSquare.getY());
        if (checkWinner() == 0 && !multiKTurn) {
            player1 = !player1;
        }
        if (checkWinner() != 0) {
            gameOver = true;
        }
        if (killPossible()) {
            killTurn = true;
        } else {
            killTurn = false;
            multiKTurn = false;
        }
    }

    public boolean getCurrentPlayer() {
        return player1;
    }

    public boolean getKillTurn() {
        return killTurn;
    }

    public boolean getMultiTurn() {
        return multiKTurn;
    }

    public void selectSquare(int x, int y) {
        Point p = new Point(x, y);
        BoardSquare b = board[x][y];
        if (p.equals(selectedSquare)) {
            return;
        }
        if (!b.isValid()) {
            return;
        }
        if (killTurn) {
            if (openMoves.contains(p)) {
                killMove(selectedSquare, p);
                return;
            }
            if (multiKTurn) {
                return;
            } else {
                if (b.hasChecker() && player1 == b.isBlack()) {
                    openMoves.clear();
                    openMoves.addAll(availableKills(x, y));
                    selectedSquare = p;
                }
            }
        } else {
            if (openMoves.contains(p)) {
                movePiece(selectedSquare, p);
            } else {
                if (b.hasChecker() && player1 == b.isBlack()) {
                    openMoves.clear();
                    openMoves.addAll(availableMoves(x, y));
                    selectedSquare = p;
                }
            }
        }
    }

    /**
     * This main method illustrates how the model is completely independent of
     * the view and controller. We can play the game from start to finish
     * without ever creating a Java Swing object.
     *
     * This is modularity in action, and modularity is the bedrock of the
     * Model-View-Controller design framework.
     *
     * Run this file to see the output of this method in your console.
     */
    public static void main(String[] args) {
        Checkers c = new Checkers();
        c.printGameState();
        System.out.println();
        System.out.println();
        System.out.println("Winner is: " + c.checkWinner());
    }
}
