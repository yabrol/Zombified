package mahjong_solitaire.events;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static mahjong_solitaire.MahjongSolitaireConstants.GAME_SCREEN_STATE;
import mahjong_solitaire.data.MahjongSolitaireDataModel;
import mahjong_solitaire.data.MahjongSolitaireMove;
import mahjong_solitaire.ui.MahjongSolitaireMiniGame;

/**
 * This event handler lets us provide additional custom responses
 * to key presses while Mahjong is running.
 * 
 * @author Richard McKenna, Yukti Abrol
 */
public class MahjongKeyHandler extends KeyAdapter
{
    // THE MAHJONG GAME ON WHICH WE'LL RESPOND
    private MahjongSolitaireMiniGame game;

    /**
     * This constructor simply inits the object by 
     * keeping the game for later.
     * 
     * @param initGame The Mahjong game that contains
     * the back button.
     */    
    public MahjongKeyHandler(MahjongSolitaireMiniGame initGame)
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
      MahjongSolitaireDataModel data = (MahjongSolitaireDataModel)game.getDataModel();
        // CHEAT BY ONE MOVE. NOTE THAT IF WE HOLD THE C
        // KEY DOWN IT WILL CONTINUALLY CHEAT
        if (ke.getKeyCode() == KeyEvent.VK_C)
        {
            // FIND A MOVE IF THERE IS ONE
            MahjongSolitaireMove move = data.findMove();
            if (move != null)
                data.processMove(move);
        }
        //undo keyboard shortcut
        if (ke.getKeyCode() == KeyEvent.VK_U)
        {
          data.undoLastMove();
        }
    }
}