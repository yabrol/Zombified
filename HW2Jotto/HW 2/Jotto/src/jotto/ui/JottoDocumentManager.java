package jotto.ui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import jotto.Jotto.JottoPropertyType;
import jotto.game.JottoGameData;
import jotto.game.JottoGameStateManager;
import properties_manager.PropertiesManager;

/**
 * JottoHTMLBuilder generates HTML content for display inside the Jotto game
 * application, including the in-game GUI and the stats page. Note that we
 * maintain both of these pages inside Documents, which store trees containing
 * all the HTML. We will make use of HTML.Tag constants to update these DOMs
 * (Document Object Models).
 *
 * @author Richard McKenna, Yukti Abrol
 */
public class JottoDocumentManager {
  // THE JOTTO GAME'S UI HAS ACCESS TO ALL COMPONENTS, SO
  // IT'S USEFUL TO HAVE IT WHEN WE NEED IT

  private JottoUI ui;
  // THESE ARE THE DOCUMENTS WE'LL BE UPDATING HERE
  private HTMLDocument gameDoc;
  private HTMLDocument statsDoc;
  // WE'LL USE THESE TO BUILD OUR HTML
  private final String START_TAG = "<";
  private final String END_TAG = ">";
  private final String SLASH = "/";
  private final String SPACE = " ";
  private final String EMPTY_TEXT = "";
  private final String NL = "\n";
  private final String QUOTE = "\"";
  private final String OPEN_PAREN = "(";
  private final String CLOSE_PAREN = ")";
  private final String COLON = ":";
  private final String EQUAL = "=";
  private final String HASH = "#";
  // THESE ARE IDs IN THE GAME DISPLAY HTML FILE SO THAT WE 
  // CAN GRAB THE NECESSARY ELEMENTS AND UPDATE THEM
  private final String GUESSES_SUBHEADER_ID = "guesses_subheader";
  private final String GUESSES_LIST_ID = "guesses_list";
  private final String WIN_DISPLAY_ID = "win_display";
  // THESE ARE IDs IN THE STATS HTML FILE SO THAT WE CAN
  // GRAB THE NECESSARY ELEMENTS AND UPDATE THEM
  private final String GAMES_PLAYED_ID = "games_played";
  private final String WINS_ID = "wins";
  private final String LOSSES_ID = "losses";
  private final String FEWEST_GUESSES_ID = "fewest_guesses";
  private final String FASTEST_WIN_ID = "fastest_win";
  private final String GAME_RESULTS_HEADER_ID = "game_results_header";
  private final String GAME_RESULTS_LIST_ID = "game_results_list";

  /**
   * This constructor just keeps the UI for later. Note that once constructed,
   * the docs will need to be set before this class can be used.
   *
   * @param initUI
   */
  public JottoDocumentManager(JottoUI initUI) {
    // KEEP THE UI FOR LATER
    ui = initUI;
  }

  /**
   * Accessor method for initializing the game doc, which displays while the
   * game is being played and displays the guesses. Note that this must be done
   * before this object can be used.
   *
   * @param initGameDoc The game document to be displayed while the game is
   * being played.
   */
  public void setGameDoc(HTMLDocument initGameDoc) {
    gameDoc = initGameDoc;
  }

  /**
   * Accessor method for initializing the stats doc, which displays past game
   * results and statistics. Note that this must be done before this object can
   * be used.
   *
   * @param initStatsDoc The stats document to be displayed on the stats screen.
   */
  public void setStatsDoc(HTMLDocument initStatsDoc) {
    statsDoc = initStatsDoc;
  }

  // BELOW ARE FOUR METHODS THAT ARE USED TO INITIALIZE AND
  // UPDATE THE GAME'S HTML:
  // -addGuessToGamePage
  // -buildGuessHTML
  // -clearGamePage
  // -updateGuessColors
  /**
   * This method lets us add a guess to the game page display without having to
   * rebuild the entire page. We just add it to the HTML list of guesses made so
   * far this game.
   *
   * @param guess Guess to be added to the display.
   */
  public void addGuessToGamePage(String guess) {
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    JottoGameData gameInProgress = ui.getGSM().getGameInProgress();

    try {
      // START BY LOADING THE LANGUAGE-DEPENDENT SUBHEADER
      String guessesSubheaderText = props.getProperty(JottoPropertyType.GAME_SUBHEADER_TEXT);
      Element h2 = gameDoc.getElement(GUESSES_SUBHEADER_ID);
      gameDoc.setInnerHTML(h2, guessesSubheaderText);

      // AND NOW FILL IN THE LIST. WE'RE GOING TO ADD 
      // LIST ITEMS TO THE ORDERED LIST
      Element ol = gameDoc.getElement(GUESSES_LIST_ID);
      int lettersInGuess = gameInProgress.calcLettersInGuess(guess);
      String htmlText = buildGuessHTML(guess, lettersInGuess);
      gameDoc.insertBeforeEnd(ol, htmlText);
    } // THE ERROR HANDLER WILL DEAL WITH ERRORS ASSOCIATED WITH BUILDING
    // THE HTML FOR THE PAGE, WHICH WOULD LIKELY BE DUE TO BAD DATA FROM
    // AN XML SETUP FILE
    catch (BadLocationException | IOException e) {
      JottoErrorHandler errorHandler = ui.getErrorHandler();
      errorHandler.processError(JottoPropertyType.INVALID_DOC_ERROR_TEXT);
    }
  }

