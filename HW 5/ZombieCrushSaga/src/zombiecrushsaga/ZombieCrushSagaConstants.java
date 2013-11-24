/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiecrushsaga;

import java.awt.Color;
import java.awt.Font;


/**
 * This class stores the types of controls and their possible states which
 * we'll use to control the flow of the application. Note that these control
 * types and states are NOT flavor-specific.
 * 
 * @author Yukti Abrol
 */
public class ZombieCrushSagaConstants {
  // WE ONLY HAVE A LIMITIED NUMBER OF UI COMPONENT TYPES IN THIS APP
    
    // TILE SPRITE TYPES
    public static final String TILE_A_TYPE = "TILE_A_TYPE";
    public static final String TILE_B_TYPE = "TILE_B_TYPE";
    public static final String TILE_C_TYPE = "TILE_C_TYPE";
    public static final String TILE_D_TYPE = "TILE_D_TYPE";
    public static final String TILE_E_TYPE = "TILE_E_TYPE";
    public static final String TILE_F_TYPE = "TILE_F_TYPE";
    
    public static final String TILE_STRIPED_TYPE = "TILE_STRIPED_TYPE";
    public static final String TILE_BOMB_TYPE = "TILE_BOMB_TYPE";
    public static final String TILE_WRAPPER_TYPE = "TILE_WRAPPER_TYPE";
    
    public static final String TILE_SPRITE_TYPE_PREFIX = "TILE_";
    
    // EACH SCREEN HAS ITS OWN BACKGROUND TYPE
    public static final String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    
    // THIS REPRESENTS THE BUTTONS ON THE SPLASH SCREEN FOR LEVEL SELECTION
    public static final String LEVEL_SELECT_BUTTON_TYPE = "LEVEL_SELECT_BUTTON_TYPE";

    // IN-GAME UI CONTROL TYPES
    public static final String BACK_BUTTON_TYPE = "BACK_BUTTON_TYPE";
    public static final String TIME_TYPE = "TIME_TYPE"; 
    public static final String LIVES_TYPE = "LIVES_TYPE";
    public static final String MOVES_TYPE = "MOVES_TYPE";
    public static final String SCORE_TYPE = "SCORE_TYPE";
    public static final String NEXT_STAR_TYPE = "NEXT_STAR_TYPE";
    public static final String STAR_TYPE = "STAR_TYPE";
    public static final String STAR_RED_TYPE = "STAR_RED_TYPE";
    public static final String STAR_BLUE_TYPE = "STAR_BLUE_TYPE";
    public static final String STAR_PURPLE_TYPE = "STAR_PURPLE_TYPE";
    
    public static final String PLAY_BUTTON_TYPE = "PLAY_GAME_BUTTON_TYPE";
    public static final String RESET_BUTTON_TYPE = "RESET_GAME_BUTTON_TYPE";
    public static final String QUIT_SPLASH_BUTTON_TYPE = "QUIT_GAME_SPLASH_BUTTON_TYPE";
    
    public static final String QUIT_SAGA_BUTTON_TYPE = "QUIT_GAME_SAGA_BUTTON_TYPE";
    public static final String UP_BUTTON_TYPE = "UP_BUTTON_TYPE";
    public static final String DOWN_BUTTON_TYPE = "DOWN_BUTTON_TYPE";
    public static final String ABOUT_BUTTON_TYPE = "ABOUT_BUTTON_TYPE";
    
    public static final String RETURN_FROM_ABOUT_BUTTON_TYPE = "RETURN_FROM_ABOUT_BUTTON_TYPE";
    
    public static final String RETURN_FROM_LEVEL_BUTTON_TYPE = "RETURN_FROM_LEVEL_BUTTON_TYPE";
    public static final String PLAY_LEVEL_BUTTON_TYPE = "PLAY_LEVEL_BUTTON_TYPE";

    // end game TYPES
    public static final String WIN_TYPE = "WIN_TYPE";
    public static final String LOSS_TYPE = "LOSS_TYPE";
    
    // WE'LL USE THESE STATES TO CONTROL SWITCHING BETWEEN THE 4
    public static final String SPLASH_SCREEN_STATE = "SPLASH_SCREEN_STATE";
    public static final String GAME_SCREEN_STATE = "GAME_SCREEN_STATE";  
    public static final String SAGA_SCREEN_STATE = "SAGA_SCREEN_STATE";
    public static final String ABOUT_SCREEN_STATE = "ABOUT_SCREEN_STATE";
    public static final String LEVEL_SCREEN_STATE = "LEVEL_SCREEN_STATE";
    
