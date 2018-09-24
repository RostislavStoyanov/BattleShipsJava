package client.network;

import board.Board;
import board.Coordinates;
import board.State;
import msg.Msg;
import msg.MsgType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


public class Client
{

    private static final Logger logger = LogManager.getLogger("Client");
    private static Scanner scanner = new Scanner(System.in);
    private Socket socket;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private String clientID, enemyId;
    private Board playerBoard, enemyBoard;
    private Listener listener;
    private Sender sender;
    private ConsoleInput consoleInput;
    private boolean placedBoards, exitFlag;


    public Client(String id)
    {
        this.clientID = id;
        playerBoard = new Board();
        enemyBoard = new Board();
        consoleInput = new ConsoleInput();
        placedBoards = false;
        exitFlag = false;
    }

    void clearScreen()
    {
        try
        {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e)
        {
            logger.warn("Exception when clearing screen");
        }
    }

    public boolean openConnection(String address, int port)
    {
        try
        {
            socket = new Socket(address, port);
        } catch (IOException e)
        {
            return false;
        }
        try
        {

            fromServer = new ObjectInputStream(socket.getInputStream());
            toServer = new ObjectOutputStream(socket.getOutputStream());
            toServer.flush();


        } catch (IOException e)
        {
            logger.warn("Error while creating socket");
        }

        try
        {
            toServer.writeObject(clientID);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        listener = new Listener(fromServer, clientID);
        sender = new Sender(toServer, clientID);

        listener.start();
        sender.start();

        return true;
    }

    public void send(Msg msg)
    {
        sender.getToSend().add(msg);
    }

    public ArrayBlockingQueue<Msg> getIncoming()
    {
        return listener.getRecieved();
    }

    private void placeShipsOfSize(int size, int count, boolean clear)
    {
        if (exitFlag)
        {
            return;
        }

        for (int i = 0; i < count; i++)
        {
            if (clear)
            {
                clearScreen();
                System.out.println("Your current board: ");
                System.out.print('\n');
                playerBoard.printBoard();
            }

            clear = true;
            System.out.println("Please enter the coordinates of the left/top most block of your ship of size " + size);
            int x, y;
            x = getCoordinate(true);
            y = getCoordinate(false);

            consoleInput.sleepLonger = true;

            while (true)
            {
                System.out.print("Do you wish the ships to be placed horizontally(H) of vertically(V): ");

                String wayToPut = scanner.next();
                wayToPut = wayToPut.toLowerCase();

                if (wayToPut.equals("exit") || wayToPut.equals("save") || wayToPut.equals("help"))
                {
                    handleSpecials(wayToPut);
                }

                if (exitFlag)
                {
                    break;
                }

                boolean putHorizontal;

                switch (wayToPut)
                {
                    case "h":
                    case "horizontally":
                        putHorizontal = true;
                        break;
                    case "v":
                    case "vertically":
                        putHorizontal = false;
                        break;
                    default:
                        System.out.println("Invalid input please try again");
                        continue;
                }

                if (putHorizontal)
                {
                    if (y + size > 10)
                    {
                        System.out.println(x);
                        System.out.println(size);
                        System.out.println("Invalid ship placement");
                        placeShipsOfSize(size, count - i, false);
                        return;
                    }
                    if (!checkForCollisionsAndPlace(true, size, x, y))
                    {
                        System.out.println("Collision detected. Please try again");

                        //try placing the ships again (count-i) without clearing the screen
                        placeShipsOfSize(size, count - i, false);
                        return;
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    if (x + size > 10)
                    {
                        System.out.println("Invalid ship placement");
                        placeShipsOfSize(size, count - i, false);
                        return;
                    }
                    if (!checkForCollisionsAndPlace(false, size, x, y))
                    {
                        System.out.println("Collision detected. Please try again");
                        placeShipsOfSize(size, count - i, false);
                        return;
                    }
                    else
                    {
                        break;
                    }
                }
            }
            consoleInput.sleepLonger = false;
        }
    }

    private void handleSpecials(String wayToPut)
    {
        switch (wayToPut)
        {
            case "help":
                System.out.println("If you wish to continue the game just follow the on-screen instructions.\n Additional commands are :");
                System.out.println("help - Prints this message.");
                System.out.println("save - Saves the current state of the game");
                System.out.println("exit - exits the game(not saved data is lost)");
                break;
            case "exit":
                System.out.println("Exiting ...");
                send(new Msg(MsgType.USER_EXIT, clientID));
                exitFlag = true;
                break;
            case "save":
                if (!placedBoards)
                {
                    System.out.println("Boards must be placed in order to save");
                    return;
                }
                System.out.print("Saving ..");
                saveGame();
                break;
        }
    }

    private void saveGame()
    {
        send(new Msg(MsgType.ASK_FOR_SAVE, clientID));
        try
        {
            if (listener.getRecieved().take().getMsgType().equals(MsgType.SAVE_SUCCESS))
            {
                System.out.print(" save successful.\n");
            }
        } catch (InterruptedException e)
        {
            logger.info("Exception when saving game");
            System.out.println(" game could not be saved.");
        }
    }

    private boolean checkForCollisionsAndPlace(boolean b, int size, int x, int y)
    {
        if (b)
        {
            for (int i = 0; i < size; i++)
            {
                if (!playerBoard.getState(x, y + i).equals(State.EMPTY))
                {
                    return false;
                }
            }
            for (int i = 0; i < size; i++)
            {
                playerBoard.setBoardState(x, y + i, State.SHIP);
            }
            return true;
        }

        for (int i = 0; i < size; i++)
        {
            if (!playerBoard.getState(x + i, y).equals(State.EMPTY))
            {
                return false;
            }
        }
        for (int i = 0; i < size; i++)
        {
            playerBoard.setBoardState(x + i, y, State.SHIP);
        }
        return true;
    }

    void setPlayerBoard()
    {
        placeShipsOfSize(5, 1, true);
        placeShipsOfSize(4, 2, true);
        placeShipsOfSize(3, 3, true);
        placeShipsOfSize(2, 4, true);

        if (exitFlag)
        {
            return;
        }

        placedBoards = true;

        logger.info("Player " + clientID + " has set board.");
    }


    public String getClientID()
    {
        return clientID;
    }

    public void closeConnection()
    {
        send(new Msg(MsgType.USER_EXIT, clientID));
        if (listener.isAlive())
        {
            listener.interrupt();
        }

        if (sender.isAlive())
        {
            sender.interrupt();
        }

        try
        {
            if (socket != null)
            {
                socket.close();
            }
        } catch (IOException e)
        {
            logger.info("Exception when closing client socket");
        }
    }

    Coordinates askForShot()
    {
        clearScreen();
        System.out.println("Enemy board:");
        enemyBoard.printBoard();

        System.out.println("Please enter the coordinates of your shot: ");
        int x, y;
        while (true)
        {
            x = getCoordinate(true);
            y = getCoordinate(false);
            if (!playerBoard.getState(x, y).equals(State.EMPTY))
            {
                System.out.println("You have already shot here,please try again");
            }
            else
            {
                break;
            }
        }
        return new Coordinates(x, y);
    }


    void updateOnPlayerShot(boolean b, Coordinates coordinates)
    {
        if (b)
        {
            enemyBoard.setBoardState(coordinates.getRow(), coordinates.getColumn(), State.HIT);
            return;
        }
        enemyBoard.setBoardState(coordinates.getRow(), coordinates.getColumn(), State.SHOT);
    }


    void printPlayerBoard()
    {
        playerBoard.printBoard();
    }

    void printEnemyBoard()
    {
        enemyBoard.printBoard();
    }

    void updateOnEnemyShot(boolean b, Coordinates hitPoint)
    {
        if (b)
        {
            playerBoard.setBoardState(hitPoint.getRow(), hitPoint.getColumn(), State.HIT);
            return;

        }
        playerBoard.setBoardState(hitPoint.getRow(), hitPoint.getColumn(), State.SHOT);
    }

    void setOpponent(String enemyId)
    {
        this.enemyId = enemyId;
    }

    void setConsoleInput(ConsoleInput consoleInput)
    {
        this.consoleInput = consoleInput;
    }

    private boolean isInteger(String input)
    {
        try
        {
            Integer.parseInt(input);
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    private int getCoordinate(boolean isLetter)
    {

        int toRet;

        while (true)
        {
            consoleInput.sleepLonger = true;

            if (isLetter)
            {
                System.out.print("Please enter the row [A-J] :");
            }
            else
            {
                System.out.print("Please enter the column [1-10] :");
            }
            String toRetStr;
            toRetStr = scanner.next();
            toRetStr = toRetStr.toUpperCase();

            if (toRetStr.equals("EXIT") || toRetStr.equals("HELP") || toRetStr.equals("SAVE"))
            {
                handleSpecials(toRetStr.toLowerCase());
                return getCoordinate(isLetter);
            }

            if (isLetter)
            {
                toRet = toRetStr.charAt(0) - 'A';
                if (toRet >= 0 && toRet < 10)
                {
                    consoleInput.sleepLonger = false;
                    return toRet;
                }
            }
            else
            {
                if (!isInteger(toRetStr))
                {
                    continue;
                }
                toRet = Integer.parseInt(toRetStr);

                if (toRet > 0 && toRet <= 10)
                {
                    toRet = toRet - 1;
                    consoleInput.sleepLonger = false;
                    return toRet;
                }
            }
        }
    }

    Board getBoard()
    {
        return new Board(playerBoard);
    }

    void setBoard(Board board)
    {
        playerBoard = new Board(board);
    }

    String getEnemy()
    {
        return enemyId;
    }

    public void showRunning(ConcurrentHashMap<String, List<String>> running)
    {
        consoleInput.printRunning(running);
    }

    void setEnemyBoard(Board board)
    {
        enemyBoard = new Board(board);
    }
}
