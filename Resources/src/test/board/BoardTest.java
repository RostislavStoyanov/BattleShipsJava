package board;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class BoardTest
{

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private Board board, board2;
    private State[][] boardStates;

    @Before
    public void setUpStreams()
    {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        board = new Board();
        boardStates = new State[][]{
                {State.EMPTY, State.SHIP, State.EMPTY, State.SHOT, State.EMPTY,            //1st
                        State.EMPTY, State.EMPTY, State.HIT, State.HIT, State.EMPTY},

                {State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,           //2nd
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,           //3rd
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.HIT, State.EMPTY, State.EMPTY, State.SHIP,             //4th
                        State.SHIP, State.SHIP, State.EMPTY, State.SHOT, State.EMPTY},

                {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY,         //5th
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.EMPTY, State.SHOT, State.EMPTY, State.EMPTY,          //6th
                        State.EMPTY, State.SHOT, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY,        //7th
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,         //8th
                        State.HIT, State.HIT, State.HIT, State.HIT, State.EMPTY},

                {State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,           //9th
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,           //10
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY}
        };
        board.setBoard(boardStates);
        board2 = new Board(board);
    }

    @After
    public void restoreStreams()
    {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private static void assert2d(State[][] expected,
                                 State[][] actual)
    {

        if (expected == null && actual == null)
        {
            return;
        }

        assert expected != null;
        if (expected.length != actual.length)
        {
            fail("The array lengths of the first dimensions aren't the same.");
        }

        for (int i = 0; i < expected.length; i++)
        {
            assertArrayEquals(expected[i], actual[i]);
        }
    }


    @Test
    public void getBoard()
    {
        assert2d(board2.getBoard(), boardStates);
    }

    @Test
    public void getStateAndSetState()
    {
        board2.setBoardState(1, 4, State.SHIP);
        assertEquals(State.SHIP, board2.getState(1, 4));

    }


    @Test
    public void returnTrueOnHit()
    {
        assertTrue(board.returnTrueOnHit(new Coordinates(0, 1)));
    }

    @Test
    public void printBoard()
    {
        board.printBoard();
        assertEquals("  1 2 3 4 5 6 7 8 9 10 \n" +
                "  _ _ _ _ _ _ _ _ _ _ \n" +
                "A|_|#|_|O|_|_|_|X|X|_|\n" +
                "B|_|#|_|_|_|_|_|_|_|_|\n" +
                "C|_|#|_|_|_|_|_|_|_|_|\n" +
                "D|_|X|_|_|#|#|#|_|O|_|\n" +
                "E|_|_|_|_|_|_|_|_|_|_|\n" +
                "F|_|_|O|_|_|_|O|_|_|_|\n" +
                "G|_|_|_|_|_|_|_|_|_|_|\n" +
                "H|_|#|_|_|_|X|X|X|X|_|\n" +
                "I|_|#|_|_|_|_|_|_|_|_|\n" +
                "J|_|#|_|_|_|_|_|_|_|_|\n", outContent.toString());

    }

    @Test
    public void equals()
    {
        assertEquals(board, board2);
    }
}