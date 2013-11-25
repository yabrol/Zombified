package zombiecrushsaga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import properties_manager.PropertiesManager;
import zombiecrushsaga.ZombieCrushSaga;
import static zombiecrushsaga.ZombieCrushSagaConstants.*;
import zombiecrushsaga.data.ZombieCrushSagaDataModel;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;

/**
 * This event handler responds to when the user requests to
 * scroll up on the saga screen. 
 * 
 * @author Yukti Abrol
 * @version 1.0
 */
public class ScrollUpHandler implements ActionListener{
    
    // HERE'S THE GAME WE'LL UPDATE
    private ZombieCrushSagaMiniGame game;
    
    /**
     * This constructor just stores the game for later.
     * 
     * @param initGame the game to update
     */
    public ScrollUpHandler(ZombieCrushSagaMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * Here is the event response. This code is executed when
     * the user clicks on the button for resetting the game,
     * which can be done when the application starts up. Note that the game 
     * data is already locked for this thread before it is called, 
     * and that it will be unlocked after it returns.
     * 
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSaga.ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
        //if at saga background 9 cant go up anymore
        if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_1_STATE))
        {
            for (String level : levels) {
                game.getGUIButtons().get(level).setState(VISIBLE_STATE);
                game.getGUIButtons().get(level).setEnabled(true);
            }
            //game.getGUIButtons().get(DOWN_BUTTON_TYPE).setEnabled(false);
            game.getGUIDecor().get(BACKGROUND_TYPE).setState(SAGA_SCREEN_2_STATE);
            for (String level : levels) {
                game.getGUIButtons().get(level).setState(INVISIBLE_STATE);
                game.getGUIButtons().get(level).setEnabled(false);
                }
        }
        else{
            for (String level : levels) {
                game.getGUIButtons().get(level).setState(INVISIBLE_STATE);
                game.getGUIButtons().get(level).setEnabled(false);
                }
            //game.getGUIButtons().get(DOWN_BUTTON_TYPE).setEnabled(true);
            if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_2_STATE))
            {
                game.getGUIDecor().get(BACKGROUND_TYPE).setState(SAGA_SCREEN_3_STATE);
            }
            else if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_3_STATE))
            {
                game.getGUIDecor().get(BACKGROUND_TYPE).setState(SAGA_SCREEN_4_STATE);
            }
            else if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_4_STATE))
            {
                game.getGUIDecor().get(BACKGROUND_TYPE).setState(SAGA_SCREEN_5_STATE);
            }
            else if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_5_STATE))
            {
                game.getGUIDecor().get(BACKGROUND_TYPE).setState(SAGA_SCREEN_6_STATE);
            }
            else if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_6_STATE))
            {
                game.getGUIDecor().get(BACKGROUND_TYPE).setState(SAGA_SCREEN_7_STATE);
            }
            else if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_7_STATE))
            {
                game.getGUIDecor().get(BACKGROUND_TYPE).setState(SAGA_SCREEN_8_STATE);
            }
            else if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_8_STATE))
            {
                game.getGUIDecor().get(BACKGROUND_TYPE).setState(SAGA_SCREEN_9_STATE);
            }
            else if(game.getGUIDecor().get(BACKGROUND_TYPE).getState().equals(SAGA_SCREEN_9_STATE))
            {
                return;
            }
        }
    }
  
}
