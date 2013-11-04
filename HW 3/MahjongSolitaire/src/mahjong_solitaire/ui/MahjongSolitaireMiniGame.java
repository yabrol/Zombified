package mahjong_solitaire.ui;

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
import mahjong_solitaire.data.MahjongSolitaireDataModel;
import mini_game.MiniGame;
import static mahjong_solitaire.MahjongSolitaireConstants.*;
import mini_game.Sprite;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import mahjong_solitaire.MahjongSolitaire.MahjongSolitairePropertyType;
import mahjong_solitaire.file.MahjongSolitaireFileManager;
import mahjong_solitaire.data.MahjongSolitaireRecord;
import mahjong_solitaire.events.BackHandler;
import mahjong_solitaire.events.ExitHandler;
import mahjong_solitaire.events.MahjongKeyHandler;
import mahjong_solitaire.events.NewGameHandler;
import mahjong_solitaire.events.SelectLevelHandler;
import mahjong_solitaire.events.StatsHandler;
import mahjong_solitaire.events.UndoHandler;

/**
 * This is the actual mini game, as extended from the mini game framework. It
 * manages all the UI elements.
 *
 * @author Richard McKenna, Yukti Abrol
 */
public class MahjongSolitaireMiniGame extends MiniGame {
  // THE PLAYER RECORD FOR EACH LEVEL, WHICH LIVES BEYOND ONE SESSION

  private MahjongSolitaireRecord record;
  // HANDLES ERROR CONDITIONS
  private MahjongSolitaireErrorHandler errorHandler;
  // MANAGES LOADING OF LEVELS AND THE PLAYER RECORDS FILES
  private MahjongSolitaireFileManager fileManager;
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
  public MahjongSolitaireRecord getPlayerRecord() {
    return record;
  }

  /**
   * Accessor method for getting the application's error handler.
   *
   * @return The error handler.
   */
  public MahjongSolitaireErrorHandler getErrorHandler() {
    return errorHandler;
  }

