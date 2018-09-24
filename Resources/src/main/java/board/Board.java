/*
 *
 *
 * This class is used to represent a player`s board
 *
 *
 */

package board;

//import java.awt.desktop.SystemEventListener;

import java.io.Serializable;
import java.util.Arrays;

public class Board implements Serializable
{
    private static final int BOARD_SIZE = 10;//board has 10x10 size
    private State[][] board;

    public Board()
    {
        board = new State[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                board[i][j] = State.EMPTY;
    }

    public Board(Board otherBoard)
    {
        board = new State[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                board[i][j] = otherBoard.board[i][j];
    }

    public State[][] getBoard()
    {
        return board;
    }

    public void setBoard(State[][] newBoard)
    {
        this.board = newBoard;
    }

    public State getState(int row, int col)
    {
        return board[row][col];
    }

    public void setBoardState(int row, int col, State state)
    {
        board[row][col] = state;
    }

    public Boolean returnTrueOnHit(Coordinates coordToHit)
    {
        return (board[coordToHit.getRow()][coordToHit.getColumn()].equals(State.SHIP));
    }


    public void printBoard()
    {

        System.out.print("  ");
        for (int j = 1; j <= 10; j++)
            System.out.printf("%d" + ' ', j);
        System.out.print("\n");

        System.out.print("  ");
        for (int j = 1; j <= 10; j++)
            System.out.print("_ ");
        System.out.print("\n");

        for (int i = 0; i < BOARD_SIZE; i++)
        {

            int currRow = i;
            System.out.printf("%1c", ((char) ('A' + currRow)));
            for (int j = 0; j < BOARD_SIZE; j++)
            {
                System.out.print('|');
                if (board[i][j] == State.EMPTY)
                {
                    System.out.print('_');
                }
                else if (board[i][j] == State.SHIP)
                {
                    System.out.print('#');
                }
                else if (board[i][j] == State.HIT)
                {
                    System.out.print('X');
                }
                else if (board[i][j] == State.SHOT)
                {
                    System.out.print('O');
                }
            }
            System.out.print('|');
            System.out.print("\n");
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass())
        {
            return false;
        }

        Board brd = (Board) obj;

        return Arrays.deepEquals(board, brd.board);
    }
}
