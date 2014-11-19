/*
 * This event handler responds to when the user wants to play. 
 * Goes to saga screen. 
 * 
 * @author Yukti Abrol
 * @version 1.0
 */
package zombiecrushsaga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;

/**
 *
 * @author Yukti
 */
public class PlayGameHandler implements ActionListener{
    // HERE'S THE GAME WE'LL UPDATE
    private ZombieCrushSagaMiniGame game;
    
  /**
     * This constructor just stores the game for later.
     * 
     * @param initGame the game to update
     */
    public PlayGameHandler(ZombieCrushSagaMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * Here is the event response. This code is executed when
     * the user clicks on the button for starting a game,
     * which can be done when the application starts up. If a previous game 
     * has been started, it loads that game. Else, it starts a new game.
     * Note that the game data is already locked for this thread before 
     * it is called, and that it will be unlocked after it returns.
     * 
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        // IF THERE IS A GAME UNDERWAY, load level progress
        if(game.getDataModel().inProgress())
        {
            game.switchToGameScreen();
        }
//        if (game.getPlayerRecord()!= null)
//        {
//            game.getDataModel();
//        }
        // RESET THE GAME AND ITS DATA--maybe?
        //game.reset();
        else
        {
            //go to saga screen
            game.switchToSagaScreen();
        }
        
    }
}
