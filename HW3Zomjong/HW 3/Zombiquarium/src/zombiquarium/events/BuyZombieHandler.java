package zombiquarium.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import zombiquarium.Zombiquarium;
import zombiquarium.ZombiquariumDataModel;

/**
 * This event handler responds to when the player clicks on the button for
 * buying a Zombie, which should spawn a new one and deduct the cost.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class BuyZombieHandler implements ActionListener
{
    // HERE'S THE GAME WE'LL UPDATE
    private Zombiquarium game;

    /**
     * This constructor just stores the game for later.
     *
     * @param initGame the game to update
     */
    public BuyZombieHandler(Zombiquarium initGame)
    {
        game = initGame;
    }

    /**
     * Here is the event response. This code is executed when the user clicks on
     * the button for making a new zombie. This method tells the data management
     * class to add a new randomly placed zombie. Note that the game data is
     * already locked for this thread before it is called, and that it will be
     * unlocked after it returns.
     *
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        // GET THE GAME'S DATA, WHICH IS ALREADY LOCKED FOR US
        ZombiquariumDataModel data = (ZombiquariumDataModel) game.getDataModel();

        // AND BUY ONE, WHICH WILL SPAWN A NEW ONE
        data.buyZombie(game);
    }
}