  /**
   * Accessor method for getting the app's file manager.
   *
   * @return The file manager.
   */
  public MahjongSolitaireFileManager getFileManager() {
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
      String dataPath = props.getProperty(MahjongSolitairePropertyType.DATA_PATH);
      String recordPath = dataPath + props.getProperty(MahjongSolitairePropertyType.RECORD_FILE_NAME);
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
    guiButtons.get(NEW_GAME_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(NEW_GAME_BUTTON_TYPE).setEnabled(true);
    guiButtons.get(BACK_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(BACK_BUTTON_TYPE).setEnabled(true);
    guiButtons.get(UNDO_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(UNDO_BUTTON_TYPE).setEnabled(true);
    guiDecor.get(TIME_TYPE).setState(VISIBLE_STATE);
    guiDecor.get(TILES_COUNT_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(STATS_BUTTON_TYPE).setState(VISIBLE_STATE);
    guiButtons.get(STATS_BUTTON_TYPE).setEnabled(true);
    guiDecor.get(TILE_STACK_TYPE).setState(VISIBLE_STATE);

    // DEACTIVATE THE LEVEL SELECT BUTTONS
    ArrayList<String> levels = props.getPropertyOptionsList(MahjongSolitairePropertyType.LEVEL_OPTIONS);
    for (String level : levels) {
      guiButtons.get(level).setState(INVISIBLE_STATE);
      guiButtons.get(level).setEnabled(false);
    }

    // MOVE THE TILES TO THE STACK AND MAKE THEM VISIBLE
    ((MahjongSolitaireDataModel) data).enableTiles(true);
    data.reset(this);

    // AND CHANGE THE SCREEN STATE
    currentScreenState = GAME_SCREEN_STATE;

    // PLAY THE GAMEPLAY SCREEN SONG
    audio.stop(MahjongSolitairePropertyType.SPLASH_SCREEN_SONG_CUE.toString());
    audio.play(MahjongSolitairePropertyType.GAMEPLAY_SONG_CUE.toString(), true);
  }

  /**
   * This method switches the application to the splash screen, making all the
   * appropriate UI controls visible & invisible.
   */
  public void switchToSplashScreen() {
    // CHANGE THE BACKGROUND
    guiDecor.get(BACKGROUND_TYPE).setState(SPLASH_SCREEN_STATE);

    // DEACTIVATE THE TOOLBAR CONTROLS
    guiButtons.get(NEW_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(NEW_GAME_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(BACK_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(BACK_BUTTON_TYPE).setEnabled(false);
    guiButtons.get(UNDO_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(UNDO_BUTTON_TYPE).setEnabled(false);
    guiDecor.get(TIME_TYPE).setState(INVISIBLE_STATE);
    guiDecor.get(TILES_COUNT_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(STATS_BUTTON_TYPE).setState(INVISIBLE_STATE);
    guiButtons.get(STATS_BUTTON_TYPE).setEnabled(false);
    guiDecor.get(TILE_STACK_TYPE).setState(INVISIBLE_STATE);

    // ACTIVATE THE LEVEL SELECT BUTTONS
    // DEACTIVATE THE LEVEL SELECT BUTTONS
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    ArrayList<String> levels = props.getPropertyOptionsList(MahjongSolitairePropertyType.LEVEL_OPTIONS);
    for (String level : levels) {
      guiButtons.get(level).setState(VISIBLE_STATE);
      guiButtons.get(level).setEnabled(true);
    }

    // DEACTIVATE ALL DIALOGS
    guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
    guiDialogs.get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);

    // HIDE THE TILES
    ((MahjongSolitaireDataModel) data).enableTiles(false);

    // MAKE THE CURRENT SCREEN THE SPLASH SCREEN
    currentScreenState = SPLASH_SCREEN_STATE;

    // PLAY THE WELCOME SCREEN SONG
    audio.play(MahjongSolitairePropertyType.SPLASH_SCREEN_SONG_CUE.toString(), true);
    audio.stop(MahjongSolitairePropertyType.GAMEPLAY_SONG_CUE.toString());
  }

  /**
   * This method updates the game grid boundaries, which will depend on the
   * level loaded.
   */
  public void updateBoundaries() {
    // NOTE THAT THE ONLY ONES WE CARE ABOUT ARE THE LEFT & TOP BOUNDARIES
    float totalWidth = ((MahjongSolitaireDataModel) data).getGridColumns() * TILE_IMAGE_WIDTH;
    float halfTotalWidth = totalWidth / 2.0f;
    float halfViewportWidth = data.getGameWidth() / 2.0f;
    boundaryLeft = halfViewportWidth - halfTotalWidth;

    // THE LEFT & TOP BOUNDARIES ARE WHERE WE START RENDERING TILES IN THE GRID
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    float topOffset = Integer.parseInt(props.getProperty(MahjongSolitairePropertyType.GAME_TOP_OFFSET.toString()));
    float totalHeight = ((MahjongSolitaireDataModel) data).getGridRows() * TILE_IMAGE_HEIGHT;
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
      String audioPath = props.getProperty(MahjongSolitairePropertyType.AUDIO_PATH);

      // LOAD ALL THE AUDIO
      loadAudioCue(MahjongSolitairePropertyType.SELECT_AUDIO_CUE);
      loadAudioCue(MahjongSolitairePropertyType.MATCH_AUDIO_CUE);
      loadAudioCue(MahjongSolitairePropertyType.NO_MATCH_AUDIO_CUE);
      loadAudioCue(MahjongSolitairePropertyType.BLOCKED_TILE_AUDIO_CUE);
      loadAudioCue(MahjongSolitairePropertyType.UNDO_AUDIO_CUE);
      loadAudioCue(MahjongSolitairePropertyType.WIN_AUDIO_CUE);
      loadAudioCue(MahjongSolitairePropertyType.LOSS_AUDIO_CUE);
      loadAudioCue(MahjongSolitairePropertyType.SPLASH_SCREEN_SONG_CUE);
      loadAudioCue(MahjongSolitairePropertyType.GAMEPLAY_SONG_CUE);

      // PLAY THE WELCOME SCREEN SONG
      audio.play(MahjongSolitairePropertyType.SPLASH_SCREEN_SONG_CUE.toString(), true);
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InvalidMidiDataException | MidiUnavailableException e) {
      errorHandler.processError(MahjongSolitairePropertyType.AUDIO_FILE_ERROR);
    }
  }

  /**
   * This helper method loads the audio file associated with audioCueType, which
   * should have been specified via an XML properties file.
   */
  private void loadAudioCue(MahjongSolitairePropertyType audioCueType)
          throws UnsupportedAudioFileException, IOException, LineUnavailableException,
          InvalidMidiDataException, MidiUnavailableException {
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    String audioPath = props.getProperty(MahjongSolitairePropertyType.AUDIO_PATH);
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
    errorHandler = new MahjongSolitaireErrorHandler(window);

    // INIT OUR FILE MANAGER
    fileManager = new MahjongSolitaireFileManager(this);

    // LOAD THE PLAYER'S RECORD FROM A FILE
    record = fileManager.loadRecord();

    // INIT OUR DATA MANAGER
    data = new MahjongSolitaireDataModel(this);

    // LOAD THE GAME DIMENSIONS
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    int gameWidth = Integer.parseInt(props.getProperty(MahjongSolitairePropertyType.GAME_WIDTH.toString()));
    int gameHeight = Integer.parseInt(props.getProperty(MahjongSolitairePropertyType.GAME_HEIGHT.toString()));
    data.setGameDimensions(gameWidth, gameHeight);

    // THIS WILL CHANGE WHEN WE LOAD A LEVEL
    boundaryLeft = Integer.parseInt(props.getProperty(MahjongSolitairePropertyType.GAME_LEFT_OFFSET.toString()));
    boundaryTop = Integer.parseInt(props.getProperty(MahjongSolitairePropertyType.GAME_TOP_OFFSET.toString()));
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
    String imgPath = props.getProperty(MahjongSolitairePropertyType.IMG_PATH);
    String windowIconFile = props.getProperty(MahjongSolitairePropertyType.WINDOW_ICON);
    img = loadImage(imgPath + windowIconFile);
    window.setIconImage(img);

    // CONSTRUCT THE PANEL WHERE WE'LL DRAW EVERYTHING
    canvas = new MahjongSolitairePanel(this, (MahjongSolitaireDataModel) data);

    // LOAD THE BACKGROUNDS, WHICH ARE GUI DECOR
    currentScreenState = SPLASH_SCREEN_STATE;
    img = loadImage(imgPath + props.getProperty(MahjongSolitairePropertyType.SPLASH_SCREEN_IMAGE_NAME));
    sT = new SpriteType(BACKGROUND_TYPE);
    sT.addState(SPLASH_SCREEN_STATE, img);
    img = loadImage(imgPath + props.getProperty(MahjongSolitairePropertyType.GAME_BACKGROUND_IMAGE_NAME));
    sT.addState(GAME_SCREEN_STATE, img);
    s = new Sprite(sT, 0, 0, 0, 0, SPLASH_SCREEN_STATE);
    guiDecor.put(BACKGROUND_TYPE, s);

    // ADD A BUTTON FOR EACH LEVEL AVAILABLE
    ArrayList<String> levels = props.getPropertyOptionsList(MahjongSolitairePropertyType.LEVEL_OPTIONS);
    ArrayList<String> levelImageNames = props.getPropertyOptionsList(MahjongSolitairePropertyType.LEVEL_IMAGE_OPTIONS);
    ArrayList<String> levelMouseOverImageNames = props.getPropertyOptionsList(MahjongSolitairePropertyType.LEVEL_MOUSE_OVER_IMAGE_OPTIONS);
    float totalWidth = levels.size() * (LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN) - LEVEL_BUTTON_MARGIN;
    float gameWidth = Integer.parseInt(props.getProperty(MahjongSolitairePropertyType.GAME_WIDTH));
    x = (gameWidth - totalWidth) / 2.0f;
    y = LEVEL_BUTTON_Y;
    for (int i = 0; i < levels.size(); i++) {
      sT = new SpriteType(LEVEL_SELECT_BUTTON_TYPE);
      img = loadImageWithColorKey(imgPath + levelImageNames.get(i), COLOR_KEY);
      sT.addState(VISIBLE_STATE, img);
      img = loadImageWithColorKey(imgPath + levelMouseOverImageNames.get(i), COLOR_KEY);
      sT.addState(MOUSE_OVER_STATE, img);
      s = new Sprite(sT, x, y, 0, 0, VISIBLE_STATE);
      guiButtons.put(levels.get(i), s);
      x += LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN;
    }

    // ADD THE CONTROLS ALONG THE NORTH OF THE GAME SCREEN

    // THEN THE NEW BUTTON
    String newButton = props.getProperty(MahjongSolitairePropertyType.NEW_BUTTON_IMAGE_NAME);
    sT = new SpriteType(NEW_GAME_BUTTON_TYPE);
    img = loadImage(imgPath + newButton);
    sT.addState(VISIBLE_STATE, img);
    String newMouseOverButton = props.getProperty(MahjongSolitairePropertyType.NEW_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + newMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, NEW_BUTTON_X, NEW_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(NEW_GAME_BUTTON_TYPE, s);
    
    //Then the back button
    String backButton = props.getProperty(MahjongSolitairePropertyType.BACK_BUTTON_IMAGE_NAME);
    sT = new SpriteType(BACK_BUTTON_TYPE);
    img = loadImage(imgPath + backButton);
    sT.addState(VISIBLE_STATE, img);
    String backMouseOverButton = props.getProperty(MahjongSolitairePropertyType.BACK_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + backMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, BACK_BUTTON_X, BACK_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(BACK_BUTTON_TYPE, s);
    
    //and the tiles count display
    String tilesCountContainer = props.getProperty(MahjongSolitairePropertyType.TILES_COUNT_IMAGE_NAME);
    sT = new SpriteType(TILES_COUNT_TYPE);
    img = loadImage(imgPath + tilesCountContainer);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, TILES_COUNT_X, TILES_COUNT_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(TILES_COUNT_TYPE, s);

    // AND THE TIME DISPLAY
    String timeContainer = props.getProperty(MahjongSolitairePropertyType.TIME_IMAGE_NAME);
    sT = new SpriteType(TIME_TYPE);
    img = loadImage(imgPath + timeContainer);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, TIME_X, TIME_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(TIME_TYPE, s);

    // AND THE STATS BUTTON
    String statsButton = props.getProperty(MahjongSolitairePropertyType.STATS_BUTTON_IMAGE_NAME);
    sT = new SpriteType(STATS_BUTTON_TYPE);
    img = loadImage(imgPath + statsButton);
    sT.addState(VISIBLE_STATE, img);
    String statsMouseOverButton = props.getProperty(MahjongSolitairePropertyType.STATS_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + statsMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, STATS_X, STATS_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(STATS_BUTTON_TYPE, s);
    
    //Then the undo button
    String undoButton = props.getProperty(MahjongSolitairePropertyType.UNDO_BUTTON_IMAGE_NAME);
    sT = new SpriteType(UNDO_BUTTON_TYPE);
    img = loadImage(imgPath + undoButton);
    sT.addState(VISIBLE_STATE, img);
    String undoMouseOverButton = props.getProperty(MahjongSolitairePropertyType.UNDO_BUTTON_MOUSE_OVER_IMAGE_NAME);
    img = loadImage(imgPath + undoMouseOverButton);
    sT.addState(MOUSE_OVER_STATE, img);
    s = new Sprite(sT, UNDO_BUTTON_X, UNDO_BUTTON_Y, 0, 0, INVISIBLE_STATE);
    guiButtons.put(UNDO_BUTTON_TYPE, s);

    // AND THE TILE STACK
    String tileStack = props.getProperty(MahjongSolitairePropertyType.TILE_STACK_IMAGE_NAME);
    sT = new SpriteType(TILE_STACK_TYPE);
    img = loadImageWithColorKey(imgPath + tileStack, COLOR_KEY);
    sT.addState(VISIBLE_STATE, img);
    s = new Sprite(sT, TILE_STACK_X, TILE_STACK_Y, 0, 0, INVISIBLE_STATE);
    guiDecor.put(TILE_STACK_TYPE, s);

    // NOW ADD THE DIALOGS

    // AND THE STATS DISPLAY
    String statsDialog = props.getProperty(MahjongSolitairePropertyType.STATS_DIALOG_IMAGE_NAME);
    sT = new SpriteType(STATS_DIALOG_TYPE);
    img = loadImageWithColorKey(imgPath + statsDialog, COLOR_KEY);
    sT.addState(VISIBLE_STATE, img);
    x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
    y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
    s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
    guiDialogs.put(STATS_DIALOG_TYPE, s);

    // AND THE WIN CONDITION DISPLAY
    String winDisplay = props.getProperty(MahjongSolitairePropertyType.WIN_DIALOG_IMAGE_NAME);
    sT = new SpriteType(WIN_DIALOG_TYPE);
    img = loadImageWithColorKey(imgPath + winDisplay, COLOR_KEY);
    sT.addState(VISIBLE_STATE, img);
    x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
    y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
    s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
    guiDialogs.put(WIN_DIALOG_TYPE, s);
    
    // and the loss condition display
    String lossDisplay = props.getProperty(MahjongSolitairePropertyType.LOSS_DIALOG_IMAGE_NAME);
    sT = new SpriteType(LOSS_DIALOG_TYPE);
    img = loadImageWithColorKey(imgPath + lossDisplay, COLOR_KEY);
    sT.addState(VISIBLE_STATE, img);
    x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
    y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
    s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
    guiDialogs.put(LOSS_DIALOG_TYPE, s);

    // THEN THE TILES STACKED TO THE TOP LEFT
    ((MahjongSolitaireDataModel) data).initTiles();
  }

  /**
   * Initializes the game event handlers for things like game gui buttons.
   */
  @Override
  public void initGUIHandlers() {
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    String dataPath = props.getProperty(MahjongSolitairePropertyType.DATA_PATH);

    // WE'LL HAVE A CUSTOM RESPONSE FOR WHEN THE USER CLOSES THE WINDOW
    window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    ExitHandler eh = new ExitHandler(this);
    window.addWindowListener(eh);

    // LEVEL BUTTON EVENT HANDLERS
    ArrayList<String> levels = props.getPropertyOptionsList(MahjongSolitairePropertyType.LEVEL_OPTIONS);
    for (String levelFile : levels) {
      SelectLevelHandler slh = new SelectLevelHandler(this, dataPath + levelFile);
      guiButtons.get(levelFile).setActionListener(slh);
    }

    // NEW GAME EVENT HANDLER
    NewGameHandler ngh = new NewGameHandler(this);
    guiButtons.get(NEW_GAME_BUTTON_TYPE).setActionListener(ngh);
    
    //back button event handler
    BackHandler bh = new BackHandler(this);
    guiButtons.get(BACK_BUTTON_TYPE).setActionListener(bh);

    // KEY LISTENER - LET'S US PROVIDE CUSTOM RESPONSES
    MahjongKeyHandler mkh = new MahjongKeyHandler(this);
    this.setKeyListener(mkh);

    // STATS BUTTON EVENT HANDLER
    StatsHandler sh = new StatsHandler(this);
    guiButtons.get(STATS_BUTTON_TYPE).setActionListener(sh);
    
    //UNDO button event handler
    UndoHandler uh = new UndoHandler(this);
    guiButtons.get(UNDO_BUTTON_TYPE).setActionListener(uh);
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