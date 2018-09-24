package server.network;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ServerTest
{

    private Server server;

    @Before
    public void SetUp()
    {
        server = new Server(4444);
    }

    @Test
    public void getGameCount()
    {
        assertEquals(0, server.getGameCount());

        server.createGameID("Game", "Creator");
        assertEquals(1, server.getGameCount());
    }


    @Test
    public void getThreadByID()
    {
        assertNull(server.getThreadByID("Game"));

        server.createGameID("Game", "Creator");
        GameThread createdGame = server.getThreadByID("Game");
        assertEquals("Game", createdGame.getGameID());
        assertEquals("Creator", createdGame.getCreatorID());
    }

    @Test
    public void userConnectedToGame() throws IOException
    {
        server.createGameID("Game", "Creator");
        GameThread g = server.getThreadByID("Game");

        g.addClient(new UserThread(null, server, "Pesho", null, null));

        server.userConnectedToGame("Game");

        ConcurrentHashMap<String, List<String>> map = server.getRunningGameThreads();
        assertEquals(1, map.size());
        map.forEach((k, v) -> assertEquals("Game Creator", k));
        map.forEach((k, v) -> assertEquals(1, v.size()));
    }


    @Test
    public void removeGame()
    {
        assertEquals(0, server.getGameCount());

        server.createGameID("Game", "Creator");
        assertEquals(1, server.getGameCount());

        server.removeGame("Game");
        assertEquals(0, server.getGameCount());
    }
}