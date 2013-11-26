package zombiecrushsaga.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JPanel;
import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import zombiecrushsaga.ZombieCrushSaga;
import zombiecrushsaga.data.ZombieCrushSagaDataModel;
import static zombiecrushsaga.ZombieCrushSagaConstants.*;
import zombiecrushsaga.data.ZombieCrushSagaRecord;
import zombiecrushsaga.file.ZombieCrushLevelRequirements;

/**
 * This class performs all of the rendering for the zombie crush game
 * application.
 *
 * @author Yukti Abrol
 */
public class ZombieCrushSagaPanel extends JPanel {
    // THIS IS ACTUALLY OUR zombiecrushsaga APP, WE NEED THIS
    // BECAUSE IT HAS THE GUI STUFF THAT WE NEED TO RENDER

    private MiniGame game;
    // AND HERE IS ALL THE GAME DATA THAT WE NEED TO RENDER
    private ZombieCrushSagaDataModel data;
    // WE'LL USE THIS TO FORMAT SOME TEXT FOR DISPLAY PURPOSES
    private NumberFormat numberFormatter;
    // WE'LL USE THIS AS THE BASE IMAGE FOR RENDERING UNSELECTED TILES
    private BufferedImage blankTileImage;
    // WE'LL USE THIS AS THE BASE IMAGE FOR RENDERING SELECTED TILES
    private BufferedImage blankTileSelectedImage;

    /**
     * This constructor stores the game and data references, which we'll need
     * for rendering.
     *
     * @param initGame the zombie crush saga game that is using this panel for
     * rendering.
     *
     * @param initData the zombie crush saga game data.
     */
    public ZombieCrushSagaPanel(MiniGame initGame, ZombieCrushSagaDataModel initData) {
        game = initGame;
        data = initData;
        numberFormatter = NumberFormat.getNumberInstance();
        numberFormatter.setMinimumFractionDigits(3);
        numberFormatter.setMaximumFractionDigits(3);
    }

    // MUTATOR METHODS
    // -setBlankTileImage
    // -setBlankTileSelectedImage
    /**
     * This mutator method sets the base image to use for rendering tiles.
     *
     * @param initBlankTileImage The image to use as the base for rendering
     * tiles.
     */
    public void setBlankTileImage(BufferedImage initBlankTileImage) {
        blankTileImage = initBlankTileImage;
    }

    /**
     * This mutator method sets the base image to use for rendering selected
     * tiles.
     *
     * @param initBlankTileSelectedImage The image to use as the base for
     * rendering selected tiles.
     */
    public void setBlankTileSelectedImage(BufferedImage initBlankTileSelectedImage) {
        blankTileSelectedImage = initBlankTileSelectedImage;
    }

    /**
     * This is where rendering starts. This method is called each frame, and the
     * entire game application is rendered here with the help of a number of
     * helper methods.
     *
     * @param g The Graphics context for this panel.
     */
    @Override
    public void paintComponent(Graphics g) {
        try {
            // MAKE SURE WE HAVE EXCLUSIVE ACCESS TO THE GAME DATA
            game.beginUsingData();

            // CLEAR THE PANEL
            super.paintComponent(g);

            // RENDER THE BACKGROUND, WHICHEVER SCREEN WE'RE ON
            renderBackground(g);

            // AND THE BUTTONS AND DECOR
            renderGUIControls(g);

            // AND THE TILES
            renderTiles(g);

            //and the dialogs
            renderDialogs(g);

            // AND THE TIME AND TILES STATS
            renderStats(g);

            // RENDERING THE GRID WHERE ALL THE TILES GO CAN BE HELPFUL
            // DURING DEBUGGIN TO BETTER UNDERSTAND HOW THEY RE LAID OUT
            renderGrid(g);

            // AND FINALLY, TEXT FOR DEBUGGING
            renderDebuggingText(g);
        } finally {
            // RELEASE THE LOCK
            game.endUsingData();
        }
    }

