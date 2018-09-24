package server.network;

import board.Board;
import board.Coordinates;
import msg.Msg;
import msg.MsgType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameThread extends Thread
{

    private final Server server;
    private String GameID;
    private UserThread[] users;
    private Board[] userBoards;
    private Board[] enemyBoards;
    private String[] savedUserId;
    private int connectedPlayers;
    private ArrayList<Coordinates> hitUser1, hitUser2;
    private int player1Hit, player2Hit;
    private String creatorID;
    private Coordinates currCoords;
    private boolean userExit, isSaved, gameWon;

    private static final Logger logger = LogManager.getLogger("Server");

    String getGameID()
    {
        return GameID;
    }

    List<UserThread> getUserList()
    {
        return new LinkedList<>(Arrays.asList(users).subList(0, connectedPlayers));
    }


    GameThread(String id, String creatorID, Server server)
    {
        this.GameID = id;
        users = new UserThread[2];
        this.creatorID = creatorID;
        this.server = server;
        connectedPlayers = 0;

        userBoards = new Board[2];
        enemyBoards = new Board[2];

        gameWon = false;
        isSaved = false;
        userExit = false;

        player1Hit = 0;
        player2Hit = 0;
        hitUser1 = new ArrayList<>();
        hitUser2 = new ArrayList<>();

    }

    GameThread(String id, String user1, String user2, Server server,
               Board user1Board, Board user2Board, Board user1EnemyBoard, Board user2EnemyBoard)
    {
        this.GameID = id;

        users = new UserThread[2];
        userBoards = new Board[2];
        savedUserId = new String[2];
        enemyBoards = new Board[2];

        gameWon = false;
        player1Hit = 0;
        player2Hit = 0;
        hitUser1 = new ArrayList<>();
        hitUser2 = new ArrayList<>();
        this.creatorID = user1;
        this.server = server;
        isSaved = true;
        connectedPlayers = 0;

        userBoards[0] = user1Board;
        userBoards[1] = user2Board;
        savedUserId[0] = user1;
        savedUserId[1] = user2;
        enemyBoards[0] = user1EnemyBoard;
        enemyBoards[1] = user2EnemyBoard;

    }

    void addClient(UserThread newUser) throws java.io.IOException
    {
        if (connectedPlayers < 2)
        {
            if (isSaved && (newUser.getClientID().equals(savedUserId[0]) || newUser.getClientID().equals(savedUserId[1])))
            {
                users[connectedPlayers] = newUser;
                userBoards[connectedPlayers++] = new Board();
            }
            else if (!isSaved)
            {
                users[connectedPlayers] = newUser;
                userBoards[connectedPlayers++] = new Board();
            }
        }
        else
        {
            throw new java.io.IOException();
        }
    }

    int getPlayersConnected()
    {
        return connectedPlayers;
    }

    @Override
    public void run()
    {

        this.setName(GameID + " - game thread");

        for (int i = 0; i < 2; i++)
        {
            if (i == 0)
            {
                users[0].write(new Msg(MsgType.SET_OPPONENT, users[0].getClientID(), users[1].getClientID()));
            }
            else
            {
                users[1].write(new Msg(MsgType.SET_OPPONENT, users[1].getClientID(), users[0].getClientID()));
            }

        }

        if (isSaved)
        {
            for (int i = 0; i < 2; i++)
            {
                users[i].write(new Msg(MsgType.YOUR_BOARD, users[i].getClientID(), userBoards[i]));
                users[i].write(new Msg(MsgType.ENEMY_BOARD, users[i].getClientID(), enemyBoards[i]));
            }
        }
        else
        {
            for (int i = 0; i < 2; i++)
                enemyBoards[i] = new Board();
            getBoards();
        }
        if (userExit)
        {
            return;
        }


        while (!gameWon && !userExit)
        {

            users[1].sendWait();

            if (hits(users[0], userBoards[1], hitUser1))
            {
                player1Hit++;
                users[0].write(new Msg(MsgType.HIT, users[0].getClientID(), currCoords));
                users[1].write(new Msg(MsgType.YOUR_FIELD_HIT, users[1].getClientID(), currCoords));
            }
            else
            {
                //if a user wants to exit hits returns false so we always get here
                if (userExit)
                {
                    return;
                }

                users[0].write(new Msg(MsgType.MISS, users[0].getClientID(), currCoords));
                users[1].write(new Msg(MsgType.FIELD_MISS, users[1].getClientID(), currCoords));
            }

            users[0].sendWait();

            if (hits(users[1], userBoards[0], hitUser2))
            {
                player2Hit++;
                users[0].write(new Msg(MsgType.YOUR_FIELD_HIT, users[0].getClientID(), currCoords));
                users[1].write(new Msg(MsgType.HIT, users[1].getClientID(), currCoords));
            }
            else
            {
                if (userExit)
                {
                    return;
                }

                users[0].write(new Msg(MsgType.FIELD_MISS, users[0].getClientID(), currCoords));
                users[1].write(new Msg(MsgType.MISS, users[1].getClientID(), currCoords));
            }

            if (haveWinner(users[0], users[1]))
            {
                gameWon = true;
                return;
            }
        }

    }


    private void terminateGame(String msg)
    {
        for (UserThread u : users)
        {
            u.terminateConnection(msg);
        }
    }

    private boolean hits(UserThread user, Board usrBoard, ArrayList<Coordinates> hitUser)
    {
        user.write(new Msg(MsgType.TAKE_SHOT, user.getClientID()));
        Msg currMsg;

        currMsg = user.receive();

        if (currMsg.getMsgType().equals(MsgType.USER_EXIT))
        {
            terminateGame("User " + user.getClientID() + " wants to exit the game. The game will be terminated");
            logger.info("Received msg from user " + user.getClientID() + " to exit game thread - " + GameID);
            userExit = true;
            return false;
        }
        else if (currMsg.getMsgType().equals(MsgType.ASK_FOR_SAVE))
        {
            saveGame(user);
            hits(user, usrBoard, hitUser);
        }
        else if (currMsg.getMsgType().equals(MsgType.SHOOTING))
        {
            currCoords = (Coordinates) currMsg.getDataObj();
            if (usrBoard.returnTrueOnHit(currCoords))
            {
                if (hitUser.contains(currCoords))
                {
                    user.write(new Msg(MsgType.ALREADY_SHOT, user.getClientID(), currCoords));
                }
                else
                {
                    hitUser.add(currCoords);


                    if (user.getClientID().equals(users[0].getClientID()))
                    {
                        enemyBoards[0].setBoardState(currCoords.getRow(), currCoords.getColumn(), board.State.HIT);
                    }

                    else
                    {
                        enemyBoards[1].setBoardState(currCoords.getRow(), currCoords.getColumn(), board.State.HIT);
                    }

                    return true;
                }
            }

            if (user.getClientID().equals(users[0].getClientID()))
            {
                enemyBoards[0].setBoardState(currCoords.getRow(), currCoords.getColumn(), board.State.SHOT);
            }

            else
            {
                enemyBoards[1].setBoardState(currCoords.getRow(), currCoords.getColumn(), board.State.SHOT);
            }
        }
        return false;
    }

    private void saveGame(UserThread user)
    {
        //user1 is the user who calls the save will resume from him(user2 will become user1)


        if (user.getClientID().equals(users[0].getClientID()))
        {
            server.save(GameID, users[0].getClientID(), users[1].getClientID(),
                    userBoards[0], userBoards[1], enemyBoards[0], enemyBoards[1]);
            users[0].write(new Msg(MsgType.SAVE_SUCCESS, users[0].getClientID()));
        }
        else
        {
            server.save(GameID, users[1].getClientID(), users[0].getClientID(),
                    userBoards[1], userBoards[0], enemyBoards[1], enemyBoards[0]);
            users[1].write(new Msg(MsgType.SAVE_SUCCESS, users[1].getClientID()));
        }

    }

    private void getBoards()
    {
        for (UserThread u : users)
            u.write(new Msg(MsgType.PLACE_SHIPS, u.getClientID()));

        for (int i = 0; i < 2; i++)
        {
            Msg msg;
            while ((msg = users[i].receive()) != null)
            {
                if (msg.getMsgType().equals(MsgType.SHIPS_PLACED) && msg.getPlayerID().equals(users[i].getClientID()))
                {
                    userBoards[i] = ((Board) msg.getDataObj());
                    break;
                }
                else if (msg.getMsgType().equals(MsgType.USER_EXIT))
                {
                    terminateGame("User " + users[i].getClientID() + " wants to exit the game. The game will be terminated");
                    logger.info("Received msg from user " + users[i].getClientID() + " to exit game thread - " + GameID);
                    userExit = true;
                    return;
                }
            }
        }
    }


    private boolean haveWinner(UserThread user1, UserThread user2)
    {
        if (player1Hit == 30 && player2Hit == 30)
        {
            user1.write(new Msg(MsgType.DRAW, user1.getClientID()));
            user2.write(new Msg(MsgType.DRAW, user1.getClientID()));
            return true;
        }

        if (player1Hit == 30)
        {
            user1.write(new Msg(MsgType.WIN, user1.getClientID()));
            user2.write(new Msg(MsgType.LOSE, user1.getClientID()));
            return true;
        }

        if (player2Hit == 30)
        {

            user1.write(new Msg(MsgType.LOSE, user1.getClientID()));
            user2.write(new Msg(MsgType.WIN, user1.getClientID()));
            return true;
        }

        return false;
    }


    String getCreatorID()
    {
        return creatorID;
    }
}