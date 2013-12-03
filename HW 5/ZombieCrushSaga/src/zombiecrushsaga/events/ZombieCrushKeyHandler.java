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
      //check if level 1
      if(data.getCurrentLevel().equals("./data/./zomcrush/Level1.zom"))
      {
          level1Cheats(ke);
      }
      
    }
    
    private void level1Cheats(KeyEvent ke)
    {
        //press 1 for 3 row cheat
        if (ke.getKeyCode() == KeyEvent.VK_1)
        {
            // FIND A MOVE IF THERE IS ONE
//            ZombieCrushSagaMove move = data.findMove();
//            if (move != null)
//                data.processMove(move);
        }
        //4 row cheat
        if (ke.getKeyCode() == KeyEvent.VK_2)
        {
            // FIND A MOVE IF THERE IS ONE
//            ZombieCrushSagaMove move = data.findMove();
//            if (move != null)
//                data.processMove(move);
        }
        //5 row cheat
        if (ke.getKeyCode() == KeyEvent.VK_3)
        {
            // FIND A MOVE IF THERE IS ONE
//            ZombieCrushSagaMove move = data.findMove();
//            if (move != null)
//                data.processMove(move);
        }
        //T cheat
        if (ke.getKeyCode() == KeyEvent.VK_4)
        {
            // FIND A MOVE IF THERE IS ONE
//            ZombieCrushSagaMove move = data.findMove();
//            if (move != null)
//                data.processMove(move);
        }
        //L cheat
        if (ke.getKeyCode() == KeyEvent.VK_5)
        {
            // FIND A MOVE IF THERE IS ONE
//            ZombieCrushSagaMove move = data.findMove();
//            if (move != null)
//                data.processMove(move);
        }
        //double combo cheat
        if (ke.getKeyCode() == KeyEvent.VK_6)
        {
            // FIND A MOVE IF THERE IS ONE
//            ZombieCrushSagaMove move = data.findMove();
//            if (move != null)
//                data.processMove(move);
        }
    }
}