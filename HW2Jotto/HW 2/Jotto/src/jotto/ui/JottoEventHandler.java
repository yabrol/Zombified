package jotto.ui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import jotto.Jotto.JottoPropertyType;
import jotto.file.JottoFileLoader;
import jotto.game.DuplicateGuessException;
import jotto.game.InvalidGuessException;
import jotto.game.WrongGuessException;
import jotto.game.JottoGameStateManager;
import xml_utilities.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;

/**
 * This class handles responses to user interactions of all sorts. Note that we
 * are registering anonymous event handlers that forward handing to methods
 * inside this class.
 *
 * @author Richard McKenna, Yukti Abrol
 */
public class JottoEventHandler {
  // THIS PROVIDES ACCESS TO ALL APPLICATION DATA AND UI
  // COMPONENTS, SO IT LET'S THE HANDLERS RESPOND APPROPRIATELY

  private JottoUI ui;

  /**
   * Constructor that simply saves the ui for later.
   *
   * @param initUI
   */
  public JottoEventHandler(JottoUI initUI) {
    ui = initUI;
  }

  /**
   * This method responds to when the user wishes to switch between the Game,
   * Stats, and Help screens.
   *
   * @param uiState The ui state, or screen, that the user wishes to switch to.
   */
  public void respondToSwitchScreenRequest(JottoUI.JottoUIState uiState) {
    // RELAY THE CHANGE TO THE UI
    ui.changeWorkspace(uiState);
  }

  /**
   * This method handles when the user selects a language for the application.
   * At that time we'll need to load language-specific controls.
   *
   * @param language The language selected by the user.
   */
  public void respondToSelectLanguageRequest(String language) {
    // WE'LL NEED THESE TO INIT THE UI SCREENS
    JottoGameStateManager gsm = ui.getGSM();
    PropertiesManager props = PropertiesManager.getPropertiesManager();

    // GET THE SELECTED LANGUAGE & ITS XML FILE
    ArrayList<String> languages = props.getPropertyOptionsList(JottoPropertyType.LANGUAGE_OPTIONS);
    ArrayList<String> languageData = props.getPropertyOptionsList(JottoPropertyType.LANGUAGE_DATA_FILE_NAMES);
    int langIndex = languages.indexOf(language);
    String langDataFile = languageData.get(langIndex);
    String langSchema = props.getProperty(JottoPropertyType.PROPERTIES_SCHEMA_FILE_NAME);
    try {
      // LOAD THE LANGUAGE SPECIFIC PROPERTIES
      props.loadProperties(langDataFile, langSchema);

      // LOAD THE WORD LIST
      String wordListFile = props.getProperty(JottoPropertyType.WORD_LIST_FILE_NAME);
      String wordList = JottoFileLoader.loadTextFile(wordListFile);
      gsm.loadWordList(wordList);

      // INITIALIZE THE USER INTERFACE WITH THE SELECTED LANGUAGE
      ui.initJottoUI();

      // WE'LL START THE GAME TOO
      gsm.startNewGame();
    } catch (InvalidXMLFileFormatException ixmlffe) {
      ui.getErrorHandler().processError(JottoPropertyType.INVALID_XML_FILE_ERROR_TEXT);
      System.exit(0);
    } catch (IOException ioe) {
      ui.getErrorHandler().processError(JottoPropertyType.INVALID_DICTIONARY_ERROR_TEXT);
      System.exit(0);
    }
  }

  /**
   * This method responds to when the user presses the new game method.
   */
  public void respondToNewGameRequest() {
    JottoGameStateManager gsm = ui.getGSM();
    gsm.startNewGame();
  }

  /**
   * This method responds to when the user presses one of the letter buttons,
   * which toggles letter coloring.
   *
   * @param source The JButton that was pressed by the user. We respond by
   * changing its color.
   */
  public void respondToToggleLetterButtonRequest(Object source) {
    // GET THE BUTTON THAT WAS PRESSED
    JButton letterButton = (JButton) source;
    JottoDocumentManager docManager = ui.getDocManager();
    char letterPressed = (letterButton.getText()).charAt(0);

    // AND TOGGLE ITS TEXT AND BACKGROUND COLORS
    Color currentBackgroundColor = letterButton.getBackground();
    if (currentBackgroundColor == Color.LIGHT_GRAY) {
      letterButton.setBackground(Color.GREEN);
      letterButton.setForeground(Color.BLUE);
      //Update the guessed letter colors
      docManager.updateGuessColors();
    } else if (currentBackgroundColor == Color.GREEN) {
      letterButton.setBackground(Color.RED);
      letterButton.setForeground(Color.WHITE);
      //Update the guessed letter colors
      docManager.updateGuessColors();
    } else {
      letterButton.setBackground(Color.LIGHT_GRAY);
      letterButton.setForeground(Color.BLACK);
      //Update the guessed letter colors
      docManager.updateGuessColors();
    }
  }

  /**
   * This method responds to when the user presses enter in the guess text
   * field.
   *
   * @param guessTextField The text field where the user typed a guess and then
   * pressed enter.
   */
  public void respondToGuessWordRequest(JTextField guessTextField) {
    String guess = guessTextField.getText();
    JottoGameStateManager gsm = ui.getGSM();

    // PROCESS THE GUESS
    try {
      gsm.processGuess(guess);
      ui.getDocManager().updateGuessColors();
    } catch (DuplicateGuessException dge) {
      ui.getErrorHandler().processError(JottoPropertyType.DUPLICATE_WORD_ERROR_TEXT);
    } catch (InvalidGuessException dge) {
      ui.getErrorHandler().processError(JottoPropertyType.INVALID_WORD_ERROR_TEXT);
    } catch (WrongGuessException dge) {
      ui.getErrorHandler().processError(JottoPropertyType.WRONG_WORD_ERROR_TEXT);
    }
  }

  /**
   * This method responds to when the user requests to exit the application.
   *
   * @param window The window that the user has requested to close.
   */
  public void respondToExitRequest(JFrame window) {
    // ENGLIS IS THE DEFAULT
    String options[] = new String[]{"Yes", "No"};
    PropertiesManager props = PropertiesManager.getPropertiesManager();
    options[0] = props.getProperty(JottoPropertyType.DEFAULT_YES_TEXT);
    options[1] = props.getProperty(JottoPropertyType.DEFAULT_NO_TEXT);
    String verifyExit = props.getProperty(JottoPropertyType.DEFAULT_EXIT_TEXT);

    // NOW WE'LL CHECK TO SEE IF LANGUAGE SPECIFIC VALUES HAVE BEEN SET
    if (props.getProperty(JottoPropertyType.YES_TEXT) != null) {
      options[0] = props.getProperty(JottoPropertyType.YES_TEXT);
      options[1] = props.getProperty(JottoPropertyType.NO_TEXT);
      verifyExit = props.getProperty(JottoPropertyType.EXIT_REQUEST_TEXT);
    }

    // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
    int selection = JOptionPane.showOptionDialog(window,
            verifyExit,
            verifyExit,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            null);
    // WHAT'S THE USER'S DECISION?
    if (selection == JOptionPane.YES_NO_OPTION) {
      // YES, LET'S EXIT
      System.exit(0);
    }
  }

  /**
   * This method responds to when the user requests to return Home to the Help
   * Screen.
   */
  public void respondToHomeRequest() {
    ui.loadPage(ui.getHelpPane(), JottoPropertyType.HELP_FILE_NAME);
  }
}