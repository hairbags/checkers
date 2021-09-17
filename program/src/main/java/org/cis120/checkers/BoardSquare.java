package org.cis120.checkers;

import java.util.*;

public final class BoardSquare {
    private final boolean isAvailable;
    private boolean hasChecker;
    private boolean blackChecker;
    private boolean kingChecker;

    /**
     * Constructor, initializes the private fields of the object.
     */

    public BoardSquare(boolean isavailable) {
        isAvailable = isavailable;
        hasChecker = false;
        blackChecker = false;
        kingChecker = false;
    }

    public class IllegalMoveException extends Exception {
        public IllegalMoveException(String errormsg) {
            super(errormsg);
        }
    }

    public void kingIt() {
        if (!hasChecker || !isAvailable) {
            return;
        }
        kingChecker = true;
    }

    /**
     * Brings a Checker to the Boardsquare
     * 
     * @param black true if incoming checker is black false if it is red
     * @param king  true if incoming checker is a king false if it is not.
     * @throws IllegalMoveException if square is already occupied or if square
     *                              is light.
     */

    public void moveTo(boolean black, boolean king) {
        if (hasChecker) {
            System.out.println("Cant move to occupied square");
            return;
        }
        if (!isAvailable) {
            System.out.println("Cant move to light square");
            return;
        }
        hasChecker = true;
        blackChecker = black;
        kingChecker = king;
    }

    /**
     * Method called if a Checker leaves this boardsquare because it either moved or
     * got killed.
     * 
     * if square is not available but method is called, creates a black king checker
     * to show up on the board so you know something is wrong. If invariants are
     * maintained
     * this should never happen.
     * 
     */

    public void vacate() {
        if (!isAvailable) {
            System.out.println("cant touch unavailable square");
            hasChecker = true;
            blackChecker = true;
            kingChecker = true;
            return;
        }
        hasChecker = false;
        blackChecker = false;
        kingChecker = false;
    }

    /**
     * This method tells you whether the boardsquare is playable (dark)
     * 
     * @return true if playable false if not.
     */

    public boolean isValid() {
        return isAvailable;
    }

    /**
     * This method tells you whether the boardsquare is occupied by a king Checker
     * 
     * @return true if King false if not.
     */

    public boolean isKing() {
        return kingChecker;
    }

    /**
     * This method tells you whether the boardsquare is occupied by a Checker
     * 
     * @return true if occupied false if not.
     */

    public boolean hasChecker() {
        return hasChecker;
    }

    /**
     * This method tells you whether the boardsquare is occupied by a black Checker
     * 
     * @return true if occupied by a black Checker false if not.
     */

    public boolean isBlack() {
        return blackChecker;
    }
}