  /**
   * This private helper method builds the HTML associated with a guess as a
   * list item, adding the proper colors as currently set by the player.
   *
   * @param guess Guess to add to the list.
   *
   * @param lettersInGuess The number of letters from the guess in the secret
   * word. We'll list this in parentheses after each guess.
   *
   * @return
   */
  private String buildGuessHTML(String guess, int lettersInGuess) {
    // FIRST THE OPENING LIST ITEM TAG WITH THE GUESS
    // AS ITS ID. THIS IS OK SINCE WE DON'T ALLOW
    // DUPLICATE GUESSES
    String htmlText = START_TAG + HTML.Tag.LI + SPACE + HTML.Attribute.ID
            + EQUAL + QUOTE + guess + QUOTE + END_TAG;

    // HERE WE'RE PUTTING THE GUESS IN THE LIST ITEM
    htmlText += guess;

    // NOW ADD INFORMATION ABOUT THE NUMBER OF LETTERS IN THE
    // GUESS THAT ARE IN THE SECRET WORD
    htmlText += SPACE + OPEN_PAREN
            + lettersInGuess
            + CLOSE_PAREN + START_TAG + SLASH + HTML.Tag.LI + END_TAG + NL;

    // RETURN THE COMPLETED HTML
    return htmlText;
  }

  /**
   * When a new game starts the game page should not have a subheader or display
   * guesses or a win state, so all of that has to be cleared out of the DOM at
   * that time. This method does the work of clearing out these nodes.
   */
  public void clearGamePage() {
    try {
      // WE'LL PUT THIS <br /> TAG IN PLACE OF THE CONTENT WE'RE REMOVING
      String lineBreak = START_TAG + HTML.Tag.BR + SPACE + SLASH + END_TAG;

      // CLEAR THE SUBHEADER
      Element h2 = gameDoc.getElement(GUESSES_SUBHEADER_ID);
      gameDoc.setInnerHTML(h2, lineBreak);

      // CLEAR THE GUESS LIST
      Element ol = gameDoc.getElement(GUESSES_LIST_ID);
      gameDoc.setInnerHTML(ol, lineBreak);

      // CLEAR THE WIN DISPLAY
      Element winH2 = gameDoc.getElement(WIN_DISPLAY_ID);
      gameDoc.setInnerHTML(winH2, lineBreak);
    } // THE ERROR HANDLER WILL DEAL WITH ERRORS ASSOCIATED WITH BUILDING
    // THE HTML FOR THE PAGE, WHICH WOULD LIKELY BE DUE TO BAD DATA FROM
    // AN XML SETUP FILE
    catch (BadLocationException | IOException ex) {
      JottoErrorHandler errorHandler = ui.getErrorHandler();
      errorHandler.processError(JottoPropertyType.INVALID_DOC_ERROR_TEXT);
    }
  }

