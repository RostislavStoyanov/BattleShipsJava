package client.network;

import board.Board;
import board.Coordinates;
import msg.Msg;
import msg.MsgType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;

public class ClientGame extends Thread
{
    private Client client;
    private boolean stop, gameTerminated;
    private static Logger logger = LogManager.getLogger("Client");
    private ConsoleInput consoleInput = new ConsoleInput(this);

    public ClientGame(Client client)
    {
        this.client = client;
        stop = false;
        gameTerminated = false;
    }

    @Override
    public void run()
    {

        this.setName(client.getClientID() + " - game thread");

        client.setConsoleInput(consoleInput);
        consoleInput.start();

        while (!stop)
        {

            ArrayBlockingQueue<Msg> incoming = client.getIncoming();
            Msg msg;
            try
            {
                while ((msg = incoming.take()) != null)
                {

                    MsgType currMsgType = msg.getMsgType();

                    if (currMsgType.equals(MsgType.SET_OPPONENT))
                    {
                        client.setOpponent((String) msg.getDataObj());
                    }
                    else if (currMsgType.equals(MsgType.PLACE_SHIPS))
                    {
                        client.setPlayerBoard();
                        client.send(new Msg(MsgType.SHIPS_PLACED, client.getClientID(), client.getBoard()));
                    }
                    else if (currMsgType.equals(MsgType.ENEMY_BOARD))
                    {
                        client.setEnemyBoard((Board) msg.getDataObj());
                    }
                    else if (currMsgType.equals(MsgType.YOUR_BOARD))
                    {
                        client.setBoard((Board) msg.getDataObj());
                    }
                    else if (currMsgType.equals(MsgType.TERMINATE))
                    {
                        System.out.println((String) msg.getDataObj());
                        this.userEndGame();
                        gameTerminated = true;
                        return;
                    }
                    else if (currMsgType.equals(MsgType.TAKE_SHOT))
                    {
                        Coordinates toShoot = client.askForShot();
                        client.send(new Msg(MsgType.SHOOTING, client.getClientID(), toShoot));
                    }
                    else if (currMsgType.equals(MsgType.HIT))
                    {
                        Coordinates hitPoint = (Coordinates) msg.getDataObj();
                        client.updateOnPlayerShot(true, hitPoint);

                        clearAndPrintBoardsAndMessage("You hit an enemy ship.");
                        sleepSoUserCanViewMessage();
                    }
                    else if (currMsgType.equals(MsgType.MISS))
                    {
                        Coordinates missPoint = (Coordinates) msg.getDataObj();
                        client.updateOnPlayerShot(false, missPoint);

                        clearAndPrintBoardsAndMessage("You missed.");
                        sleepSoUserCanViewMessage();
                    }
                    else if (currMsgType.equals(MsgType.YOUR_FIELD_HIT))
                    {
                        Coordinates hitPoint = (Coordinates) msg.getDataObj();
                        client.updateOnEnemyShot(true, hitPoint);

                        clearAndPrintBoardsAndMessage("One of your ships has been hit");
                        sleepSoUserCanViewMessage();
                    }
                    else if (currMsgType.equals(MsgType.FIELD_MISS))
                    {
                        Coordinates missPoint = (Coordinates) msg.getDataObj();
                        client.updateOnEnemyShot(false, missPoint);

                        clearAndPrintBoardsAndMessage("Enemy shot but missed");
                        sleepSoUserCanViewMessage();

                    }
                    else if (currMsgType.equals(MsgType.WAIT))
                    {
                        clearAndPrintBoardsAndMessage(client.getEnemy() + "'s turn. Please wait!");
                    }
                    else if (currMsgType.equals(MsgType.WIN))
                    {
                        clearAndPrintBoardsAndMessage("Congratulation!! You win.");
                        sleepSoUserCanViewMessage();
                        this.userEndGame();
                        gameTerminated = true;
                        return;
                    }
                    else if (currMsgType.equals(MsgType.DRAW))
                    {
                        clearAndPrintBoardsAndMessage("Draw.");
                        sleepSoUserCanViewMessage();
                        this.userEndGame();
                        gameTerminated = true;
                        return;
                    }
                    else if (currMsgType.equals(MsgType.LOSE))
                    {
                        clearAndPrintBoardsAndMessage("You lose. Better luck next time.");
                        sleepSoUserCanViewMessage();
                        this.userEndGame();
                        gameTerminated = true;
                        return;
                    }

                }
            } catch (InterruptedException e)
            {
                logger.info("Interrupted while queue was waiting for messages");
            }
        }
    }

    private void clearAndPrintBoardsAndMessage(String message)
    {

        client.clearScreen();

        System.out.println(message);

        System.out.println(client.getEnemy() + " board: ");
        client.printEnemyBoard();

        System.out.println("Your board: ");
        client.printPlayerBoard();
    }

    void userEndGame()
    {
        client.closeConnection();
        stop = true;
        logger.info("Client" + client.getClientID() + "exited the game");
    }


    private void sleepSoUserCanViewMessage()
    {
        try
        {
            sleep(3000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public boolean getGameTerminated()
    {
        return gameTerminated;
    }
}