    public static final String ABOUT_DIALOG_TYPE = "ABOUT_DIALOG_TYPE";
    public static final String LEVEL_DIALOG_TYPE = "LEVEL_DIALOG_TYPE";
    
    public static final String SAGA_SCREEN_1_STATE = "SAGA_SCREEN_1_STATE";
    public static final String SAGA_SCREEN_2_STATE = "SAGA_SCREEN_2_STATE";
    public static final String SAGA_SCREEN_3_STATE = "SAGA_SCREEN_3_STATE";
    public static final String SAGA_SCREEN_4_STATE = "SAGA_SCREEN_4_STATE";
    public static final String SAGA_SCREEN_5_STATE = "SAGA_SCREEN_5_STATE";
    public static final String SAGA_SCREEN_6_STATE = "SAGA_SCREEN_6_STATE";
    public static final String SAGA_SCREEN_7_STATE = "SAGA_SCREEN_7_STATE";
    public static final String SAGA_SCREEN_8_STATE = "SAGA_SCREEN_8_STATE";
    public static final String SAGA_SCREEN_9_STATE = "SAGA_SCREEN_9_STATE";

    // THE TILES MAY HAVE 4 STATES:
        // - INVISIBLE_STATE: USED WHEN ON THE SPLASH SCREEN, MEANS A TILE
            // IS NOT DRAWN AND CANNOT BE CLICKED
        // - VISIBLE_STATE: USED WHEN ON THE GAME SCREEN, MEANS A TILE
            // IS VISIBLE AND CAN BE CLICKED (TO SELECT IT), BUT IS NOT CURRENTLY SELECTED
        // - SELECTED_STATE: USED WHEN ON THE GAME SCREEN, MEANS A TILE
            // IS VISIBLE AND CAN BE CLICKED (TO UNSELECT IT), AND IS CURRENTLY SELECTED     
        // - NOT_AVAILABLE_STATE: USED FOR A TILE THE USER HAS CLICKED ON THAT
            // IS NOT FREE. THIS LET'S US GIVE THE USER SOME FEEDBACK
    public static final String INVISIBLE_STATE = "INVISIBLE_STATE";
    public static final String VISIBLE_STATE = "VISIBLE_STATE";
    public static final String SELECTED_STATE = "SELECTED_STATE";
    public static final String INCORRECTLY_SELECTED_STATE = "NOT_AVAILABLE_STATE";
    public static final String MOUSE_OVER_STATE = "MOUSE_OVER_STATE";

    // THE BUTTONS MAY HAVE 2 STATES:
        // - INVISIBLE_STATE: MEANS A BUTTON IS NOT DRAWN AND CAN'T BE CLICKED
        // - VISIBLE_STATE: MEANS A BUTTON IS DRAWN AND CAN BE CLICKED
        // - MOUSE_OVER_STATE: MEANS A BUTTON IS DRAWN WITH SOME HIGHLIGHTING
            // BECAUSE THE MOUSE IS HOVERING OVER THE BUTTON

    // UI CONTROL SIZE AND POSITION SETTINGS
    
    // OR POSITIONING THE LEVEL SELECT BUTTONS
    public static final int LEVEL_BUTTON_WIDTH = 96;
    public static final int LEVEL_BUTTON_MARGIN = 200;
    public static final int LEVEL_BUTTON_Y = 96;

    // FOR STACKING TILES ON THE GRID
    //public static final int NUM_TILES = 144;
    public static final int TILE_IMAGE_OFFSET = 1;
    public static final int TILE_IMAGE_WIDTH = 55;
    public static final int TILE_IMAGE_HEIGHT = 55;
    public static final int Z_TILE_OFFSET = 5;

    // FOR MOVING TILES AROUND
    public static final int MAX_TILE_VELOCITY = 70;
    
    // DIMENSIONS OF GAME SCREEN
    public static final int MAX_SCREEN_WIDTH = 1280;
    public static final int MAX_SCREEN_HEIGHT = 720;
    public static final int SAGA_SCREEN_HEIGHT = 1524;
    
    // UI CONTROLS POSITIONS IN THE GAME SCREEN
    public static final int CONTROLS_MARGIN = 0;
    public static final int BACK_BUTTON_X = 0;
    public static final int BACK_BUTTON_Y = 0;
    public static final int LIVES_X = BACK_BUTTON_X + 81;
    public static final int LIVES_Y = 0;
    public static final int TIME_X = BACK_BUTTON_X + 81 +145 + CONTROLS_MARGIN;
    public static final int TIME_Y = 0;
    public static final int TIME_OFFSET = 150;
    public static final int TIME_TEXT_OFFSET = 55;
    public static final int MOVES_X = BACK_BUTTON_X + 81 +145 + CONTROLS_MARGIN;
    public static final int MOVES_Y = 0;
    public static final int SCORE_X = MOVES_X + 194;
    public static final int SCORE_Y = 0;
    public static final int STAR_X = SCORE_X + 232;
    public static final int STAR_Y = 0;
    public static final int STAR_OFFSET = 60;
    public static final int NEXT_STAR_X = STAR_X + 154;
    public static final int NEXT_STAR_Y = 0;
    public static final int NEXT_STAR_OFFSET = 170;
    
