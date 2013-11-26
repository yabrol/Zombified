package zombiecrushsaga.data;

import zombiecrushsaga.ui.ZombieCrushSagaTile;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Random;
import zombiecrushsaga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import mini_game.MiniGame;
import mini_game.MiniGameDataModel;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import static zombiecrushsaga.ZombieCrushSagaConstants.*;
import zombiecrushsaga.file.ZombieCrushLevelRequirements;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;
import zombiecrushsaga.ui.ZombieCrushSagaPanel;

/**
 * This class manages the game data for zombie crush saga.
 *
 * @author Yukti Abrol
 */
public class ZombieCrushSagaDataModel extends MiniGameDataModel {
    // THIS CLASS HAS A REFERERENCE TO THE MINI GAME SO THAT IT
    // CAN NOTIFY IT TO UPDATE THE DISPLAY WHEN THE DATA MODEL CHANGES

    private MiniGame miniGame;
    //contains reqs of each level
    private ArrayList<ZombieCrushLevelRequirements> allReqs;
    // THE LEVEL GRID REFERS TO THE LAYOUT FOR A GIVEN LEVEL, MEANING
    // HOW MANY TILES FIT INTO EACH CELL WHEN FIRST STARTING A LEVEL
    private int[][] levelGrid;
    // LEVEL GRID DIMENSIONS
    private int gridColumns;
    private int gridRows;
    //tiles in level
    private int totNumTiles;
    //level available?
    private boolean levelAvailable = false;
    // THIS STORES THE TILES ON THE GRID DURING THE GAME
    private ArrayList<ZombieCrushSagaTile>[][] tileGrid;
    // THESE ARE THE TILES THE PLAYER HAS MATCHED
    private ArrayList<ZombieCrushSagaTile> stackTiles;
    //tiles player has
    private ArrayList<ZombieCrushSagaTile> playTiles;
    // THESE ARE THE TILES THAT ARE MOVING AROUND, AND SO WE HAVE TO UPDATE
    private ArrayList<ZombieCrushSagaTile> movingTiles;
    // THIS IS A SELECTED TILE, MEANING THE FIRST OF A PAIR THE PLAYER
    // IS TRYING TO MATCH. THERE CAN ONLY BE ONE OF THESE AT ANY TIME
    private ZombieCrushSagaTile selectedTile;
    // THE INITIAL LOCATION OF TILES BEFORE BEING PLACED IN THE GRID
    private int unassignedTilesX;
    private int unassignedTilesY;
    // THESE ARE USED FOR TIMING THE GAME
    private GregorianCalendar startTime;
    private GregorianCalendar endTime;
    //these are used for scoring
    private int numStars = 0;
    private int currScore = 0;
    private int numMovesLeft;
    private ArrayList<ZombieCrushSagaMove> prevMoves = new ArrayList();
    // THE REFERENCE TO THE FILE BEING PLAYED
    private String currentLevel;
    private ZombieCrushLevelRequirements currReqs;
    private ArrayList<Point> jellyCoordinates;

    /**
     * Constructor for initializing this data model, it will create the data
     * structures for storing tiles, but not the tile grid itself, that is
     * dependent of file loading, and so should be subsequently initialized.
     *
     * @param initMiniGame The zombie crush game UI.
     */
    public ZombieCrushSagaDataModel(MiniGame initMiniGame) {
        // KEEP THE GAME FOR LATER
        miniGame = initMiniGame;

        // INIT THESE FOR HOLDING MATCHED AND MOVING TILES
        stackTiles = new ArrayList();
        movingTiles = new ArrayList();
        playTiles = new ArrayList();

        allReqs = ((ZombieCrushSagaMiniGame) miniGame).getFileManager().getAllLevelRequirements();
    }

    // INIT METHODS - AFTER CONSTRUCTION, THESE METHODS SETUP A GAME FOR USE
    // - initTiles
    // - initTile
    // - initLevelGrid
    // - initSpriteType
    /**
     * This method loads the tiles, creating an individual sprite for each. Note
     * that tiles may be of various types, which is important during the tile
     * matching tests.
     */
    public void initTiles() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(ZombieCrushSagaPropertyType.IMG_PATH);
        int spriteTypeID = 0;
        SpriteType sT;

        // WE'LL RENDER ALL THE TILES ON TOP OF THE BLANK TILE
        String blankTileFileName = props.getProperty(ZombieCrushSagaPropertyType.BLANK_TILE_IMAGE_NAME);
        BufferedImage blankTileImage = miniGame.loadImageWithColorKey(imgPath + blankTileFileName, COLOR_KEY);
        ((ZombieCrushSagaPanel) (miniGame.getCanvas())).setBlankTileImage(blankTileImage);

        // THIS IS A HIGHLIGHTED BLANK TILE FOR WHEN THE PLAYER SELECTS ONE
        String blankTileSelectedFileName = props.getProperty(ZombieCrushSagaPropertyType.BLANK_TILE_SELECTED_IMAGE_NAME);
        BufferedImage blankTileSelectedImage = miniGame.loadImageWithColorKey(imgPath + blankTileSelectedFileName, COLOR_KEY);
        ((ZombieCrushSagaPanel) (miniGame.getCanvas())).setBlankTileSelectedImage(blankTileSelectedImage);

