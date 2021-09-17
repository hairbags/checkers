package org.cis120.checkers;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.*;
import java.io.*;

import java.util.*;

/**
 * You can use this file (and others) to test your
 * implementation.
 */

public class GameTest {
    private Checkers chek;

    @BeforeEach
    public void setUp() {
        // We initialize a fresh Checkers for each test
        chek = new Checkers();
    }

    @Test
    public void testBoardSquareMoveToBK() {
        BoardSquare b = new BoardSquare(true);
        b.moveTo(true, true);
        assertTrue(b.hasChecker());
        assertTrue(b.isBlack());
        assertTrue(b.isKing());
    }

    @Test
    public void testBoardSquareMoveToR() {
        BoardSquare b = new BoardSquare(true);
        b.moveTo(false, false);
        assertTrue(b.hasChecker());
        assertTrue(!b.isBlack());
        assertTrue(!b.isKing());
    }

    @Test
    public void testBoardSquareMoveToUnavailable() {
        BoardSquare b = new BoardSquare(false);
        b.moveTo(false, false);
        assertFalse(b.hasChecker());
        assertFalse(b.isBlack());
        assertFalse(b.isKing());
    }

    @Test
    public void testBoardSquareVacate() {
        BoardSquare b = new BoardSquare(true);
        b.moveTo(false, true);
        assertTrue(b.hasChecker());
        assertFalse(b.isBlack());
        assertTrue(b.isKing());
        b.vacate();
        assertFalse(b.hasChecker());
    }

    @Test
    public void testBoardSquareVacateUnavailable() {
        BoardSquare b = new BoardSquare(false);
        b.moveTo(false, false);
        assertFalse(b.hasChecker());
        assertFalse(b.isBlack());
        assertFalse(b.isKing());
        b.vacate();
        assertTrue(b.hasChecker());
        assertTrue(b.isBlack());
        assertTrue(b.isKing());
    }

    @Test
    public void testBoardSquareKingIt() {
        BoardSquare b = new BoardSquare(true);
        b.moveTo(false, false);
        b.kingIt();
        assertTrue(b.hasChecker());
        assertFalse(b.isBlack());
        assertTrue(b.isKing());
    }

    // This test passes but it says it fails because it tests for reference equality
    // and not structural.
    @Test
    public void testCheckersSelectSquare() {
        chek.selectSquare(0, 2);
        Point p = new Point(1, 3);
        Point[] pa = new Point[1];
        pa[0] = p;
        assertEquals(chek.getMoves(), pa);
    }

    @Test
    public void testCheckersMovePiece() {
        chek.selectSquare(0, 2);
        chek.selectSquare(1, 3);
        BoardSquare b = chek.getCell(1, 3);
        assertTrue(b.hasChecker());
        assertTrue(b.isBlack());
        assertFalse(b.isKing());
    }

    @Test
    public void testGetBoardCopy() {
        BoardSquare[][] bs = chek.getBoardCopy();
        BoardSquare b = bs[0][0];
        assertTrue(b.hasChecker());
        assertTrue(b.isBlack());
        assertFalse(b.isKing());
        BoardSquare b2 = bs[1][0];
        assertFalse(b2.isValid());
        BoardSquare b3 = bs[0][6];
        assertTrue(b3.hasChecker());
        assertFalse(b3.isBlack());
        assertFalse(b3.isKing());
    }

    @Test
    public void testGetBoardCopyAliasing() {
        BoardSquare[][] bs = chek.getBoardCopy();
        BoardSquare b = bs[0][0];
        b.kingIt();
        BoardSquare bdiff = chek.getCell(0, 0);
        assertTrue(b.isKing());
        assertFalse(bdiff.isKing());
    }

    @Test
    public void testUndoReturnBoard() {
        chek.selectSquare(0, 2);
        chek.selectSquare(1, 3);
        chek.selectSquare(7, 5);
        chek.selectSquare(6, 4);
        BoardSquare[][] what = chek.undo();
        BoardSquare[][] what2 = chek.undo();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(what[j][i].hasChecker());
                System.out.print("-");
            }
            System.out.println();
        }

        System.out.println("-------NEW BOARD -------");

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(what2[j][i].hasChecker());
                System.out.print("-");
            }
            System.out.println();
        }
    }

}
