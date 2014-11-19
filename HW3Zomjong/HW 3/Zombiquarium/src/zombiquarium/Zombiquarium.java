package zombiquarium;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JOptionPane;

import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;

import zombiquarium.events.BuyTrophyHandler;
import zombiquarium.events.BuyZombieHandler;
import zombiquarium.events.NewGameHandler;

/**
 * Here lies Zombiquarium. Note that the intention of this game application is
 * not to rip off PopCap Games and make a million dollars, but rather, to learn
 * a bit about Java programming ... and then perhaps use that knowledge to make
 * a million dollars. Million dollar bills may be sent to my inbox in the Stony
 * Brook University Computer Science Department.
 *
 * This is our custom Zombiquarium Game. Note that it extends the MiniGame class
 * and overrides all the proper methods for setting up the Data, the GUI, the
 * Event Handlers, and update timer and timer task, the thing that actually does
 * the update and forces scheduled rendering.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class Zombiquarium extends MiniGame
{
    // THESE CONSTANTS SETUP THE GAME DIMENSIONS. THE GAME WIDTH
    // AND HEIGHT SHOULD MIRROR THE BACKGROUND IMAGE DIMENSIONS. WE
    // WILL NOT RENDER ANYTHING OUTSIDE THOSE BOUNDS. THE BOUNDARY
    // VALUES REFER TO AN INVISIBLE, PLAYABLE REGION. WE WON'T LET
    // THE ZOMBIES GO OUTSIDE THAT REGION AND WE WON'T HANDLE USER
    // MOUSE CLICKS THERE. NOTE THAT WE USE REAL NUMBERS FOR THE
    // BOUNDARY VALUES, WHICH REPRESENT PERCENTAGES. SO 0.2f FOR THE
    // TOP BOUNDARY WOULD MEAN 20% FROM THE TOP OF THE CANVAS
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    public static final float BOUNDARY_TOP = 0.1f;
    public static final float BOUNDARY_BOTTOM = 0.05f;
    public static final float BOUNDARY_LEFT = 0.01f;
    public static final float BOUNDARY_RIGHT = 0.01f;
    
    // HERE IS ALL THE ZOMBIQUARIUM SETUP VALUES. NOTE THAT
    // A GOOD ALTERNATIVE APPROACH WOULD BE TO LOAD THESE
    // VALUES FROM A FILE, WHICH WOULD LET A GAME DESIGNER
    // EDIT THEM AND TEST CHANGES WITHOUT HAVING THE WRITE
    // SOURCE CODE, WHICH IS GOOD, BECAUSE AS PROGRAMMERS
    // WE DON'T WANT NON-PROGRAMMERS MANGLING OUR ART. THAT'S
    // THE WHOLE POINT OF SCRIPTING LANGUAGES. NOTE THAT I 
    // STRONGLY ADVISE YOU TO CHANGE THESE VALUES DURING 
    // TESTING DEPENDING ON WHAT YOU ARE DOING TO MAKE YOUR 
    // LIFE EASIER, THEY SHOULD BE RETURNED TO THEIR PROPER 
    // VALUES UPON SUBMISSION.
    public static final int STARTING_SUN = 50;
    public static final int COST_OF_TROPHY = 1000;
    public static final int COST_OF_ZOMBIE = 100;
    public static final int COST_OF_BRAIN = 5;
    public static final int VALUE_OF_SUN = 25;
    public static final int STARTING_ZOMBIE_HEALTH = 1000;
    public static final int ZOMBIE_HEALTH_DEC = 2;
    public static final int ZOMBIE_DYING_THRESHOLD = 500;
    public static final int DEAD_ON_DISPLAY_TIME = 200;
    public static final int ZOMBIE_SUN_GEN_INTERVAL = 500;
    public static final float SUN_FALL_VELOCITY = 0.2f;
    public static final float BRAIN_FALL_VELOCITY = 0.3f;
    public static final float ZOMBIE_MAX_VELOCITY = 3.0f;
    public static final float ZOMBIE_MIN_VELOCITY = 0.7f;
    public static final float ZOMBIE_SIGHT_DISTANCE = 100.0f;
    public static final float MAX_BRAINS = 3;
    public static final Insets ZOMBIE_MOUTH_AABB = new Insets(34, 4, 50, 26);
    
    // HERE ARE SOME APP-LEVEL SETTINGS, LIKE THE FRAME RATE. ALSO,
    // WE WILL BE LOADING SpriteType DATA FROM A FILE, SO THAT FILE
    // LOCATION IS PROVIDED HERE AS WELL. NOTE THAT IT MIGHT BE A 
    // GOOD IDEA TO LOAD ALL OF THESE SETTINGS FROM A FILE, BUT ALAS,
    // THERE ARE ONLY SO MANY HOURS IN A DAY
    public static final int FRAME_RATE = 30;
    public static final String APP_TITLE = "Zombiquarium";
    public static final String SPRITE_TYPES_SETUP_FILE = "./data/ZombiquariumGameData.txt";
    public static final String SETUP_DELIMITER = "\\|";
    public static final Color COLOR_KEY = new Color(220, 110, 0);
    
    // WE'LL USE THESE JUST TO MAKE SURE WE'RE CONSISTENT
    // WE'LL USE THESE FOR IDENTIFYING SPRITE TYPES WHEN WE NEED TO GET THEM
    public static final String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    public static final String BRAIN_TYPE = "BRAIN_TYPE";
    public static final String ZOMBIE_TYPE = "ZOMBIE_TYPE";
    public static final String SUN_TYPE = "SUN_TYPE";
    
    // AND THESE FOR IDENTIFYING CONTROL STATES WHEN
    // WE NEED TO EITHER TEST OR CHANGE THEM
    public static final String NORMAL_ZOMBIE_LEFT_STATE = "NORMAL_ZOMBIE_LEFT_STATE";
    public static final String NORMAL_ZOMBIE_RIGHT_STATE = "NORMAL_ZOMBIE_RIGHT_STATE";
    public static final String DYING_ZOMBIE_LEFT_STATE = "DYING_ZOMBIE_LEFT_STATE";
    public static final String DYING_ZOMBIE_RIGHT_STATE = "DYING_ZOMBIE_RIGHT_STATE";
    public static final String DEAD_ZOMBIE_LEFT_STATE = "DEAD_ZOMBIE_LEFT_STATE";
    public static final String DEAD_ZOMBIE_RIGHT_STATE = "DEAD_ZOMBIE_RIGHT_STATE";
    public static final String ENABLED_STATE = "ENABLED_STATE";
    public static final String DISABLED_STATE = "DIABLED_STATE";
    public static final String DEFAULT_STATE = "DEFAULT_STATE";
    public static final String MOUSE_OVER_STATE = "MOUSE_OVER_STATE";
    public static final String INVISIBLE_STATE = "INVISIBLE_STATE";
    public static final String VISIBLE_STATE = "VISIBLE_STATE";
    
    // THESE ARE THE GUI SPRITE TYPES
    public static final String NORTH_TOOLBAR_TYPE = "NORTH_TOOLBAR";
    public static final String PROGRESS_TYPE = "PROGRESS";
    public static final String WIN_DISPLAY_TYPE = "WIN_DISPLAY";
    public static final String LOSS_DISPLAY_TYPE = "LOSS_DISPLAY";
    public static final String NEW_GAME_TYPE = "NEW_GAME";
    public static final String BUY_ZOMBIE_TYPE = "BUY_ZOMBIE";
    public static final String BUY_TROPHY_TYPE = "BUY_TROPHY";
    
    // AND HERE ARE ALL OUR GUI SETTINGS.
    public static final Font PROGRESS_METER_FONT = new Font("Serif", Font.BOLD, 9);
    public static final Color PROGRESS_METER_TEXT_COLOR = new Color(201, 168, 88);
    public static final Font SUN_FONT = new Font("Serif", Font.BOLD, 18);
    public static final Color SUN_TEXT_COLOR = Color.BLACK;
    public static final Font DEBUGGING_TEXT_FONT = new Font("Monospaced", Font.BOLD, 16);
    public static Color debugTextColor = Color.WHITE;
    public static final Insets PROGRESS_BAR_CORNERS = new Insets(7, 7, 13, 149);
    public static final Color PROGRSS_BAR_COLOR = new Color(0, 100, 0);

    /**
     * Default, and only, constructor. It doesn't do anything, instead leaving
     * initialization to init methods.
     */
    public Zombiquarium()
    {
    }

    // OVERRIDDEN METHODS - ZOMBIQUARIUM IMPLEMENTATIONS
        // initData
        // initGUIControls
        // initGUIHandlers
        // reset
        // updateGUI
    
    /**
     * Initializes the complete data model for this application, forcing the
     * setting of all game data, including all needed SpriteType objects.
     */
    @Override
    public void initData()
    {
        // INIT OUR DATA MANAGER
        data = new ZombiquariumDataModel();
        data.setGameDimensions(GAME_WIDTH, GAME_HEIGHT);

        boundaryLeft = BOUNDARY_LEFT * GAME_WIDTH;
        boundaryRight = GAME_WIDTH - (GAME_WIDTH * BOUNDARY_RIGHT);
        boundaryTop = BOUNDARY_TOP * GAME_HEIGHT;
        boundaryBottom = GAME_HEIGHT - (GAME_HEIGHT * BOUNDARY_BOTTOM);

        // LOAD THE SPRITE TYPE INFO FROM THE FILE
        initSpriteTypes();
    }

    /**
     * A helper method for initizliaing the data, this method loads all the
     * SpriteTypes from the SPRITE_TYPES_SETUP_FILE and puts them in the data
     * model.
     */
    private void initSpriteTypes()
    {
        // AND NOW LOAD IT WITH ALL THE DATA
        BufferedReader reader;
        try
        {
            FileReader fr = new FileReader(SPRITE_TYPES_SETUP_FILE);
            reader = new BufferedReader(fr);

            // THE FIRST LINE IN THE FILE SHOULD LIST THE
            // NUMBER OF SPRITE TYPES TO BE DESCRIBED
            int numSpriteTypes = Integer.parseInt(reader.readLine());
            String line;
            String[] lineData;

            // NOW GET THEIR INFO ONE AT A TIME
            for (int i = 0; i < numSpriteTypes; i++)
            {
                // NOW FOR EACH SPRITE TYPE, EXTRACT
                // THE NAME AND THE NUMBER OF IMAGES
                line = reader.readLine();
                lineData = line.split(SETUP_DELIMITER);
                String spriteTypeID = lineData[0];
                int numImagesForSpriteType = Integer.parseInt(lineData[1]);
                SpriteType spriteType = new SpriteType(spriteTypeID);
                data.addSpriteType(spriteType);

                // NOW LOAD THE IMAGES FOR THIS SPRITE TYPE
                for (int j = 0; j < numImagesForSpriteType; j++)
                {
                    line = reader.readLine();
                    lineData = line.split(SETUP_DELIMITER);
                    String imageID = lineData[0];
                    String imageFileName = lineData[1];
                    BufferedImage imageToLoad = loadImageWithColorKey(imageFileName, COLOR_KEY);

                    // AND ADD THE STATE/IMAGE PAIRING
                    spriteType.addState(imageID, imageToLoad);
                }
            }
        } catch (Exception e)
        {
            JOptionPane.showMessageDialog(window, "Error Loading Game Data");
            System.exit(0);
        }
    }

    /**
     * For initializing all GUI controls, specifically all the buttons and
     * decor. Note that this method must construct the canvas with its custom
     * renderer.
     */
    public void initGUIControls()
    {
        // CONSTRUCT THE PANEL WHERE WE'LL DRAW EVERYTHING
        canvas = new ZombiquariumPanel(this, (ZombiquariumDataModel) data);

        // WE'LL JUST SHARE THESE
        BufferedImage img;
        float x, y;
        SpriteType sT;
        Sprite s;

        // FIRST LOAD THE BACKGROUND, WHICH IS GUI DECOR
        sT = new SpriteType(BACKGROUND_TYPE);
        img = loadImage("./data/images/Background.png");
        sT.addState(DEFAULT_STATE, img);
        x = 0;
        y = 0;
        s = new Sprite(sT, x, y, 0, 0, DEFAULT_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);

        // AND NOW THE TOOLBAR AT THE TOP
        sT = new SpriteType(NORTH_TOOLBAR_TYPE);
        img = loadImage("./data/images/ToolbarBackground.png");
        sT.addState(DEFAULT_STATE, img);
        x = 0;
        y = 0;
        s = new Sprite(sT, x, y, 0, 0, DEFAULT_STATE);
        guiDecor.put(NORTH_TOOLBAR_TYPE, s);

        // NOW THE PROGRESS BAR, BOTTOM RIGHT
        sT = new SpriteType(PROGRESS_TYPE);
        img = loadImage("./data/images/Progress.png");
        sT.addState(DEFAULT_STATE, img);
        x = data.getGameWidth() - img.getWidth(null);
        y = data.getGameHeight() - img.getHeight(null);
        s = new Sprite(sT, x, y, 0, 0, DEFAULT_STATE);
        guiDecor.put(PROGRESS_TYPE, s);

        // AND THE WIN CONDITION DISPLAY
        sT = new SpriteType(WIN_DISPLAY_TYPE);
        img = loadImage("./data/images/WinDisplay.png");
        sT.addState(VISIBLE_STATE, img);
        x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
        y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
        img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        sT.addState(INVISIBLE_STATE, img);
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDecor.put(WIN_DISPLAY_TYPE, s);

        // AND LOSS CONDITION DISPLAY
        sT = new SpriteType(LOSS_DISPLAY_TYPE);
        img = loadImageWithColorKey("./data/images/LossDisplay.png", COLOR_KEY);
        sT.addState(VISIBLE_STATE, img);
        x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
        y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
        img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        sT.addState(INVISIBLE_STATE, img);
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDecor.put(LOSS_DISPLAY_TYPE, s);

        // AND NOW THE BUTTONS

        // FIRST THE NEW GAME BUTTON
        sT = new SpriteType(NEW_GAME_TYPE);
        img = loadImage("./data/images/NewGame.png");
        sT.addState(DEFAULT_STATE, img);
        img = loadImage("./data/images/NewGameMouseOver.png");
        sT.addState(MOUSE_OVER_STATE, img);
        x = data.getGameWidth() - img.getWidth(null);
        y = 0;
        s = new Sprite(sT, x, y, 0, 0, DEFAULT_STATE);
        guiButtons.put(NEW_GAME_TYPE, s);

        // NOW THE BUY ZOMBIE BUTTON
        sT = new SpriteType(BUY_ZOMBIE_TYPE);
        img = loadImage("./data/images/BuyZombieEnabled.png");
        sT.addState(ENABLED_STATE, img);
        img = loadImage("./data/images/BuyZombieDisabled.png");
        sT.addState(DISABLED_STATE, img);
        x = 80;
        y = 7;
        s = new Sprite(sT, x, y, 0, 0, DISABLED_STATE);
        guiButtons.put(BUY_ZOMBIE_TYPE, s);

        // NOW THE BUY TROPHIE BUTTON
        sT = new SpriteType(BUY_TROPHY_TYPE);
        img = loadImage("./data/images/BuyTrophyDisabled.png");
        sT.addState(DISABLED_STATE, img);
        img = loadImage("./data/images/BuyTrophyEnabled.png");
        sT.addState(ENABLED_STATE, img);
        x = 135;
        y = 7;
        s = new Sprite(sT, x, y, 0, 0, DISABLED_STATE);
        guiButtons.put(BUY_TROPHY_TYPE, s);
    }

    /**
     * For initializing all the button handlers for the GUI.
     */
    @Override
    public void initGUIHandlers()
    {
        NewGameHandler ngh = new NewGameHandler(this);
        guiButtons.get(NEW_GAME_TYPE).setActionListener(ngh);

        BuyZombieHandler bzh = new BuyZombieHandler(this);
        guiButtons.get(BUY_ZOMBIE_TYPE).setActionListener(bzh);

        BuyTrophyHandler bth = new BuyTrophyHandler(this);
        guiButtons.get(BUY_TROPHY_TYPE).setActionListener(bth);
    }

    /**
     * Called when a game is restarted from the beginning, it resets all game
     * data and GUI controls so that the game may start anew.
     */
    @Override
    public void reset()
    {
        guiDecor.get(WIN_DISPLAY_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(LOSS_DISPLAY_TYPE).setState(INVISIBLE_STATE);
        data.reset(this);
    }

    /**
     * This mutator method changes the color of the debug text.
     *
     * @param initColor Color to use for rendering debug text.
     */
    public static void setDebugTextColor(Color initColor)
    {
        debugTextColor = initColor;
    }

    /**
     * Called each frame, this method updates the rendering state of all
     * relevant GUI controls, like displaying win and loss states and whether
     * certain buttons should be enabled or disabled.
     */
    @Override
    public void updateGUI()
    {
        // IF THE GAME IS OVER, DISPLAY THE APPROPRIATE RESPONSE
        if (data.won())
        {
            guiDecor.get(WIN_DISPLAY_TYPE).setState(VISIBLE_STATE);
        } else if (data.lost())
        {
            guiDecor.get(LOSS_DISPLAY_TYPE).setState(VISIBLE_STATE);
        }

        // DOES THE PLAYER HAVE ENOUGH SUN TO BUY A ZOMBIE?
        int currentSun = ((ZombiquariumDataModel) data).getCurrentSun();
        if (currentSun >= COST_OF_ZOMBIE)
        {
            guiButtons.get(BUY_ZOMBIE_TYPE).setState(ENABLED_STATE);
        } else
        {
            guiButtons.get(BUY_ZOMBIE_TYPE).setState(DISABLED_STATE);
        }

        // DOES THE PLAYER HAVE ENOUGH SUN TO BUY THE TROPHY (WIN)?
        if (currentSun >= COST_OF_TROPHY)
        {
            guiButtons.get(BUY_TROPHY_TYPE).setState(ENABLED_STATE);
        } else
        {
            guiButtons.get(BUY_TROPHY_TYPE).setState(DISABLED_STATE);
        }
    }

    /**
     * The Zombiquarium game application starts here. All game data and GUI
     * initialization is done through the constructor, so we will just construct
     * our game and set it visible to start it up.
     *
     * @param args command line arguments, which will not be used
     */
    public static void main(String[] args)
    {
        Zombiquarium game = new Zombiquarium();
        game.initMiniGame(APP_TITLE, FRAME_RATE);
        game.startGame();
    }

    /**
     * This game won't have any audio, but we could add it here if we wanted.
     */
    @Override
    public void initAudioContent()
    {
        // AUDIO COULD BE ADDED HERE
    }
}