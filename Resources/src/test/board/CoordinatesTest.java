package board;

import org.junit.BeforeClass;

import static org.junit.Assert.assertEquals;

public class CoordinatesTest
{
    private static Coordinates coord;
    private static Coordinates cord2;
    private static Coordinates coordCopy;

    @BeforeClass
    public static void setUp()
    {
        coord = new Coordinates(3, 5);
        cord2 = new Coordinates(7, 8);
        coordCopy = new Coordinates(cord2);

    }

    @org.junit.Test
    public void getRow() throws Exception
    {

        assertEquals("Should be 3", 3, coord.getRow());
        assertEquals("Should be 7", 7, cord2.getRow());
        assertEquals("Testing copy,should be 7", 7, coordCopy.getRow());
    }

    @org.junit.Test
    public void getColumn() throws Exception
    {
        assertEquals("Should be 5", 5, coord.getColumn());
        assertEquals("Should be 8", 8, cord2.getColumn());
        assertEquals("Testing copy,should be 8", 8, coordCopy.getColumn());
    }

}