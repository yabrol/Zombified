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
        str += "level num" + levelNumber + "\n";
        str += "numMoves" + numMoves + "\n";
        str += "star1Score" + star1Score + "\n";
        str += "star2Score" + star2Score+ "\n";
        str += "star3Score" + star3Score+ "\n";
        str += "totTiles" + totTiles+ "\n";
        str += "additionalReq" + additionalReq;
        return str;
    }
}
