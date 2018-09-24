package msg;

import board.Board;
import board.Coordinates;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

public class MsgTest
{

    private Msg simpleMsg, stringMsg, mapMsg, boardMsg, coordinatesMsg;

    @Before
    public void setUp()
    {
        simpleMsg = new Msg(MsgType.JOINED_GAME, "simpleMsgClient");

        stringMsg = new Msg(MsgType.WIN, "stringMsgClient", "RandomString");

        ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>();
        mapMsg = new Msg(MsgType.MISS, "mapMsgClient", map);

        Board board = new Board();
        boardMsg = new Msg(MsgType.HIT, "mapMsgClient", board);

        Coordinates c = new Coordinates(1, 1);
        coordinatesMsg = new Msg(MsgType.CREATE_GAME, "coordMsgClient", c);
    }


    @Test
    public void getMsgType()
    {
        assertEquals(simpleMsg.getMsgType(), MsgType.JOINED_GAME);
        assertEquals(stringMsg.getMsgType(), MsgType.WIN);
        assertEquals(mapMsg.getMsgType(), MsgType.MISS);
        assertEquals(boardMsg.getMsgType(), MsgType.HIT);
        assertEquals(coordinatesMsg.getMsgType(), MsgType.CREATE_GAME);
    }

    @Test
    public void setMsgType()
    {
        simpleMsg.setMsgType(MsgType.DRAW);
        stringMsg.setMsgType(MsgType.LOSE);
        mapMsg.setMsgType(MsgType.PLACE_SHIPS);
        boardMsg.setMsgType(MsgType.TERMINATE);
        coordinatesMsg.setMsgType(MsgType.USER_EXIT);

        assertEquals(simpleMsg.getMsgType(), MsgType.DRAW);
        assertEquals(stringMsg.getMsgType(), MsgType.LOSE);
        assertEquals(mapMsg.getMsgType(), MsgType.PLACE_SHIPS);
        assertEquals(boardMsg.getMsgType(), MsgType.TERMINATE);
        assertEquals(coordinatesMsg.getMsgType(), MsgType.USER_EXIT);
    }

    @Test
    public void getPlayerID()
    {
        assertEquals(simpleMsg.getPlayerID(), "simpleMsgClient");
        assertEquals(stringMsg.getPlayerID(), "stringMsgClient");
        assertEquals(mapMsg.getPlayerID(), "mapMsgClient");
    }

    @Test
    public void setPlayerID()
    {
        simpleMsg.setPlayerID("setTest1");
        stringMsg.setPlayerID("setTest2");

        assertEquals(simpleMsg.getPlayerID(), "setTest1");
        assertEquals(stringMsg.getPlayerID(), "setTest2");
    }

    @Test
    public void getDataObj()
    {
        assertNull(simpleMsg.getDataObj());

        assertThat(stringMsg.getDataObj(), instanceOf(String.class));
        assertEquals(stringMsg.getDataObj(), "RandomString");

        assertThat(boardMsg.getDataObj(), instanceOf(Board.class));

        assertThat(mapMsg.getDataObj(), instanceOf(ConcurrentHashMap.class));

        assertThat(coordinatesMsg.getDataObj(), instanceOf(Coordinates.class));
    }
}