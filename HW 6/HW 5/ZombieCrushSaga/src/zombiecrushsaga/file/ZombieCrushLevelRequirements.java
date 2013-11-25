package zombiecrushsaga.file;

/**
 * this class holds the requirements for each level
 * @author yukti
 */
public class ZombieCrushLevelRequirements {
    public int levelNumber;
    public int numMoves;
    public int star1Score;
    public int star2Score;
    public int star3Score;
    public String additionalReq;
    public int totTiles;
    
    @Override
    public String toString()
    {
        String str = "";
        str += "level num" + levelNumber;
        str += "numMoves" + numMoves;
        str += "star1Score" + star1Score;
        str += "star2Score" + star2Score;
        str += "star3Score" + star3Score;
        str += "totTiles" + totTiles;
        str += "additionalReq" + additionalReq;
        return str;
    }
}
