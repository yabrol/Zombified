package zombiecrushsaga.events;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import zombiecrushsaga.data.ZombieCrushSagaDataModel;
import zombiecrushsaga.data.ZombieCrushSagaMove;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;

/**
 * This event handler lets us provide additional custom responses
 * to key presses while Mahjong is running.
 * 
 * @author Richard McKenna, Yukti Abrol
 */
public class ZombieCrushKeyHandler extends KeyAdapter
{
    // THE MAHJONG GAME ON WHICH WE'LL RESPOND
    private ZombieCrushSagaMiniGame game;

    /**
     * This constructor simply inits the object by 
     * keeping the game for later.
     * 
     * @param initGame The Mahjong game that contains
     * the back button.
     */    
    public ZombieCrushKeyHandler(ZombieCrushSagaMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * This method provides a custom game response to when the user
     * presses a keyboard key.
     * 
     * @param ke Event object containing information about the event,
     * like which key was pressed.
     */
    @Override
    public void keyPressed(KeyEvent ke)
    {
      ZombieCrushSagaDataModel data = (ZombieCrushSagaDataModel)game.getDataModel();
        // CHEAT BY ONE MOVE. NOTE THAT IF WE HOLD THE C
        // KEY DOWN IT WILL CONTINUALLY CHEAT
        if (ke.getKeyCode() == KeyEvent.VK_C)
        {
            // FIND A MOVE IF THERE IS ONE
            ZombieCrushSagaMove move = data.findMove();
            if (move != null)
                data.processMove(move);
        }
    }
}