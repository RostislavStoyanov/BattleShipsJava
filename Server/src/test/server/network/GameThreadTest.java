package server.network;

import board.Board;
import board.State;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameThreadTest
{

    private GameThread currThread, anotherThread;
    private UserThread user1, user2;
    private Board user1Board, user2Board, user1EnemyBoard, user2EnemyBoard;

    @Before
    public void setUp()
    {
        Server server = new Server(4444);
        user1 = new UserThread(null, server, "Pesho", null, null);
        user2 = new UserThread(null, server, "Tosho", null, null);

        currThread = new GameThread("Test game", "User", server);

        setBoards();
        anotherThread = new GameThread("Another game", "Pesho", "Tosho",
                server, user1Board, user2Board, user1EnemyBoard, user2EnemyBoard);
    }

    private void setBoards()
    {

        user1EnemyBoard = new Board();
        user2EnemyBoard = new Board();
        user1Board = new Board();
        user2Board = new Board();

        State[][] board = new State[][]{
                {State.SHIP, State.SHIP, State.SHIP, State.SHIP, State.SHIP,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.SHIP, State.SHIP, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.SHIP, State.SHIP, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.SHIP, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.SHIP, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.SHIP, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

        };
        user1Board.setBoard(board);

        State[][] bo2 = new State[][]{
                {State.EMPTY, State.SHIP, State.EMPTY, State.SHIP, State.SHIP,
                        State.SHIP, State.SHIP, State.EMPTY, State.SHIP, State.SHIP},

                {State.EMPTY, State.SHIP, State.EMPTY, State.SHIP, State.EMPTY,
                        State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.SHIP, State.EMPTY, State.SHIP, State.EMPTY,
                        State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.SHIP, State.EMPTY, State.SHIP, State.EMPTY,
                        State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.SHIP, State.SHIP, State.SHIP, State.EMPTY, State.EMPTY,
                        State.SHIP, State.SHIP, State.SHIP, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.EMPTY, State.EMPTY, State.SHIP, State.SHIP,
                        State.SHIP, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

                {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.SHIP, State.SHIP},

                {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY,
                        State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},

        };
        user2Board.setBoard(bo2);

    }

    @Test
    public void getGameID()
    {
        assertEquals("Test game", currThread.getGameID());
    }


    @Test
    public void getUserList() throws IOException
    {
        currThread.addClient(user1);
        currThread.addClient(user2);


        List<UserThread> list = currThread.getUserList();
        assertTrue(list.contains(user1) && list.contains(user2));

        List<UserThread> list2 = anotherThread.getUserList();
        assertTrue(list2.isEmpty());
    }

    @Test
    public void addClient() throws IOException
    {

        currThread.addClient(user1);

    }

    @Test
    public void getPlayersConnected() throws IOException
    {
        assertEquals(currThread.getPlayersConnected(), 0);

        currThread.addClient(user1);

        assertEquals(currThread.getPlayersConnected(), 1);

        currThread.addClient(user2);

        assertEquals(currThread.getPlayersConnected(), 2);

        assertEquals(anotherThread.getPlayersConnected(), 0);
    }

    @Test
    public void getCreatorID()
    {
        assertEquals("User", currThread.getCreatorID());
        assertEquals("Pesho", anotherThread.getCreatorID());
    }
}