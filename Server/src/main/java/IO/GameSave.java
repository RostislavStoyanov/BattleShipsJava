package IO;

import board.Board;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;

public class GameSave
{
    private String gameId, user1Id, user2Id;

    public Board getUser1Board()
    {
        return user1Board;
    }

    public Board getUser2Board()
    {
        return user2Board;
    }

    public Board getUser1EnemyBoard()
    {
        return user1EnemyBoard;
    }

    public Board getUser2EnemyBoard()
    {
        return user2EnemyBoard;
    }

    private Board user1Board, user2Board, user1EnemyBoard, user2EnemyBoard;

    public Path getSavePath()
    {
        return savePath;
    }

    private Path savePath;
    private static final Logger logger = LogManager.getLogger("Server");

    public GameSave(String gameId, String user1Id, String user2Id,
                    Board user1Board, Board user2Board, Board user1EnemyBoard, Board user2EnemyBoard)
    {
        this.gameId = gameId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.user1Board = user1Board;
        this.user2Board = user2Board;
        this.user1EnemyBoard = user1EnemyBoard;
        this.user2EnemyBoard = user2EnemyBoard;
    }

    public GameSave(String gameId, String user1Id, String user2Id, Path gamePath)
    {
        this.gameId = gameId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.savePath = gamePath;
    }

    public String getGameId()
    {
        return gameId;
    }

    public String getUser1Id()
    {
        return user1Id;
    }

    public String getUser2Id()
    {
        return user2Id;
    }

    public void save()
    {
        Path currentRelativePath = Paths.get("");
        savePath = Paths.get(currentRelativePath.toAbsolutePath().toString() + ("/saves"));
        new File(savePath.toString()).mkdirs();

        try
        {
            FileOutputStream fileOut =
                    new FileOutputStream(savePath.toString() + '\\' + gameId);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(gameId);
            out.writeObject(user1Id);
            out.writeObject(user2Id);
            out.writeObject(user1Board);
            out.writeObject(user1EnemyBoard);
            out.writeObject(user2Board);
            out.writeObject(user2EnemyBoard);

            out.close();
            fileOut.close();
        } catch (IOException i)
        {
            logger.warn("Exception when serializing game");
        }
    }


    public void delete()
    {
        try
        {
            Files.delete(savePath);
        } catch (NoSuchFileException x)
        {
            logger.warn("No such file when saving game");
        } catch (DirectoryNotEmptyException x)
        {
            logger.warn("DirectoryNotEmptyException when saving game");
        } catch (IOException x)
        {
            logger.warn("file permission error when saving");
        }
    }

    public boolean restore()
    {
        Path currentRelativePath = Paths.get("");
        savePath = Paths.get(currentRelativePath.toAbsolutePath().toString() + ("/saves"));
        try
        {
            FileInputStream fileIn = new FileInputStream(savePath.toString() +'/'+ gameId);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            gameId = (String) in.readObject();
            user1Id = (String) in.readObject();
            user2Id = (String) in.readObject();
            user1Board = (Board) in.readObject();
            user1EnemyBoard = (Board) in.readObject();
            user2Board = (Board) in.readObject();
            user2EnemyBoard = (Board) in.readObject();

            in.close();
            fileIn.close();
        } catch (IOException e)
        {
            logger.error("Exception when restoring file - IO ",e);
            return false;
        } catch (ClassNotFoundException c)
        {
            logger.info("Exception when restoring file class not found");
            return false;
        }
        return true;
    }
}