    public static final int UP_BUTTON_X = MAX_SCREEN_WIDTH - 95;
    public static final int UP_BUTTON_Y = 0;
    public static final int DOWN_BUTTON_X = MAX_SCREEN_WIDTH - 95;
    public static final int DOWN_BUTTON_Y = 86;
    public static final int ABOUT_BUTTON_X = MAX_SCREEN_WIDTH - 95;
    public static final int ABOUT_BUTTON_Y = DOWN_BUTTON_Y + 86;
    public static final int QUIT_SAGA_BUTTON_X = MAX_SCREEN_WIDTH - 95;
    public static final int QUIT_SAGA_BUTTON_Y = ABOUT_BUTTON_Y + 51;
    
    public static final int PLAY_BUTTON_X = 160;
    public static final int PLAY_BUTTON_Y = (int)(MAX_SCREEN_HEIGHT/4);
    public static final int RESET_BUTTON_X = PLAY_BUTTON_X + 120;
    public static final int RESET_BUTTON_Y = (int)(MAX_SCREEN_HEIGHT/2); 
    public static final int QUIT_SPLASH_BUTTON_X = RESET_BUTTON_X + 120;
    public static final int QUIT_SPLASH_BUTTON_Y = (int)(MAX_SCREEN_HEIGHT*.75);
    
    public static final int RETURN_FROM_ABOUT_BUTTON_X = (int)(MAX_SCREEN_WIDTH/2) - 225;
    public static final int RETURN_FROM_ABOUT_BUTTON_Y = 600;
    
    public static final int RETURN_FROM_LEVEL_BUTTON_X = (int)(MAX_SCREEN_WIDTH/2) - 420;
    public static final int RETURN_FROM_LEVEL_BUTTON_Y = 600;
    public static final int PLAY_LEVEL_BUTTON_X = (int)(MAX_SCREEN_WIDTH/2) + 70;
    public static final int PLAY_LEVEL_BUTTON_Y = 600;
       
    // THESE ARE USED FOR FORMATTING THE TIME OF GAME
    public static final long MILLIS_IN_A_SECOND = 1000;
    public static final long MILLIS_IN_A_MINUTE = 1000 * 60;
    public static final long MILLIS_IN_AN_HOUR  = 1000 * 60 * 60;

    // USED FOR DOING OUR VICTORY ANIMATION
    public static final int WIN_PATH_NODES = 8;
    public static final int WIN_PATH_TOLERANCE = 100;
    public static final int WIN_PATH_COORD = 100;

    // COLORS USED FOR RENDERING VARIOUS THINGS, INCLUDING THE
    // COLOR KEY, WHICH REFERS TO THE COLOR TO IGNORE WHEN
    // LOADING ART.
    public static final Color COLOR_KEY = new Color(255, 174, 201);
    public static final Color DEBUG_TEXT_COLOR = Color.BLACK;
    public static final Color TEXT_DISPLAY_COLOR = new Color (10, 160, 10);
    public static final Color SELECTED_TILE_COLOR = Color.CYAN;
    public static final Color JELLY_TILE_COLOR = new Color(255,255,0,100);
    public static final Color INCORRECTLY_SELECTED_TILE_COLOR = new Color(255, 50, 50, 100);
    public static final Color STATS_COLOR = new Color(0, 60, 0);
    public static final Color LEVEL_NUM_COLOR = new Color(34,177,76);

    // FONTS USED DURING FOR TEXTUAL GAME DISPLAYS
    public static final Font TEXT_DISPLAY_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 40);
    public static final Font DEBUG_TEXT_FONT = new Font(Font.MONOSPACED, Font.BOLD, 14);
    public static final Font STATS_FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);
    public static final Font LEVEL_NUM_FONT = new Font("Showcard Gothic", Font.BOLD, 75);
    public static final Font LEVEL_TEXT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    
    // AND AUDIO STUFF
    public static final String SUCCESS_AUDIO_TYPE = "SUCCESS_AUDIO_TYPE";
    public static final String FAILURE_AUDIO_TYPE = "FAILURE_AUDIO_TYPE";
////    public static final String THEME_SONG_TYPE = "THEME_SONG_TYPE";
  
}
