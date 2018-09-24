package IO;


import server.network.Server;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class InputHandler extends Thread
{
    private static ConcurrentHashMap<String, List<String>> runningGames;
    private Server server;
    private Scanner scanner = new Scanner(System.in);
    private boolean stop = false;

    public InputHandler(Server server)
    {
        this.server = server;
    }

    public void run()
    {
        while (!stop)
        {
            String input = scanner.nextLine();
            input = input.toLowerCase();
            switch (input)
            {
                case "help":
                    System.out.println("help - Displays this message; \n" +
                            "show - Displays all created games; \n" +
                            "saved list - Displays all saved games; \n" +
                            "exit - Stops the server. \n");
                    break;
                case "show":
                    runningGames = server.getRunningGameThreads();
                    printRunning();
                    break;
                case "exit":
                    System.out.println("Exiting ..");
                    server.closeServer();
                    stop = true;
                    return;
                case "saved list":
                    System.out.println("Currently saved games: ");
                    server.printSavedGames();
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    private static void printRunning()
    {
        //Find the longest game id and creator name in order to have the printing size

        if (runningGames.isEmpty())
        {
            System.out.println("No running games");
            return;
        }

        int longestGameId = 4,
                longestName = 8;

        //Calculating row sizes
        for (Map.Entry<String, List<String>> entry : runningGames.entrySet())
        {

            String head = entry.getKey();
            String[] splitted = head.split("\\s");

            int currGameIdLength = splitted[0].length();
            int currNameLength = splitted[1].length();
            if (currGameIdLength > longestGameId)
            {
                longestGameId = currGameIdLength;
            }

            if (currNameLength > longestName)
            {
                longestName = currNameLength;
            }
        }


        int columnName = longestName + 2;
        int columnGameID = longestGameId + 2;


        //Printing head row
        System.out.printf("|%" + columnGameID + "s"
                + "|" + "%" + columnName + "s"
                + "|" + "%14s|%9s" + "|\n", "Name", "Creator", "Status", "Players");

        //Printing separator row
        System.out.print('|');
        for (int i = 0; i < columnGameID; i++)
            System.out.print('-');
        System.out.print('|');
        for (int i = 0; i < columnName; i++)
            System.out.print('-');
        System.out.print('|');
        for (int i = 0; i < 14; i++)
            System.out.print('-');
        System.out.print('|');
        for (int i = 0; i < 9; i++)
            System.out.print('-');
        System.out.print("|\n");


        for (Map.Entry<String, List<String>> entry : runningGames.entrySet())
        {
            String head = entry.getKey();
            String[] splitted = head.split("\\s");
            String status;
            if (entry.getValue().size() < 2)
            {
                status = "pending";
            }
            else
            {
                status = "in progress";
            }
            int players = entry.getValue().size();
            String playersString = Integer.toString(players) + '/' + '2';
            System.out.printf("|%" + columnGameID + "s"
                    + "|" + "%" + columnName + "s"
                    + "|" + "%14s|%9s" + "|\n", splitted[0], splitted[1], status, playersString);
        }
    }
}
