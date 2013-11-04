package jotto.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This class serves as a cheat code handler, letting us, 
 * the developers, toggle settings as we like via key
 * press combinations in order to test different behavior
 * during development.
 * 
 * @author Richard McKenna
 */
public class CheatKeyHandler extends KeyAdapter
{
    // THE UI HAS EVERYTHING WE NEED
    private JottoUI ui;

    /**
     * This constructor keeps the ui for later, when this object
     * responds to keyboard presses.
     * 
     * @param initUI 
     */
    public CheatKeyHandler(JottoUI initUI)
    {
        // WE'LL NEED THIS WHEN WE RESPOND TO OUR CHEATS
        ui = initUI;
    }

    /**
     * This method provides responses to the user pressing keys
     * on the keyboard. Note that we can respond to different
     * keyboard combinations in different ways.
     * 
     * @param ke This contains information about the key press,
     * including which key was pressed and whether other modifiers,
     * like the control key, are pressed as well.
     */
    @Override
    public void keyPressed(KeyEvent ke)
    {
        // GET THE KEY THAT WAS PRESSED IN ASSOCIATION WITH
        // THIS METHOD CALL.
        int keyCode = ke.getKeyCode();

        // IS CONTROL-C PRESSED?
        if ((keyCode == KeyEvent.VK_C) 
                && ke.isControlDown())
        {
            // A CHEAT TO DISPLAY THE SECRET WORD
            ui.displaySecretWord();
        }
    }
}