package client.network;

import msg.Msg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class Sender extends Thread
{
    private static final Logger logger = LogManager.getLogger("Client");
    private ArrayBlockingQueue<Msg> toSend;
    private ObjectOutputStream toServer;
    private String userId;

    Sender(ObjectOutputStream toServer, String userId)
    {
        this.toSend = new ArrayBlockingQueue<>(10);
        this.toServer = toServer;
        this.userId = userId;
    }

    ArrayBlockingQueue<Msg> getToSend()
    {
        return toSend;
    }

    private void send(Msg msg)
    {
        try
        {
            toServer.writeObject(msg);
            logger.info(userId + " sent: " + msg.getMsgType());
        } catch (IOException e)
        {
            logger.info("Exception occurred while writing message to server");
        }
    }

    @Override
    public void run()
    {
        this.setName("Message sender");

        Msg msg;
        try
        {
            while ((msg = toSend.take()) != null)
            {
                send(msg);
            }
        } catch (InterruptedException e)
        {
            logger.info("Thread interrupted");
        }
    }
}
