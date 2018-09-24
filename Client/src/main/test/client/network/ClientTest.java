package client.network;

import board.Board;
import board.State;
import org.junit.After;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ClientTest
{

    private Client client;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @org.junit.Before
    public void setUp()
    {
        client = new Client("User");
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @org.junit.Test
    public void getClientID()
    {
        assertEquals("User", client.getClientID());
    }

    @org.junit.Test
    public void printPlayerBoard()
    {
        Board toPlace = new Board();
        State[][] boardStates = new State[][]{
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
        toPlace.setBoard(boardStates);

        client.setBoard(toPlace);
        client.printPlayerBoard();
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

    @After
    public void restoreStreams()
    {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }


    @org.junit.Test
    public void printEnemyBoard()
    {
        Board toPlace = new Board();
        State[][] boardStates = new State[][]{
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
        toPlace.setBoard(boardStates);

        client.setEnemyBoard(toPlace);
        client.printEnemyBoard();
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


    @org.junit.Test
    public void getEnemy()
    {
        client.setOpponent("Enemy");
        assertEquals("Enemy", client.getEnemy());
    }
}