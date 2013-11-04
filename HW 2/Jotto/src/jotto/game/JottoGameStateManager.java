package jotto.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import jotto.ui.JottoUI;

/**
 * JottoGameStateManager manages all the data for Jotto. Note that it does
 * so completely independent of the presentation of the game.
 * 
 * @author Richard McKenna, Yukti Abrol
 */
public class JottoGameStateManager
{
    // THE GAME WILL ALWAYS BE IN
    // ONE OF THESE THREE STATES
    public enum JottoGameState
    {
        GAME_NOT_STARTED,
        GAME_IN_PROGRESS,
        GAME_OVER
    }
    
    // STORES THE CURRENT STATE OF THIS GAME
    private JottoGameState currentGameState;
    
    // WHEN THE STATE OF THE GAME CHANGES IT WILL NEED TO BE
    // REFLECTED IN THE USER INTERFACE, SO THIS CLASS NEEDS
    // A REFERENCE TO THE UI
    private JottoUI ui;

    // THIS IS THE DICTIONARY OF LEGAL GUESS WORDS
    private ArrayList<String> wordList;
    
    // THESE WORDS DON'T HAVE ANY REPEATING LETTERS AND
    // SO ARE CANDIDATES FOR SECRET WORDS
    private ArrayList<String> nonRepeatingWordList;
    
    // THIS IS THE GAME CURRENTLY BEING PLAYED
    private JottoGameData gameInProgress;
    
    // HOLDS ALL OF THE COMPLETED GAMES. NOTE THAT THE GAME
    // IN PROGRESS IS NOT ADDED UNTIL IT IS COMPLETED
    private ArrayList<JottoGameData> gamesHistory;
    
    // THIS IS 
    private final String NEWLINE_DELIMITER = "\n";
 
    /**
     * This constructor initializes this class for use, but does
     * not start a game.
     * 
     * @param initUI A reference to the Jotto user interface, this
     * game state manager needs to inform it of when this state
     * changes so that it can display the appropriate changes.
     */
    public JottoGameStateManager(JottoUI initUI)
    {
        // STORE THIS FOR LATER
        ui = initUI;
        
        // WE HAVE NOT STARTED A GAME YET
        currentGameState = JottoGameState.GAME_NOT_STARTED;
        
        // NO GAMES HAVE BEEN PLAYED YET, BUT INITIALIZE
        // THE DATA STRCUTURE FOR PLACING COMPLETED GAMES
        gamesHistory = new ArrayList();
        
        // THE FIRST GAME HAS NOT BEEN STARTED YET
        gameInProgress = null;   
    }

    // ACCESSOR METHODS
    
    /**
     * Accessor method for getting the game currently being played.
     * 
     * @return The game currently being played.
     */
    public JottoGameData    getGameInProgress() { return gameInProgress; }
    
    /**
     * Accessor method for getting the number of games that have been played.
     * 
     * @return The total number of games that have been played during
     * this game session.
     */
    public int getGamesPlayed() { return gamesHistory.size(); }
    
    /**
     * Accessor method for getting all the games that have been completed.
     * 
     * @return An Iterator that allows one to go through all the games
     * that have been played so far.
     */
    public Iterator<JottoGameData> getGamesHistoryIterator() { return gamesHistory.iterator(); }
    
    /**
     * Accessor method for testing to see if any games have been started yet.
     * 
     * @return true if at least one game has already been started during this
     * session, false otherwise.
     */
    public boolean          isGameNotStarted()       { return currentGameState == JottoGameState.GAME_NOT_STARTED; }
    
    /**
     * Accessor method for testing to see if the current game is over.
     * 
     * @return true if the game in progress has completed, false otherwise.
     */
    public boolean          isGameOver()        { return currentGameState == JottoGameState.GAME_OVER; }

    /**
     * Accessor method for testing to see if the current game is in progress.
     * 
     * @return true if a game is in progress, false otherwise.
     */
    public boolean          isGameInProgress()  { return currentGameState == JottoGameState.GAME_IN_PROGRESS; }
    
    /**
     * Counts and returns the number of wins during this game session.
     * 
     * @return The number of games in that have been completed that
     * the player won.
     */
    public int getWins()
    {
        // ITERATE THROUGH ALL THE COMPLETED GAMES
        Iterator<JottoGameData> it = gamesHistory.iterator();
        int wins = 0;
        while(it.hasNext())
        {
            // GET THE NEXT GAME IN THE SEQUENCE
            JottoGameData game = it.next();
            
            // IF IT ENDED IN A WIN, INC THE COUNTER
            if (game.isWordFound())
                wins++;
        }
        return wins;
    }

