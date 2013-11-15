package zombiecrushsaga.ui;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import zombiecrushsaga.data.ZombieCrushSagaDataModel;
import mini_game.MiniGame;
import static zombiecrushsaga.ZombieCrushSagaConstants.*;
import mini_game.Sprite;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import zombiecrushsaga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import zombiecrushsaga.file.ZombieCrushSagaFileManager;
import zombiecrushsaga.data.ZombieCrushSagaRecord;
import zombiecrushsaga.events.AboutHandler;
import zombiecrushsaga.events.QuitLevelHandler;
import zombiecrushsaga.events.QuitGameHandler;
import zombiecrushsaga.events.ResetGameHandler;
import zombiecrushsaga.events.SelectLevelHandler;
import zombiecrushsaga.events.LevelScoreHandler;
import zombiecrushsaga.events.PlayGameHandler;
import zombiecrushsaga.events.PlayLevelHandler;
import zombiecrushsaga.events.ResetAllHandler;
import zombiecrushsaga.events.ReturnToSagaHandler;
import zombiecrushsaga.events.ScrollDownHandler;
import zombiecrushsaga.events.ScrollUpHandler;

/**
 * This is the actual mini game, as extended from the mini game framework. It
 * manages all the UI elements.
 *
 * @author Yukti Abrol
 */
public class ZombieCrushSagaMiniGame extends MiniGame {
  // THE PLAYER RECORD FOR EACH LEVEL, WHICH LIVES BEYOND ONE SESSION

  private ZombieCrushSagaRecord record;
  // HANDLES ERROR CONDITIONS
  private ZombieCrushSagaErrorHandler errorHandler;
  // MANAGES LOADING OF LEVELS AND THE PLAYER RECORDS FILES
  private ZombieCrushSagaFileManager fileManager;
  // THE SCREEN CURRENTLY BEING PLAYED
  private String currentScreenState;

  // ACCESSOR METHODS
  // - getPlayerRecord
  // - getErrorHandler
  // - getFileManager
  // - isCurrentScreenState
  /**
   * Accessor method for getting the player record object, which summarizes the
   * player's record on all levels.
   *
   * @return The player's complete record.
   */
  public ZombieCrushSagaRecord getPlayerRecord() {
    return record;
  }

  /**
   * Accessor method for getting the application's error handler.
   *
   * @return The error handler.
   */
  public ZombieCrushSagaErrorHandler getErrorHandler() {
    return errorHandler;
  }

  /**
   * Accessor method for getting the app's file manager.
   *
   * @return The file manager.
   */
  public ZombieCrushSagaFileManager getFileManager() {
    return fileManager;
  }

  /**
   * Used for testing to see if the current screen state matches the
   * testScreenState argument. If it mates, true is returned, else false.
   *
   * @param testScreenState Screen state to test against the current state.
   *
   * @return true if the current state is testScreenState, false otherwise.
   */
  public boolean isCurrentScreenState(String testScreenState) {
    return testScreenState.equals(currentScreenState);
  }

  // SERVICE METHODS
  // - displayStats
  // - savePlayerRecord
  // - switchToGameScreen
  // - switchToSplashScreen
  // - updateBoundaries
  /**
   * This method displays makes the stats dialog display visible, which includes
   * the text inside.
   */
  public void displayStats() {
    // MAKE SURE ONLY THE PROPER DIALOG IS VISIBLE
    guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(STATS_DIALOG_TYPE).setState(VISIBLE_STATE);
  }

