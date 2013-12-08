package zombiecrushsaga.ui;

import java.util.ArrayList;
import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;
import zombiecrushsaga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import static zombiecrushsaga.ZombieCrushSagaConstants.*;

/**
 * This class represents a single tile in the game world.
 * 
 * @author Yukti Abrol
 */
public class ZombieCrushSagaTile extends Sprite
{
    // EACH TILE IS EITHER AN A, B, OR C TYPE. IT MIGHT
    // BE BEST TO DO THIS WITH AN enum, BUT WE COULD
    // ALSO DO IT THIS WAY, WITH STRING CONSTANTS
    private String tileType;
    private String specTileType;

    // WHEN WE PUT A TILE IN THE GRID WE TELL IT WHAT COLUMN AND ROW
    // IT IS LOCATED TO MAKE THE UNDO OPERATION EASY LATER ON
    private int gridColumn;
    private int gridRow;
    
    // THIS IS true WHEN THIS TILE IS MOVING, WHICH HELPS US FIGURE
    // OUT WHEN IT HAS REACHED A DESTINATION NODE
    private boolean movingToTarget;
    
    // THE TARGET COORDINATES IN WHICH IT IS CURRENTLY HEADING
    private float targetX;
    private float targetY;
    
    // WIN ANIMATIONS CAN BE GENERATED SIMPLY BY PUTTING TILES ON A PATH    
    private ArrayList<Integer> winPath;
    
    // THIS INDEX KEEPS TRACK OF WHICH NODE ON THE WIN ANIMATION PATH
    // THIS TILE IS CURRENTLY TARGETING
    private int winPathIndex;
    
    private int maxVel = 10;
    
    /**
     * This constructor initializes this tile for use, including all the
     * sprite-related data from its ancestor class, Sprite.
     */
    public ZombieCrushSagaTile(    SpriteType initSpriteType,
                                    float initX, 	float initY,
                                    float initVx, 	float initVy,
                                    String initState,   String initTileType, String specialType)
    {
        // SEND ALL THE Sprite DATA TO A Sprite CONSTRUCTOR
        super(initSpriteType, initX, initY, initVx, initVy, initState);
        
        // INIT THE TILE TYPE
        tileType = initTileType;
        specTileType = specialType;
    }
    
    // ACCESSOR METHODS
        // -getTileType
        // -getGridColumn
        // -getGridRow
        // -getTargetX
        // -getTargetY
        // -isMovingToTarget
    
    /**
     * Accessor method for getting this tile type.
     * 
     * @return The tile type for this tile.
     */
    public String getTileType()   
    { 
        return tileType;  
    }
    
    /**
     * returns special type:
     * @return 
     */
    public String getSpecialType()
    {
        return specTileType;
    }
    
    /**
     * Accessor method for getting the tile grid column that this tile
     * is either currently in, or was most recently in.
     * 
     * @return The grid column this tile is or most recently was located in.
     */
    public int getGridColumn() 
    { 
        return gridColumn; 
    }
    
    /**
     * Accessor method for getting the tile grid row that this tile
     * is either currently in, or was most recently in.
     * 
     * @return The grid row this tile is or most recently was located in.
     */
    public int getGridRow() 
    { 
        return gridRow; 
    }
    
    /**
     * Accessor method for getting the x-axis target coordinate for this tile.
     * 
     * @return The x-axis target coordinate for this tile.
     */
    public float getTargetX() 
    { 
        return targetX; 
    }
    
    /**
     * Accessor method for getting the y-axis target coordinate for this tile.
     * 
     * @return The y-axis target coordinate for this teil.
     */
    public float getTargetY() 
    { 
        return targetY; 
    }
    
    /**
     * Accessor method for getting whether this tile is currently moving toward
     * target coordinates or not.
     * 
     * @return true if this tile is currently moving toward target coordinates,
     * false otherwise.
     */
    public boolean isMovingToTarget() 
    { 
        return movingToTarget; 
    }
    
    // MUTATOR METHODS
        // -setGridCell
        // -setTarget
    
    /**
     * Mutator method for setting both the grid column and row that
     * this tile is being placed in.
     * 
     * @param initGridColumn The column this tile is being placed in
     * in the zombie crush game grid.
     * 
     * @param initGridRow The row this tile is being placed in
     * in the zombie crush game grid.
     */
    public void setGridCell(int initGridColumn, int initGridRow)
    {
        gridColumn = initGridColumn;
        gridRow = initGridRow;
    }
    
    /**
     * Mutator method for setting bot the x-axis and y-axis target
     * coordinates for this tile.
     * 
     * @param initTargetX The x-axis target coordinate to move this
     * tile towards.
     * 
     * @param initTargetY The y-axis target coordinate to move this
     * tile towards.
     */
    public void setTarget(float initTargetX, float initTargetY) 
    {
        targetX = initTargetX; 
        targetY = initTargetY;
    }  

    // METHOD FOR MATHING
        // -match
    
