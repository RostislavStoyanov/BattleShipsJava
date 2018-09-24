package server.network;


import msg.Msg;
import msg.MsgType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserThread extends Thread
{

    private String clientID;
    private Socket clientSocket;
    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;
    private GameThread gameThread;
    private Server currServer;

    private static final Logger logger = LogManager.getLogger("Server");

    String getClientID()
    {
        return clientID;
    }


    UserThread(Socket clientSocket, Server server, String clientID, ObjectInputStream fromClient, ObjectOutputStream toClient)
    {
        this.clientSocket = clientSocket;
        this.currServer = server;
        this.clientID = clientID;

        this.toClient = toClient;
        this.fromClient = fromClient;
    }


    void write(Msg msg)
    {
        try
        {
            toClient.writeObject(msg);
        } catch (IOException e)
        {
            logger.error("Exception when sending message to client");
        }
    }

    Msg receive()
    {
        try
        {
            return (Msg) fromClient.readObject();
        } catch (IOException e)
        {
            logger.warn("Exception when reading message");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void closeSocket()
    {
        try
        {
            clientSocket.close();
        } catch (IOException e)
        {
            logger.info("Client socket closed");
        }
    }

    @Override
    public void run()
    {
        this.setName("User thread - " + clientID);
        logger.info("User thread started");
        try
        {
            Msg clientMsg;
            while ((clientMsg = (Msg) fromClient.readObject()) != null)
            {
                //User wants to join a random game

                if (clientMsg.getMsgType().equals(MsgType.JOIN_GAME))
                {
                    if (currServer.getGameCount() == 0)
                    {
                        toClient.writeObject(new Msg(MsgType.NO_GAMES, clientID));
                    }
                    else
                    {
                        gameThread = currServer.getRandom();
                        if (gameThread != null)
                        {
                            write(new Msg(MsgType.JOINED_GAME, clientID, gameThread.getGameID()));
                            break;
                        }
                        else
                        {
                            write(new Msg(MsgType.NO_GAMES, clientID));
                        }
                    }

                }

                //User joins a game with game id
                else if (clientMsg.getMsgType().equals(MsgType.JOIN_GAME_ID))
                {
                    String userGameID = (String) clientMsg.getDataObj();
                    gameThread = currServer.getThreadByID(userGameID);
                    if (gameThread != null)
                    {
                        write(new Msg(MsgType.JOINED_GAME, clientID, userGameID));
                        break;
                    }
                    else
                    {
                        write(new Msg(MsgType.NO_GAMES, clientID));
                    }
                }

                //User creates a game but does not join it
                else if (clientMsg.getMsgType().equals(MsgType.CREATE_GAME))
                {
                    String userGameID = (String) clientMsg.getDataObj();
                    gameThread = currServer.createGameID(userGameID, clientID);

                    logger.info("Game " + userGameID + " created - creator " + clientID);
                }

                //User views currenly running games
                else if (clientMsg.getMsgType().equals(MsgType.GIVE_GAME_LIST))
                {
                    ConcurrentHashMap<String, List<String>> sendToUser = currServer.getRunningGameThreads();
                    if (sendToUser.isEmpty())
                    {
                        write(new Msg(MsgType.NO_GAMES, clientID));
                    }
                    else
                    {
                        write(new Msg(MsgType.GAME_LIST, clientID, sendToUser));
                    }
                }

                //user wants to view saved games with his participation
                else if (clientMsg.getMsgType().equals(MsgType.GIVE_SAVED_LIST))
                {
                    List<String> toSend = currServer.savedGamesWithUser(clientID);
                    if (toSend.isEmpty())
                    {
                        write(new Msg(MsgType.NO_GAMES, clientID));
                    }
                    else
                    {
                        write(new Msg(MsgType.SAVED_GAMES_LIST, clientID, toSend));
                    }
                }

                //user wants to restore a game
                else if (clientMsg.getMsgType().equals(MsgType.LOAD_GAME))
                {
                    if (currServer.loadGame((String) clientMsg.getDataObj()))
                    {
                        write(new Msg(MsgType.GAME_LOADED, clientID));
                    }
                    else
                    {
                        write(new Msg(MsgType.LOAD_FAILED, clientID));
                    }
                }
                else if (clientMsg.getMsgType().equals(MsgType.USER_EXIT))
                {
                    try
                    {
                        toClient.close();
                    } finally
                    {
                        fromClient.close();
                    }
                    return;
                }
            }

            gameThread.addClient(this);
            currServer.userConnectedToGame(gameThread.getGameID());

            if (gameThread.getPlayersConnected() == 2)
            {
                gameThread.start();
            }
        } catch (IOException e)
        {
            logger.error("Exception when dealing with message from client " + clientID);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    void terminateConnection(String message)
    {
        write(new Msg(MsgType.TERMINATE, clientID, message));
        closeSocket();
        if (gameThread != null)
        {
            currServer.removeGame(gameThread.getGameID());
        }
    }

    void sendWait()
    {
        write(new Msg(MsgType.WAIT, clientID));
    }
}


