package zombiquarium;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import javax.swing.JPanel;

import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;
import static zombiquarium.Zombiquarium.*;

/**
 * This is where all rendering for the Zombiquarium game application will be
 * done. This includes all rendering of the game background, GUI controls, game
 * sprites, and even debugging text.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class ZombiquariumPanel extends JPanel
{
    // THIS IS ACTUALLY OUR Zombiquarium APP, WE NEED THIS
    // BECAUSE IT HAS THE GUI STUFF THAT WE NEED TO RENDER
    private MiniGame game;
    
    // AND HERE IS ALL THE GAME DATA THAT WE NEED TO RENDER
    private ZombiquariumDataModel data;

    /**
     * This constructor stores the game and data references, which we'll need
     * for rendering.
     *
     * @param initGame the Zombiquarium game that is using this panel for
     * rendering.
     *
     * @param initData the Zombiquarium game data.
     */
    public ZombiquariumPanel(MiniGame initGame, ZombiquariumDataModel initData)
    {
        game = initGame;
        data = initData;
    }

    /**
     * This is where rendering starts. Note that the order of rendering is of
     * particular importance, since things drawn first will be on the bottom,
     * and things rendered last will be on top.
     *
     * @param g the Graphics context for this panel. Draw commands given through
     * g will render things onto this panel.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        renderToGraphicsContext(g);
    }

    /**
     * This method does the actual rendering. I put this in a separate method
     * because it allows us to render content to an image as well as to this
     * panel.
     *
     * @param g Graphics context to render to.
     */
    public void renderToGraphicsContext(Graphics g)
    {
        // RENDER THE FISH TANK
        renderBackground(g);

        // AND THE SUNS, BRAINS, AND ZOMBIES
        renderGameSprites(g);

        // AND THE BUTTONS AND DECOR
        renderGUIControls(g);

        // NOW THE CHANGING NUMBER IN THE GUI
        renderStats(g);

        // AND FINALLY, TEXT FOR DEBUGGING
        renderDebuggingText(g);
    }

    /**
     * Renders the fishtank background image.
     *
     * @param g the Graphics context of this panel.
     */
    public void renderBackground(Graphics g)
    {
        Sprite bg = game.getGUIDecor().get(BACKGROUND_TYPE);
        renderSprite(g, bg);
    }

    /**
     * Renders all the game sprites, including Zombies, Brains, and Suns, in
     * that order.
     *
     * @param g the Graphics context of this panel.
     */
    public void renderGameSprites(Graphics g)
    {
        // ZOMBIES ARE DRAWN FIRST BECAUSE THEY SHOW UP UNDER BRAINS AND SUNS
        Iterator<Sprite> zombiesIt = data.getZombiesIterator();
        renderSprites(g, zombiesIt);

        // BRAINS APPEAR SECOND
        Iterator<Sprite> brainsIt = data.getBrainsIterator();
        renderSprites(g, brainsIt);

        // AND SUNS LAST
        Iterator<Sprite> sunsIt = data.getSunsIterator();
        renderSprites(g, sunsIt);
    }

    /**
     * Renders all the Sprites produced by the spritesIt argument, in order,
     * into g's Graphics context.
     *
     * @param g this panel's rendering context.
     *
     * @param spritesIt an Iterator that can access all the Sprites for
     * rendering, one at a time.
     */
    public void renderSprites(Graphics g,
            Iterator<Sprite> spritesIt)
    {
        while (spritesIt.hasNext())
        {
            Sprite spriteToRender = spritesIt.next();
            renderSprite(g, spriteToRender);
        }
    }

    /**
     * Renders all the GUI decor and buttons.
     *
     * @param g this panel's rendering context.
     */
    public void renderGUIControls(Graphics g)
    {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> decorSprites = game.getGUIDecor().values();
        for (Sprite s : decorSprites)
        {
            // MAKE SURE NOT TO RENDERING THE BACKGROUND
            // ON TOP OF EVERYTHING ELSE
            if (!s.getSpriteType().getSpriteTypeID().equals(BACKGROUND_TYPE))
            {
                renderSprite(g, s);
            }
        }

        // AND NOW RENDERING THE BUTTONS
        Collection<Sprite> buttonSprites = game.getGUIButtons().values();
        for (Sprite s : buttonSprites)
        {
            renderSprite(g, s);
        }
    }

    /**
     * This method renders the on-screen stats that change as the game
     * progresses. This means things like the amount of sun the player has
     * accumulated and how close the player is to winning in the progress bar.
     *
     * @param g the Graphics context for this panel
     */
    public void renderStats(Graphics g)
    {
        // FIRST THE SUN STATS TOP LEFT
        String sunText = "" + data.getCurrentSun();
        g.setFont(SUN_FONT);
        g.setColor(SUN_TEXT_COLOR);
        int width = (g.getFontMetrics(SUN_FONT)).stringWidth(sunText);
        int centerX = 40;
        int x = centerX - (width / 2);
        int y = 76;
        g.drawString(sunText, x, y);

        // AND THEN THE PROGRESS BAR ON THE BOTTOM

        // FIRST THE ACTUAL BAR
        int progX = (int) game.getGUIDecor().get(PROGRESS_TYPE).getX();
        int progY = (int) game.getGUIDecor().get(PROGRESS_TYPE).getY();
        float barHeight = PROGRESS_BAR_CORNERS.bottom - PROGRESS_BAR_CORNERS.top;
        float barPercentage = ((float) data.getCurrentSun() / (float) COST_OF_TROPHY);
        float barWidth = barPercentage * (PROGRESS_BAR_CORNERS.right - PROGRESS_BAR_CORNERS.left);
        int barX = progX + PROGRESS_BAR_CORNERS.left;
        int barY = progY + PROGRESS_BAR_CORNERS.top;
        g.setColor(PROGRSS_BAR_COLOR);
        g.fillRect(barX, barY, (int) barWidth, (int) barHeight);

        // AND THEN THE TEXT ON THE PROGRESS BAR
        String progressText = data.getCurrentSun() + "/" + COST_OF_TROPHY + " Sun";
        g.setFont(PROGRESS_METER_FONT);
        g.setColor(PROGRESS_METER_TEXT_COLOR);
        x = data.getGameWidth() - 100;
        y = data.getGameHeight() - 10;
        g.drawString(progressText, x, y);
    }

    /**
     * Renders the s Sprite into the Graphics context g. Note that each Sprite
     * knows its own x,y coordinate location.
     *
     * @param g the Graphics context of this panel
     *
     * @param s the Sprite to be rendered
     */
    public void renderSprite(Graphics g, Sprite s)
    {
        if (!s.getState().equals(INVISIBLE_STATE))
        {
            SpriteType bgST = s.getSpriteType();
            Image img = bgST.getStateImage(s.getState());
            g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);
        }
    }

    /**
     * Renders the debugging text to the panel. Note that the rendering will
     * only actually be done if data has activated debug text rendering.
     *
     * @param g the Graphics context for this panel
     */
    public void renderDebuggingText(Graphics g)
    {
        if (data.isDebugTextRenderingActive())
        {
            g.setFont(DEBUGGING_TEXT_FONT);
            g.setColor(debugTextColor);
            Iterator<String> it = data.getDebugText().iterator();
            int x = data.getDebugTextX();
            int y = data.getDebugTextY();
            while (it.hasNext())
            {
                try
                {
                    String text = it.next();
                    g.drawString(text, x, y);
                    y += 20;
                } catch (ConcurrentModificationException cme)
                {
                    // A RACE CONDITION HAS OCCURRED, BUT
                    // SINCE THIS IS JUST DISPLAYING SOMETHING
                    // THAT IS NOT OF CONSEQUENCE, WE'LL IGNORE
                    // IT
                    System.out.println("RACE CONDITION, DO YOU KNOW WHY?");
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie)
                    {
                    }
                }
            }
        }
    }
}