        String imgFile;
        Random generator = new Random();
        int picker;
        ArrayList<String> typeATiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_A_TILES);
        ArrayList<String> typeBTiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_B_TILES);
        ArrayList<String> typeCTiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_C_TILES);
        ArrayList<String> typeDTiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_D_TILES);
        ArrayList<String> typeETiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_E_TILES);
        ArrayList<String> typeFTiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_F_TILES);

        while (stackTiles.size() < totNumTiles)//IT WORKS WITH STACKTILES
        {
            picker = generator.nextInt(6);
            if (picker == 0) {
                // FIRST THE TYPE A TILES
                imgFile = imgPath + typeATiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_A_TYPE);
                spriteTypeID++;
            } else if (picker == 1) {
                // THEN THE TYPE B TILES
                imgFile = imgPath + typeBTiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_B_TYPE);
                spriteTypeID++;
            } else if (picker == 2) {
                // THEN THE TYPE C TILES
                imgFile = imgPath + typeCTiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_C_TYPE);
                spriteTypeID++;
            } else if (picker == 3) {
                // THEN THE TYPE D TILES
                imgFile = imgPath + typeDTiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_D_TYPE);
                spriteTypeID++;
            } else if (picker == 4) {
                // THEN THE TYPE E TILES
                imgFile = imgPath + typeETiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_E_TYPE);
                spriteTypeID++;
            } else if (picker == 5) {
                // THEN THE TYPE F TILES
                imgFile = imgPath + typeFTiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_F_TYPE);
                spriteTypeID++;
            }
        }

    }

    /**
     * Helper method for loading the tiles, it constructs the prescribed tile
     * type using the provided sprite type.
     *
     * @param sT The sprite type to use to represent this tile during rendering.
     *
     * @param tileType The type of tile. Note that there are 3 broad categories.
     */
    private void initTile(SpriteType sT, String tileType) {
        // CONSTRUCT THE TILE
        ZombieCrushSagaTile newTile = new ZombieCrushSagaTile(sT, unassignedTilesX, unassignedTilesY, 0, 0, INVISIBLE_STATE, tileType);

        // AND ADD IT TO THE STACK
        stackTiles.add(newTile);
        playTiles.add(newTile);
    }

    /**
     * make more tiles!!
     */
    public void moreTiles() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(ZombieCrushSagaPropertyType.IMG_PATH);
        int spriteTypeID = 0;
        SpriteType sT;

        // WE'LL RENDER ALL THE TILES ON TOP OF THE BLANK TILE
        String blankTileFileName = props.getProperty(ZombieCrushSagaPropertyType.BLANK_TILE_IMAGE_NAME);
        BufferedImage blankTileImage = miniGame.loadImageWithColorKey(imgPath + blankTileFileName, COLOR_KEY);
        ((ZombieCrushSagaPanel) (miniGame.getCanvas())).setBlankTileImage(blankTileImage);

        // THIS IS A HIGHLIGHTED BLANK TILE FOR WHEN THE PLAYER SELECTS ONE
        String blankTileSelectedFileName = props.getProperty(ZombieCrushSagaPropertyType.BLANK_TILE_SELECTED_IMAGE_NAME);
        BufferedImage blankTileSelectedImage = miniGame.loadImageWithColorKey(imgPath + blankTileSelectedFileName, COLOR_KEY);
        ((ZombieCrushSagaPanel) (miniGame.getCanvas())).setBlankTileSelectedImage(blankTileSelectedImage);

        String imgFile;
        Random generator = new Random();
        int picker;
        ArrayList<String> typeATiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_A_TILES);
        ArrayList<String> typeBTiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_B_TILES);
        ArrayList<String> typeCTiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_C_TILES);
        ArrayList<String> typeDTiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_D_TILES);
        ArrayList<String> typeETiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_E_TILES);
        ArrayList<String> typeFTiles = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.TYPE_F_TILES);

        while (playTiles.size() < totNumTiles) {
            picker = generator.nextInt(6);
            if (picker == 0) {
                // FIRST THE TYPE A TILES
                imgFile = imgPath + typeATiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_A_TYPE);
                spriteTypeID++;
            } else if (picker == 1) {
                // THEN THE TYPE B TILES
                imgFile = imgPath + typeBTiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_B_TYPE);
                spriteTypeID++;
            } else if (picker == 2) {
                // THEN THE TYPE C TILES
                imgFile = imgPath + typeCTiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_C_TYPE);
                spriteTypeID++;
            } else if (picker == 3) {
                // THEN THE TYPE D TILES
                imgFile = imgPath + typeDTiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_D_TYPE);
                spriteTypeID++;
            } else if (picker == 4) {
                // THEN THE TYPE E TILES
                imgFile = imgPath + typeETiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_E_TYPE);
                spriteTypeID++;
            } else if (picker == 5) {
                // THEN THE TYPE F TILES
                imgFile = imgPath + typeFTiles.get(0);
                sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
                initTile(sT, TILE_F_TYPE);
                spriteTypeID++;
            }
        }
    }

    /**
     * Called after a level has been selected, it initializes the grid so that
     * it is the proper dimensions.
     *
     * @param initGrid The grid distribution of tiles, where each cell specifies
     * the number of tiles to be stacked in that cell.
     *
     * @param initGridColumns The columns in the grid for the level selected.
     *
     * @param initGridRows The rows in the grid for the level selected.
     */
    public void initLevelGrid(int[][] initGrid, int initGridColumns, int initGridRows) {
        // KEEP ALL THE GRID INFO
        levelGrid = initGrid;
        gridColumns = initGridColumns;
        gridRows = initGridRows;

        // AND BUILD THE TILE GRID FOR STORING THE TILES
        // SINCE WE NOW KNOW ITS DIMENSIONS
        tileGrid = new ArrayList[gridColumns][gridRows];
        for (int i = 0; i < gridColumns; i++) {
            for (int j = 0; j < gridRows; j++) {
                // EACH CELL HAS A STACK OF TILES, WE'LL USE
                // AN ARRAY LIST FOR THE STACK
                tileGrid[i][j] = new ArrayList();
            }
        }
        // MAKE ALL THE TILES VISIBLE
        enableTiles(true);
    }

    /**
     * This helper method initializes a sprite type for a tile or set of similar
     * tiles to be created.
     */
    private SpriteType initTileSpriteType(String imgFile, String spriteTypeID) {
        // WE'LL MAKE A NEW SPRITE TYPE FOR EACH GROUP OF SIMILAR LOOKING TILES
        SpriteType sT = new SpriteType(spriteTypeID);
        addSpriteType(sT);

        // LOAD THE ART
        BufferedImage img = miniGame.loadImageWithColorKey(imgFile, COLOR_KEY);
        Image tempImage = img.getScaledInstance(TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT, BufferedImage.SCALE_SMOOTH);
        img = new BufferedImage(TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(tempImage, 0, 0, null);

        // WE'LL USE THE SAME IMAGtE FOR ALL STATES
        sT.addState(INVISIBLE_STATE, img);
        sT.addState(VISIBLE_STATE, img);
        sT.addState(SELECTED_STATE, img);
        sT.addState(JELLY_STATE, img);
        return sT;
    }

    // ACCESSOR METHODS
    /**
     * gets total num of tiles. must have initialzed the game first
     *
     * @return totnumtiles
     */
    public int getTotalNumberOfTiles() {
        return totNumTiles;
    }

    /**
     * returns number of moves left in this level
     *
     * @return
     */
    public int getNumMovesLeft() {
        return numMovesLeft;
    }

    /**
     * returns num of stars based on curr score
     *
     * @return
     */
    public int getNumStars() {
        return numStars;
    }

    /**
     * returns the level's requirements
     *
     * @return curr reqs
     */
    public ZombieCrushLevelRequirements getcurrentReqs() {
        return currReqs;
    }

    /**
     * gets if level is available.level 1 is always true
     *
     * @return levelCompleted
     */
    public boolean getLevelAvailable() {
        return levelAvailable;
    }

    /**
     * Accessor method for getting the level currently being played.
     *
     * @return The level name used currently for the game screen.
     */
    public String getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Accessor method for getting the number of tile columns in the game grid.
     *
     * @return The number of columns (left to right) in the grid for the level
     * currently loaded.
     */
    public int getGridColumns() {
        return gridColumns;
    }

    /**
     * Accessor method for getting the number of tile rows in the game grid.
     *
     * @return The number of rows (top to bottom) in the grid for the level
     * currently loaded.
     */
    public int getGridRows() {
        return gridRows;
    }

    /**
     * Accessor method for getting the tile grid, which has all the tiles the
     * user may select from.
     *
     * @return The main 2D grid of tiles the user selects tiles from.
     */
    public ArrayList<ZombieCrushSagaTile>[][] getTileGrid() {
        return tileGrid;
    }

    /**
     * Accessor method for getting the stack tiles.
     *
     * @return The stack tiles, which are the tiles the matched tiles are placed
     * in.
     */
    public ArrayList<ZombieCrushSagaTile> getStackTiles() {
        return stackTiles;
    }

    /**
     * Accessor method for getting the moving tiles.
     *
     * @return The moving tiles, which are the tiles currently being animated as
     * they move around the game.
     */
    public Iterator<ZombieCrushSagaTile> getMovingTiles() {
        return movingTiles.iterator();
    }

    /**
     * Mutator method for setting the currently loaded level.
     *
     * @param initCurrentLevel The level name currently being used to play the
     * game.
     */
    public void setCurrentLevel(String initCurrentLevel) {
        currentLevel = initCurrentLevel;
        if (currentLevel.equals("./data/./zomcrush/Level1.zom")) {
            levelAvailable = true;
        } //else check if previous level has been completed via record
        else {
            String currLevelNum = currentLevel.replaceAll("./data/./zomcrush/Level", "");
            currLevelNum = currLevelNum.replaceAll(".zom", "");
            int prevLevelNum = Integer.parseInt(currLevelNum) - 1;
            String prevLevel = "./data/./zomcrush/Level" + prevLevelNum + ".zom";
            int prevWins = ((ZombieCrushSagaMiniGame) miniGame).getPlayerRecord().getWins(prevLevel);
            if (prevWins != 0) {
                levelAvailable = true;
            } else {
                levelAvailable = false;
            }
        }
        String currLevelNum = currentLevel.replaceAll("./data/./zomcrush/Level", "");
        currLevelNum = currLevelNum.replaceAll(".zom", "");
        currReqs = allReqs.get(Integer.parseInt(currLevelNum) - 1);
        totNumTiles = currReqs.totTiles;
        numMovesLeft = currReqs.numMoves;
        jellyCoordinates = new ArrayList();
    }

    /**
     * Used to calculate the x-axis pixel location in the game grid for a tile
     * placed at column with stack position z.
     *
     * @param column The column in the grid the tile is located.
     *
     * @param z The level of the tile in the stack at the given grid location.
     *
     * @return The x-axis pixel location of the tile
     */
    public int calculateTileXInGrid(int column, int z) {
        int cellWidth = TILE_IMAGE_WIDTH;
        float leftEdge = miniGame.getBoundaryLeft();
        return (int) (leftEdge + (cellWidth * column) - (Z_TILE_OFFSET * z));
    }

    /**
     * Used to calculate the y-axis pixel location in the game grid for a tile
     * placed at row with stack position z.
     *
     * @param row The row in the grid the tile is located.
     *
     * @param z The level of the tile in the stack at the given grid location.
     *
     * @return The y-axis pixel location of the tile
     */
    public int calculateTileYInGrid(int row, int z) {
        int cellHeight = TILE_IMAGE_HEIGHT;
        float topEdge = miniGame.getBoundaryTop();
        return (int) (topEdge + (cellHeight * row) - (Z_TILE_OFFSET * z));
    }

    /**
     * Used to calculate the grid column for the x-axis pixel location.
     *
     * @param x The x-axis pixel location for the request.
     *
     * @return The column that corresponds to the x-axis location x.
     */
    public int calculateGridCellColumn(int x) {
        float leftEdge = miniGame.getBoundaryLeft();
        x = (int) (x - leftEdge);
        return x / TILE_IMAGE_WIDTH;
    }

    /**
     * Used to calculate the grid row for the y-axis pixel location.
     *
     * @param y The y-axis pixel location for the request.
     *
     * @return The row that corresponds to the y-axis location y.
     */
    public int calculateGridCellRow(int y) {
        float topEdge = miniGame.getBoundaryTop();
        y = (int) (y - topEdge);
        return y / TILE_IMAGE_HEIGHT;
    }

    // TIME TEXT METHODS
    // - timeToText
    // - gameTimeToText
    /**
     * This method creates and returns a textual description of the timeInMillis
     * argument as a time duration in the format of (H:MM:SS).
     *
     * @param timeInMillis The time to be represented textually.
     *
     * @return A textual representation of timeInMillis.
     */
    public String timeToText(long timeInMillis) {
        // FIRST CALCULATE THE NUMBER OF HOURS,
        // SECONDS, AND MINUTES
        long hours = timeInMillis / MILLIS_IN_AN_HOUR;
        timeInMillis -= hours * MILLIS_IN_AN_HOUR;
        long minutes = timeInMillis / MILLIS_IN_A_MINUTE;
        timeInMillis -= minutes * MILLIS_IN_A_MINUTE;
        long seconds = timeInMillis / MILLIS_IN_A_SECOND;

        // THEN ADD THE TIME OF GAME SUMMARIZED IN PARENTHESES
        String minutesText = "" + minutes;
        if (minutes < 10) {
            minutesText = "0" + minutesText;
        }
        String secondsText = "" + seconds;
        if (seconds < 10) {
            secondsText = "0" + secondsText;
        }
        return hours + ":" + minutesText + ":" + secondsText;
    }

    /**
     * This method builds and returns a textual representation of the game time.
     * Note that the game may still be in progress.
     *
     * @return The duration of the current game represented textually.
     */
    public String gameTimeToText() {
        // CALCULATE GAME TIME USING HOURS : MINUTES : SECONDS
        if ((startTime == null) || (endTime == null)) {
            return "";
        }
        long timeInMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        return timeToText(timeInMillis);
    }

    /**
     * returns current score
     *
     * @return
     */
    public int getCurrentScore() {
        return currScore;
    }

    // GAME DATA SERVICE METHODS
    // -enableTiles
    // -findMove
    // -moveAllTilesToStack
    // -moveTiles
    // -playWinAnimation
    // -processMove
    // -selectTile
    /**
     * This method can be used to make all of the tiles either visible (true) or
     * invisible (false). This should be used when switching between the splash
     * and game screens.
     *
     * @param enable Specifies whether the tiles should be made visible or not.
     */
    public void enableTiles(boolean enable) {
        // PUT ALL THE TILES IN ONE PLACE WHERE WE CAN PROCESS THEM TOGETHER
//        moveAllTilesToStack();

        // GO THROUGH ALL OF THEM 
        for (ZombieCrushSagaTile tile : playTiles) {
            // AND SET THEM PROPERLY
            if (enable) {
                tile.setState(VISIBLE_STATE);
            } else {
                tile.setState(INVISIBLE_STATE);
            }
        }
    }

    /**
     * This method examines the current game grid and finds and returns a valid
     * move that is available.
     *
     * @return A move that can be made, or null if none exist.
     */
    public ZombieCrushSagaMove findMove() {
        // MAKE A MOVE TO FILL IN 
        ZombieCrushSagaMove move = new ZombieCrushSagaMove();
        ArrayList<ZombieCrushSagaTile> removeTiles;
        ArrayList<ZombieCrushSagaTile> stack1;
        ZombieCrushSagaTile testTile1, testTile2;
        ArrayList<ZombieCrushSagaTile> stack2;

        // GO THROUGH THE ENTIRE GRID TO FIND A MATCH BETWEEN AVAILABLE TILES
        for (int i = 0; i < gridColumns; i++) {
            for (int j = 0; j < gridRows; j++) {
                stack1 = tileGrid[i][j];
                if (stack1.size() > 0) {
                    // GET THE FIRST TILE
                    testTile1 = stack1.get(0);

                    //check one up, one down, one left, one right
                    if (j - 1 >= 0) {
                        stack2 = tileGrid[i][j - 1];
                        if (stack2.size() > 0) //if there is a tile there
                        {
                            testTile2 = stack2.get(0);
                            //then check different shapes: t,l,5,4,3
                            //if that shape exists, return move
                            //move contains 1: pos of test tile and 2: where to move it
                            // and all the tiles that will be removed if this is done
                            swap(testTile1,testTile2);
                            removeTiles = checkTshape(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j - 1;
                                move.moveType = T_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkTshape(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j - 1;
                                move.moveType = T_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkLshape(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j - 1;
                                move.moveType = L_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkLshape(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j - 1;
                                move.moveType = L_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check5Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j - 1;
                                move.moveType = ROW_5_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check5Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j - 1;
                                move.moveType = ROW_5_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check4Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j - 1;
                                move.moveType = ROW_4_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check4Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j - 1;
                                move.moveType = ROW_4_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check3Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j - 1;
                                move.moveType = ROW_3_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check3Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j - 1;
                                move.moveType = ROW_3_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            //swap back
                            swap(testTile1,testTile2);
                        }
                    }
                    if (j + 1 < gridRows) {
                        stack2 = tileGrid[i][j + 1];
                        if (stack2.size() > 0) //if there is a tile there
                        {
                            testTile2 = stack2.get(0);
                            //then check different shapes: t,l,5,4,3
                            //if that shape exists, return move
                            //move contains 1: pos of test tile and 2: where to move it
                            // and all the tiles that will be removed if this is done
                            swap(testTile1,testTile2);
                            removeTiles = checkTshape(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j + 1;
                                move.moveType = T_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkTshape(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j + 1;
                                move.moveType = T_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkLshape(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j + 1;
                                move.moveType = L_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkLshape(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j + 1;
                                move.moveType = L_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check5Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j + 1;
                                move.moveType = ROW_5_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check5Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j + 1;
                                move.moveType = ROW_5_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check4Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j + 1;
                                move.moveType = ROW_4_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check4Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j + 1;
                                move.moveType = ROW_4_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check3Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i;
                                move.row2 = j + 1;
                                move.moveType = ROW_3_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check3Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i;
                                move.row1 = j + 1;
                                move.moveType = ROW_3_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            //swap back
                            swap(testTile1,testTile2);
                        }
                    }
                    if (i - 1 >= 0) {
                        stack2 = tileGrid[i - 1][j];
                        if (stack2.size() > 0) //if there is a tile there
                        {
                            testTile2 = stack2.get(0);
                            //then check different shapes: t,l,5,4,3
                            //if that shape exists, return move
                            //move contains 1: pos of test tile and 2: where to move it
                            // and all the tiles that will be removed if this is done
                            swap(testTile1,testTile2);
                            removeTiles = checkTshape(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i - 1;
                                move.row2 = j;
                                move.moveType = T_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkTshape(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i - 1;
                                move.row1 = j;
                                move.moveType = T_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkLshape(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i - 1;
                                move.row2 = j;
                                move.moveType = L_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkLshape(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2= j;
                                move.col1 = i - 1;
                                move.row1 = j;
                                move.moveType = L_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check5Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i - 1;
                                move.row2 = j;
                                move.moveType = ROW_5_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check5Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i - 1;
                                move.row1 = j;
                                move.moveType = ROW_5_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check4Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i - 1;
                                move.row2 = j;
                                move.moveType = ROW_4_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check4Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i - 1;
                                move.row1 = j;
                                move.moveType = ROW_4_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check3Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i - 1;
                                move.row2 = j;
                                move.moveType = ROW_3_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check3Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i - 1;
                                move.row1 = j;
                                move.moveType = ROW_3_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            //swap back
                            swap(testTile1,testTile2);
                        }
                    }
                    if (i + 1 < gridColumns) {
                        stack2 = tileGrid[i + 1][j];
                        if (stack2.size() > 0) //if there is a tile there
                        {
                            testTile2 = stack2.get(0);
                            //then check different shapes: t,l,5,4,3
                            //if that shape exists, return move
                            //move contains 1: pos of test tile and 2: where to move it
                            // and all the tiles that will be removed if this is done
                            swap(testTile1,testTile2);
                            removeTiles = checkTshape(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i + 1;
                                move.row2 = j;
                                move.moveType = T_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkTshape(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i + 1;
                                move.row1 = j;
                                move.moveType = T_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkLshape(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i + 1;
                                move.row2 = j;
                                move.moveType = L_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = checkLshape(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i + 1;
                                move.row1 = j;
                                move.moveType = L_SHAPE_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check5Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i + 1;
                                move.row2 = j;
                                move.moveType = ROW_5_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check5Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i + 1;
                                move.row1 = j;
                                move.moveType = ROW_5_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check4Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i + 1;
                                move.row2 = j;
                                move.moveType = ROW_4_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check4Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i + 1;
                                move.row1 = j;
                                move.moveType = ROW_4_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check3Row(testTile1);
                            if (removeTiles != null) {
                                move.col1 = i;
                                move.row1 = j;
                                move.col2 = i + 1;
                                move.row2 = j;
                                move.moveType = ROW_3_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            removeTiles = check3Row(testTile2);
                            if (removeTiles != null) {
                                move.col2 = i;
                                move.row2 = j;
                                move.col1 = i + 1;
                                move.row1 = j;
                                move.moveType = ROW_3_MOVE;
                                move.tilesToRemove = removeTiles;
                                //swap back
                                swap(testTile1,testTile2);
                                return move;
                            }
                            swap(testTile1,testTile2);
                        }
                    }
                }
            }
        }
        // WE'VE SEARCHED THE ENTIRE GRID AND THERE
        // ARE NO POSSIBLE MOVES REMAINING
        return null;
    }

    /**
     * checks if t shape can be formed if the testTile is moved to the inputed
     * coordinates
     *
     * @param testTile
     * @return
     */
    public ArrayList<ZombieCrushSagaTile> checkTshape(ZombieCrushSagaTile testTile) {
        ArrayList<ZombieCrushSagaTile> tilesToRemove = new ArrayList();
        tilesToRemove.add(testTile);
        ArrayList<ZombieCrushSagaTile> stack2, stack3, stack4, stack5;
        ZombieCrushSagaTile test2, test3, test4, test5;
        int x = testTile.getGridColumn();
        int y = testTile.getGridRow();
        //figure out which direction you moved from, the opposite side should have 2 matching
        //and each adjacent side should have 1 matching
        int x1 = testTile.getGridColumn();
        int y1 = testTile.getGridRow();
        //if x1 > x, moved left
        if (x1 > x) {
            //check if all are available
            if (x - 2 >= 0 && y + 1 < gridRows && y - 1 >= 0) {
                stack2 = tileGrid[x - 1][y];
                stack3 = tileGrid[x - 2][y];
                stack4 = tileGrid[x][y + 1];
                stack5 = tileGrid[x][y - 1];
                if (stack2.size() > 0 && stack3.size() > 0 && stack4.size() > 0 && stack5.size() > 0) {
                    test2 = stack2.get(0);
                    test3 = stack3.get(0);
                    test4 = stack4.get(0);
                    test5 = stack5.get(0);
                    if (test2.match(testTile) && test3.match(testTile) && test4.match(testTile)
                            && test5.match(testTile)) {
                        tilesToRemove.add(test2);
                        tilesToRemove.add(test3);
                        tilesToRemove.add(test4);
                        tilesToRemove.add(test5);
                        return tilesToRemove;
                    } else {
                        return null;
                    }
                }
            }
        } //moved right
        else if (x1 < x) {
            //check if all are available
            if (x + 2 < gridColumns && y + 1 < gridRows && y - 1 >= 0) {
                stack2 = tileGrid[x + 1][y];
                stack3 = tileGrid[x + 2][y];
                stack4 = tileGrid[x][y + 1];
                stack5 = tileGrid[x][y - 1];
                if (stack2.size() > 0 && stack3.size() > 0 && stack4.size() > 0 && stack5.size() > 0) {
                    test2 = stack2.get(0);
                    test3 = stack3.get(0);
                    test4 = stack4.get(0);
                    test5 = stack5.get(0);
                    if (test2.match(testTile) && test3.match(testTile) && test4.match(testTile)
                            && test5.match(testTile)) {
                        tilesToRemove.add(test2);
                        tilesToRemove.add(test3);
                        tilesToRemove.add(test4);
                        tilesToRemove.add(test5);
                        return tilesToRemove;
                    } else {
                        return null;
                    }
                }
            }
        } //if y1 > y, moved up
        else if (y1 > y) {
            //check if all are available
            if (y - 2 >= 0 && x + 1 < gridColumns && x - 1 >= 0) {
                stack2 = tileGrid[x][y - 1];
                stack3 = tileGrid[x][y - 2];
                stack4 = tileGrid[x + 1][y];
                stack5 = tileGrid[x - 1][y];
                if (stack2.size() > 0 && stack3.size() > 0 && stack4.size() > 0 && stack5.size() > 0) {
                    test2 = stack2.get(0);
                    test3 = stack3.get(0);
                    test4 = stack4.get(0);
                    test5 = stack5.get(0);
                    if (test2.match(testTile) && test3.match(testTile) && test4.match(testTile)
                            && test5.match(testTile)) {
                        tilesToRemove.add(test2);
                        tilesToRemove.add(test3);
                        tilesToRemove.add(test4);
                        tilesToRemove.add(test5);
                        return tilesToRemove;
                    } else {
                        return null;
                    }
                }
            }
        } //move down
        else if (y1 < y) {
            //check if all are available
            if (y + 2 < gridRows && x + 1 < gridColumns && x - 1 >= 0) {
                stack2 = tileGrid[x][y + 1];
                stack3 = tileGrid[x][y + 2];
                stack4 = tileGrid[x + 1][y];
                stack5 = tileGrid[x - 1][y];
                if (stack2.size() > 0 && stack3.size() > 0 && stack4.size() > 0 && stack5.size() > 0) {
                    test2 = stack2.get(0);
                    test3 = stack3.get(0);
                    test4 = stack4.get(0);
                    test5 = stack5.get(0);
                    if (test2.match(testTile) && test3.match(testTile) && test4.match(testTile)
                            && test5.match(testTile)) {
                        tilesToRemove.add(test2);
                        tilesToRemove.add(test3);
                        tilesToRemove.add(test4);
                        tilesToRemove.add(test5);
                        return tilesToRemove;
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * checks if L shape can be formed if the testTile is moved to the inputted
     * coordinates
     *
     * @param testTile
     * @return
     */
    public ArrayList<ZombieCrushSagaTile> checkLshape(ZombieCrushSagaTile testTile) {
        ArrayList<ZombieCrushSagaTile> tilesToRemove = new ArrayList();
        //check up twice. both should match
        //if this is true, then try left for 2 matching, else try right for 2 matching
        //if not, then try down twice. if both match, then try left and right for 2 matching
        tilesToRemove.add(testTile);
        ArrayList<ZombieCrushSagaTile> stack2;
        ZombieCrushSagaTile test2;
        int x = testTile.getGridColumn();
        int y = testTile.getGridRow();
        //check up & right
        for (int i = 1; i < 3; i++) {
            if (y + i < gridRows) {
                stack2 = tileGrid[x][y + i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
            if (x + i < gridColumns) {
                stack2 = tileGrid[x + i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 5) {
            return tilesToRemove;
        } else {
            tilesToRemove = new ArrayList();
            tilesToRemove.add(testTile);
        }
        //check down and right
        for (int i = 1; i < 5; i++) {
            if (y - i >= 0) {
                stack2 = tileGrid[x][y - i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
            if (x + i < gridColumns) {
                stack2 = tileGrid[x + i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 5) {
            return tilesToRemove;
        } else {
            tilesToRemove = new ArrayList();
            tilesToRemove.add(testTile);
        }
        //check up and left
        for (int i = 1; i < 5; i++) {
            if (y + i < gridRows) {
                stack2 = tileGrid[x][y + i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
            if (x - i >= 0) {
                stack2 = tileGrid[x - i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 5) {
            return tilesToRemove;
        } else {
            tilesToRemove = new ArrayList();
            tilesToRemove.add(testTile);
        }
        //check down and left
        for (int i = 1; i < 5; i++) {
            if (y - i >= 0) {
                stack2 = tileGrid[x][y - i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
            if (x - i >= 0) {
                stack2 = tileGrid[x - i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 5) {
            return tilesToRemove;
        } else {
            return null;
        }
    }

    /**
     * checks if 5 in a row can be formed if the testTile is moved to the
     * inputted coordinates
     *
     * @param testTile
     * @return
     */
    public ArrayList<ZombieCrushSagaTile> check5Row(ZombieCrushSagaTile testTile) {
        ArrayList<ZombieCrushSagaTile> tilesToRemove = new ArrayList();
        tilesToRemove.add(testTile);
        ArrayList<ZombieCrushSagaTile> stack2;
        ZombieCrushSagaTile test2;
        int x = testTile.getGridColumn();
        int y = testTile.getGridRow();
        //check up
        for (int i = 1; i < 5; i++) {
            if (y + i < gridRows) {
                stack2 = tileGrid[x][y + i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        //check down
        for (int i = 1; i < 5; i++) {
            if (y - i >= 0) {
                stack2 = tileGrid[x][y - i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 5) {
            return tilesToRemove;
        } else {
            tilesToRemove = new ArrayList();
            tilesToRemove.add(testTile);
        }
        //check right
        for (int i = 1; i < 5; i++) {
            if (x + i < gridColumns) {
                stack2 = tileGrid[x + i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        //check left
        for (int i = 1; i < 5; i++) {
            if (x - i >= 0) {
                stack2 = tileGrid[x - i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 5) {
            return tilesToRemove;
        } else {
            return null;
        }
    }

    /**
     * checks if 4 in a row can be formed if the testTile is moved to the
     * inputted coordinates
     *
     * @param testTile
     * @return
     */
    public ArrayList<ZombieCrushSagaTile> check4Row(ZombieCrushSagaTile testTile) {
        ArrayList<ZombieCrushSagaTile> tilesToRemove = new ArrayList();
        tilesToRemove.add(testTile);
        ArrayList<ZombieCrushSagaTile> stack2;
        ZombieCrushSagaTile test2;
        int x = testTile.getGridColumn();
        int y = testTile.getGridRow();
        //check up
        for (int i = 1; i < 4; i++) {
            if (y + i < gridRows) {
                stack2 = tileGrid[x][y + i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        //check down
        for (int i = 1; i < 4; i++) {
            if (y - i >= 0) {
                stack2 = tileGrid[x][y - i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 4) {
            return tilesToRemove;
        } else {
            tilesToRemove = new ArrayList();
            tilesToRemove.add(testTile);
        }
        //check right
        for (int i = 1; i < 4; i++) {
            if (x + i < gridColumns) {
                stack2 = tileGrid[x + i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        //check left
        for (int i = 1; i < 4; i++) {
            if (x - i >= 0) {
                stack2 = tileGrid[x - i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 4) {
            return tilesToRemove;
        } else {
            return null;
        }
    }

    /**
     * checks if 3 in a row can be formed if the testTile is moved to the
     * inputted coordinates
     *
     * @param testTile
     * @return
     */
    public ArrayList<ZombieCrushSagaTile> check3Row(ZombieCrushSagaTile testTile) {
        ArrayList<ZombieCrushSagaTile> tilesToRemove = new ArrayList();
        tilesToRemove.add(testTile);
        ArrayList<ZombieCrushSagaTile> stack2;
        ZombieCrushSagaTile test2;
        int x = testTile.getGridColumn();
        int y = testTile.getGridRow();
        //check up
        for (int i = 1; i < 3; i++) {
            if (y + i < gridRows) {
                stack2 = tileGrid[x][y + i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        //check down
        for (int i = 1; i < 3; i++) {
            if (y - i >= 0) {
                stack2 = tileGrid[x][y - i];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 3) {
            return tilesToRemove;
        } else {
            tilesToRemove = new ArrayList();
            tilesToRemove.add(testTile);
        }
        //check right
        for (int i = 1; i < 3; i++) {
            if (x + i < gridColumns) {
                stack2 = tileGrid[x + i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        //check left
        for (int i = 1; i < 3; i++) {
            if (x - i >= 0) {
                stack2 = tileGrid[x - i][y];
                if (stack2.size() > 0) {
                    test2 = stack2.get(0);
                    if (test2.match(testTile)) {
                        tilesToRemove.add(test2);
                    } else {
                        break;
                    }
                }
            }
        }
        if (tilesToRemove.size() >= 3) {
            return tilesToRemove;
        } else {
            return null;
        }
    }

    /**
     * returns the list of tiles that will be removed if there was a special
     * tile
     *
     * @param specTile
     * @return
     */
    public ArrayList<ZombieCrushSagaTile> processSpecial(ZombieCrushSagaTile specTile) {
        ArrayList<ZombieCrushSagaTile> tilesToRemove = new ArrayList();
        int x = specTile.getGridColumn();
        int y = specTile.getGridRow();
        //probably needs the move as well

        //figure out what type of special it is
        //if striped, randomly pick vertical or horizontal and remove all in that direction
        //if wrapper, remove adjacent 8
        //if bomb, remove all of same type
        return tilesToRemove;
    }

    /**
     * This method moves all the tiles not currently in the stack to the stack.
     */
    public void moveAllTilesToStack() {
        for (int i = 0; i < gridColumns; i++) {
            for (int j = 0; j < gridRows; j++) {
                ArrayList<ZombieCrushSagaTile> cellStack = tileGrid[i][j];
                moveTiles(cellStack, stackTiles);
            }
        }
    }

    /**
     * This method removes all the tiles in from argument and moves them to
     * argument.
     *
     * @param from The source data structure of tiles.
     *
     * @param to The destination data structure of tiles.
     */
    private void moveTiles(ArrayList<ZombieCrushSagaTile> from, ArrayList<ZombieCrushSagaTile> to) {
        // GO THROUGH ALL THE TILES, TOP TO BOTTOM
        for (int i = from.size() - 1; i >= 0; i--) {
            ZombieCrushSagaTile tile = from.remove(i);

            // ONLY ADD IT IF IT'S NOT THERE ALREADY
            if (!to.contains(tile)) {
                to.add(tile);
            }
        }
    }

    /**
     * This method updates all the necessary state information to process the
     * move argument.
     *
     * @param move The move to make. Note that a move specifies the cell
     * locations for a match.
     */
    public void processMove(ZombieCrushSagaMove move) {
        // REMOVE THE MOVE TILES FROM THE GRID
        ArrayList<ZombieCrushSagaTile> stack1 = move.tilesToRemove;
        ZombieCrushSagaTile test1 = stack1.get(stack1.size() - 1);
        //check if previous moves were same type
        int seq = 1;
        prevMoves.add(move);
        ZombieCrushSagaMove pMove;
        for (int i = 1; prevMoves.size() > i; i++) {
            pMove = prevMoves.get(prevMoves.size() - 1 - i);
            if (move.moveType.equals(pMove.moveType)) {
                if (!(move.moveType.equals(T_SHAPE_MOVE) || move.moveType.equals(L_SHAPE_MOVE))) {
                    seq++;
                }
            } else {
                prevMoves = new ArrayList();
                prevMoves.add(move);
                break;
            }
        }
        updateScore(stack1, seq);
        numMovesLeft--;
        swap(test1, tileGrid[move.col2][move.row2].get(0));

        //remove them
        for (ZombieCrushSagaTile tile1 : stack1) {
//            stack1.remove(tile1);
            // MAKE SURE BOTH ARE UNSELECTED
            tile1.setState(VISIBLE_STATE);
            // SEND THEM TO THE STACK
            tile1.setTarget(TILE_STACK_X + TILE_STACK_OFFSET_X, TILE_STACK_Y + TILE_STACK_OFFSET_Y);
            tile1.startMovingToTarget(MAX_TILE_VELOCITY);
            stackTiles.add(tile1);
            playTiles.remove(tile1);
            tileGrid[tile1.getGridColumn()][tile1.getGridRow()].remove(0);
            // MAKE SURE THEY MOVE
            movingTiles.add(tile1);
        }

        //add more tiles
        moreTiles();
        updateGrid();

        // AND MAKE SURE NEW TILES CAN BE SELECTED
        selectedTile = null;

        // PLAY THE AUDIO CUE
        miniGame.getAudio().play(ZombieCrushSagaPropertyType.MATCH_AUDIO_CUE.toString(), false);

        // NOW CHECK TO SEE IF THE GAME HAS EITHER BEEN WON OR LOST

        // HAS THE PLAYER WON?
        if (numMovesLeft == 0 && currScore >= currReqs.star1Score) {
            // YUP UPDATE EVERYTHING ACCORDINGLY
            endGameAsWin();
        } else if (numMovesLeft > 0) {
            // SEE IF THERE ARE ANY MOVES LEFT
            ZombieCrushSagaMove possibleMove = this.findMove();
            if (possibleMove == null) {
                Collections.shuffle(playTiles);
                //put tiles in new spots
                updateGrid();
            }
        } else {
            endGameAsLoss();
        }
    }

    public ArrayList<ZombieCrushSagaTile> hasSpecial(ArrayList<ZombieCrushSagaTile> stack1) {
        ArrayList<ZombieCrushSagaTile> specialTiles = new ArrayList();
        for (ZombieCrushSagaTile tile : stack1) {
            if (tile.getSpriteType().getSpriteTypeID().equals(TILE_STRIPED_TYPE)
                    || tile.getSpriteType().getSpriteTypeID().equals(TILE_BOMB_TYPE)
                    || tile.getSpriteType().getSpriteTypeID().equals(TILE_WRAPPER_TYPE)) {
                specialTiles.add(tile);
            }
        }
        if (specialTiles.size() > 0) {
            return specialTiles;
        } else {
            return null;
        }
    }

    /**
     * updates the score
     *
     * @param stack1 - move.tilesToRemove
     * @param sequential - # of sequential moves of same type. min = 1
     */
    public void updateScore(ArrayList<ZombieCrushSagaTile> stack1, int sequential) {
        int baseScore = 0;
        int levelMultiplier = currReqs.levelNumber;
        ArrayList<ZombieCrushSagaTile> specialStack = hasSpecial(stack1);
        switch (stack1.size()) {
            case 3:
                baseScore = 20 + 10 * levelMultiplier; //special score
            //                if (specialStack.size()>0)
            //                {
            //                    baseScore = specialStack.size()*3*baseScore 
            //                            + baseScore*(stack1.size() - specialStack.size());
            //                }
            //                //or sequential score
            //                else
            {
                baseScore *= stack1.size();
                baseScore *= sequential;
            }
            case 4:
                baseScore = 30 + 10 * levelMultiplier; //special score
            //                if (specialStack.size()>0)
            //                {
            //                    baseScore = specialStack.size()*3*baseScore 
            //                            + baseScore*(stack1.size() - specialStack.size());
            //                }
            //                //or sequential score
            //                else
            {
                baseScore *= stack1.size();
                baseScore *= sequential;
            }
            case 5:
                baseScore = 40 + 10 * levelMultiplier; //special score
            //                if (specialStack.size()>0)
            //                {
            //                    baseScore = specialStack.size()*3*baseScore 
            //                            + baseScore*(stack1.size() - specialStack.size());
            //                }
            //                //or sequential score
            //                else
            {
                baseScore *= stack1.size();
                baseScore *= sequential;
            }
        }
        currScore += baseScore;
        if (currScore < currReqs.star1Score) {
            numStars = 0;
        } else if (currScore > currReqs.star1Score) {
            numStars = 1;
        } else if (currScore > currReqs.star2Score) {
            numStars = 2;
        } else if (currScore > currReqs.star3Score) {
            numStars = 3;
        }
    }

    /**
     * This method attempts to select the selectTile argument. Note that this
     * may be the first or second selected tile. If a tile is already selected,
     * it will attempt to process a match/move.
     *
     * @param selectTile The tile to select.
     */
    public void selectTile(ZombieCrushSagaTile selectTile) {
        // IF IT'S ALREADY THE SELECTED TILE, DESELECT IT
        if (selectTile == selectedTile) {
            selectedTile = null;
            selectTile.setState(VISIBLE_STATE);
            return;
        }

        if (selectedTile == null) {
            selectedTile = selectTile;
            selectedTile.setState(SELECTED_STATE);
            miniGame.getAudio().play(ZombieCrushSagaPropertyType.SELECT_AUDIO_CUE.toString(), false);
            return;
        }
        //make sure the two tiles are adjacent but not diagonal
        if ((Math.abs(selectedTile.getGridColumn() - selectTile.getGridColumn()) > 1
                && Math.abs(selectedTile.getGridRow() - selectTile.getGridRow()) < 1)
                || (Math.abs(selectedTile.getGridRow() - selectTile.getGridRow()) > 1
                && Math.abs(selectedTile.getGridColumn() - selectTile.getGridColumn()) < 1)) {
            miniGame.getAudio().play(ZombieCrushSagaPropertyType.NO_MATCH_AUDIO_CUE.toString(), false);
            selectTile.setState(VISIBLE_STATE);
            selectedTile.setState(VISIBLE_STATE);
            selectedTile = null;
            return;
        }
        //remove
        ZombieCrushSagaMove move = new ZombieCrushSagaMove();
        move.col1 = selectedTile.getGridColumn();
        move.row1 = selectedTile.getGridRow();
        move.col2 = selectTile.getGridColumn();
        move.row2 = selectTile.getGridRow();

        //make them move towards each other
        float x = calculateTileXInGrid(selectTile.getGridColumn(), 0);
        float y = calculateTileYInGrid(selectTile.getGridRow(), 0);
        selectedTile.setTarget(x, y);
        selectedTile.startMovingToTarget(MIN_TILE_VELOCITY);
        movingTiles.add(selectedTile);

        x = calculateTileXInGrid(selectedTile.getGridColumn(), 0);
        y = calculateTileYInGrid(selectedTile.getGridRow(), 0);
        selectTile.setTarget(x, y);
        selectTile.startMovingToTarget(MIN_TILE_VELOCITY);
        movingTiles.add(selectTile);

        //make sure all things are moved before continuing
        while (movingTiles.size() > 0) {
            updateAll(miniGame);
        }
        //swap the tiles
        swap(selectedTile, selectTile);

        move.tilesToRemove = checkTshape(selectedTile);
        if (move.tilesToRemove == null) {
            move.tilesToRemove = checkTshape(selectTile);
            if (move.tilesToRemove == null) {
                move.tilesToRemove = checkLshape(selectedTile);
                if (move.tilesToRemove == null) {
                    move.tilesToRemove = checkLshape(selectTile);
                    if (move.tilesToRemove == null) {
                        move.tilesToRemove = check5Row(selectedTile);
                        if (move.tilesToRemove == null) {
                            move.tilesToRemove = check5Row(selectTile);
                            if (move.tilesToRemove == null) {
                                move.tilesToRemove = check4Row(selectedTile);
                                if (move.tilesToRemove == null) {
                                    move.tilesToRemove = check4Row(selectTile);
                                    if (move.tilesToRemove == null) {
                                        move.tilesToRemove = check3Row(selectedTile);
                                        if (move.tilesToRemove == null) {
                                            move.tilesToRemove = check3Row(selectTile);
                                            if (move.tilesToRemove == null) {
                                                //make them move back where they came from
                                                x = calculateTileXInGrid(selectedTile.getGridColumn(), 0);
                                                y = calculateTileYInGrid(selectedTile.getGridRow(), 0);
                                                selectedTile.setTarget(x, y);
                                                selectedTile.startMovingToTarget(MIN_TILE_VELOCITY);
                                                movingTiles.add(selectedTile);

                                                x = calculateTileXInGrid(selectTile.getGridColumn(), 0);
                                                y = calculateTileYInGrid(selectTile.getGridRow(), 0);
                                                selectTile.setTarget(x, y);
                                                selectTile.startMovingToTarget(MIN_TILE_VELOCITY);
                                                movingTiles.add(selectTile);

                                                //make sure all things are moved before continuing
                                                while (movingTiles.size() > 0) {
                                                    updateAll(miniGame);
                                                }
                                                //swap the tiles
                                                swap(selectedTile, selectTile);

                                                selectTile.setState(VISIBLE_STATE);
                                                selectedTile.setState(VISIBLE_STATE);
                                                selectedTile = null;
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (move.tilesToRemove != null) {
            processMove(move);
        }
    }

    // OVERRIDDEN METHODS
    // - checkMousePressOnSprites
    // - endGameAsWin
    // - endGameAsLoss
    // - reset
    // - updateAll
    // - updateDebugText
    /**
     * This method provides a custom game response for handling mouse clicks on
     * the game screen. We'll use this to close game dialogs as well as to
     * listen for mouse clicks on grid cells.
     *
     * @param game The zombie crush game.
     *
     * @param x The x-axis pixel location of the mouse click.
     *
     * @param y The y-axis pixel location of the mouse click.
     */
    @Override
    public void checkMousePressOnSprites(MiniGame game, int x, int y) {
        // FIGURE OUT THE CELL IN THE GRID
        int col = calculateGridCellColumn(x);
        int row = calculateGridCellRow(y);

        // CHECK THE TOP OF THE STACK AT col, row
        ArrayList<ZombieCrushSagaTile> tileStack = tileGrid[col][row];
        if (tileStack.size() > 0) {
            // GET AND TRY TO SELECT THE TOP TILE IN THAT CELL, IF THERE IS ONE
            ZombieCrushSagaTile testTile = tileStack.get(tileStack.size() - 1);
            if (testTile.containsPoint(x, y)) {
                selectTile(testTile);
            }
        }
    }

    /**
     * Called when the game is won, it will record the ending game time, update
     * the player record, display the win dialog, and play the win animation.
     */
    @Override
    public void endGameAsWin() {
        // UPDATE THE GAME STATE USING THE INHERITED FUNCTIONALITY
        super.endGameAsWin();

        // RECORD THE TIME IT TOOK TO COMPLETE THE GAME
        long gameTime = endTime.getTimeInMillis() - startTime.getTimeInMillis();

        // RECORD IT AS A WIN
        ((ZombieCrushSagaMiniGame) miniGame).getPlayerRecord().addWin(currentLevel, gameTime, numStars, currScore);
        ((ZombieCrushSagaMiniGame) miniGame).savePlayerRecord();

        // DISPLAY THE WIN DIALOG
        ((ZombieCrushSagaMiniGame) miniGame).switchToLevelScreen();
        miniGame.getGUIDecor().get(WIN_TYPE).setState(VISIBLE_STATE);

        // AND PLAY THE WIN AUDIO
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.ABOUT_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.LEVEL_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.SAGA_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
        miniGame.getAudio().play(ZombieCrushSagaPropertyType.WIN_AUDIO_CUE.toString(), false);
    }

    /**
     * Called when the game is lost, it will update the player record, display
     * the loss dialog, and play the loss sound.
     */
    @Override
    public void endGameAsLoss() {
        // UPDATE THE GAME STATE USING THE INHERITED FUNCTIONALITY
        super.endGameAsLoss();

        // RECORD IT AS A loss
        ((ZombieCrushSagaMiniGame) miniGame).getPlayerRecord().addLoss(currentLevel);
        ((ZombieCrushSagaMiniGame) miniGame).savePlayerRecord();

        // DISPLAY THE loss DIALOG
        ((ZombieCrushSagaMiniGame) miniGame).switchToLevelScreen();
        miniGame.getGUIDecor().get(LOSS_TYPE).setState(VISIBLE_STATE);

        // AND PLAY THE LOSS AUDIO
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.ABOUT_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.LEVEL_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.SAGA_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
        miniGame.getAudio().play(ZombieCrushSagaPropertyType.LOSS_AUDIO_CUE.toString(), false);
    }

    /**
     * update the grid used after move removes tiles and we make more tiles to
     * replace them or if we wanna shuffle things up
     */
    public void updateGrid() {
        //take tiles from above and move them down
        if (playTiles.size() < totNumTiles) {
            ZombieCrushSagaTile tile;
            for (int i = 0; i < gridColumns; i++) {
                for (int j = gridRows - 1; j >= 0; j--) {
                    //needs to be tile there but isnt
                    if (levelGrid[i][j] > 0 && (tileGrid[i][j]).isEmpty()) {
                        //see if you can get the tile that is on top
                        if (j - 1 >= 0) {
                            if (!((tileGrid[i][j - 1]).isEmpty())) {
                                tile = tileGrid[i][j - 1].remove(0);
                                //and move it down one
                                tileGrid[i][j].add(tile);
                                tile.setGridCell(i, j);
                            }
                        }
                    }
                }
            }
            //make more tiles
            moreTiles();
        }

        // NOW LET'S REMOVE THEM FROM THE STACK
        // AND PUT THE TILES IN THE GRID        
        for (int i = 0; i < gridColumns; i++) {
            for (int j = 0; j < gridRows; j++) {
                if (levelGrid[i][j] > 0) {
                    if (levelGrid[i][j] > 1) {
                        //its jelly!
                        jellyCoordinates.add(new Point(i, j));
                    }
                    // TAKE THE TILE OUT OF THE STACK
                    ZombieCrushSagaTile tile = playTiles.get(playTiles.size() - 1);

                    // PUT IT IN THE GRID if there is no tile there already
                    if ((tileGrid[i][j]).isEmpty()) {
                        tileGrid[i][j].add(tile);
                        tile.setGridCell(i, j);

                        // WE'LL ANIMATE IT GOING TO THE GRID, SO FIGURE
                        // OUT WHERE IT'S GOING AND GET IT MOVING
                        float x = calculateTileXInGrid(i, 0);
                        float y = calculateTileYInGrid(j, 0);
                        tile.setTarget(x, y);
                        tile.startMovingToTarget(MAX_TILE_VELOCITY);
                        movingTiles.add(tile);
                    }
                }
            }
        }
        //after updating the grid, see if we have some premade matches!
        selfMatches();
    }

    /**
     * Called when a game is started, the game grid is reset.
     *
     * @param game
     */
    @Override
    public void reset(MiniGame game) {
        // PUT ALL THE TILES IN ONE PLACE AND MAKE THEM VISIBLE
        moveAllTilesToStack();
        //get brand new tiles
        stackTiles = new ArrayList();
        initTiles();

        for (ZombieCrushSagaTile tile : stackTiles) {
            tile.setX(TILE_STACK_X);
            tile.setY(TILE_STACK_Y);
            tile.setState(VISIBLE_STATE);
        }

        // RANDOMLY ORDER THEM
        Collections.shuffle(stackTiles);

        // START THE CLOCK
        startTime = new GregorianCalendar();

        // NOW LET'S REMOVE THEM FROM THE STACK
        // AND PUT THE TILES IN THE GRID        
        for (int i = 0; i < gridColumns; i++) {
            for (int j = 0; j < gridRows; j++) {
                if (levelGrid[i][j] > 0) {
                    if (levelGrid[i][j] > 1) {
                        //its jelly!
                        jellyCoordinates.add(new Point(i, j));
                    }
                    // TAKE THE TILE OUT OF THE STACK
                    ZombieCrushSagaTile tile = stackTiles.remove(stackTiles.size() - 1);

                    // PUT IT IN THE GRID
                    tileGrid[i][j].add(tile);
                    tile.setGridCell(i, j);

                    // WE'LL ANIMATE IT GOING TO THE GRID, SO FIGURE
                    // OUT WHERE IT'S GOING AND GET IT MOVING
                    float x = calculateTileXInGrid(i, 0);
                    float y = calculateTileYInGrid(j, 0);
                    tile.setTarget(x, y);
                    tile.startMovingToTarget(MAX_TILE_VELOCITY);
                    movingTiles.add(tile);
                }
            }
        }
        // AND START ALL UPDATES
        beginGame();

        // CLEAR ANY WIN OR LOSS DISPLAY
        miniGame.getGUIDecor().get(WIN_TYPE).setState(INVISIBLE_STATE);
        miniGame.getGUIDecor().get(LOSS_TYPE).setState(INVISIBLE_STATE);
    }

    /**
     * swap these two tiles on the grid
     * @param t1
     * @param t2 
     */
    public void swap(ZombieCrushSagaTile t1, ZombieCrushSagaTile t2) {
        int x1 = t1.getGridColumn();
        int y1 = t1.getGridRow();
        int x2 = t2.getGridColumn();
        int y2 = t2.getGridRow();

        tileGrid[x1][y1].remove(0);
        tileGrid[x1][y1].add(t2);
        t2.setGridCell(x1, y1);

        tileGrid[x2][y2].remove(0);
        tileGrid[x2][y2].add(t1);
        t1.setGridCell(x2, y2);
    }

    /**
     * go through grid and see if any matches are already made if there are,
     * process them as moves
     */
    public void selfMatches() {
        //make self matches
        ZombieCrushSagaMove move = new ZombieCrushSagaMove();
        ArrayList<ZombieCrushSagaTile> removeTiles;
        ArrayList<ZombieCrushSagaTile> stack1;
        ZombieCrushSagaTile testTile1;
        for (int i = 0; i < gridColumns; i++) {
            for (int j = 0; j < gridRows; j++) {
                stack1 = tileGrid[i][j];
                if (stack1.size() > 0) {
                    // GET THE FIRST TILE
                    testTile1 = stack1.get(0);
                    removeTiles = checkTshape(testTile1);
                    if (removeTiles != null) {
                        move.col1 = i;
                        move.row1 = j;
                        move.col2 = i;
                        move.row2 = j;
                        move.tilesToRemove = removeTiles;
                        move.moveType = T_SHAPE_MOVE;
                        numMovesLeft++;
                        processMove(move);
                    }
                    removeTiles = checkLshape(testTile1);
                    if (removeTiles != null) {
                        move.col1 = i;
                        move.row1 = j;
                        move.col2 = i;
                        move.row2 = j;
                        move.tilesToRemove = removeTiles;
                        move.moveType = L_SHAPE_MOVE;
                        numMovesLeft++;
                        processMove(move);
                    }
                    removeTiles = check5Row(testTile1);
                    if (removeTiles != null) {
                        move.col1 = i;
                        move.row1 = j;
                        move.col2 = i;
                        move.row2 = j;
                        move.tilesToRemove = removeTiles;
                        move.moveType = ROW_5_MOVE;
                        numMovesLeft++;
                        processMove(move);
                    }
                    removeTiles = check4Row(testTile1);
                    if (removeTiles != null) {
                        move.col1 = i;
                        move.row1 = j;
                        move.col2 = i;
                        move.row2 = j;
                        move.tilesToRemove = removeTiles;
                        move.moveType = ROW_4_MOVE;
                        numMovesLeft++;
                        processMove(move);
                    }
                    removeTiles = check3Row(testTile1);
                    if (removeTiles != null) {
                        move.col1 = i;
                        move.row1 = j;
                        move.col2 = i;
                        move.row2 = j;
                        move.tilesToRemove = removeTiles;
                        move.moveType = ROW_3_MOVE;
                        numMovesLeft++;
                        processMove(move);
                    }
                }
            }
        }
    }

    /**
     * Called each frame, this method updates all the game objects.
     *
     * @param game The zombie crush game to be updated.
     */
    @Override
    public void updateAll(MiniGame game) {
        // MAKE SURE THIS THREAD HAS EXCLUSIVE ACCESS TO THE DATA
        try {
            game.beginUsingData();

            // WE ONLY NEED TO UPDATE AND MOVE THE MOVING TILES
            for (int i = 0; i < movingTiles.size(); i++) {
                // GET THE NEXT TILE
                ZombieCrushSagaTile tile = movingTiles.get(i);

                // THIS WILL UPDATE IT'S POSITION USING ITS VELOCITY
                tile.update(game);

                // IF IT'S REACHED ITS DESTINATION, REMOVE IT
                // FROM THE LIST OF MOVING TILES
                if (!tile.isMovingToTarget()) {
                    movingTiles.remove(tile);
                }
            }

            // IF THE GAME IS STILL ON, THE TIMER SHOULD CONTINUE
            if (inProgress()) {
                // KEEP THE GAME TIMER GOING IF THE GAME STILL IS
                endTime = new GregorianCalendar();
            }
        } finally {
            // MAKE SURE WE RELEASE THE LOCK WHETHER THERE IS
            // AN EXCEPTION THROWN OR NOT
            game.endUsingData();
        }
    }

    /**
     * This method is for updating any debug text to present to the screen. In a
     * graphical application like this it's sometimes useful to display data in
     * the GUI.
     *
     * @param game The zombie crush game about which to display info.
     */
    @Override
    public void updateDebugText(MiniGame game) {
    }
}