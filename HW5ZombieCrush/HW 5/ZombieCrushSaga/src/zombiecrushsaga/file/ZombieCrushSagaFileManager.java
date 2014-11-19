package zombiecrushsaga.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import zombiecrushsaga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import zombiecrushsaga.data.ZombieCrushLevelRecord;
import zombiecrushsaga.data.ZombieCrushSagaDataModel;
import zombiecrushsaga.data.ZombieCrushSagaRecord;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;
import properties_manager.PropertiesManager;

/**
 * This class provides services for efficiently loading and saving
 * binary files for the zombie crush game application.
 * 
 * @author Yukti Abrol
 */
public class ZombieCrushSagaFileManager
{
    // WE'LL LET THE GAME KNOW WHEN DATA LOADING IS COMPLETE
    private ZombieCrushSagaMiniGame miniGame;
    
    /**
     * Constructor for initializing this file manager, it simply keeps
     * the game for later.
     * 
     * @param initMiniGame The game for which this class loads data.
     */
    public ZombieCrushSagaFileManager(ZombieCrushSagaMiniGame initMiniGame)
    {
        // KEEP IT FOR LATER
        miniGame = initMiniGame;
    }

    /**
     * This method loads the contents of the levelFile argument so that
     * the player may then play that level. 
     * 
     * @param levelFile Level to load.
     */
    public void loadLevel(String levelFile)
    {
        // LOAD THE RAW DATA SO WE CAN USE IT
        // OUR LEVEL FILES WILL HAVE THE DIMENSIONS FIRST,
        // FOLLOWED BY THE GRID VALUES
        try
        {
            File fileToOpen = new File(levelFile);

            // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
            // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
            // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
            byte[] bytes = new byte[Long.valueOf(fileToOpen.length()).intValue()];
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            FileInputStream fis = new FileInputStream(fileToOpen);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            // HERE IT IS, THE ONLY READY REQUEST WE NEED
            bis.read(bytes);
            bis.close();
            
            // NOW WE NEED TO LOAD THE DATA FROM THE BYTE ARRAY
            DataInputStream dis = new DataInputStream(bais);
            
            // NOTE THAT WE NEED TO LOAD THE DATA IN THE SAME
            // ORDER AND FORMAT AS WE SAVED IT
            
            // FIRST READ THE GRID DIMENSIONS
            int initGridColumns = dis.readInt();
            int initGridRows = dis.readInt();
            int[][] newGrid = new int[initGridColumns][initGridRows];
            
            // AND NOW ALL THE CELL VALUES
            for (int i = 0; i < initGridColumns; i++)
            {                        
                for (int j = 0; j < initGridRows; j++)
                {
                    newGrid[i][j] = dis.readInt();
                }
            }
            
            // EVERYTHING WENT AS PLANNED SO LET'S MAKE IT PERMANENT
            ZombieCrushSagaDataModel dataModel = (ZombieCrushSagaDataModel)miniGame.getDataModel();
            dataModel.initLevelGrid(newGrid, initGridColumns, initGridRows);
            dataModel.setCurrentLevel(levelFile);
            
            miniGame.updateBoundaries();
        }
        catch(Exception e)
        {
            // LEVEL LOADING ERROR
            miniGame.getErrorHandler().processError(ZombieCrushSagaPropertyType.LOAD_LEVEL_ERROR, e.toString());
        }
    }    
    
    /**
     * This method loads the player record from the records file
     * so that the user may view stats.
     * 
     * @return The fully loaded record from the player record file.
     */
    public ZombieCrushSagaRecord loadRecord()
    {
        ZombieCrushSagaRecord recordToLoad = new ZombieCrushSagaRecord();
        
        // LOAD THE RAW DATA SO WE CAN USE IT
        // OUR LEVEL FILES WILL HAVE THE DIMENSIONS FIRST,
        // FOLLOWED BY THE GRID VALUES
        try
        {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);
            String recordPath = dataPath + props.getProperty(ZombieCrushSagaPropertyType.RECORD_FILE_NAME);
            File fileToOpen = new File(recordPath);

            // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
            // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
            // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
            byte[] bytes = new byte[Long.valueOf(fileToOpen.length()).intValue()];
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            FileInputStream fis = new FileInputStream(fileToOpen);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            // HERE IT IS, THE ONLY READY REQUEST WE NEED
            bis.read(bytes);
            bis.close();
            
            // NOW WE NEED TO LOAD THE DATA FROM THE BYTE ARRAY
            DataInputStream dis = new DataInputStream(bais);
            
            // NOTE THAT WE NEED TO LOAD THE DATA IN THE SAME
            // ORDER AND FORMAT AS WE SAVED IT
            // FIRST READ THE NUMBER OF LEVELS
            int numLevels = dis.readInt();

            for (int i = 0; i < numLevels; i++)
            {
                String levelName = dis.readUTF();
                ZombieCrushLevelRecord rec = new ZombieCrushLevelRecord();
                rec.gamesPlayed = dis.readInt();
                rec.wins = dis.readInt();
                rec.losses = dis.readInt();
                rec.fastestTime = dis.readLong();
                rec.numStars = dis.readInt();
                rec.highScore = dis.readInt();
                recordToLoad.addZombieCrushLevelRecord(levelName, rec);
            }
        }
        catch(Exception e)
        {
            // THERE WAS NO RECORD TO LOAD, SO WE'LL JUST RETURN AN
            // EMPTY ONE AND SQUELCH THIS EXCEPTION
        }        
        return recordToLoad;
    }
    
    public ArrayList<ZombieCrushLevelRequirements> getAllLevelRequirements()
    {
        ArrayList<ZombieCrushLevelRequirements> allReqs = new ArrayList();
        
        //load raw data
        try{
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);
            String recordPath = dataPath + props.getProperty(ZombieCrushSagaPropertyType.LEVEL_REQUIREMENTS);
            File fileToOpen = new File(recordPath);

            BufferedReader readBuffer = new BufferedReader(new FileReader(fileToOpen));
            String str, s;
            String splitarray[];
            
            while((str=readBuffer.readLine())!=null)
            {
                ZombieCrushLevelRequirements levReq = new ZombieCrushLevelRequirements();
                splitarray = str.split("\t");
                //level
                s = splitarray[0];
                levReq.levelNumber = Integer.parseInt(s);
                //moves
                s = splitarray[1];
                levReq.numMoves = Integer.parseInt(s);
                //1 star
                s = splitarray[2];
                levReq.star1Score = Integer.parseInt(s);
                //2 star
                s = splitarray[3];
                levReq.star2Score = Integer.parseInt(s);
                //3 star
                s = splitarray[4];
                levReq.star3Score = Integer.parseInt(s);
                //tot tiles
                s = splitarray[5];
                levReq.totTiles = Integer.parseInt(s);
                //additional
                s = splitarray[6];
                levReq.additionalReq = s;
                //jelly
                if(s.equalsIgnoreCase("Clear All Jelly"))
                    levReq.jelly = true;
                else
                    levReq.jelly = false;
                
                allReqs.add(levReq);   
            }
            readBuffer.close();
        }
        catch(IOException | NumberFormatException e)
        {
            // LEVEL LOADING ERROR
            miniGame.getErrorHandler().processError(ZombieCrushSagaPropertyType.LOAD_LEVEL_REQS_ERROR, e.toString());
        }
        return allReqs;
    }
}