package zombiecrushsaga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;

/**
 * This class manages when the user clicks the window X to
 * kill the application.
 * 
 * @author Yukti Abrol
 */
public class QuitGameHandler extends WindowAdapter implements ActionListener
{
    private ZombieCrushSagaMiniGame miniGame;
    
    public QuitGameHandler(ZombieCrushSagaMiniGame initMiniGame)
    {
        miniGame = initMiniGame;
    }
    
    /**
     * This method is called when the user clicks the window'w X. We 
     * respond by giving the player a loss if the game is still going on.
     * 
     * @param we Window event object.
     */
    @Override
    public void windowClosing(WindowEvent we)
    {
        // IF THE GAME IS STILL GOING ON, END IT AS A LOSS
        if (miniGame.getDataModel().inProgress())
        {
            miniGame.getDataModel().endGameAsLoss();
        }
        // AND CLOSE THE ALL
        System.exit(0);
    }
    
    /**
     * Here is the event response. This code is executed when
     * the user clicks on the button for quitting game,
     * which can be done when the application starts up, 
     * or on the saga screen. Note that the game 
     * data is already locked for this thread before it is called, 
     * and that it will be unlocked after it returns.
     * 
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        // IF THERE IS A GAME UNDERWAY, COUNT IT AS A LOSS
        if (miniGame.getDataModel().inProgress())
        {
            miniGame.getDataModel().endGameAsLoss();
        }
        System.exit(0);
    }
    
}