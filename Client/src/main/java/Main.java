import client.network.Client;
import client.network.ClientGame;
import msg.Msg;
import msg.MsgType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


public class Main
{
    private static Client client;
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean connectedToServer = false, breakFlag = false, serverExit = false;
    private static Logger logger = LogManager.getLogger("Client");
    private static ClientGame clientGame;

    private static void joinGameID(String id)
    {
        if (!connectedToServer)
        {
            System.out.println("Must be connected to a server in order to join a game");
            return;
        }

        client.send(new Msg(MsgType.JOIN_GAME_ID, client.getClientID(), id));
        try
        {

            Msg hasGame = client.getIncoming().take();

            if (isOk(hasGame, MsgType.JOINED_GAME))
            {
                System.out.println("Joined game " + hasGame.getDataObj());
                clientGame = new ClientGame(client);
                breakFlag = true;
            }

        } catch (InterruptedException e)
        {
            logger.warn("Exception when getting message for joining game");
        }

    }

    private static boolean isOk(Msg msg, MsgType expected)
    {

        if (msg.getMsgType().equals(MsgType.NO_GAMES))
        {
            System.out.println(1);
            System.out.println("No currently running games");
            return false;
        }
        else if (msg.getMsgType().equals(MsgType.TERMINATE))
        {
            System.out.println("Server has exited");
            serverExit = true;
            breakFlag = true;
            return false;
        }
        else
        {
            return msg.getMsgType().equals(expected);
        }
    }