  /**
   * This method forces the file manager to save the current player record.
   */
  public void savePlayerRecord() {
    // THIS CURRENTLY DOES NOTHING, INSTEAD, IT MUST SAVE ALL THE
    // PLAYER RECORDS IN THE SAME FORMAT IT IS BEING LOADED
    try{
      PropertiesManager props = PropertiesManager.getPropertiesManager();
      String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);
      String recordPath = dataPath + props.getProperty(ZombieCrushSagaPropertyType.RECORD_FILE_NAME);
      File fileToOpen = new File(recordPath);
      
      byte[] bytes = record.toByteArray();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      FileOutputStream fos = new FileOutputStream(fileToOpen);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      bos.write(bytes);
      bos.close();
      
      System.out.println("save");
    }
    catch(IOException e)
    {
      //didn't save properly
    }
  }

  /**
   * This method switches the application to the game screen, making all the
   * appropriate UI controls visible & invisible.
   */
  public void switchToGameScreen() {
    PropertiesManager props = PropertiesManager.getPropertiesManager();

    // CHANGE THE BACKGROUND
    guiDecor.get(BACKGROUND_TYPE).setState(GAME_SCREEN_STATE);

    // ACTIVATE THE TOOLBAR AND ITS CONTROLS
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setState(VISIBLE_STATE);
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setEnabled(true);
    guiButtons.get(BACK_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(BACK_BUTTON_TYPE).setEnabled(true);
//    guiDecor.get(TIME_TYPE).setState(VISIBLE_STATE);
//    guiDecor.get(TILES_COUNT_TYPE).setState(VISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setState(VISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setEnabled(true);
//    guiDecor.get(TILE_STACK_TYPE).setState(VISIBLE_STATE);
    guiDecor.get(LIVES_TYPE).setState(VISIBLE_STATE);
    guiDecor.get(MOVES_TYPE).setState(VISIBLE_STATE);
    guiDecor.get(SCORE_TYPE).setState(VISIBLE_STATE);
    guiDecor.get(STAR_TYPE).setState(VISIBLE_STATE);
    guiDecor.get(NEXT_STAR_TYPE).setState(VISIBLE_STATE);

    // DEACTIVATE THE LEVEL SELECT BUTTONS
    ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
    for (String level : levels) {
      guiButtons.get(level).setState(INVISIBLE_STATE);
      guiButtons.get(level).setEnabled(false);
    }
    
    // deactivate saga buttons
    guiButtons.get(UP_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(UP_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(DOWN_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(DOWN_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(ABOUT_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(ABOUT_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setEnabled(false);
    
    //deactivate ABOUT SCREEN buttons
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setEnabled(false);
    
    //deactivate LEVEL SCORE buttons
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setEnabled(false);
    
    // deactivate splash buttons
    guiButtons.get(PLAY_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(PLAY_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(RESET_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RESET_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setEnabled(false);

    // MOVE THE TILES TO THE STACK AND MAKE THEM VISIBLE
    //((ZombieCrushSagaDataModel) data).enableTiles(true);
    //data.reset(this);

    // AND CHANGE THE SCREEN STATE
    currentScreenState = GAME_SCREEN_STATE;

    // PLAY THE GAMEPLAY SCREEN SONG
    audio.stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.SAGA_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.ABOUT_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.LEVEL_SCREEN_SONG_CUE.toString());
    audio.play(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString(), true);
  }
  
   /**
   * This method switches the application to the saga screen, making all the
   * appropriate UI controls visible & invisible.
   */
  public void switchToSagaScreen() {
    // CHANGE THE BACKGROUND
    guiDecor.get(BACKGROUND_TYPE).setState(SAGA_SCREEN_STATE);

    // DEACTIVATE THE TOOLBAR CONTROLS
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(BACK_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(BACK_BUTTON_TYPE).setEnabled(false);
//    guiDecor.get(TIME_TYPE).setState(INVISIBLE_STATE);
//    guiDecor.get(TILES_COUNT_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setEnabled(false);
//    guiDecor.get(TILE_STACK_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(LIVES_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(MOVES_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(SCORE_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(STAR_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(NEXT_STAR_TYPE).setState(INVISIBLE_STATE);

    // ACTIVATE THE LEVEL SELECT BUTTONS
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
    for (String level : levels) {
      guiButtons.get(level).setState(VISIBLE_STATE);
      guiButtons.get(level).setEnabled(true);
    }
    
    // activate saga buttons
    guiButtons.get(UP_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(UP_BUTTON_TYPE).setEnabled(true);
    guiButtons.get(DOWN_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(DOWN_BUTTON_TYPE).setEnabled(true);
    guiButtons.get(ABOUT_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(ABOUT_BUTTON_TYPE).setEnabled(true);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setEnabled(true);
    
    //deactivate ABOUT SCREEN buttons
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setEnabled(false);
    
    //deactivate LEVEL SCORE buttons
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setEnabled(false);
    
    // deactivate splash buttons
    guiButtons.get(PLAY_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(PLAY_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(RESET_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RESET_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setEnabled(false);

    // DEACTIVATE ALL DIALOGS
    guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);

    // HIDE THE TILES
    ((ZombieCrushSagaDataModel) data).enableTiles(false);

    // MAKE THE CURRENT SCREEN THE SPLASH SCREEN
    currentScreenState = SAGA_SCREEN_STATE;

    // PLAY THE saga SCREEN SONG
    audio.play(ZombieCrushSagaPropertyType.SAGA_SCREEN_SONG_CUE.toString(), true);
    audio.stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.ABOUT_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.LEVEL_SCREEN_SONG_CUE.toString());
  }
  
  /**
   * This method switches the application to the about screen, making all the
   * appropriate UI controls visible & invisible.
   */
  public void switchToAboutScreen() {
    // CHANGE THE BACKGROUND
    guiDecor.get(BACKGROUND_TYPE).setState(ABOUT_SCREEN_STATE);

    // DEACTIVATE THE TOOLBAR CONTROLS
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(BACK_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(BACK_BUTTON_TYPE).setEnabled(false);
//    guiDecor.get(TIME_TYPE).setState(INVISIBLE_STATE);
//    guiDecor.get(TILES_COUNT_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setEnabled(false);
//    guiDecor.get(TILE_STACK_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(LIVES_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(MOVES_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(SCORE_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(STAR_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(NEXT_STAR_TYPE).setState(INVISIBLE_STATE);

    // DEACTIVATE THE LEVEL SELECT BUTTONS
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
    for (String level : levels) {
      guiButtons.get(level).setState(INVISIBLE_STATE);
      guiButtons.get(level).setEnabled(false);
    }
    
    // DEactivate saga buttons
    guiButtons.get(UP_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(UP_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(DOWN_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(DOWN_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(ABOUT_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(ABOUT_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setEnabled(false);
    
    //activate ABOUT SCREEN buttons
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setEnabled(true);
    
    //deactivate LEVEL SCORE buttons
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setEnabled(false);
    
    // deactivate splash buttons
    guiButtons.get(PLAY_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(PLAY_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(RESET_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RESET_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setEnabled(false);

    // DEACTIVATE ALL DIALOGS
    guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);

    // HIDE THE TILES
    ((ZombieCrushSagaDataModel) data).enableTiles(false);

    // MAKE THE CURRENT SCREEN THE SPLASH SCREEN
    currentScreenState = ABOUT_SCREEN_STATE;

    // PLAY THE saga SCREEN SONG
    audio.play(ZombieCrushSagaPropertyType.ABOUT_SCREEN_SONG_CUE.toString(), true);
    audio.stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.SAGA_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.LEVEL_SCREEN_SONG_CUE.toString());
  }
  
  /**
   * This method switches the application to the level screen, making all the
   * appropriate UI controls visible & invisible.
   */
  public void switchToLevelScreen() {
    // CHANGE THE BACKGROUND
    guiDecor.get(BACKGROUND_TYPE).setState(LEVEL_SCREEN_STATE);

    // DEACTIVATE THE TOOLBAR CONTROLS
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(BACK_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(BACK_BUTTON_TYPE).setEnabled(false);
//    guiDecor.get(TIME_TYPE).setState(INVISIBLE_STATE);
//    guiDecor.get(TILES_COUNT_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setEnabled(false);
//    guiDecor.get(TILE_STACK_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(LIVES_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(MOVES_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(SCORE_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(STAR_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(NEXT_STAR_TYPE).setState(INVISIBLE_STATE);

    // DEACTIVATE THE LEVEL SELECT BUTTONS
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
    for (String level : levels) {
      guiButtons.get(level).setState(INVISIBLE_STATE);
      guiButtons.get(level).setEnabled(false);
    }
    
    // DEactivate saga buttons
    guiButtons.get(UP_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(UP_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(DOWN_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(DOWN_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(ABOUT_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(ABOUT_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setEnabled(false);
    
    //DEactivate ABOUT SCREEN buttons
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setEnabled(false);
    
    //activate LEVEL SCORE buttons
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setEnabled(true);
    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setEnabled(true);
    
    // deactivate splash buttons
    guiButtons.get(PLAY_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(PLAY_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(RESET_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(RESET_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setEnabled(false);

    // DEACTIVATE ALL DIALOGS
    guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);

    // HIDE THE TILES
    ((ZombieCrushSagaDataModel) data).enableTiles(false);

    // MAKE THE CURRENT SCREEN THE SPLASH SCREEN
    currentScreenState = LEVEL_SCREEN_STATE;

    // PLAY THE saga SCREEN SONG
    audio.play(ZombieCrushSagaPropertyType.LEVEL_SCREEN_SONG_CUE.toString(), true);
    audio.stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.SAGA_SCREEN_SONG_CUE.toString());
    audio.stop(ZombieCrushSagaPropertyType.ABOUT_SCREEN_SONG_CUE.toString());
  }

  /**
   * This method updates the game grid boundaries, which will depend on the
   * level loaded.
   */
  public void updateBoundaries() {
    // NOTE THAT THE ONLY ONES WE CARE ABOUT ARE THE LEFT & TOP BOUNDARIES
    float totalWidth = ((ZombieCrushSagaDataModel) data).getGridColumns() * TILE_IMAGE_WIDTH;
    float halfTotalWidth = totalWidth / 2.0f;
    float halfViewportWidth = data.getGameWidth() / 2.0f;
    boundaryLeft = halfViewportWidth - halfTotalWidth;

    // THE LEFT & TOP BOUNDARIES ARE WHERE WE START RENDERING TILES IN THE GRID
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    float topOffset = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_TOP_OFFSET.toString()));
    float totalHeight = ((ZombieCrushSagaDataModel) data).getGridRows() * TILE_IMAGE_HEIGHT;
    float halfTotalHeight = totalHeight / 2.0f;
    float halfViewportHeight = (data.getGameHeight() - topOffset) / 2.0f;
    boundaryTop = topOffset + halfViewportHeight - halfTotalHeight;
  }

  // METHODS OVERRIDDEN FROM MiniGame
  // - initAudioContent
  // - initData
  // - initGUIControls
  // - initGUIHandlers
  // - reset
  // - updateGUI
  @Override
  /**
   * Initializes the sound and music to be used by the application.
   */
  public void initAudioContent() {
    try {
      PropertiesManager props = PropertiesManager.getPropertiesManager();
      String audioPath = props.getProperty(ZombieCrushSagaPropertyType.AUDIO_PATH);

      // LOAD ALL THE AUDIO
      loadAudioCue(ZombieCrushSagaPropertyType.SELECT_AUDIO_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.MATCH_AUDIO_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.NO_MATCH_AUDIO_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.BLOCKED_TILE_AUDIO_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.WIN_AUDIO_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.LOSS_AUDIO_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.SAGA_SCREEN_SONG_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.ABOUT_SCREEN_SONG_CUE);
      loadAudioCue(ZombieCrushSagaPropertyType.LEVEL_SCREEN_SONG_CUE);

      // PLAY THE WELCOME SCREEN SONG
      audio.play(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString(), true);
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InvalidMidiDataException | MidiUnavailableException e) {
      errorHandler.processError(ZombieCrushSagaPropertyType.AUDIO_FILE_ERROR);
    }
  }

  /**
   * This helper method loads the audio file associated with audioCueType, which
   * should have been specified via an XML properties file.
   */
  private void loadAudioCue(ZombieCrushSagaPropertyType audioCueType)
          throws UnsupportedAudioFileException, IOException, LineUnavailableException,
          InvalidMidiDataException, MidiUnavailableException {
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    String audioPath = props.getProperty(ZombieCrushSagaPropertyType.AUDIO_PATH);
    String cue = props.getProperty(audioCueType.toString());
    audio.loadAudio(audioCueType.toString(), audioPath + cue);
  }

  /**
   * Initializes the game data used by the application. Note that it is this
   * method's obligation to construct and set this Game's custom GameDataModel
   * object as well as any other needed game objects.
   */
  @Override
  public void initData() {
    // INIT OUR ERROR HANDLER
    errorHandler = new ZombieCrushSagaErrorHandler(window);

    // INIT OUR FILE MANAGER
    fileManager = new ZombieCrushSagaFileManager(this);

    // LOAD THE PLAYER'S RECORD FROM A FILE
    record = fileManager.loadRecord();

    // INIT OUR DATA MANAGER
    data = new ZombieCrushSagaDataModel(this);

    // LOAD THE GAME DIMENSIONS
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    int gameWidth = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_WIDTH.toString()));
    int gameHeight = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_HEIGHT.toString()));
    data.setGameDimensions(gameWidth, gameHeight);

    // THIS WILL CHANGE WHEN WE LOAD A LEVEL
    boundaryLeft = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_LEFT_OFFSET.toString()));
    boundaryTop = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_TOP_OFFSET.toString()));
    boundaryRight = gameWidth - boundaryLeft;
    boundaryBottom = gameHeight;
  }

  /**
   * Initializes the game controls, like buttons, used by the game application.
   * Note that this includes the tiles, which serve as buttons of sorts.
   */
  @Override
  public void initGUIControls() {
    // WE'LL USE AND REUSE THESE FOR LOADING STUFF
    BufferedImage img;
    float x, y;
    SpriteType sT;
    Sprite s;

    // FIRST PUT THE ICON IN THE WINDOW
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    String imgPath = props.getProperty(ZombieCrushSagaPropertyType.IMG_PATH);
    String windowIconFile = props.getProperty(ZombieCrushSagaPropertyType.WINDOW_ICON);
    img = loadImage(imgPath + windowIconFile);
    window.setIconImage(img);

    // CONSTRUCT THE PANEL WHERE WE'LL DRAW EVERYTHING
    canvas = new ZombieCrushSagaPanel(this, (ZombieCrushSagaDataModel) data);

    // LOAD THE BACKGROUNDS, WHICH ARE GUI DECOR
    currentScreenState = SPLASH_SCREEN_STATE;
    img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.SPLASH_SCREEN_IMAGE_NAME));
    sT = new SpriteType(BACKGROUND_TYPE);
    sT.addState(SPLASH_SCREEN_STATE, img);
    img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.GAME_BACKGROUND_IMAGE_NAME));
    sT.addState(GAME_SCREEN_STATE, img);
    img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.SAGA_SCREEN_IMAGE_NAME));
    sT.addState(SAGA_SCREEN_STATE, img);
    img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.ABOUT_SCREEN_IMAGE_NAME));
    sT.addState(ABOUT_SCREEN_STATE, img);
    img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.LEVEL_SCREEN_IMAGE_NAME));
    sT.addState(LEVEL_SCREEN_STATE, img);
    
    s = new Sprite(sT, 0, 0, 0, 0, SPLASH_SCREEN_STATE);
    guiDecor.put(BACKGROUND_TYPE, s);

    // ADD A BUTTON FOR EACH LEVEL AVAILABLE
    ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
    ArrayList<String> levelImageNames = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_IMAGE_OPTIONS);
    ArrayList<String> levelMouseOverImageNames = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_MOUSE_OVER_IMAGE_OPTIONS);
//    float totalWidth = levels.size() * (LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN) - LEVEL_BUTTON_MARGIN;
//    float gameWidth = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_WIDTH));
    x = 2*LEVEL_BUTTON_WIDTH;//(gameWidth - totalWidth)/ 2.0f;
    y = MAX_SCREEN_HEIGHT - LEVEL_BUTTON_Y; 
    for (int i = 0; i < levels.size(); i++) {
      sT = new SpriteType(LEVEL_SELECT_BUTTON_TYPE);
      img = loadImageWithColorKey(imgPath + levelImageNames.get(i), COLOR_KEY);
      sT.addState(VISIBLE_STATE, img);
      img = loadImageWithColorKey(imgPath + levelMouseOverImageNames.get(i), COLOR_KEY);
      sT.addState(MOUSE_OVER_STATE, img);
      s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
      guiButtons.put(levels.get(i), s);
      if(i==1)
          guiButtons.get(levels.get(i)).setEnabled(true);
      else
          guiButtons.get(levels.get(i)).setEnabled(false);
      if(i == 5)
      {
          y = y - LEVEL_BUTTON_Y;
          x -= LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN;//2*LEVEL_BUTTON_WIDTH;
      }
      else if(i == 4)
      {
          x +=  (int)(LEVEL_BUTTON_MARGIN*.75);
      }
      else if(i >= 5 && i < 10)
      {
          x -= LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN;
      }
      else if (i>10)
      {
          y = y - LEVEL_BUTTON_Y;
          x = LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN;
      }
      else
      {
          x += LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN;
      }
    }

    // ADD THE CONTROLS ALONG THE NORTH OF THE GAME SCREEN

    // THEN THE NEW BUTTON
//    String newButton = props.getProperty(ZombieCrushSagaPropertyType.NEW_BUTTON_IMAGE_NAME);
//    sT = new SpriteType(NEW_GAME_BUTTON_TYPE);
//    img = loadImage(imgPath + newButton);
//    sT.addState(VISIBLE_STATE, img);
//    String newMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.NEW_BUTTON_MOUSE_OVER_IMAGE_NAME);
//    img = loadImage(imgPath + newMouseOverButton);
//    sT.addState(MOUSE_OVER_STATE, img);
//    s = new Sprite(sT, NEW_BUTTON_X, NEW_BUTTON_Y, 0, 0, INVISIBLE_STATE);
//    guiButtons.put(NEW_GAME_BUTTON_TYPE, s);
    
    //Then the back button
    String backButton = props.getProperty(ZombieCrushSagaPropertyType.BACK_BUTTON_IMAGE_NAME);
    sT = new SpriteType(BACK_BUTTON_TYPE);
    img = loadImage(imgPath + backButton);
    sT.addState(VISIBLE_STATE, img);
    String backMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.BACK_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + backMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, BACK_BUTTON_X, BACK_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(BACK_BUTTON_TYPE, s);
    
    //and the tiles count display
//    String tilesCountContainer = props.getProperty(ZombieCrushSagaPropertyType.TILES_COUNT_IMAGE_NAME);
//    sT = new SpriteType(TILES_COUNT_TYPE);
//    img = loadImage(imgPath + tilesCountContainer);
//    sT.addState(VISIBLE_STATE, img);
//    s = new Sprite(sT, TILES_COUNT_X, TILES_COUNT_Y, 0, 0, INVISIBLE_STATE);
//    guiDecor.put(TILES_COUNT_TYPE, s);

    // AND THE TIME DISPLAY
    String timeContainer = props.getProperty(ZombieCrushSagaPropertyType.TIME_IMAGE_NAME);
    sT = new SpriteType(TIME_TYPE);
    img = loadImage(imgPath + timeContainer);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, TIME_X, TIME_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(TIME_TYPE, s);
    
    // AND THE lives DISPLAY
    String livesContainer = props.getProperty(ZombieCrushSagaPropertyType.LIVES_IMAGE_NAME);
    sT = new SpriteType(LIVES_TYPE);
    img = loadImage(imgPath + livesContainer);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, LIVES_X, LIVES_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(LIVES_TYPE, s);
    
    // AND THE MOVES DISPLAY
    String movesContainer = props.getProperty(ZombieCrushSagaPropertyType.MOVES_IMAGE_NAME);
    sT = new SpriteType(MOVES_TYPE);
    img = loadImage(imgPath + movesContainer);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, MOVES_X,MOVES_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(MOVES_TYPE, s);
    
    // AND THE SCORE DISPLAY
    String scoreContainer = props.getProperty(ZombieCrushSagaPropertyType.SCORE_IMAGE_NAME);
    sT = new SpriteType(SCORE_TYPE);
    img = loadImage(imgPath + scoreContainer);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, SCORE_X, SCORE_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(SCORE_TYPE, s);
    
    // AND THE nextStar DISPLAY
    String nextStarContainer = props.getProperty(ZombieCrushSagaPropertyType.NEXT_STAR_IMAGE_NAME);
    sT = new SpriteType(NEXT_STAR_TYPE);
    img = loadImage(imgPath + nextStarContainer);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, NEXT_STAR_X, NEXT_STAR_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(NEXT_STAR_TYPE, s);
    
    // AND THE star DISPLAY
    String starContainer = props.getProperty(ZombieCrushSagaPropertyType.STAR_IMAGE_NAME);
    sT = new SpriteType(STAR_TYPE);
    img = loadImage(imgPath + starContainer);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, STAR_X, STAR_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(STAR_TYPE, s);

    // AND THE STATS BUTTON
//    String statsButton = props.getProperty(ZombieCrushSagaPropertyType.STATS_BUTTON_IMAGE_NAME);
//    sT = new SpriteType(STATS_BUTTON_TYPE);
//    img = loadImage(imgPath + statsButton);
//    sT.addState(VISIBLE_STATE, img);
//    String statsMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.STATS_BUTTON_MOUSE_OVER_IMAGE_NAME);
//    img = loadImage(imgPath + statsMouseOverButton);
//    sT.addState(MOUSE_OVER_STATE, img);
//    s = new Sprite(sT, STATS_X, STATS_Y, 0, 0, INVISIBLE_STATE);
//    guiButtons.put(STATS_BUTTON_TYPE, s);
    
    //Then the up button
    String upButton = props.getProperty(ZombieCrushSagaPropertyType.UP_BUTTON_IMAGE_NAME);
    sT = new SpriteType(UP_BUTTON_TYPE);
    img = loadImage(imgPath + upButton);
    sT.addState(VISIBLE_STATE, img);
    String upMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.UP_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + upMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, UP_BUTTON_X, UP_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(UP_BUTTON_TYPE, s);
    
    //Then the down button
    String downButton = props.getProperty(ZombieCrushSagaPropertyType.DOWN_BUTTON_IMAGE_NAME);
    sT = new SpriteType(DOWN_BUTTON_TYPE);
    img = loadImage(imgPath + downButton);
    sT.addState(VISIBLE_STATE, img);
    String downMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.DOWN_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + downMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, DOWN_BUTTON_X, DOWN_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(DOWN_BUTTON_TYPE, s);
    
    //Then the about button
    String aboutButton = props.getProperty(ZombieCrushSagaPropertyType.ABOUT_BUTTON_IMAGE_NAME);
    sT = new SpriteType(ABOUT_BUTTON_TYPE);
    img = loadImage(imgPath + aboutButton);
    sT.addState(VISIBLE_STATE, img);
    String aboutMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.ABOUT_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + aboutMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, ABOUT_BUTTON_X, ABOUT_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(ABOUT_BUTTON_TYPE, s);
    
    //Then the return to saga button ON ABOUT SCREEN
    String returnToSagaButton = props.getProperty(ZombieCrushSagaPropertyType.RETURN_FROM_ABOUT_BUTTON_IMAGE_NAME);
    sT = new SpriteType(RETURN_FROM_ABOUT_BUTTON_TYPE);
    img = loadImage(imgPath + returnToSagaButton);
    sT.addState(VISIBLE_STATE, img);
    String returnToSagaMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.RETURN_FROM_ABOUT_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + returnToSagaMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, RETURN_FROM_ABOUT_BUTTON_X, RETURN_FROM_ABOUT_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(RETURN_FROM_ABOUT_BUTTON_TYPE, s);
    
    //Then the return to saga button FROM LEVEL SCREEN
    String returnToSagaButton2 = props.getProperty(ZombieCrushSagaPropertyType.RETURN_FROM_LEVEL_BUTTON_IMAGE_NAME);
    sT = new SpriteType(RETURN_FROM_LEVEL_BUTTON_TYPE);
    img = loadImage(imgPath + returnToSagaButton2);
    sT.addState(VISIBLE_STATE, img);
    String returnToSagaMouseOverButton2 = props.getProperty(ZombieCrushSagaPropertyType.RETURN_FROM_LEVEL_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + returnToSagaMouseOverButton2);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, RETURN_FROM_LEVEL_BUTTON_X, RETURN_FROM_LEVEL_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(RETURN_FROM_LEVEL_BUTTON_TYPE, s);
    
    //Then the PLAY LEVEL button
    String playLevelButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_LEVEL_BUTTON_IMAGE_NAME);
    sT = new SpriteType(PLAY_LEVEL_BUTTON_TYPE);
    img = loadImage(imgPath + playLevelButton);
    sT.addState(VISIBLE_STATE, img);
    String playLevelMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_LEVEL_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + playLevelMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, PLAY_LEVEL_BUTTON_X, PLAY_LEVEL_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(PLAY_LEVEL_BUTTON_TYPE, s);

    //Then the play button
    String playButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_BUTTON_IMAGE_NAME);
    sT = new SpriteType(PLAY_BUTTON_TYPE);
    img = loadImage(imgPath + playButton);
    sT.addState(VISIBLE_STATE, img);
    String playMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + playMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, PLAY_BUTTON_X, PLAY_BUTTON_Y, 0, 0, VISIBLE_STATE);
    guiButtons.put(PLAY_BUTTON_TYPE, s);
    
    //Then the reset button
    String resetButton = props.getProperty(ZombieCrushSagaPropertyType.RESET_BUTTON_IMAGE_NAME);
    sT = new SpriteType(RESET_BUTTON_TYPE);
    img = loadImage(imgPath + resetButton);
    sT.addState(VISIBLE_STATE, img);
    String resetMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.RESET_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + resetMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, RESET_BUTTON_X, RESET_BUTTON_Y, 0, 0, VISIBLE_STATE);
    guiButtons.put(RESET_BUTTON_TYPE, s);
    if(data.inProgress())
        guiButtons.get(RESET_BUTTON_TYPE).setEnabled(true);
    else
        guiButtons.get(RESET_BUTTON_TYPE).setEnabled(false);
    
    //Then the quit buttons
    String quitButton = props.getProperty(ZombieCrushSagaPropertyType.QUIT_BUTTON_SPLASH_IMAGE_NAME);
    sT = new SpriteType(QUIT_SPLASH_BUTTON_TYPE);
    img = loadImage(imgPath + quitButton);
    sT.addState(VISIBLE_STATE, img);
    String quitMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.QUIT_BUTTON_SPLASH_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + quitMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, QUIT_SPLASH_BUTTON_X, QUIT_SPLASH_BUTTON_Y, 0, 0, VISIBLE_STATE);
    guiButtons.put(QUIT_SPLASH_BUTTON_TYPE, s);
    
    String quitButton2 = props.getProperty(ZombieCrushSagaPropertyType.QUIT_BUTTON_SAGA_IMAGE_NAME);
    sT = new SpriteType(QUIT_SAGA_BUTTON_TYPE);
    img = loadImage(imgPath + quitButton2);
    sT.addState(VISIBLE_STATE, img);
    String quitMouseOverButton2 = props.getProperty(ZombieCrushSagaPropertyType.QUIT_BUTTON_SAGA_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + quitMouseOverButton2);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, QUIT_SAGA_BUTTON_X, QUIT_SAGA_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(QUIT_SAGA_BUTTON_TYPE, s);

//    // AND THE TILE STACK
//    String tileStack = props.getProperty(ZombieCrushSagaPropertyType.TILE_STACK_IMAGE_NAME);
//    sT = new SpriteType(TILE_STACK_TYPE);
//    img = loadImageWithColorKey(imgPath + tileStack, COLOR_KEY);
//    sT.addState(VISIBLE_STATE, img);
//    s = new Sprite(sT, TILE_STACK_X, TILE_STACK_Y, 0, 0, INVISIBLE_STATE);
//    guiDecor.put(TILE_STACK_TYPE, s);

    // NOW ADD THE DIALOGS

    // AND THE STATS DISPLAY
    String statsDialog = props.getProperty(ZombieCrushSagaPropertyType.STATS_DIALOG_IMAGE_NAME);
    sT = new SpriteType(STATS_DIALOG_TYPE);
    img = loadImageWithColorKey(imgPath + statsDialog, COLOR_KEY);
    sT.addState(VISIBLE_STATE, img);
    x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
    y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
    s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
    guiDialogs.put(STATS_DIALOG_TYPE, s);

    // AND THE WIN CONDITION DISPLAY
    String winDisplay = props.getProperty(ZombieCrushSagaPropertyType.WIN_DIALOG_IMAGE_NAME);
    sT = new SpriteType(WIN_DIALOG_TYPE);
    img = loadImageWithColorKey(imgPath + winDisplay, COLOR_KEY);
    sT.addState(VISIBLE_STATE, img);
    x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
    y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
    s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
    guiDialogs.put(WIN_DIALOG_TYPE, s);
    
    // and the loss condition display
    String lossDisplay = props.getProperty(ZombieCrushSagaPropertyType.LOSS_DIALOG_IMAGE_NAME);
    sT = new SpriteType(LOSS_DIALOG_TYPE);
    img = loadImageWithColorKey(imgPath + lossDisplay, COLOR_KEY);
    sT.addState(VISIBLE_STATE, img);
    x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
    y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
    s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
    guiDialogs.put(LOSS_DIALOG_TYPE, s);

    // THEN THE TILES STACKED TO THE TOP LEFT
    ((ZombieCrushSagaDataModel) data).initTiles();
  }

  /**
   * Initializes the game event handlers for things like game gui buttons.
   */
  @Override
  public void initGUIHandlers() {
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);

    // WE'LL HAVE A CUSTOM RESPONSE FOR WHEN THE USER CLOSES THE WINDOW
    window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    QuitGameHandler eh = new QuitGameHandler(this);
    window.addWindowListener(eh);

    // LEVEL BUTTON EVENT HANDLERS
    ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
    for (String levelFile : levels) {
      SelectLevelHandler slh = new SelectLevelHandler(this, dataPath + levelFile);
      guiButtons.get(levelFile).setActionListener(slh);
    }

    // NEW GAME EVENT HANDLER
//    ResetGameHandler ngh = new ResetGameHandler(this);
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setActionListener(ngh);
    
    //back button event handler
    QuitLevelHandler bh = new QuitLevelHandler(this);
    guiButtons.get(BACK_BUTTON_TYPE).setActionListener(bh);

    // STATS BUTTON EVENT HANDLER
//    LevelScoreHandler sh = new LevelScoreHandler(this);
//    guiButtons.get(STATS_BUTTON_TYPE).setActionListener(sh);
    
    //play button event handler
    PlayGameHandler ph = new PlayGameHandler(this);
    guiButtons.get(PLAY_BUTTON_TYPE).setActionListener(ph);
    
    //reset button event handler
    ResetAllHandler rh = new ResetAllHandler(this);
    guiButtons.get(RESET_BUTTON_TYPE).setActionListener(rh);
    
    //quit buttons event handler
    QuitGameHandler qh = new QuitGameHandler(this);
    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setActionListener(qh);
    
    QuitGameHandler qh2 = new QuitGameHandler(this);
    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setActionListener(qh2);
    
    //up button event handler
    ScrollUpHandler uh = new ScrollUpHandler(this);
    guiButtons.get(UP_BUTTON_TYPE).setActionListener(uh);
    
    //down button event handler
    ScrollDownHandler dh = new ScrollDownHandler(this);
    guiButtons.get(DOWN_BUTTON_TYPE).setActionListener(dh);
    
    //ABOUT button event handler
    AboutHandler abh = new AboutHandler(this);
    guiButtons.get(ABOUT_BUTTON_TYPE).setActionListener(abh);
    
    //ABOUT RETURN button event handler
    ReturnToSagaHandler rtsh = new ReturnToSagaHandler(this);
    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setActionListener(rtsh);
    
    //LEVEL RETURN button event handler
    ReturnToSagaHandler rtsh2 = new ReturnToSagaHandler(this);
    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setActionListener(rtsh2);
    
    //PLAY LEVEL button event handler
    //for (String levelFile : levels) {
      //PlayLevelHandler plh = new PlayLevelHandler(this);//, dataPath + levelFile);
      //guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setActionListener(plh);
    //}
    
  }

  /**
   * Invoked when a new game is started, it resets all relevant game data and
   * gui control states.
   */
  @Override
  public void reset() {
    data.reset(this);
  }

  /**
   * Updates the state of all gui controls according to the current game
   * conditions.
   */
  @Override
  public void updateGUI() {
    // GO THROUGH THE VISIBLE BUTTONS TO TRIGGER MOUSE OVERS
    Iterator<Sprite> buttonsIt = guiButtons.values().iterator();
    while (buttonsIt.hasNext()) {
      Sprite button = buttonsIt.next();

      // ARE WE ENTERING A BUTTON?
      if (button.getState().equals(VISIBLE_STATE)) {
        if (button.containsPoint(data.getLastMouseX(), data.getLastMouseY())) {
          button.setState(MOUSE_OVER_STATE);
        }
      } // ARE WE EXITING A BUTTON?
      else if (button.getState().equals(MOUSE_OVER_STATE)) {
        if (!button.containsPoint(data.getLastMouseX(), data.getLastMouseY())) {
          button.setState(VISIBLE_STATE);
        }
      }
    }
  }
}

/**
   * This method switches the application to the splash screen, making all the
   * appropriate UI controls visible & invisible.
   */
//  public void switchToSplashScreen() {
//    // CHANGE THE BACKGROUND
//    guiDecor.get(BACKGROUND_TYPE).setState(SPLASH_SCREEN_STATE);
//
//    // DEACTIVATE THE TOOLBAR CONTROLS
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(NEW_GAME_BUTTON_TYPE).setEnabled(false);
//    guiButtons.get(BACK_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(BACK_BUTTON_TYPE).setEnabled(false);
//    guiDecor.get(TIME_TYPE).setState(INVISIBLE_STATE);
//    guiDecor.get(TILES_COUNT_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(STATS_BUTTON_TYPE).setEnabled(false);
//    guiDecor.get(TILE_STACK_TYPE).setState(INVISIBLE_STATE);
//
//    // DEACTIVATE THE LEVEL SELECT BUTTONS
//    PropertiesManager props = PropertiesManager.getPropertiesManager();
//    ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
//    for (String level : levels) {
//      guiButtons.get(level).setState(INVISIBLE_STATE);
//      guiButtons.get(level).setEnabled(false);
//    }
//    
//    // deactivate saga buttons
//    guiButtons.get(UP_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(UP_BUTTON_TYPE).setEnabled(false);
//    guiButtons.get(DOWN_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(DOWN_BUTTON_TYPE).setEnabled(false);
//    guiButtons.get(ABOUT_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(ABOUT_BUTTON_TYPE).setEnabled(false);
//    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(QUIT_SAGA_BUTTON_TYPE).setEnabled(false);
//    
//    //deactivate ABOUT SCREEN buttons
//    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(RETURN_FROM_ABOUT_BUTTON_TYPE).setEnabled(false);
//    
//    //deactivate LEVEL SCORE buttons
//    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(RETURN_FROM_LEVEL_BUTTON_TYPE).setEnabled(false);
//    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
//    guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setEnabled(false);
//    
//    // DEACTIVATE ALL DIALOGS
//    guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
//    guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
//    guiDialogs.get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);
//    
//    // activate splash buttons
//    guiButtons.get(PLAY_BUTTON_TYPE).setState(VISIBLE_STATE);
//    guiButtons.get(PLAY_BUTTON_TYPE).setEnabled(true);
//    guiButtons.get(RESET_BUTTON_TYPE).setState(VISIBLE_STATE);
//    guiButtons.get(RESET_BUTTON_TYPE).setEnabled(true);
//    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setState(VISIBLE_STATE);
//    guiButtons.get(QUIT_SPLASH_BUTTON_TYPE).setEnabled(true);
//    
//    // HIDE THE TILES
//    ((ZombieCrushSagaDataModel) data).enableTiles(false);
//
//    // MAKE THE CURRENT SCREEN THE SPLASH SCREEN
//    currentScreenState = SPLASH_SCREEN_STATE;
//
//    // PLAY THE WELCOME SCREEN SONG
//    audio.play(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString(), true);
//    audio.stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
//    audio.stop(ZombieCrushSagaPropertyType.SAGA_SCREEN_SONG_CUE.toString());
//    audio.stop(ZombieCrushSagaPropertyType.ABOUT_SCREEN_SONG_CUE.toString());
//    audio.stop(ZombieCrushSagaPropertyType.LEVEL_SCREEN_SONG_CUE.toString());
//  }
//  