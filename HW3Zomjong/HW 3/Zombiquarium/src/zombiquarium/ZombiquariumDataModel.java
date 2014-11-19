package zombiquarium;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import mini_game.MiniGame;
import mini_game.MiniGameDataModel;
import mini_game.Sprite;
import mini_game.SpriteType;

import zombiquarium.sprites.Zombie;
import static zombiquarium.Zombiquarium.*;

/**
 * This class manages the game data for the Zombiquarium game application. Note
 * that this game is built using the MiniGame Framework as its base. This class
 * contains methods for managing game data and their states.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class ZombiquariumDataModel extends MiniGameDataModel
{
    // AMOUNT OF SUN THE PLAYER CURRENTLY HAS
    private int currentSun;
    
    // HERE ARE THE SPRITES CURRENTLY ACTIVE IN THE GAME
    private Vector<Sprite> zombies;
    private Vector<Sprite> brains;
    private Vector<Sprite> suns;
    
    // EACH FRAME, WE MAY NEED TO REMOVE BRAINS AND ZOMBIES DURING
    // THE UPDATE OPERATION AND WE DO SO WITH AN ITERATOR, 
    // SO WE'LL PUT THOSE TO BE REMOVED HERE AND REMOVE THEM ALL 
    // AT ONCE AT THE END OF THE FRAME, THIS HELPS PREVENT PROBLEMS
    // WHERE WE REMOVE SOMETHING WHILE ITERATING THROUGH A DATA
    // STRUCTURE'S ITERATOR, RUINING THAT ITERATOR
    private Vector<Sprite> brainsToRemove;
    private Vector<Sprite> zombiesToRemove;

    /**
     * Default constructor, it initializes all data structures for managing the
     * Sprites and sets the sun to the STARTING_SUN value, as specified in
     * Zombiquarium.
     */
    public ZombiquariumDataModel()
    {
        // INITIAL VALUE
        currentSun = STARTING_SUN;

        // INIT THE DATA STRUCTURES FOR ACTIVE SPRITES
        zombies = new Vector();
        brains = new Vector();
        suns = new Vector();

        // AND THE ONES FOR KEEPEING TRACK OF REMOVALS
        brainsToRemove = new Vector();
        zombiesToRemove = new Vector();
    }

    // ACCESSOR METHODS
        // getClickedSun
        // getCurrentSun
        // getNumBrains
        // getNumSuns
        // getNumZombies
        // getBrainsIterator
        // getSunsIterator
        // getZombiesIterator
    
    /**
     * This accessor method looks through the active suns and finds the first
     * one that has the x,y point inside its bounding box, returning that
     * Sprite. If no Sprite contains that point, null is returned.
     *
     * @return the Sprite with a bounding box containing (x,y)
     */
    public Sprite getClickedSun(float x, float y)
    {
        // GO THROUGH ALL THE SPRITES IN suns
        for (Sprite s : suns)
        {
            // IF IT CONTAINS THE POINT, RETURN IT
            if (s.containsPoint(x, y))
            {
                return s;
            }
        }
        // NO SPRITE WAS FOUND TO CONTAIN THE POINT, SO RETURN null
        return null;
    }

    /**
     * Accessor method for getting the number of suns the player has earned and
     * can use for purchasing Zombies and a Trophy.
     *
     * @return the current number of suns the player has
     */
    public int getCurrentSun()
    {
        return currentSun;
    }

    /**
     * Accessor method for getting the number of brains currently active in the
     * game. Note that there is typically a limit on this value, which will be
     * specified by Zombiquarium's MAX_BRAINS constant.
     *
     * @return the number of active brains in the game (on screen).
     */
    public int getNumBrains()
    {
        return brains.size();
    }

    /**
     * Accessor method for getting the number of suns currently active in the
     * game.
     *
     * @return the number of active suns in the game (on screen).
     */
    public int getNumSuns()
    {
        return suns.size();
    }

    /**
     * Accessor method for getting the number of zombies currently active in the
     * game.
     *
     * @return the number of active zombies in the game (on screen).
     */
    public int getNumZombies()
    {
        return zombies.size();
    }

    /**
     * Accessor method for getting an Iterator for producing all of the active
     * brains in the game. Note to be careful not to change the structure of the
     * root data structure mid-iteration.
     *
     * @return the Iterator for producing the active brains.
     */
    public Iterator<Sprite> getBrainsIterator()
    {
        return brains.iterator();
    }

    /**
     * Accessor method for getting an Iterator for producing all of the active
     * suns in the game. Note to be careful not to change the structure of the
     * root data structure mid-iteration.
     *
     * @return the Iterator for producing the active suns.
     */
    public Iterator<Sprite> getSunsIterator()
    {
        return suns.iterator();
    }

    /**
     * Accessor method for getting an Iterator for producing all of the active
     * zombies in the game. Note to be careful not to change the structure of
     * the root data structure mid-iteration.
     *
     * @return the Iterator for producing the active zombies.
     */
    public Iterator<Sprite> getZombiesIterator()
    {
        return zombies.iterator();
    }

    // MUTATOR METHODS
        // addToCurrentSun
        // addBrain
        // addSun
        // addZombie
        // buyTrophy
        // buyZombie
        // removeSun
        // reset
    
    /**
     * Increments the player's sun total by VALUE_OF_SUN, a constant defined in
     * Zombiquarium.
     */
    public void addToCurrentSun()
    {
        currentSun += VALUE_OF_SUN;
    }

    /**
     * Adds the brainToAdd parameter to the data structure storing the active
     * sprites. Once added, it will be rendered and zombies may try to eat it.
     *
     * @param brainToAdd initialized Brain to be added to the game.
     */
    public void addBrain(Sprite brainToAdd)
    {
        // A BRAIN CAN ONLY BE BOUGHT WITH ENOUGH SUN
        if (currentSun >= COST_OF_BRAIN)
        {
            // BRAIN PURCHASED, NOW MAKE IT PART OF THE GAME
            brains.add(brainToAdd);
            currentSun -= COST_OF_BRAIN;
        }
    }

    /**
     * Adds the sunToAdd parameter to the data structure storing the active
     * sprites. Once added, it will be rendered and the player may click on it.
     *
     * @param sunToAdd initialized Sun to be added to the game.
     */
    public void addSun(Sprite sunToAdd)
    {
        suns.add(sunToAdd);
    }

    /**
     * Adds the zombieToAdd to the data structure storing the active sprites.
     * Once added, it will be rendered and it may produce sun, eat brains, and
     * die.
     *
     * @param zombieToAdd initialized Zombie to be added to the game.
     */
    public void addZombie(Sprite zombieToAdd)
    {
        zombies.add(zombieToAdd);
    }

    /**
     * Deducts the cost of a trophy from the player's sun total.
     */
    public void buyTrophy()
    {
        if (currentSun >= COST_OF_TROPHY)
        {
            currentSun -= COST_OF_TROPHY;
            endGameAsWin();
        }
    }

    /**
     * Deducts the cost of a snorkling zombie from the player's sun total.
     *
     * @param game the Zombiquarium game in progress.
     */
    public void buyZombie(MiniGame game)
    {
        if (currentSun >= COST_OF_ZOMBIE)
        {
            currentSun -= COST_OF_ZOMBIE;
            spawnRandomZombie(game);
        }
    }

    /**
     * Provides the appropriate game response to a player pressing the mouse at
     * (x,y) on the game canvas. Note that the appropriate response may be to
     * place a brain, or collect sun, or do nothing.
     *
     * @param game the currently active game
     *
     * @param x the x-axis location of the mouse press
     *
     * @param y the y-axis location of the mouse press
     */
    public void checkMousePressOnSprites(MiniGame game, int x, int y)
    {
        // WE'LL NEED Zombiquarium STUFF AS WELL
        Zombiquarium zGame = (Zombiquarium) game;
        SpriteType zT = spriteTypes.get(ZOMBIE_TYPE);
        int zW = zT.getWidth();
        int zH = zT.getHeight();

        // DID THE PLAYER CLICK ON A SUN? IF YEs, REMOVE IT
        Sprite clickedSun = getClickedSun(x, y);
        if (clickedSun != null)
        {
            removeSun(clickedSun);
        } // TRY TO PLACE A BRAIN AS LONG AS
        // THERE ARE NOT ALREADY TOO MANY. NOTE
        // THAT WE WILL USE A SMALLER PLAYING
        // AREA FOR PLACING BRAINS BECAUSE WE 
        // DON'T WANT TO PUT THEM WHERE ZOMBIES
        // CAN'T REACH THEM
        else if ((getNumBrains() < MAX_BRAINS)
                && (x >= (zGame.getBoundaryLeft() + zW))
                && (x <= (zGame.getBoundaryRight() - zW))
                && (y >= (zGame.getBoundaryTop() + zH))
                && (y <= (zGame.getBoundaryBottom() - zH)))
        {
            SpriteType brainType = getSpriteType(BRAIN_TYPE);
            Sprite newBrain = new Sprite(brainType, x, y, 0, .2f, DEFAULT_STATE);
            addBrain(newBrain);
        }
    }

    /**
     * Adds the brainToRemove parameter to the list of brains to be removed from
     * the data manager which will be done at the end of each update, before
     * rendering.
     *
     * @param brainToRemove brain to mark as to be removed, which would be
     * because it was eaten.
     */
    public void markBrainForDeletion(Sprite brainToRemove)
    {
        brainsToRemove.add(brainToRemove);
    }

    /**
     * Adds the zombieToRemove parameter to the list of zombies to be removed
     * from the data manager which will be done at the end of each update,
     * before rendering.
     *
     * @param zombiesToRemove zombie to mark as to be removed, which would be
     * because it didn't eat enough brains.
     */
    public void markZombieForDeletion(Sprite zombieToRemove)
    {
        zombiesToRemove.add(zombieToRemove);
    }

    /**
     * Removes the sunToRemove parameter from the data structure of active suns
     * in the game. We don't use a deletion list because suns do not interact
     * with other game objects, the way zombies and brains do. Once a sun is
     * created, it can be clicked on and removed, but that's it.
     *
     * @param sunToRemove the sun to remove from the game.
     */
    public void removeSun(Sprite sunToRemove)
    {
        currentSun += VALUE_OF_SUN;
        suns.remove(sunToRemove);
    }

    /**
     * Resets all the game data so that a brand new game may be played.
     *
     * @param game the Zombiquarium game in progress
     */
    public void reset(MiniGame game)
    {
        // RESET THE PLAYER'S SUN TOTAL
        currentSun = STARTING_SUN;

        // EMPTY ALL THE SPRITE DATA STRUCTURES
        zombies.clear();
        brains.clear();
        suns.clear();

        // WE START THE GAME WITH 2 ZOMBIES SWIMMING
        for (int i = 0; i < 2; i++)
        {
            spawnRandomZombie(game);
        }

        // LET'S GO
        beginGame();
    }

    // STATE TESTING METHODS
        // containsBrain
        // isBrainMarkedForDeletion
        // isZombieMarkedForDeletion
    
    /**
     * Tests to see if the testBrain argument is currently active in the game.
     * If it is, true is returned, else false.
     *
     * @param testBrain the brain to test to see if it's active in the game
     *
     * @return true if the brain is still active in the game, false otherwise.
     */
    public boolean containsBrain(Sprite testBrain)
    {
        return brains.contains(testBrain);
    }

    /**
     * Tests to see if the brainToTest parameter is already marked for deletion.
     * The point is that zombies don't want to target brains that have already
     * been eaten, which is why they would end up on that list.
     *
     * @param brainToTest the brain to test to see if it has been marked for
     * deletion this update cycle.
     *
     * @return true if brainToTest has been marked, false otherwise.
     */
    public boolean isBrainMarkedForDeletion(Sprite brainToTest)
    {
        return brainsToRemove.contains(brainToTest);
    }

    /**
     * Tests to see if the zombieToTest parameter is already marked for
     * deletion.
     *
     * @param zombieToTest the brain to test to see if it has been marked for
     * deletion this update cycle.
     *
     * @return true if zombieToTest has been marked, false otherwise.
     */
    public boolean isZombieMarkedForDeletion(Sprite zombieToTest)
    {
        return zombiesToRemove.contains(zombieToTest);
    }

    // UPDATE METHODS
        // updateAll
        // updateDebugText
    
    /**
     * Called each frame, this thread already has a lock on the data. This
     * method updates all the game sprites as needed.
     *
     * @param game the game in progress
     */
    public void updateAll(MiniGame game)
    {
        // EMPTY STUFF WE GOT RID OF LAST FRAME
        brainsToRemove.clear();
        zombiesToRemove.clear();

        // UPDATE ALL SPRITES
        updateAll(game, zombies);
        updateAll(game, brains);
        updateAll(game, suns);

        // REMOVE BRAINS THAT HAVE SUNK TOO LOW
        for (Sprite b : brains)
        {
            if ((b.getY() + b.getSpriteType().getHeight())
                    >= game.getBoundaryBottom())
            {
                System.out.println("BRAIN TO REMOVE: " + b.getY());
                System.out.println("BOUNDARY_BOTTOM: " + BOUNDARY_BOTTOM);
                markBrainForDeletion(b);
            }
        }

        // REMOVE THE EATEN BRAINS
        removeMarkedSprites(brains, brainsToRemove);
        if (zombiesToRemove.size() > 0)
        {
            removeMarkedSprites(zombies, zombiesToRemove);
        }

        // DID THE PLAYER LOSE?
        if (this.inProgress() && (zombies.size() == 0))
        {
            endGameAsLoss();
        }
    }

    /**
     * Called each frame, this method specifies what debug text to render. Note
     * that this can help with debugging because rather than use a
     * System.out.print statement that is scrolling at a fast frame rate, we can
     * observe variables on screen with the rest of the game as it's being
     * rendered.
     *
     * @return game the active game being played
     */
    public void updateDebugText(MiniGame game)
    {
        debugText.clear();
        debugText.add("ZOMBIQUARIUM STATS");
        debugText.add("Frame Rate: " + game.getFrameRate() + " FPS");
        debugText.add("Num Zombies: " + zombies.size());
        debugText.add("Num Suns: " + suns.size());
        debugText.add("Num Brains: " + brains.size());

        if (zombies.size() > 0)
        {
            for (int i = 0; i < zombies.size(); i++)
            {
                Zombie z = (Zombie) zombies.get(i);
                debugText.add("Zombie #" + i);
                debugText.add("\tPosition: (" + z.getX() + ", " + z.getY() + ")");
                debugText.add("\tAABB Position: (" + z.getAABBx() + ", " + z.getAABBy() + ")");
                Sprite tB = z.getTargetBrain();
                if (tB != null)
                {
                    debugText.add("\tTarget Brain ID: " + tB.getID());
                }
            }
        }

        if (brains.size() > 0)
        {
            debugText.add("Game Boundary Bottom: " + game.getBoundaryBottom());
            for (int i = 0; i < brains.size(); i++)
            {
                Sprite b = brains.get(i);
                debugText.add("Brain # " + i);
                debugText.add("\tPosition: (" + b.getX() + ", " + b.getY() + ")");
            }
        }

        // AND NOW FIGURE OUT WHAT'S THERE IN THE BACKGROUND IMAGE. 
        Sprite bg = game.getGUIDecor().get(BACKGROUND_TYPE);
        SpriteType bgST = bg.getSpriteType();
        BufferedImage img = bgST.getStateImage(bg.getState());
        int rgb = img.getRGB(lastMouseX, lastMouseY);
        Color c = new Color(rgb);
        //          Zombiquarium.setDebugTextColor(c);
        debugText.add("Pixel RGBA: " + "(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "," + c.getAlpha() + ")");

        // UNCOMMENT THIS TO SEE PIXEL WRITING
        // img.setRGB(lastMouseX, lastMouseY, Color.pink.getRGB());
    }

    // PRIVATE HELPER METHODS
        // removeMarkedSprites
        // spawnRandomZombie
        // updateAll
    
    /**
     * Iterates through the removeList argument and removes all occurrences of
     * those objects from sprites.
     *
     * @param sprites the data structure that we will be removing Sprites from.
     *
     * @param removeList the data structure containing the Sprites to remove.
     */
    private void removeMarkedSprites(
            Vector<Sprite> sprites,
            Vector<Sprite> removeList)
    {
        // GO THROUGH EACH ONE AND REMOVE IF FOUND
        // NOT THAT THE Vector CLASS' remove METHOD
        // SIMPLY DOES NOTHING IF THE ARGUMENT PASSED
        // IS NOT FOUND
        for (Sprite s : removeList)
        {
            sprites.remove(s);
        }
    }

    /**
     * Constructs, initializes, and adds a randomly placed zombie and starts it
     * swimming with a randomly chosen direction and velocity.
     */
    private void spawnRandomZombie(MiniGame game)
    {
        SpriteType zombieType = spriteTypes.get(ZOMBIE_TYPE);
        float rangeX = game.getBoundaryRight() - game.getBoundaryLeft();
        float rangeY = game.getBoundaryBottom() - game.getBoundaryTop();
        float x = (float) (Math.random() * rangeX) + game.getBoundaryLeft();
        float y = (float) (Math.random() * rangeY) + game.getBoundaryTop();
        Zombie zombieToAdd = new Zombie(zombieType, x, y, 0, 0, NORMAL_ZOMBIE_RIGHT_STATE);
        zombieToAdd.initSwim();
        zombies.add(zombieToAdd);
    }

    /**
     * Helper method for updating all the sprites. This will update all the
     * sprites in a particular vector, sprites.
     *
     * @param game the game being played, which should actually be of type
     * Zombiquarium.
     *
     * @param sprites the Vector of sprites to update.
     */
    private void updateAll(MiniGame game, Vector<Sprite> sprites)
    {
        for (Sprite s : sprites)
        {
            s.update(game);
        }
    }
}