    /**
     * This method tests to see if this tile matches the testTile argument
     * TYPE and returns true if they match, false otherwise. A-F
     * 
     * @param testTile The tile to compare this tile to.0
     * 
     * @return true if this tile is a match for the testTile argument,
     * false otherwise.
     */
    public boolean match(ZombieCrushSagaTile testTile)
    {
        //check if same type
      if( this.tileType.equals(testTile.getTileType()))
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    
    /**
     * This tests to make sure this matches exact tile as testTile argument.
     * This is to check if is a special tile
     * 
     * @param testTile
     * @return 
     */
    public String matchTile(ZombieCrushSagaTile testTile)
    {
        //check if identical
          if(this.specTileType.equals(testTile.getSpecialType()))
          {
            return testTile.getSpecialType();
          }
          else
          {
            return null;
          }
    }
         
    // PATHFINDING METHODS
        // -calculateDistanceToTarget
        // -initWinPath
        // -startMovingToTarget
        // -updateWinPath
    
    /**
     * This method calculates the distance from this tile's current location
     * to the target coordinates on a direct line.
     * 
     * @return The total distance on a direct line from where the tile is
     * currently, to where its target is.
     */
    public float calculateDistanceToTarget()
    {
        // GET THE X-AXIS DISTANCE TO GO
        float diffX = targetX - x;
        
        // AND THE Y-AXIS DISTANCE TO GO
        float diffY = targetY - y;
        
        // AND EMPLOY THE PYTHAGOREAN THEOREM TO CALCULATE THE DISTANCE
        float distance = (float)Math.sqrt((diffX * diffX) + (diffY * diffY));
        
        // AND RETURN THE DISTANCE
        return distance;
    }
    
    /**
     * This method builds a path for this tile for the 
     * win animations by slightly randomizing the locations
     * of the nodes in the winPathNodes argument.
     */
    public void initWinPath(ArrayList<Integer> winPathNodes)
    {
        // CONSTRUCT THE PATH
        winPath = new ArrayList(winPathNodes.size());
        for (int i = 0; i < winPathNodes.size(); i+=2)
        {
            // AND FILL IT WITH FUZZY PATH NODES
            int toleranceX = (int)(WIN_PATH_TOLERANCE * Math.random()) - (WIN_PATH_TOLERANCE/2);
            int toleranceY = (int)(WIN_PATH_TOLERANCE * Math.random()) - (WIN_PATH_TOLERANCE/2);
            int x = winPathNodes.get(i) + toleranceX;
            int y = winPathNodes.get(i+1) + toleranceY;
            winPath.add(x);
            winPath.add(y);
        }
    }    
    
    /**
     * Allows the tile to start moving by initializing its properly
     * scaled velocity vector pointed towards it target coordinates.
     * 
     * @param maxVelocity The maximum velocity of this tile, which
     * we'll then compute the x and y axis components for taking into
     * account the trajectory angle.
     */
    public void startMovingToTarget(int maxVelocity)
    {
        // LET ITS POSITIONG GET UPDATED
        movingToTarget = true;
        maxVel = maxVelocity;
        
        // CALCULATE THE ANGLE OF THE TRAJECTORY TO THE TARGET
        float diffX = targetX - x;
        float diffY = targetY - y;
        float tanResult = diffY/diffX;
        float angleInRadians = (float)Math.atan(tanResult);
        
        // COMPUTE THE X VELOCITY COMPONENT
        vX = (float)(maxVelocity * Math.cos(angleInRadians));
        
        // CLAMP THE VELOCTY IN CASE OF NEGATIVE ANGLES
        if ((diffX < 0) && (vX > 0)) vX *= -1;
        if ((diffX > 0) && (vX < 0)) vX *= -1;
        
        // COMPUTE THE Y VELOCITY COMPONENT
        vY = (float)(maxVelocity * Math.sin(angleInRadians));        
        
        // CLAMP THE VELOCITY IN CASE OF NEGATIVE ANGLES
        if ((diffY < 0) && (vY > 0)) vY *= -1;
        if ((diffY > 0) && (vY < 0)) vY *= -1;
    }
    
    /**
     * After a win, while the tiles are animating, this method is called
     * each frame to make sure that when the tile reaches the next node
     * in the path, it moves on to the following path node.
     * 
     * @param game zombie crush game we are updating.
     */
    public void updateWinPath(MiniGame game)
    {
        // IS THE TILE ALMOST AT THE PATH NODE IT'S TARGETING?
        if (calculateDistanceToTarget() < maxVel)
        {
            // PUT IT RIGHT ON THE NODE
            x = targetX;
            y = targetY;
            
            // AND TARGET THE NEXT NODE IN THE PATH
            targetX = winPath.get(winPathIndex);
            targetY = winPath.get(winPathIndex+1);
            
            // START THE TILE MOVING AGAIN AND RANDOMIZE IT'S SPEED
            startMovingToTarget((int)(Math.random() * maxVel) + 1);
            
            // AND ON TO THE NEXT PATH FOR THE NEXT TIME WE PICK A TARGET
            winPathIndex += 2;
            winPathIndex %= (WIN_PATH_NODES * 2);
        }
        // JUST A NORMAL PATHING UPDATE
        else
        {
            // THIS WILL SIMPLY UPDATE THIS TILE'S POSITION USING ITS CURRENT VELOCITY
            super.update(game);
        }        
    }    
    
    // METHODS OVERRIDDEN FROM Sprite
        // -update

    /**
     * Called each frame, this method ensures that this tile is updated
     * according to the path it is on.
     * 
     * @param game The zombie crush game this tile is part of.
     */
    @Override
    public void update(MiniGame game)
    {
        // IF WE ARE IN A POST-WIN STATE WE ARE PLAYING THE WIN
        // ANIMATION, SO MAKE SURE THIS TILE FOLLOWS THE PATH
        if (game.getDataModel().won())
        {
            updateWinPath(game);
        }
        // IF NOT, IF THIS TILE IS ALMOST AT ITS TARGET DESTINATION,
        // JUST GO TO THE TARGET AND THEN STOP MOVING
        else if (calculateDistanceToTarget() < maxVel)
        {
            vX = 0;
            vY = 0;
            x = targetX;
            y = targetY;
            movingToTarget = false;
        }
        // OTHERWISE, JUST DO A NORMAL UPDATE, WHICH WILL CHANGE ITS POSITION
        // USING ITS CURRENT VELOCITY.
        else
        {
            super.update(game);
        }
    }
}