package msg;

import board.Board;
import board.Coordinates;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Msg implements Serializable
{
    private MsgType msgType;
    private String playerID;
    private Object dataObj;

    public MsgType getMsgType()
    {
        return msgType;
    }

    void setMsgType(MsgType newMsgType)
    {
        this.msgType = newMsgType;
    }

    public String getPlayerID()
    {
        return playerID;
    }

    void setPlayerID(String newId)
    {
        this.playerID = newId;
    }

    public Object getDataObj()
    {
        return dataObj;
    }

    public Msg(MsgType msgType, String playerID, Board board)
    {
        this.msgType = msgType;
        this.playerID = playerID;
        this.dataObj = new Board(board);
    }

    public Msg(MsgType msgType, String playerId)
    {
        this.msgType = msgType;
        this.playerID = playerId;
        dataObj = null;
    }

    public Msg(MsgType msgType, String playerID, Coordinates coord)
    {
        this.msgType = msgType;
        this.playerID = playerID;
        dataObj = new Coordinates(coord);
    }

    public Msg(MsgType msgType, String playerID, ConcurrentHashMap<String, List<String>> map)
    {
        this.msgType = msgType;
        this.playerID = playerID;
        dataObj = new ConcurrentHashMap<>(map);
    }

    public Msg(MsgType msgType, String playerID, String gameID)
    {
        this.msgType = msgType;
        this.playerID = playerID;
        dataObj = gameID;
    }

    public Msg(MsgType msgType, String playerID, List<String> games)
    {
        this.msgType = msgType;
        this.playerID = playerID;
        dataObj = games;
    }
}
