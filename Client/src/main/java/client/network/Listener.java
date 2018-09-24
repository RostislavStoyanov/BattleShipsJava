package client.network;

import msg.Msg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class Listener extends Thread
{
    private static Logger logger = LogManager.getLogger("Client");
    private ObjectInputStream fromServer;
    private ArrayBlockingQueue<Msg> recieved;
    private String userId;

    Listener(ObjectInputStream fromServer, String userId)
    {
        this.fromServer = fromServer;
        this.recieved = new ArrayBlockingQueue<>(10);
        this.userId = userId;
    }

    ArrayBlockingQueue<Msg> getRecieved()
    {
        return recieved;
    }

    @Override
    public void run()
    {
        this.setName("Listener");

        Msg msg;
        try
        {
            while ((msg = (Msg) fromServer.readObject()) != null)
            {
                recieved.add(msg);
            }
        } catch (IOException | ClassNotFoundException e)
        {
            logger.warn(userId + "  had exception in client when receiving message");
        }
    }
}
