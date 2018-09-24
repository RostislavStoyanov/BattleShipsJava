//server main

import server.network.Server;


public class Main
{


    private static void startServer(int port)
    {
        Server server = new Server(port);
        System.out.println("Server started - listening on port " + port);

        server.run();
    }


    public static void main(String[] args)
    {
        if (args.length > 2)
        {
            System.out.println("Invalid input.");
        }

        else
        {
            if (args.length == 1 && args[0].equals("--help"))
            {
                System.out.println("--help - Displays this message");
                System.out.println("--start [port] -Starts a Battleships server on the specified port(default is 4444)");
            }

            else if (args.length == 1 && args[0].equals("--start"))
            {
                startServer(4444);
            }

            else if (args.length == 2 && args[0].equals("--start"))
            {

                if (!args[1].matches("\\d+"))
                {
                    System.out.println("Invalid input. Port should be a integer");
                    return;
                }

                startServer(Integer.parseInt(args[1]));
            }
            else
            {
                System.out.println("Invalid input");
            }
        }
    }
}
