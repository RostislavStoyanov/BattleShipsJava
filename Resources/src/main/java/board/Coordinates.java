/*
 *
 * This class is used to represent coordinates of a cell in the field
 *
 *
 */

package board;

import java.io.Serializable;

public class Coordinates implements Serializable
{
    private int Row, Column;

    public Coordinates(Integer row, Integer column)
    {
        this.Row = row;
        this.Column = column;
    }

    public Coordinates(Coordinates coord)
    {
        if (coord == null)
        {
            return;
        }

        this.Row = coord.Row;
        this.Column = coord.Column;
    }

    public int getRow()
    {
        return Row;
    }

    public int getColumn()
    {
        return Column;
    }
}
