package zombiecrushsaga.data;

/**
 * This class stores game results for a given level. Note that this is
 * just a data holding class. It will be manipulated fully by the
 * zombiecrushsagaRecord class, which stores all the records and manages
 * loading and saving.
 * 
 * @author Yukti Abrol
 */
public class ZombieCrushLevelRecord
{
    public int gamesPlayed;
    public int wins;
    public int losses;
    public long fastestTime;
}
