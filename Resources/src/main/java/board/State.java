/*
 *
 * Used to store all the states in which a cell can be
 *
 * No test required
 *
 */

package board;

import java.io.Serializable;

public enum State implements Serializable
{

    EMPTY,//empty cell
    SHIP,//cell with a ship on it
    HIT,//cell with a ship that has been revealed
    SHOT//empty cell that has been revealed

}
