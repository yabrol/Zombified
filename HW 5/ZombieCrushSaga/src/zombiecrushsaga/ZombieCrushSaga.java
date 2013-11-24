/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiecrushsaga;

import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;
import zombiecrushsaga.ui.ZombieCrushSagaErrorHandler;
import xml_utilities.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;

/**
 * ZombieCrushSaga is a game application that's ready to be customized to play
 * different flavors of the game. It has been setup using art from Plants vs.
 * Zombies.
 *
 * @author Yukti Abrol
 */
public class ZombieCrushSaga {

  // THIS HAS THE FULL USER INTERFACE AND ONCE IN EVENT
  // HANDLING MODE, BASICALLY IT BECOMES THE FOCAL
  // POINT, RUNNING THE UI AND EVERYTHING ELSE
  static ZombieCrushSagaMiniGame miniGame = new ZombieCrushSagaMiniGame();

  
  // WE'LL LOAD ALL THE UI AND ART PROPERTIES FROM FILES,
    // BUT WE'LL NEED THESE VALUES TO START THE PROCESS
    static String PROPERTY_TYPES_LIST = "property_types.txt";
    static String UI_PROPERTIES_FILE_NAME = "properties.xml";
    static String PROPERTIES_SCHEMA_FILE_NAME = "properties_schema.xsd";    
    static String DATA_PATH = "./data/";

