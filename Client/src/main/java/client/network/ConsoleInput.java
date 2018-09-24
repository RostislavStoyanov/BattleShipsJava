package client.network;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsoleInput extends Thread
{
    private ClientGame clientGame;
    private boolean getInput = false;
    boolean sleepLonger;

    ConsoleInput(ClientGame clientGame)
    {
        this.clientGame = clientGame;
    }

    ConsoleInput()
    {
    }

    @SuppressWarnings("Duplicates")
    void printRunning(ConcurrentHashMap<String, List<String>> runningGames)
    {
        //Find the longest game id and creator name in order to have the printing size

        if (runningGames.isEmpty())
        {
            System.out.println("No running games");
            return;
        }

        int longestGameId = 4, longestName = 8;

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

    private String call() throws IOException
    {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in));
        String input;
        do
        {
            try
            {
                // wait until we have data to complete a readLine()
                while (!br.ready() || sleepLonger)
                {
                    Thread.sleep(200);
                }
                input = br.readLine();
            } catch (InterruptedException e)
            {
                return null;
            }
        } while ("".equals(input));

        return input;
    }

    @Override
    public void run()
    {
        String input = "";

        while (!getInput)
        {

            try
            {
                input = call();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            if (input == null)
            {
                continue;
            }

            input = input.toLowerCase();

            switch (input)
            {
                case "help":
                    System.out.println("Here should be a list of available commands like help exit save");
                    break;
                case "exit":
                    clientGame.userEndGame();
                    getInput = true;
                    break;
                default:
                    System.out.println("Invalid input!!");
                    break;

            }
        }
    }
}