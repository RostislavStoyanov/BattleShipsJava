package server.network;

import IO.GameSave;
import IO.InputHandler;
import board.Board;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server extends Thread
{

    private int port;
    private ServerSocket serverSocket;
    private static final Logger logger = LogManager.getLogger("Server");
    private ConcurrentHashMap<GameThread, Integer> runningGameThreads;
    private InputHandler inputHandler;
    private boolean timeToStop = false;
    private ArrayList<GameSave> saveArray;
    private ArrayList<UserThread> users;


    int getGameCount()
    {
        return runningGameThreads.size();
    }

    GameThread getRandom()
    {
        List<GameThread> available = new LinkedList<>();
        for (Map.Entry<GameThread, Integer> entry : runningGameThreads.entrySet())
            if (entry.getValue() < 2)
            {
                available.add(entry.getKey());
            }
        if (available.isEmpty())
        {
            return null;
        }
        else
        {
            SecureRandom random = new SecureRandom();
            return available.get(random.nextInt(available.size()));
        }

    }

    public void closeServer()
    {
        for (UserThread u : users)
            u.terminateConnection("Server exit");
        try
        {
            timeToStop = true;
            serverSocket.close();
        } catch (IOException e)
        {
            logger.warn("Exception when closing server");
        }
    }

    public synchronized ConcurrentHashMap<String, List<String>> getRunningGameThreads()
    {
        ConcurrentHashMap<String, List<String>> sendToUser = new ConcurrentHashMap<>();
        for (Map.Entry<GameThread, Integer> entry : runningGameThreads.entrySet())
        {
            List<String> toAdd = new LinkedList<>();
            List<UserThread> toIterate = entry.getKey().getUserList();
            for (UserThread u : toIterate)
            {
                toAdd.add(u.getClientID());
            }
            String headString = entry.getKey().getGameID() + ' ' + entry.getKey().getCreatorID();
            sendToUser.put(headString, toAdd);
        }
        return sendToUser;
    }

    GameThread getThreadByID(String id)
    {
        for (Map.Entry<GameThread, Integer> entry : runningGameThreads.entrySet())
            if (entry.getKey().getGameID().equals(id))
            {
                return entry.getKey();
            }
        return null;
    }

    public Server(int port)
    {
        this.port = port;
        runningGameThreads = new ConcurrentHashMap<>();
        saveArray = new ArrayList<>();
        users = new ArrayList<>();
        try
        {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e)
        {
            logger.warn("Exception when creating server");
        }

    }

    public void run()
    {
        Path list = Paths.get("." + "/SaveList.txt");
        try
        {
            Files.lines(list.toAbsolutePath()).forEach(this::createSave);
        } catch (IOException e)
        {
            logger.warn("Exception when reading save list");
        }

        this.setName("Server thread");
        logger.info("Server thread run");

        inputHandler = new InputHandler(this);
        serverStart();
    }

    private void createSave(String s)
    {
        List<String> split = Stream.of(s.split(","))
                .map(String::new)
                .collect(Collectors.toList());

        try
        {
            GameSave save = new GameSave(split.get(0), split.get(1), split.get(2), Paths.get(split.get(3)));
            saveArray.add(save);

        } catch (Exception e)
        {
            logger.warn("Corrupt game list");
        }

    }

    private void serverStart()
    {
        logger.info("Server listens on port: " + port);

        inputHandler.start();
        Socket client;

        while (!timeToStop)
        {
            try
            {
                logger.info("Waiting for connections");
                if ((client = serverSocket.accept()) != null)
                {
                    logger.info("A user has connected");
                    ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
                    toClient.flush();
                    ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());


                    String clientID = (String) fromClient.readObject();
                    UserThread userThread = new UserThread(client, this, clientID, fromClient, toClient);
                    users.add(userThread);
                    userThread.start();
                }
            } catch (IOException e)
            {
                logger.info(e.getMessage());
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }

        }
    }

    //update count when user connects to a given game
    void userConnectedToGame(String gameId)
    {
        for (Map.Entry<GameThread, Integer> entry : runningGameThreads.entrySet())
        {
            if (entry.getKey().getGameID().equals(gameId))
            {
                runningGameThreads.put(entry.getKey(), entry.getValue() + 1);
                return;
            }
        }
    }

    //create a new game
    GameThread createGameID(String userGameID, String gameCreator)
    {
        GameThread newGame = new GameThread(userGameID, gameCreator, this);
        runningGameThreads.put(newGame, 0);
        return newGame;
    }

    //remove a game that has been stopped for some reason
    void removeGame(String gameID)
    {
        for (Map.Entry<GameThread, Integer> entry : runningGameThreads.entrySet())
        {
            if (entry.getKey().getGameID().equals(gameID))
            {
                runningGameThreads.remove(entry.getKey());
            }
        }
    }

    void save(String gameId, String client1ID, String client2ID,
              Board user1Board, Board user2Board, Board user1EnemyBoard, Board user2EnemyBoard)
    {
        for (ListIterator<GameSave> iter = saveArray.listIterator(); iter.hasNext(); )
        {
            GameSave e = iter.next();
            if (e.getGameId().equals(gameId) && e.getUser1Id().equals(client1ID) && e.getUser2Id().equals(client2ID))
            {
                e.delete();
                iter.remove();
            }
        }
        saveArray.add(new GameSave(gameId, client1ID, client2ID, user1Board, user2Board, user1EnemyBoard, user2EnemyBoard));
        saveArray.get(saveArray.size() - 1).save();
        writeSavedGames();
    }

    private void writeSavedGames()
    {
        FileWriter fileWriter;
        try
        {
            fileWriter = new FileWriter("SaveList.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for (GameSave e : saveArray)
            {
                printWriter.printf("%s,%s,%s,%s\n", e.getGameId(), e.getUser1Id(), e.getUser2Id(), e.getSavePath().toString());
            }
            printWriter.close();
        } catch (IOException e)
        {
            logger.warn("Exception when writing list of saved games");
        }
    }

    List<String> savedGamesWithUser(String user)
    {
        List<String> toRet = new ArrayList<>();
        for (GameSave save : saveArray)
        {
            if (save.getUser1Id().equals(user) || save.getUser2Id().equals(user))
            {
                toRet.add(save.getGameId());
            }
        }
        return toRet;
    }

    boolean loadGame(String gameId)
    {
        for (GameSave e : saveArray)
        {
            if (e.getGameId().equals(gameId))
            {
                if(e.restore())
                {
                    loadSavedGame(e);
                    return true;
                }
            }
        }
        return false;
    }

    private void loadSavedGame(GameSave e)
    {
        GameThread newGame = new GameThread(e.getGameId(), e.getUser1Id(), e.getUser2Id(), this, e.getUser1Board(), e.getUser2Board(),
                e.getUser1EnemyBoard(), e.getUser2EnemyBoard());
        runningGameThreads.put(newGame, 0);
    }

    public void printSavedGames()
    {
        int i = 1;
        for (GameSave s : saveArray)
        {
            System.out.println(i + "." + s.getGameId() + ' ' + s.getUser1Id() + ' ' + s.getUser2Id());
            i++;
        }
    }
}