    /**
     * This is where the zombie crush saga game application starts execution. We'll
     * load the application properties and then use them to build our
     * user interface and start the window in event handling mode. Once
     * in that mode, all code execution will happen in response to a 
     * user request.
     */
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    try
        {
            // LOAD THE SETTINGS FOR STARTING THE APP
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(ZombieCrushSagaPropertyType.UI_PROPERTIES_FILE_NAME, UI_PROPERTIES_FILE_NAME);
            props.addProperty(ZombieCrushSagaPropertyType.PROPERTIES_SCHEMA_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            props.addProperty(ZombieCrushSagaPropertyType.DATA_PATH.toString(), DATA_PATH);
            props.loadProperties(UI_PROPERTIES_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            
            // THEN WE'LL LOAD THE zombie crush FLAVOR AS SPECIFIED BY THE PROPERTIES FILE
            String gameFlavorFile = props.getProperty(ZombieCrushSagaPropertyType.GAME_FLAVOR_FILE_NAME);
            props.loadProperties(gameFlavorFile, PROPERTIES_SCHEMA_FILE_NAME);
                               
            // NOW WE CAN LOAD THE UI, WHICH WILL USE ALL THE FLAVORED CONTENT
            String appTitle = props.getProperty(ZombieCrushSagaPropertyType.GAME_TITLE_TEXT);
            int fps = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.FPS));
            miniGame.initMiniGame(appTitle, fps);
            miniGame.startGame();
        }
        // THERE WAS A PROBLEM LOADING THE PROPERTIES FILE
        catch(InvalidXMLFileFormatException ixmlffe)
        {
            // LET THE ERROR HANDLER PROVIDE THE RESPONSE
            ZombieCrushSagaErrorHandler errorHandler = miniGame.getErrorHandler();
            errorHandler.processError(ZombieCrushSagaPropertyType.INVALID_XML_FILE_ERROR_TEXT);
        }
  }
  
  
  /**
     * zombiecrushsagaPropertyType represents the types of data that will need
     * to be extracted from XML files.
     */
    public enum ZombieCrushSagaPropertyType
    {
        /* SETUP FILE NAMES */
        UI_PROPERTIES_FILE_NAME,
        PROPERTIES_SCHEMA_FILE_NAME,
        GAME_FLAVOR_FILE_NAME,
        RECORD_FILE_NAME,

        /* DIRECTORIES FOR FILE LOADING */
        AUDIO_PATH,
        DATA_PATH,
        IMG_PATH,
        
        /* WINDOW DIMENSIONS & FRAME RATE */
        WINDOW_WIDTH,
        WINDOW_HEIGHT,
        FPS,
        GAME_WIDTH,
        GAME_HEIGHT,
        GAME_LEFT_OFFSET,
        GAME_TOP_OFFSET,
        
        /* GAME TEXT */
        GAME_TITLE_TEXT,
        EXIT_REQUEST_TEXT,
        INVALID_XML_FILE_ERROR_TEXT,
        ERROR_DIALOG_TITLE_TEXT,
        
        /* ERROR TYPES */
        AUDIO_FILE_ERROR,
        LOAD_LEVEL_ERROR,
        RECORD_SAVE_ERROR,
        LOAD_LEVEL_REQS_ERROR,

        /* IMAGE FILE NAMES */
        WINDOW_ICON,
        SPLASH_SCREEN_IMAGE_NAME,
        GAME_BACKGROUND_IMAGE_NAME,
        SAGA_SCREEN_1_IMAGE_NAME,
        SAGA_SCREEN_2_IMAGE_NAME,
        SAGA_SCREEN_3_IMAGE_NAME,
        SAGA_SCREEN_4_IMAGE_NAME,
        SAGA_SCREEN_5_IMAGE_NAME,
        SAGA_SCREEN_6_IMAGE_NAME,
        SAGA_SCREEN_7_IMAGE_NAME,
        SAGA_SCREEN_8_IMAGE_NAME,
        SAGA_SCREEN_9_IMAGE_NAME,
        
        ABOUT_SCREEN_IMAGE_NAME,
        LEVEL_SCREEN_IMAGE_NAME,
        
        ABOUT_DIALOG_IMAGE_NAME,
        LEVEL_DIALOG_IMAGE_NAME,
        
        BLANK_TILE_IMAGE_NAME,
        BLANK_TILE_SELECTED_IMAGE_NAME,
        
        BACK_BUTTON_IMAGE_NAME,
        BACK_BUTTON_MOUSE_OVER_IMAGE_NAME,
        TIME_IMAGE_NAME,
        LIVES_IMAGE_NAME,
        MOVES_IMAGE_NAME,
        NEXT_STAR_IMAGE_NAME,
        SCORE_IMAGE_NAME,
        STAR_IMAGE_NAME,
        STAR_RED_IMAGE_NAME,
        STAR_BLUE_IMAGE_NAME,
        STAR_PURPLE_IMAGE_NAME,
        
        PLAY_BUTTON_IMAGE_NAME,
        RESET_BUTTON_IMAGE_NAME,
        QUIT_BUTTON_SPLASH_IMAGE_NAME,
        PLAY_BUTTON_MOUSE_OVER_IMAGE_NAME,
        RESET_BUTTON_MOUSE_OVER_IMAGE_NAME,
        QUIT_BUTTON_SPLASH_MOUSE_OVER_IMAGE_NAME,
        
        QUIT_BUTTON_SAGA_IMAGE_NAME,
        UP_BUTTON_IMAGE_NAME,
        DOWN_BUTTON_IMAGE_NAME,
        ABOUT_BUTTON_IMAGE_NAME,
        QUIT_BUTTON_SAGA_MOUSE_OVER_IMAGE_NAME,
        UP_BUTTON_MOUSE_OVER_IMAGE_NAME,
        DOWN_BUTTON_MOUSE_OVER_IMAGE_NAME,
        ABOUT_BUTTON_MOUSE_OVER_IMAGE_NAME,
        
        RETURN_FROM_ABOUT_BUTTON_IMAGE_NAME,
        RETURN_FROM_ABOUT_BUTTON_MOUSE_OVER_IMAGE_NAME,
        
        PLAY_LEVEL_BUTTON_IMAGE_NAME,
        PLAY_LEVEL_BUTTON_MOUSE_OVER_IMAGE_NAME,
        RETURN_FROM_LEVEL_BUTTON_IMAGE_NAME,
        RETURN_FROM_LEVEL_BUTTON_MOUSE_OVER_IMAGE_NAME,
        
        // AND THE end game
        WIN_IMAGE_NAME,
        LOSS_IMAGE_NAME,
        
        /* TILE LOADING STUFF */
        LEVEL_OPTIONS,
        LEVEL_IMAGE_OPTIONS,
        LEVEL_MOUSE_OVER_IMAGE_OPTIONS,
        TYPE_A_TILES,
        TYPE_B_TILES,
        TYPE_C_TILES,
        TYPE_D_TILES,
        TYPE_E_TILES,
        TYPE_F_TILES,
        TYPE_STRIPED_TILES,
        TYPE_BOMB_TILES,
        TYPE_WRAPPER_TILES,
        
        LEVEL_REQUIREMENTS,
        
        /* AUDIO CUES */
        SELECT_AUDIO_CUE,
        MATCH_AUDIO_CUE,
        NO_MATCH_AUDIO_CUE,
        BLOCKED_TILE_AUDIO_CUE,
        WIN_AUDIO_CUE,
        LOSS_AUDIO_CUE,
        SPLASH_SCREEN_SONG_CUE,
        SAGA_SCREEN_SONG_CUE,
        ABOUT_SCREEN_SONG_CUE,
        LEVEL_SCREEN_SONG_CUE,
        GAMEPLAY_SONG_CUE
    }
  
  
}
