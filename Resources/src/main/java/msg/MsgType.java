/*
 *
 *
 * Describes all the possible messages
 *
 * No tests required
 *
 *
 */
package msg;

import java.io.Serializable;

public enum MsgType implements Serializable
{
    NO_GAMES,
    PLACE_SHIPS,
    SHIPS_PLACED,
    WIN,
    JOIN_GAME_ID,
    JOIN_GAME,
    CREATE_GAME,
    ALREADY_SHOT,
    HIT,
    MISS,
    DRAW,
    GAME_LIST,
    YOUR_FIELD_HIT,
    FIELD_MISS,
    TAKE_SHOT,
    SHOOTING,
    SET_OPPONENT,
    TERMINATE, JOINED_GAME,
    USER_EXIT,
    GIVE_GAME_LIST,
    ASK_FOR_SAVE,
    GIVE_SAVED_LIST,
    SAVED_GAMES_LIST,
    LOAD_GAME,
    LOAD_FAILED,
    GAME_LOADED,
    ENEMY_BOARD,
    YOUR_BOARD,
    WAIT,
    SAVE_SUCCESS,
    LOSE
}