    // RENDERING HELPER METHODS
    // - renderBackground
    // - renderGUIControls
    // - renderTiles
    // - renderDialogs
    // - renderGrid
    // - renderDebuggingText
    /**
     * Renders the background image, which is different depending on the screen.
     *
     * @param g the Graphics context of this panel.
     */
    public void renderBackground(Graphics g) {
        // THERE IS ONLY ONE CURRENTLY SET
        Sprite bg = game.getGUIDecor().get(BACKGROUND_TYPE);
        renderSprite(g, bg);
    }

    public void renderDialogs(Graphics g) {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> dialogSprites = game.getGUIDialogs().values();
        for (Sprite s : dialogSprites) {
            // RENDER THE DIALOG, NOTE IT WILL ONLY DO IT IF IT'S VISIBLE
            renderSprite(g, s);
            //if level dialog open
            if (s.getSpriteType().getSpriteTypeID().equals(LEVEL_DIALOG_TYPE) && s.getState().equals(VISIBLE_STATE)) {
                g.setFont(LEVEL_NUM_FONT);
                g.setColor(LEVEL_NUM_COLOR);
                String level = (data.getCurrentLevel());
                level = level.toLowerCase();
                level = level.replaceAll("./data/./zomcrush/level", "");
                level = level.replaceAll(".zom", "");
                g.drawString(level, 780, 185);

                g.setFont(LEVEL_TEXT_FONT);
                g.setColor(STATS_COLOR);
                //what needs to be done to win
                String levelinfo;
                ZombieCrushSagaRecord rec = ((ZombieCrushSagaMiniGame) game).getFileManager().loadRecord();
                ZombieCrushLevelRequirements currReqs = data.getcurrentReqs();
                levelinfo = "TARGET SCORE: " + currReqs.star1Score;
                g.drawString(levelinfo, 300, 300);
                levelinfo = "ADDITIONAL REQUIREMENTS: " + currReqs.additionalReq;
                g.drawString(levelinfo, 300, 340);
                //high score
                levelinfo = "HIGH SCORE: " + rec.getHighScore((data.getCurrentLevel()));
                g.drawString(levelinfo, 300, 380);
            } else if (s.getSpriteType().getSpriteTypeID().equals(ABOUT_DIALOG_TYPE) && s.getState().equals(VISIBLE_STATE)) {
                g.setColor(STATS_COLOR);
                g.setFont(STATS_FONT);
                g.drawString("Zombie Crush Saga is a zombiefied version of King’s popular", 200, 250);
                g.drawString("casual game, Candy Crush Saga TM.", 200, 270);
                g.drawString("Tens of millions of people are playing King’s Candy Crush Saga,", 200, 290);
                g.drawString("an addictive casual game that turns one into a mindless zombie", 200, 310);
                g.drawString("as one walks about, bumping into things while leveling up on a", 200, 330);
                g.drawString("mobile device. What could be better? Well how about if we", 200, 350);
                g.drawString("infuse the game itself with zombies? Yes, that’s right,", 200, 370);
                g.drawString("forget the candy, we’re going to make Zombie Crush Saga.", 200, 390);
                g.drawString("Developed by: Yukti Abrol", 200, 500);
            } else if (s.getSpriteType().getSpriteTypeID().equals(WIN_TYPE) && s.getState().equals(VISIBLE_STATE)) {
                g.setFont(LEVEL_NUM_FONT);
                g.setColor(LEVEL_NUM_COLOR);
                String level = (data.getCurrentLevel());
                level = level.toLowerCase();
                level = level.replaceAll("./data/./zomcrush/level", "");
                level = level.replaceAll(".zom", "");
                g.drawString(level, 780, 185);

                g.setFont(LEVEL_TEXT_FONT);
                g.setColor(STATS_COLOR);
                //what needs to be done to win
                String levelinfo;
                ZombieCrushSagaRecord rec = ((ZombieCrushSagaMiniGame) game).getFileManager().loadRecord();
                ZombieCrushLevelRequirements currReqs = data.getcurrentReqs();
                levelinfo = "TARGET SCORE: " + currReqs.star1Score;
                g.drawString(levelinfo, 300, 300);
                levelinfo = "ADDITIONAL REQUIREMENTS: " + currReqs.additionalReq;
                g.drawString(levelinfo, 300, 340);
                //high score
                levelinfo = "HIGH SCORE: " + rec.getHighScore((data.getCurrentLevel()));
                g.drawString(levelinfo, 300, 380);
                //your score
                levelinfo = "YOUR SCORE: " + data.getCurrentScore();
                g.drawString(levelinfo, 300, 420);
                //STARS
                levelinfo = "YOUR STARS: ";
                g.drawString(levelinfo, 300, 440);


                int numStars = data.getNumStars();
                PropertiesManager props = PropertiesManager.getPropertiesManager();
                String imgPath = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.IMG_PATH);
                BufferedImage img;
                int x;
                int y = 480;
                if (numStars == 1) {
                    x = STAR_X;
                    String s1 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_RED_IMAGE_NAME);
                    img = game.loadImage(imgPath + s1);
                    g.drawImage(blankTileImage, x, y, this);
                } else if (numStars == 2) {
                    x = STAR_X;
                    String s1 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_RED_IMAGE_NAME);
                    img = game.loadImage(imgPath + s1);
                    g.drawImage(blankTileImage, x, y, this);
                    x = STAR_X + STAR_OFFSET;
                    String s2 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_BLUE_IMAGE_NAME);
                    img = game.loadImage(imgPath + s2);
                    g.drawImage(blankTileImage, x, y, this);
                } else if (numStars == 3) {
                    x = STAR_X;
                    String s1 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_RED_IMAGE_NAME);
                    img = game.loadImage(imgPath + s1);
                    g.drawImage(blankTileImage, x, y, this);
                    x = STAR_X + STAR_OFFSET;
                    String s2 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_BLUE_IMAGE_NAME);
                    img = game.loadImage(imgPath + s2);
                    g.drawImage(blankTileImage, x, y, this);
                    x = STAR_X + STAR_OFFSET + STAR_OFFSET;
                    String s3 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_PURPLE_IMAGE_NAME);
                    img = game.loadImage(imgPath + s3);
                    g.drawImage(blankTileImage, x, y, this);
                }
            } else if (s.getSpriteType().getSpriteTypeID().equals(LOSS_TYPE) && s.getState().equals(VISIBLE_STATE)) {
                g.setFont(LEVEL_NUM_FONT);
                g.setColor(LEVEL_NUM_COLOR);
                String level = (data.getCurrentLevel());
                level = level.toLowerCase();
                level = level.replaceAll("./data/./zomcrush/level", "");
                level = level.replaceAll(".zom", "");
                g.drawString(level, 780, 185);

                g.setFont(LEVEL_TEXT_FONT);
                g.setColor(STATS_COLOR);
                //what needs to be done to win
                String levelinfo;
                ZombieCrushSagaRecord rec = ((ZombieCrushSagaMiniGame) game).getFileManager().loadRecord();
                ZombieCrushLevelRequirements currReqs = data.getcurrentReqs();
                levelinfo = "TARGET SCORE: " + currReqs.star1Score;
                g.drawString(levelinfo, 300, 300);
                levelinfo = "ADDITIONAL REQUIREMENTS: " + currReqs.additionalReq;
                g.drawString(levelinfo, 300, 340);
                //high score
                levelinfo = "HIGH SCORE: " + rec.getHighScore((data.getCurrentLevel()));
                g.drawString(levelinfo, 300, 380);
                //your score
                levelinfo = "YOUR SCORE: " + data.getCurrentScore();
                g.drawString(levelinfo, 300, 420);
            }
        }
    }

    /**
     * Renders all the GUI decor and buttons.
     *
     * @param g this panel's rendering context.
     */
    public void renderGUIControls(Graphics g) {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> decorSprites = game.getGUIDecor().values();
        for (Sprite s : decorSprites) {
            renderSprite(g, s);
        }

        // AND NOW RENDER THE BUTTONS
        Collection<Sprite> buttonSprites = game.getGUIButtons().values();
        for (Sprite s : buttonSprites) {
            renderSprite(g, s);
        }
    }

    /**
     * This method renders the on-screen stats that change as the game
     * progresses. This means things like the game time and the number of tiles
     * remaining.
     *
     * @param g the Graphics context for this panel
     */
    public void renderStats(Graphics g) {
        // RENDER THE GAME TIME
        if (((ZombieCrushSagaMiniGame) game).isCurrentScreenState(GAME_SCREEN_STATE)
                && data.inProgress() || data.isPaused()) {
//            // RENDER THE TIME
//            String time = data.gameTimeToText();
            int x = TIME_X + TIME_OFFSET;
            int y = TIME_Y;
            //add a bigger font
            g.setFont(TEXT_DISPLAY_FONT);

            //render the score
            String str = "";
            str += data.getCurrentScore();
            g.drawString(str, x, y);

            //the moves
            x = MOVES_X + TIME_OFFSET;
            str = "";
            str += data.getNumMovesLeft();
            g.drawString(str, x, y);

            //and stars
            int numStars = data.getNumStars();
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String imgPath = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.IMG_PATH);
            BufferedImage img;
            if (numStars == 1) {
                x = STAR_X;
                String s1 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_RED_IMAGE_NAME);
                img = game.loadImage(imgPath + s1);
                g.drawImage(blankTileImage, x, y, this);
            } else if (numStars == 2) {
                x = STAR_X;
                String s1 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_RED_IMAGE_NAME);
                img = game.loadImage(imgPath + s1);
                g.drawImage(blankTileImage, x, y, this);
                x = STAR_X + STAR_OFFSET;
                String s2 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_BLUE_IMAGE_NAME);
                img = game.loadImage(imgPath + s2);
                g.drawImage(blankTileImage, x, y, this);
            } else if (numStars == 3) {
                x = STAR_X;
                String s1 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_RED_IMAGE_NAME);
                img = game.loadImage(imgPath + s1);
                g.drawImage(blankTileImage, x, y, this);
                x = STAR_X + STAR_OFFSET;
                String s2 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_BLUE_IMAGE_NAME);
                img = game.loadImage(imgPath + s2);
                g.drawImage(blankTileImage, x, y, this);
                x = STAR_X + STAR_OFFSET + STAR_OFFSET;
                String s3 = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.STAR_PURPLE_IMAGE_NAME);
                img = game.loadImage(imgPath + s3);
                g.drawImage(blankTileImage, x, y, this);
            }
        }
    }

    /**
     * Renders all the game tiles, doing so carefully such that they are
     * rendered in the proper order.
     *
     * @param g the Graphics context of this panel.
     */
    public void renderTiles(Graphics g) {
        // DRAW THE TOP TILES ON THE STACK
        if (!data.won()) {
            // WE DRAW ONLY THE TOP 4 (OR 2 IF THERE ARE ONLY 2). THE REASON
            // WE DRAW 4 IS THAT WHILE WE MOVE MATCHES TO THE STACK WE WANT
            // TO SEE THE STACK
            ArrayList<ZombieCrushSagaTile> stackTiles = data.getStackTiles();
            if (stackTiles.size() > 3) {
                renderTile(g, stackTiles.get(stackTiles.size() - 3));
                renderTile(g, stackTiles.get(stackTiles.size() - 4));
            }
            if (stackTiles.size() > 1) {
                renderTile(g, stackTiles.get(stackTiles.size() - 1));
                renderTile(g, stackTiles.get(stackTiles.size() - 2));
            }
        }

        // THEN DRAW THE GRID TILES BOTTOM TO TOP USING
        // THE TILE'S Z TO STAGGER THEM AND GIVE THE ILLUSION
        // OF DEPTH
        ArrayList<ZombieCrushSagaTile>[][] tileGrid = data.getTileGrid();
        boolean noneOnLevel = false;
        int zIndex = 0;
        while (!noneOnLevel) {
            int levelCounter = 0;
            for (int i = 0; i < data.getGridColumns(); i++) {
                for (int j = 0; j < data.getGridRows(); j++) {
                    if (tileGrid[i][j].size() > zIndex) {
                        ZombieCrushSagaTile tile = tileGrid[i][j].get(zIndex);
                        renderTile(g, tile);
                        levelCounter++;
                    }
                }
            }
            if (levelCounter == 0) {
                noneOnLevel = true;
            }
            zIndex++;
        }

        // THEN DRAW ALL THE MOVING TILES
        Iterator<ZombieCrushSagaTile> movingTiles = data.getMovingTiles();
        while (movingTiles.hasNext()) {
            ZombieCrushSagaTile tile = movingTiles.next();
            renderTile(g, tile);
        }
    }

    /**
     * Helper method for rendering the tiles that are currently moving.
     *
     * @param g Rendering context for this panel.
     *
     * @param tileToRender Tile to render to this panel.
     */
    public void renderTile(Graphics g, ZombieCrushSagaTile tileToRender) {
        // ONLY RENDER VISIBLE TILES
        if (!tileToRender.getState().equals(INVISIBLE_STATE)) {
            // FIRST DRAW THE BLANK TILE IMAGE
            if (tileToRender.getState().equals(SELECTED_STATE)) {
                g.drawImage(blankTileSelectedImage, (int) tileToRender.getX(), (int) tileToRender.getY(), null);
            } else if (tileToRender.getState().equals(VISIBLE_STATE)) {
                g.drawImage(blankTileImage, (int) tileToRender.getX(), (int) tileToRender.getY(), null);
            }

            // THEN THE TILE IMAGE
            SpriteType bgST = tileToRender.getSpriteType();
            Image img = bgST.getStateImage(tileToRender.getState());
            g.drawImage(img, (int) tileToRender.getX() + TILE_IMAGE_OFFSET, (int) tileToRender.getY() + TILE_IMAGE_OFFSET, bgST.getWidth(), bgST.getHeight(), null);

            // IF THE TILE IS SELECTED, HIGHLIGHT IT
            if (tileToRender.getState().equals(SELECTED_STATE)) {
                g.setColor(SELECTED_TILE_COLOR);
                g.fillRoundRect((int) tileToRender.getX(), (int) tileToRender.getY(), bgST.getWidth(), bgST.getHeight(), 5, 5);
            } else if (tileToRender.getState().equals(JELLY_STATE)) {
                g.setColor(JELLY_TILE_COLOR);
                g.fillRoundRect((int) tileToRender.getX(), (int) tileToRender.getY(), bgST.getWidth(), bgST.getHeight(), 5, 5);
            }
        }
    }

    /**
     * Renders the s Sprite into the Graphics context g. Note that each Sprite
     * knows its own x,y coordinate location.
     *
     * @param g the Graphics context of this panel
     *
     * @param s the Sprite to be rendered
     */
    public void renderSprite(Graphics g, Sprite s) {
        // ONLY RENDER THE VISIBLE ONES
        if (!s.getState().equals(INVISIBLE_STATE)) {
            SpriteType bgST = s.getSpriteType();
            Image img = bgST.getStateImage(s.getState());
            g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);
        }
    }

    /**
     * This method renders grid lines in the game tile grid to help during
     * debugging.
     *
     * @param g Graphics context for this panel.
     */
    public void renderGrid(Graphics g) {
        // ONLY RENDER THE GRID IF WE'RE DEBUGGING
        if (data.isDebugTextRenderingActive()) {
            for (int i = 0; i < data.getGridColumns(); i++) {
                for (int j = 0; j < data.getGridRows(); j++) {
                    int x = data.calculateTileXInGrid(i, 0);
                    int y = data.calculateTileYInGrid(j, 0);
                    g.drawRect(x, y, TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT);
                }
            }
        }
    }

    /**
     * Renders the debugging text to the panel. Note that the rendering will
     * only actually be done if data has activated debug text rendering.
     *
     * @param g the Graphics context for this panel
     */
    public void renderDebuggingText(Graphics g) {
        // IF IT'S ACTIVATED
        if (data.isDebugTextRenderingActive()) {
            // ENABLE PROPER RENDER SETTINGS
            g.setFont(DEBUG_TEXT_FONT);
            g.setColor(DEBUG_TEXT_COLOR);

            // GO THROUGH ALL THE DEBUG TEXT
            Iterator<String> it = data.getDebugText().iterator();
            int x = data.getDebugTextX();
            int y = data.getDebugTextY();
            while (it.hasNext()) {
                // RENDER THE TEXT
                String text = it.next();
                g.drawString(text, x, y);
                y += 20;
            }
        }
    }
}