  /**
   * This method adds the data from the completedGame argument to the stats
   * page, as well as loading all the newly computed stats for all the games
   * played.
   *
   * @param completedGame Game whose summary will be added to the stats page.
   */
  public void addGameResultToStatsPage(JottoGameData completedGame) {
    // GET THE GAME STATS
    JottoGameStateManager gsm = ui.getGSM();
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    int gamesPlayed = gsm.getGamesPlayed();
    int wins = gsm.getWins();
    int losses = gamesPlayed - wins;

    try {
      // USE THE STATS TO UPDATE THE TABLE AT THE TOP OF THE PAGE
      Element gamePlayedElement = statsDoc.getElement(GAMES_PLAYED_ID);
      statsDoc.setInnerHTML(gamePlayedElement, EMPTY_TEXT + gamesPlayed);

      Element winsElement = statsDoc.getElement(WINS_ID);
      statsDoc.setInnerHTML(winsElement, EMPTY_TEXT + wins);

      Element lossElement = statsDoc.getElement(LOSSES_ID);
      statsDoc.setInnerHTML(lossElement, EMPTY_TEXT + losses);

      Element fewestWinElement = statsDoc.getElement(FEWEST_GUESSES_ID);
      statsDoc.setInnerHTML(fewestWinElement, EMPTY_TEXT + calculcateFewest());

      Element fastestWinElement = statsDoc.getElement(FASTEST_WIN_ID);
      statsDoc.setInnerHTML(fastestWinElement, EMPTY_TEXT + calculateFast());

      Element gameResultsHeaderElement = statsDoc.getElement(GAME_RESULTS_HEADER_ID);
      String resultsSubheaderText = props.getProperty(JottoPropertyType.GAME_RESULTS_TEXT);
      statsDoc.setInnerHTML(gameResultsHeaderElement, resultsSubheaderText);

      Element gameResultsElement = statsDoc.getElement(GAME_RESULTS_LIST_ID);
      statsDoc.setInnerHTML(gameResultsElement, EMPTY_TEXT + listAllGames());

    } // WE'LL LET THE ERROR HANDLER TAKE CARE OF ANY ERRORS,
    // WHICH COULD HAPPEN IF XML SETUP FILES ARE IMPROPERLY
    // FORMATTED
    catch (BadLocationException | IOException e) {
      JottoErrorHandler errorHandler = ui.getErrorHandler();
      errorHandler.processError(JottoPropertyType.INVALID_DOC_ERROR_TEXT);
    }
  }

  /**
   * This is a private method to list all the games. For each game, the secret
   * word, the game time, and all the guesses are listed.
   */
  private String listAllGames() {
    String allText = START_TAG + HTML.Tag.OL + END_TAG;
    Iterator<JottoGameData> gamesHistory = ui.getGSM().getGamesHistoryIterator();
    JottoGameData currGame;
    if (!gamesHistory.hasNext()) {
      allText = "-";
    } else {
      while (gamesHistory.hasNext()) {
        currGame = gamesHistory.next();
        allText += START_TAG + HTML.Tag.LI + END_TAG;
        allText += currGame.toString();
        allText += START_TAG + SLASH + HTML.Tag.LI + END_TAG + NL;
      }
      allText += START_TAG + SLASH + HTML.Tag.OL + END_TAG + NL;
    }
    return allText;
  }

  /**
   * This is a private method to calculate the fewest guesses to win a game and
   * what to display on the stats.
   */
  private String calculcateFewest() {
    String fewText;
    JottoGameData shortGame, currGame;
    shortGame = null;
    Iterator<JottoGameData> gamesHistory = ui.getGSM().getGamesHistoryIterator();
    //find the shortest game
    if (gamesHistory.hasNext()) {
      currGame = gamesHistory.next();
      if (currGame.isWordFound()) {
        shortGame = currGame;
      }
      while (gamesHistory.hasNext()) {
        currGame = gamesHistory.next();
        if (shortGame.isWordFound() && currGame.isWordFound()) {
          if (shortGame.getNumGuesses() > currGame.getNumGuesses()) {
            shortGame = currGame;
          }
        }
      }
    }
    //if no shortest game
    if (shortGame == null) {
      fewText = "-";
    } else {
      fewText = shortGame.toString();
    }
    return fewText;
  }

  /**
   * This is a private method to calculate the fastest game and what to display
   * on the stats.
   *
   */
  private String calculateFast() {
    String fastText;
    JottoGameData shortGame, currGame;
    shortGame = null;
    Iterator<JottoGameData> gamesHistory = ui.getGSM().getGamesHistoryIterator();
    //find the shortest game
    if (gamesHistory.hasNext()) {
      currGame = gamesHistory.next();
      if (currGame.isWordFound()) {
        shortGame = currGame;
      }
      while (gamesHistory.hasNext()) {
        currGame = gamesHistory.next();
        if (shortGame.isWordFound() && currGame.isWordFound()) {
          if (shortGame.getTimeOfGame() > currGame.getTimeOfGame()) {
            shortGame = currGame;
          }
        }
      }
    }
    //if no fastest game
    if (shortGame != null) {
      fastText = shortGame.toString();
    } else {
      fastText = "-";
    }
    return fastText;
  }

