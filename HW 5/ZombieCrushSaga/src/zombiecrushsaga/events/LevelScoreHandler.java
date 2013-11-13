package zombiecrushsaga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;

/**
 * This event handler responds to when the user clicks on the Stats
 * button, which triggers displaying the stats dialog.
 * 
 * @author Yukti Abrol
 */
public class LevelScoreHandler implements ActionListener
{
    // THE zombie crush GAME CONTAINING THE UNDO BUTTON
    private ZombieCrushSagaMiniGame miniGame;
    
    /**
     * This constructor simply inits the object by 
     * keeping the game for later.
     * 
     * @param initGame The zombie crush game that contains
     * the back button.
     */
    public LevelScoreHandler(ZombieCrushSagaMiniGame initMiniGame)
    {
        miniGame = initMiniGame;
    }

    /**
     * This method provides the response, which is simply to
     * display the game stats in a dialog. We let the game
     * take care of implementing how.
     * 
     * @param ae Event object.
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        // DISPLAY THE STATS
        miniGame.displayStats();
    }
}