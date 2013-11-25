package zombiecrushsaga.data;

import java.util.ArrayList;
import zombiecrushsaga.ui.ZombieCrushSagaTile;

/**
 * This class represents a single move. It stores the grid locations
 * of the two tiles being matched.
 * 
 * @author Yukti Abrol
 */
public class ZombieCrushSagaMove
{
    //where it is
    public int col1;
    public int row1;
    //where to move it
    public int col2;
    public int row2;
    
    public ArrayList<ZombieCrushSagaTile> tilesToRemove;
}