  /**
   * This method is called when the game has been won to place the YOU WIN!
   * label below the guesses on the HTML.
   *
   */
  public void addWinLabel() {
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    JottoGameData gameInProgress = ui.getGSM().getGameInProgress();

    try {
      // START BY LOADING THE LANGUAGE-DEPENDENT SUBHEADER
      String winText = props.getProperty(JottoPropertyType.WIN_DISPLAY_TEXT);
      String htmlWinText = START_TAG + HTML.Tag.H1 + SPACE + HTML.Attribute.ID
              + EQUAL + QUOTE + WIN_DISPLAY_ID + QUOTE + HTML.Attribute.STYLE
              + EQUAL + QUOTE + "font-size:150%" + QUOTE + END_TAG + START_TAG
              + HTML.Tag.STRONG + END_TAG + winText + START_TAG + SLASH
              + HTML.Tag.STRONG + END_TAG + START_TAG + SLASH + HTML.Tag.H1
              + END_TAG + NL;
      Element h2 = gameDoc.getElement(WIN_DISPLAY_ID);
      gameDoc.insertBeforeEnd(h2, htmlWinText);


    } // THE ERROR HANDLER WILL DEAL WITH ERRORS ASSOCIATED WITH BUILDING
    // THE HTML FOR THE PAGE, WHICH WOULD LIKELY BE DUE TO BAD DATA FROM
    // AN XML SETUP FILE
    catch (BadLocationException | IOException e) {
      JottoErrorHandler errorHandler = ui.getErrorHandler();
      errorHandler.processError(JottoPropertyType.INVALID_DOC_ERROR_TEXT);
    }
  }

  /**
   * Updates the colors of all previous guesses based on letters that were
   * clicked to change color.
   *
   * @param letter The character that was clicked and will now have a background
   * color.
   * @param bgColor The color of the background that the character will have.
   */
  public void updateGuessColors() {
    JottoGameData gameInProgress = ui.getGSM().getGameInProgress();
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    try {
      ArrayList<String> letters = props.getPropertyOptionsList(JottoPropertyType.LETTER_OPTIONS);
            
      ArrayList<Character> red = new ArrayList<Character>();
      ArrayList<Character> green = new ArrayList<Character>();
      ArrayList<Character> white = new ArrayList<Character>();
      
      char testChar;
      Color letterColor;
      //assign each character a color
      for(String l:letters)
      {
        testChar = l.charAt(0);
        letterColor = ui.getColorForChar(testChar);
        if(letterColor.getRed() == 255 && letterColor.getGreen() == 0)
        {
          red.add(testChar);
        }
        else if(letterColor.getGreen() == 255 && letterColor.getRed() == 0)
        {
          green.add(testChar);
        }
        else
        {
          white.add(testChar);
        }
      }
      //add color to each guess
      
      Iterator<String> guessIt = gameInProgress.guessesIterator();
      String testWord, htmlString;
      
      Element e;
      int lettersInGuess;
      while (guessIt.hasNext()) {
        testWord = (String) guessIt.next();
        lettersInGuess = gameInProgress.calcLettersInGuess(testWord);
        e = gameDoc.getElement(testWord);
        htmlString = START_TAG + HTML.Tag.LI + SPACE + HTML.Attribute.ID
                + EQUAL + QUOTE + testWord + QUOTE + END_TAG;
        for (int i = 0; i < 5; i++) {
          testChar = testWord.charAt(i);
          if (red.contains(testChar)) {
            //using span to change background color of character
            htmlString += START_TAG + HTML.Tag.SPAN + SPACE
                    + HTML.Attribute.STYLE + EQUAL + QUOTE + "background-color:"
                    + "red" + QUOTE + END_TAG;
            htmlString += testChar;
            htmlString += START_TAG + SLASH + HTML.Tag.SPAN + END_TAG;
          } else if(green.contains(testChar)){
            htmlString += START_TAG + HTML.Tag.SPAN + SPACE
                    + HTML.Attribute.STYLE + EQUAL + QUOTE + "background-color:"
                    + "green" + QUOTE + END_TAG;
            htmlString += testChar;
            htmlString += START_TAG + SLASH + HTML.Tag.SPAN + END_TAG;
          }
          else{
            htmlString += START_TAG + HTML.Tag.SPAN + SPACE
                    + HTML.Attribute.STYLE + EQUAL + QUOTE + "background-color:"
                    + "white" + QUOTE + END_TAG;
            htmlString += testChar;
            htmlString += START_TAG + SLASH + HTML.Tag.SPAN + END_TAG;
          }
        }
        htmlString += SPACE + OPEN_PAREN + lettersInGuess + CLOSE_PAREN
                + START_TAG + SLASH + HTML.Tag.LI + END_TAG + NL;
        gameDoc.setInnerHTML(e, htmlString);
      }
    } // THE ERROR HANDLER WILL DEAL WITH ERRORS ASSOCIATED WITH BUILDING
    // THE HTML FOR THE PAGE, WHICH WOULD LIKELY BE DUE TO BAD DATA FROM
    // AN XML SETUP FILE
    catch (BadLocationException | IOException e) {
      JottoErrorHandler errorHandler = ui.getErrorHandler();
      errorHandler.processError(JottoPropertyType.INVALID_DOC_ERROR_TEXT);
    }
  }
}
