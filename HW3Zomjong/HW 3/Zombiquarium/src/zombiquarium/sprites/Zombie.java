package zombiquarium.sprites;

import static zombiquarium.Zombiquarium.*;

import java.util.Iterator;

import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;

import zombiquarium.Zombiquarium;
import zombiquarium.ZombiquariumDataModel;

/**
 * A Zombie is a Sprite with an overridden update method that provides custom
 * AI. The Zombie behaves as a snorkling zombie should in the Zombiquarium game.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class Zombie extends Sprite
{
    // THE BRAIN THE ZOMBIE IS CURRENTLY GOING AFTER
    private Sprite targetBrain;
    
    // HP
    private int health;
    
    // TIME REMAINING ON SHOWING DEAD ZOMBIE
    private int deadOnDisplayTimeLeft;
    
    // INTERVAL BETWEEN SUN SPAWNINGS
    private int spawnSunTimeLeft;

    /**
     * Constructor for initializing the Zombie, it sets all important properties
     * for placing, rendering, and initially moving the Sprite.
     *
     * @param initSpriteType the zombie's SpriteType, which dictates what images
     * may be used for rendering this zombie.
     *
     * @param initX initial x-axis coordinate
     *
     * @param initY initial y=axis coordinate
     *
     * @param initVx initial x-axis velocity, referring to x-axis units moved
     * each frame.
     *
     * @param initVy initial y-axis velocity, referring to y-axis units moved
     * each frame.
     *
     * @param initState initial state, which dictates which image from its
     * SpriteType to start off using.
     */
    public Zombie(SpriteType initSpriteType, float initX, float initY,
            float initVx, float initVy, String initState)
    {
        // INIT ALL THE Sprite STUFF
        super(initSpriteType, initX, initY, initVx, initVy, initState);

        // AND THEN THE DEFAULTS, SOME OF WHICH ARE PROVIDED BY Zombiquarium
        targetBrain = null;
        health = STARTING_ZOMBIE_HEALTH;
        deadOnDisplayTimeLeft = DEAD_ON_DISPLAY_TIME;
        spawnSunTimeLeft = ZOMBIE_SUN_GEN_INTERVAL;

        // INIT THE AABB, THE MOUTH
        aabbWidth = ZOMBIE_MOUTH_AABB.right - ZOMBIE_MOUTH_AABB.left;
        aabbHeight = ZOMBIE_MOUTH_AABB.bottom - ZOMBIE_MOUTH_AABB.top;
        correctAABB();
    }

    /**
     * This helper method makes sure the Zombie's AABB, which should be at its
     * mouth, is correctly updated, whether it's facing left or right.
     */
    private void correctAABB()
    {
        aabbY = ZOMBIE_MOUTH_AABB.top;
        if (state.equals(NORMAL_ZOMBIE_LEFT_STATE)
                || state.equals(DYING_ZOMBIE_LEFT_STATE))
        {
            aabbX = ZOMBIE_MOUTH_AABB.left;
        } 
        else
        {
            aabbX = this.getSpriteType().getWidth() - aabbWidth - ZOMBIE_MOUTH_AABB.left;
        }
    }

    // ACCESSOR METHODS
        // getHP
        // getTargetBrain
    
    /**
     * Accessor method for getting this zombie's HP, which represents its
     * health.
     *
     * @return the current zombie hit points (health)
     */
    public int getHP()
    {
        return health;
    }

    /**
     * Accessor method for getting this zombie's current target brain, which it
     * should be tracking.
     *
     * @return the brain Sprite this zombie is currently targeting
     */
    public Sprite getTargetBrain()
    {
        return targetBrain;
    }

    // IMPORTANT METHODS FOR USE
        // initSwim
        // isDead
        // update
    
    /**
     * This method starts a positioned Zombie on his way, randomly swimming
     * either left or right at a random initial speed in the range from
     * ZOMBIE_MIN_VELOCITY to ZOMBIE_MAX_VELOCITY.
     */
    public void initSwim()
    {
        // LET'S START THE ZOMBIE SWIMMING LEFT AT A FUZZY VELOCITY
        state = NORMAL_ZOMBIE_LEFT_STATE;
        float range = ZOMBIE_MAX_VELOCITY - ZOMBIE_MIN_VELOCITY;
        vX = -(float) ((Math.random() * range) + ZOMBIE_MIN_VELOCITY);

        // AND RANDOMLY CHANGE IT TO RIGHT FOR SOME
        if (Math.random() > 0.5)
        {
            state = NORMAL_ZOMBIE_RIGHT_STATE;
            vX *= -1;
        }

        // THE ZOMBIE ONLY CHANGES Y WHEN PURSUING A BRAIN
        vY = 0;

        // ALWAYS NEED TO MAKE SURE THE AABB IS NEAR THE MOUTH
        correctAABB();
    }

    /**
     * Tests to see if this zombie is dead or not. If dead, true is returned,
     * else false.
     *
     * @return true if this zombie is dead, false otherwise
     */
    public boolean isDead()
    {
        return (state.equals(DEAD_ZOMBIE_LEFT_STATE)
                || state.equals(DEAD_ZOMBIE_RIGHT_STATE));
    }

    /**
     * This method overrides the update method in Sprite. This would be called
     * each frame on each zombie. It provides all AI and collision detection and
     * response for this Sprite. Note that the data has been locked and will be
     * unlocked before and after this method is called, so it does not have to
     * be done here.
     *
     * @param game the Zombiquarium game this Zombie is part of.
     */
    @Override
    public void update(MiniGame game)
    {
        // UPDATE THE POSITION OF THIS SPRITE AS WE NORMALLY DO
        super.update(game);

        // GET THE DATA SO WE CAN CHANGE IT
        ZombiquariumDataModel data = (ZombiquariumDataModel) game.getDataModel();

        // NOW PERFORM THE AI LOGIC

        // DO WE HAVE TO TRANSITION TO DYING OR DEAD?
        verifyDeathAndDying(data);

        // DEAD ZOMBIES FLOAT WHERE THEY ARE, LIVING ONES SPAWN SUNS,
        // SWIM, AND CHASE AND EAT BRAINS
        if (!isDead())
        {
            // FIRST SEE IF IT'S TIME TO SPAWN SOME SUN
            verifySunSpawning(data);

            // AND THEN GO AFTER THE BRAINS
            chaseBrains((Zombiquarium) game, data);
        }
    }

    // AH, THE LIFE OF A SNORKLING ZOMBIE
    private void chaseBrains(Zombiquarium game,
            ZombiquariumDataModel data)
    {
        // DOES THIS ZOMBIE ALREADY HAVE A TARGET BRAIN?
        if (targetBrain != null)
        {
            // THEN MAKE SURE THE TARGET BRAIN HASN'T BEEN
            // EATEN BY ANOTHER GREEDY ZOMBIE
            if (!data.containsBrain(targetBrain)
                    || data.isBrainMarkedForDeletion(targetBrain))
            {
                // IT HAS BEEN EATEN ALREADY, SO DON'T TARGET IT
                targetBrain = null;

                // AND GET BACK TO SWIMMING
                initSwim();
            } // NOW CHECK TO SEE IF THE ZOMBIE CAN EAT THE BRAIN
            else if (aabbsOverlap(targetBrain))
            {
                // IT CAN, SO GIVE THE ZOMBIE ITS HEALTH BACK
                health = STARTING_ZOMBIE_HEALTH;

                // AND GET BACK TO A RELAXING ZOMBIE SWIM
                initSwim();

                // AND REMOVE THE EATEN BRAIN FROM THE SYSTEM
                data.markBrainForDeletion(targetBrain);
                targetBrain = null;
            } // MUST FOLLOW BRAIN
            else
            {
                // UPDATE THE ZOMBIE'S VELOCITY SO IT'S
                // TRACKING THE TARGET BRAIN'S CENTER
                float deltaX = (targetBrain.getX() + (targetBrain.getSpriteType().getWidth() / 2))
                        - (x + aabbX + (aabbWidth / 2));
                float deltaY = (targetBrain.getY() + (targetBrain.getSpriteType().getHeight() / 2))
                        - (y + aabbY + (aabbHeight / 2));
                float totalDistance = (float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                float velRatio = ZOMBIE_MAX_VELOCITY / totalDistance;
                vX = deltaX * velRatio;
                vY = deltaY * velRatio;

                // ONLY CORRECT THE AABB IF THE BRAIN IS
                // NOT EVEN WITH THE ZOMBIE, OTHERWIZE IT
                // WILL CAUSE THRASHING
                if ((targetBrain.getX() > aabbX)
                        || (x > targetBrain.getAABBx()))
                {
                    correctAABB();
                }
            }
        } // OTHERWISE THIS ZOMBIE IS NOT CURRENTLY FOLLOWING
        // A BRAIN, SO LET'S SEE IF WE CAN PICK A NEW TARGET
        else
        {
            // IF THERE ANY BRAINS TO TARGET, 
            // THEN PICK THE CLOSEST ONE AND GO AFTER IT
            if (data.getNumBrains() > 0)
            {
                chooseTargetBrain(data);
            }

            // OTHERWISE JUST GO BACK AND FORTH
            if (targetBrain == null)
            {
                // WE JUST LEAVE DEAD ZOMBIES WHERE THEY ARE
                if ((!state.equals(DEAD_ZOMBIE_LEFT_STATE))
                        && (!state.equals(DEAD_ZOMBIE_RIGHT_STATE)))
                {
                    if (x <= game.getBoundaryLeft())
                    {
                        vX *= -1;
                        vY = 0;
                        if (state.equals(NORMAL_ZOMBIE_LEFT_STATE))
                        {
                            state = NORMAL_ZOMBIE_RIGHT_STATE;
                        } else if (state.equals(DYING_ZOMBIE_LEFT_STATE))
                        {
                            state = DYING_ZOMBIE_RIGHT_STATE;
                        }
                    } else if (x >= (game.getBoundaryRight() - spriteType.getWidth()))
                    {
                        vX *= -1;
                        vY = 0;
                        if (state.equals(NORMAL_ZOMBIE_RIGHT_STATE))
                        {
                            state = NORMAL_ZOMBIE_LEFT_STATE;
                        } else if (state.equals(DYING_ZOMBIE_RIGHT_STATE))
                        {
                            state = DYING_ZOMBIE_LEFT_STATE;
                        }
                    }
                    correctAABB();
                }
            }
        }
    }

    private void verifySunSpawning(ZombiquariumDataModel data)
    {
        spawnSunTimeLeft--;
        if (spawnSunTimeLeft == 0)
        {
            SpriteType sunType = data.getSpriteType(SUN_TYPE);
            Sprite newSun = new Sprite(sunType, x, y, 0, SUN_FALL_VELOCITY, DEFAULT_STATE);
            data.addSun(newSun);
            spawnSunTimeLeft = ZOMBIE_SUN_GEN_INTERVAL;
        }
    }

    private void verifyDeathAndDying(ZombiquariumDataModel data)
    {
        // FIRST DECREMENT HEALTH
        health -= ZOMBIE_HEALTH_DEC;

        // IS THE ZOMBIE DYING?
        if (health < ZOMBIE_DYING_THRESHOLD)
        {
            if (state.equals(NORMAL_ZOMBIE_LEFT_STATE))
            {
                state = DYING_ZOMBIE_LEFT_STATE;
            } else if (state.equals(NORMAL_ZOMBIE_RIGHT_STATE))
            {
                state = DYING_ZOMBIE_RIGHT_STATE;
            }
        }

        // IS THIS POOR SNORKLING ZOMBIE DEAD?
        if (health <= 0)
        {
            if (state.equals(DYING_ZOMBIE_LEFT_STATE))
            {
                state = DEAD_ZOMBIE_LEFT_STATE;
            } else
            {
                state = DEAD_ZOMBIE_RIGHT_STATE;
            }
            vX = 0;
            vY = 0;
            deadOnDisplayTimeLeft--;

            if (deadOnDisplayTimeLeft <= 0)
            {
                data.markZombieForDeletion(this);
            }
        }
    }

    private void chooseTargetBrain(ZombiquariumDataModel data)
    {
        Iterator<Sprite> brainsIt = data.getBrainsIterator();
        float minDistance = -1.0f;
        // FIND THE FIRST ONE THE ZOMBIE CAN SEE
        while (brainsIt.hasNext() && minDistance < 0.0f)
        {
            Sprite testBrain = brainsIt.next();
            float testDistance = calculateDistanceToSprite(testBrain);
            if (testDistance <= ZOMBIE_SIGHT_DISTANCE)
            {
                minDistance = testDistance;
                targetBrain = testBrain;
            }
        }

        // NOW SEE IF THERE IS A REMAINING ONE THAT'S CLOSER
        while (brainsIt.hasNext())
        {
            Sprite testBrain = brainsIt.next();
            float testDistance = calculateDistanceToSprite(testBrain);
            if (testDistance < minDistance)
            {
                targetBrain = testBrain;
                minDistance = testDistance;
            }
        }
    }
}