    private static List<String> showSaved()
    {
        if (!connectedToServer)
        {
            System.out.println("Must be connected to a server in order to view games list");
            return null;
        }
        client.send(new Msg(MsgType.GIVE_SAVED_LIST, client.getClientID()));
        try
        {
            Msg hasGame = client.getIncoming().take();

            if (isOk(hasGame, MsgType.SAVED_GAMES_LIST))
            {
                System.out.println("Your saved games: ");
                List<String> gameList = (List<String>) hasGame.getDataObj();
                for (int i = 0; i < gameList.size(); i++)
                    System.out.println(i + 1 + ". " + gameList.get(i));
                breakFlag = true;
                return gameList;
            }


        } catch (InterruptedException e)
        {
            logger.warn("Exception when getting list of saved games");
        }
        return null;
    }


    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Invalid input. Use --help for more info");
        }
        else
        {
            if (args[0].equals("--help"))
            {
                System.out.println("--help -Prints this message");
                System.out.println("--user_name Creates a client with the specified user name");
            }
            else
            {
                String userId = args[0].substring(2);
                createUser(userId);
            }
        }
    }

    private static void createUser(String userName)
    {

        for (int i = 0; i < 21; i++)
            System.out.print('#');
        System.out.println("\n BATTLESHIPS ONLINE #");
        for (int i = 0; i < 21; i++)
            System.out.print('#');
        System.out.println("\n You can always use help for a list of available commands.\n");

        client = new Client(userName);
        connectedToServer = false;

        handleInput();

    }

    private static void handleInput()
    {
        while (true)
        {
            breakFlag = false;
            String currInput = scanner.nextLine();
            currInput = currInput.toLowerCase();

            if (currInput.equals("help"))
            {
                System.out.println("help - Prints this message.");
                System.out.println("exit - Closes the client application");
                System.out.println("connect ip_address:port - Connects to the server at the given ip and port");
                System.out.println("create game_id - Creates a game with the specified id");
                System.out.println("join[gameID] - Joins the game with givenID(if not specified joins a random game)");
                System.out.println("show - Shows the currently running games on the server you are connected to");
                System.out.println("saved list - Shows list of saved games in which you have participated");
                System.out.println("load game - Displays a list of saved games and let user choose which one to load");
            }
            else if (currInput.equals("exit"))
            {
                System.out.println("Exiting..");
                if (connectedToServer)
                {
                    client.closeConnection();
                }
                break;
            }
            else if (currInput.matches("(connect)(\\s)(\\d)+(.)(\\d)+(.)(\\d)+(.)(\\d)+(:)(\\d)+") && !connectedToServer)
            {
                String[] splitted = currInput.split("(\\s+|\\s*:)");
                if (splitted.length != 3)
                {
                    System.out.println("Invalid input");
                    continue;
                }
                int port = Integer.parseInt(splitted[2]);

                if (client.openConnection(splitted[1], port))
                {
                    connectedToServer = true;
                    System.out.printf("Successfully connected to server at %s:%d \n", splitted[1], port);
                }
                else
                {
                    System.out.printf("No server found at %s:%d \n", splitted[1], port);
                }
            }
            else if (currInput.matches("(create)(\\s)(\\w)+"))
            {
                if (!connectedToServer)
                {
                    System.out.println("Must be connected to server in order to create a game");
                    continue;
                }
                String[] splitted = currInput.split("\\s+");
                client.send(new Msg(MsgType.CREATE_GAME, client.getClientID(), splitted[1]));
            }
            else if (currInput.equals("join"))
            {
                if (!connectedToServer)
                {
                    System.out.println("Must be connected to a server in order to join a game");
                    continue;
                }


                System.out.println("Joining a random game ...");

                client.send(new Msg(MsgType.JOIN_GAME, client.getClientID()));
                try
                {
                    Msg hasGame = client.getIncoming().take();

                    if (isOk(hasGame, MsgType.JOINED_GAME))
                    {
                        System.out.println("Joined game " + hasGame.getDataObj());
                        clientGame = new ClientGame(client);
                        break;
                    }

                    else if (breakFlag)
                    {
                        break;
                    }

                } catch (InterruptedException e)
                {
                    logger.warn("Exception when getting message for joining game");
                }
            }

            //print running games
            else if (currInput.equals("show"))
            {
                if (!connectedToServer)
                {
                    System.out.println("Must be connected to a server in order to view games list");
                    continue;
                }
                client.send(new Msg(MsgType.GIVE_GAME_LIST, client.getClientID()));
                try
                {
                    Msg hasGame = client.getIncoming().take();

                    if (isOk(hasGame, MsgType.GAME_LIST))
                    {
                        System.out.println("Currently running games: ");
                        if (hasGame.getDataObj() instanceof ConcurrentHashMap)
                        {

                            client.showRunning((ConcurrentHashMap<String, List<String>>) hasGame.getDataObj());
                        }
                        else
                        {
                            System.out.println("An error has occurred");
                            logger.warn("Error when printing messages");
                        }
                    }

                    else if (breakFlag)
                    {
                        break;
                    }

                } catch (InterruptedException e)
                {
                    logger.warn("Exception when getting message for joining game");
                }
            }
            else if (currInput.equals("saved list"))
            {
                showSaved();
            }
            else if (currInput.matches("(join)(\\s)(\\w)+"))
            {

                String[] splitted = currInput.split("\\s+");
                System.out.println("Joining game " + splitted[1]);
                joinGameID(splitted[1]);
                if (breakFlag || serverExit)
                {
                    break;
                }
            }
            else if (currInput.equals("load game"))
            {
                List<String> saved = showSaved();
                if (saved == null)
                {
                    System.out.println("No game available to load");
                    continue;
                }
                int idx;

                while (true)
                {
                    System.out.println("Please enter the index of the game you with to load (" + 1 + " - " + saved.size() + ")");
                    idx = scanner.nextInt();
                    if (idx < 1 || idx > saved.size())
                    {
                        System.out.println("Invalid index try again");
                    }
                    else
                    {
                        break;
                    }
                }

                System.out.println("Loading " + saved.get(idx - 1));
                client.send(new Msg(MsgType.LOAD_GAME, client.getClientID(), saved.get(idx - 1)));

                try
                {
                    Msg hasGame = client.getIncoming().take();
                    if (hasGame.getMsgType().equals(MsgType.LOAD_FAILED))
                    {
                        System.out.println("Your game couldn't be loaded");
                    }
                    else if (hasGame.getMsgType().equals(MsgType.GAME_LOADED))
                    {
                        System.out.println("Game loaded successfully... Joining the game");
                        joinGameID(saved.get(idx - 1));
                        if (breakFlag || serverExit)
                        {
                            break;
                        }
                    }
                } catch (InterruptedException e)
                {
                    logger.warn("Exception when getting message for joining game");
                }

            }
            else
            {
                System.out.println("Invalid input ...");
            }
        }
        if (clientGame != null)
        {
            clientGame.start();

            if (clientGame.getGameTerminated())
            {
                handleInput();
            }
        }
    }


}
