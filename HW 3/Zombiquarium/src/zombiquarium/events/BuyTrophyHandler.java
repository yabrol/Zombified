package zombiquarium.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import zombiquarium.Zombiquarium;
import zombiquarium.ZombiquariumDataModel;

/**
 * This event handler responds to when the player clicks on the button for
 * buying a Trophy, which should effectively end the game.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class BuyTrophyHandler implements ActionListener
{
    // HERE'S THE GAME WE'LL UPDATE
    private Zombiquarium game;

    /**
     * This constructor just stores the game for later.
     *
     * @param initGame the game to update
     */
    public BuyTrophyHandler(Zombiquarium initGame)
    {
        game = initGame;
    }

    /**
     * Here is the event response. This code is executed when the user clicks on
     * the button for buying the trophy, which is how the user wins the game.
     * Note that the game data is already locked for this thread before it is
     * called, and that it will be unlocked after it returns.
     *
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        // GET THE GAME'S DATA MODEL, WHICH IS ALREADY LOCKED FOR US
        ZombiquariumDataModel data = (ZombiquariumDataModel) game.getDataModel();

        // UPDATE THE DATA
        data.buyTrophy();
    }
}