    /**
     * Tests to see if the testWord has any repeating letters
     * in the word. 
     * 
     * @param testWord Word to test for repeating letters.
     * 
     * @return true if the word has repeating letters,
     * false otherwise. For example, 'hello' would return true
     * because it has repeating 'l' letters. 'great' would
     * return false because it has no repeating letters.
     */
    public boolean hasRepeatingLetters(String testWord)
    {
        for (int i = 0; i < testWord.length(); i++)
        {
            char testChar = testWord.charAt(i);
            for (int j = i + 1; j < testWord.length(); j++)
            {
                char testChar2 = testWord.charAt(j);
                if (testChar == testChar2)
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Tests to see if testWord is in the dictionary being
     * used for the game.
     * 
     * @param testWord Word to test to see if it is in the dictionary,
     * and therefore can be guessed by game players.
     * 
     * @return true if the game is in the dictionary, and so can be
     * guessed, false otherwise.
     */
    public boolean isInDictionary(String testWord)
    {
        // WE KNOW THE wordList IS SORTED, SINCE WE DID SO WHEN WE 
        // LOADED IT, SO WE'LL JUST USE THE JAVA API'S BINARY
        // SEARCH IMPLEMENTATION, SINCE IT WORKS AND IT'S FAST
        int index = Arrays.binarySearch(wordList.toArray(), testWord);
        return (index >= 0);
    }    
 
    /**
     * Initializes the dictionary to be used to play the game.
     * 
     * @param initWordList This String contains all the words to
     * be loaded into the dictionary, separated by newline characters.
     */
    public void loadWordList(String initWordList)
    {
        // SEPARATE THE LOADED STRING INTO WORDS
        String[] words = initWordList.split(NEWLINE_DELIMITER);
        
        // WE'LL ACTUALLY USE 2 LISTS, ONE WITH ALL THE WORDS, WHICH
        // ARE ALL LEGAL FOR GUESSES
        wordList = new ArrayList();
        
        // AND ONE WITH ALL THE WORDS WITH NO REPEATING LETTERS,
        // WHICH MAKES THEM CANDIDATES TO BE SECRET WORDS
        nonRepeatingWordList = new ArrayList();
        
        // GO THROUGH ALL THE WORDS 
        for (int i = 0; i < words.length; i++)
        {
            // WE'LL USE ALL CAPS
            String word = words[i].toUpperCase();
            
            // ADD THE WORDS TO THEIR CORRECT LISTS
            wordList.add(word);
            if (!this.hasRepeatingLetters(word))
            {
                nonRepeatingWordList.add(word);
            }
        }
    }    
    
    /**
     * This method starts a new game, initializing all the necessary data for
     * that new game as well as recording the current game (if it exists)
     * in the games history data structure. It also lets the user interface
     * know about this change of state such that it may reflect this change.
     */
    public void startNewGame() 
    {
        // IS THERE A GAME ALREADY UNDERWAY?
        // YES, SO END THAT GAME AS A LOSS
        if (!isGameNotStarted() && (!gamesHistory.contains(gameInProgress)))
            gamesHistory.add(gameInProgress);

        // IF THERE IS A GAME IN PROGRESS AND THE PLAYER HASN'T WON, THAT MEANS
        // THE PLAYER IS QUITTING, SO WE NEED TO SAVE THE GAME TO OUR HISTORY
        // DATA STRUCTURE. NOTE THAT IF THE PLAYER WON THE GAME, IT WOULD HAVE
        // ALREADY BEEN SAVED SINCE THERE WOULD BE NO GUARANTEE THE PLAYER WOULD
        // CHOOSE TO PLAY AGAIN
        if (isGameInProgress() && !gameInProgress.isWordFound())
        {
            // QUIT THE GAME, WHICH SETS THE END TIME
            gameInProgress.giveUp();
            
            // MAKE SURE THE STATS PAGE KNOWS ABOUT THE COMPLETED GAME
            ui.getDocManager().addGameResultToStatsPage(gameInProgress);
        }
        
        // AND NOW MAKE A NEW GAME
        makeNewGame();
        
        // AND MAKE SURE THE UI REFLECTS A NEW GAME
        ui.resetUI();
    }
    
    /**
     * This method chooses a secret word and uses it to create
     * a new game, effectively starting it.
     */
    public void makeNewGame()
    {
        // FIRST PICK THE SECRET WORD
        int randomNum = (int)(Math.random() * nonRepeatingWordList.size());
        String secretWord = nonRepeatingWordList.get(randomNum);
        
        // THEN MAKE THE GAME WITH IT
        gameInProgress = new JottoGameData(secretWord);
        
        // THE GAME IS OFFICIALLY UNDERWAY
        currentGameState = JottoGameState.GAME_IN_PROGRESS;
    }
    
    /**
     * This method processes the guess, updating the game state
     * accordingly.
     * 
     * @param guess The word that the player is guessing is the
     * secret word. Note that it must be in the dictionary.
     * 
     * @throws DuplicateGuessException Thrown for the case when
     * the guess has already been made this game.
     * @throws InvalidGuessException Thrown for the case when
     * the guess is not in the dictionary.
     * @throws WrongGuessException for the case when the
     * guess is not the correct number of letters
     */
    public void processGuess(String guess) throws DuplicateGuessException,
            InvalidGuessException,WrongGuessException
    {
        // IF THIS GUESS HAS ALREADY BEEN MADE
        if (gameInProgress.guesses.contains(guess))
        {
            // NOTE THAT THIS IS ALSO OUR OWN CUSTOM
            // EXCEPTION TYPE
            throw new DuplicateGuessException(guess);
        }
        else{
          //check if 5 letters
          if(guess.length() != 5)
          {
            throw new WrongGuessException(guess);
          }
          //check if a word
          if(!(ui.getGSM().isInDictionary(guess)))
          {
            throw new InvalidGuessException(guess);
          }
        }

        // RECORD THE GUESS
        gameInProgress.guess(guess);
        
        // IS IT THE WORD?
        if (gameInProgress.isWordFound())
        {
            // CHANGE THE GAME STATE
            currentGameState = JottoGameState.GAME_OVER;
            
            //Add the YOU WIN! label
            ui.getDocManager().addWinLabel();
            
            // ADD THE COMPLETED GAME TO THE HISTORY
            gamesHistory.add(gameInProgress);
            
            // AND MAKE SURE THE STATS PAGE IS CURRENT
            ui.getDocManager().addGameResultToStatsPage(gameInProgress);
            
        }

        // THE UI NEEDS TO RENDER WITH THE LATEST STATE INFO
        ui.getDocManager().addGuessToGamePage(guess);
        
        // AND EMPTY THE TEXT FIELD
        ui.clearGuessTextField();
    }
}