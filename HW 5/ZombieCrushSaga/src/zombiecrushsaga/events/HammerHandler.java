package zombiecrushsaga.events;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;
import static zombiecrushsaga.ZombieCrushSagaConstants.*;

/**
 * This event handler responds to when the user requests to go back. 
 * 
 * @author Yukti Abrol
 * @version 1.0
 */
public class HammerHandler implements ActionListener
{
    // HERE'S THE GAME WE'LL UPDATE
    private ZombieCrushSagaMiniGame game;
    
    /**
     * This constructor just stores the game for later.
     * 
     * @param initGame the game to update
     */
    public HammerHandler(ZombieCrushSagaMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * Here is the event response. This code is executed when
     * the user clicks on the button for starting a new game,
     * which can be done when the application starts up, during
     * a game, or after a game has been played. Note that the game 
     * data is already locked for this thread before it is called, 
     * and that it will be unlocked after it returns.
     * 
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        if (game.getDataModel().inProgress())
        {
            //make mouse into hammer
            BufferedImage image = game.getGUIButtons().get(ZOMBIE_HAMMER_TYPE).getSpriteType().getStateImage(VISIBLE_STATE);
            Cursor curs = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "MyCursor");
            game.getCanvas().setCursor(curs);
        }
    }
}