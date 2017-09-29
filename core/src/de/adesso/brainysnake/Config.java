package de.adesso.brainysnake;

import com.badlogic.gdx.graphics.Color;

public class Config {

    /*Gamepleysettings*/

    public static final int MAX_ROUNDS = 1000;
    /*others*/

    public static final float UPDATE_RATE       = 30f;
    public static final Color POINT_COLLOR      = Color.RED;
    public static final float GHOSTMODE_OPACITY = 1f;
    public static final Color GHOSTMODE_COLOR = Color.GRAY;
    public static final float SNAKE_BODY_LIGHTING = 0.7f;
    public static int APPLICATION_WIDTH         = 1280;
    public static int APPLICATION_HEIGHT        = 900;
    public static int SNAKE_CUBE_SIZE           = 10;
    public static Color LEVEL_COLOR             = Color.WHITE;
    public static Color BARRIER_COLOR           = Color.LIGHT_GRAY;
    public static Color DEFAULT_PLAYER_COLOR    = Color.RED;
    public static Color BLINK_COLOR             = Color.YELLOW;
    public static Color DEAD_COLOR              = Color.VIOLET;
    public static int BLINKING_SPEED            = 5; // Blink length in Update-Calls
    public static int QUANTITY_BARRIERS         = 6;
    public static final int INITIAL_PLAYER_LENGTH = 10;
    public static final int MAX_POINTS_IN_LEVEL = 15;
    public static final int GHOST_TIME = 30;
    public static final int BLINK_TIME = ((int)UPDATE_RATE/10)+1;
    public static final int MAX_AGENT_PROCESSING_TIME_MS = 100;
}
