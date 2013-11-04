package jotto.game;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * JottoGameData stores the data necessary for a single Jotto game. Note that
 * this class works in concert with the JottoGameStateManager, so all instance
 * variables have default (package-level) access.
 *
 * @author Richard McKenna, Yukti Abrol
 */
public class JottoGameData {
  // THE WORD THE PLAYER IS TRYING TO GUESS

  String secretWord;
  // WHEN FOUND, THE GAME IS OVER AND THE PLAYER WINS, BUT IF
  // THE PLAYER GIVES UP THIS WELL REMAIN FALSE
  boolean wordFound;
  // HISTORY OF ALL GUESSES THIS GAME
  ArrayList<String> guesses;
  // START AND END TIME WILL BE USED TO CALCULATE THE 
  // TIME IT TAKES TO PLAY THIS GAME
  GregorianCalendar startTime;
  GregorianCalendar endTime;
  // THESE ARE USED FOR FORMATTING THE TIME OF GAME
  final long MILLIS_IN_A_SECOND = 1000;
  final long MILLIS_IN_A_MINUTE = 1000 * 60;
  final long MILLIS_IN_AN_HOUR = 1000 * 60 * 60;

  /*
   * Construct this object when a game begins.
   */
  public JottoGameData(String initSecretWord) {
    secretWord = initSecretWord;
    wordFound = false;
    guesses = new ArrayList();
    startTime = new GregorianCalendar();
    endTime = null;
  }

  // ACCESSOR METHODS
  /**
   * Accessor method for this game's secret word.
   *
   * @return The secret word for this game.
   */
  public String getSecretWord() {
    return secretWord;
  }

  /**
   * Accessor method for testing to see if the secred word has been found this
   * game or not.
   *
   * @return true if the secret word has been found this game, false otherwise.
   */
  public boolean isWordFound() {
    return wordFound;
  }

  /**
   * Accessor method for getting the number of guesses the player made this
   * game.
   *
   * @return The number of guesses the player made this game.
   */
  public int getNumGuesses() {
    return guesses.size();
  }

  /**
   * Accessor method for testing to see if the user has made at least one guess.
   *
   * @return true if the user has made at least one guess, false otherwise.
   */
  public boolean hasGuessBeenMade() {
    return !guesses.isEmpty();
  }

  /**
   * Accessor method for going through all the guesses this game.
   *
   * @return An Iterator for going through each guess made this game.
   */
  public Iterator<String> guessesIterator() {
    return guesses.iterator();
  }

  /**
   * Gets the total time (in milliseconds) that this game took.
   *
   * @return The time of the game in milliseconds.
   */
  public long getTimeOfGame() {
    // IF THE GAME ISN'T OVER YET, THERE IS NO POINT IN CONTINUING
    if (endTime == null) {
      return -1;
    }

    // THE TIME OF THE GAME IS END-START
    long startTimeInMillis = startTime.getTimeInMillis();
    long endTimeInMillis = endTime.getTimeInMillis();

    // CALC THE DIFF AND RETURN IT
    long diff = endTimeInMillis - startTimeInMillis;
    return diff;
  }

  /**
   * Calculates the number of letters in the guess argument that are in the
   * secret word. Note that if guess has repeating characters that are in the
   * secret word, both are counted. So, if the secret word is RHYME, and the
   * guess is MUMMY, this method would return 4, meaning one for each M, and one
   * for Y.
   *
   * @param guess The word to test against the secret word.
   *
   * @return The number of letters in guess that are in this game's secret word.
   */
  public int calcLettersInGuess(String guess) {
    String secret = getSecretWord();
    int counter = 0;
    //checks if guess is secret word for optimization
    if (secret.equalsIgnoreCase(guess)) {
      return 5;
    } //if not, checks each letter of guess with each letter of secret
    else {
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
          if (secret.charAt(i) == guess.charAt(j)) {
            counter++;
          }
        }
      }
    }
    return counter;

  }

  /**
   * This method tests the guessWord argument to see if it is the secret word.
   * If it is, the game is over, the player has won, and so the game data is
   * updated accordingly.
   *
   * @param guessWord Word to test to see if it's this game's secret word.
   */
  public void guess(String guessWord) {
    //check if secret word
    String secret = getSecretWord();
    guesses.add(guessWord);
    if (guessWord.equalsIgnoreCase(secret)) {
      wordFound = true;
      giveUp();
    }
  }

  /**
   * Called when a player quits a game before guessing the secret word, not that
   * it simply calculates the end time. Also note that it does not change the
   * wordFound variable, since that is used to keep track of wins and losses.
   */
  public void giveUp() {
    endTime = new GregorianCalendar();
  }

  /**
   * Builds and returns a texual summary of this game.
   *
   * @return A textual summary of this game, including the secred word, the time
   * of the game, and a listing of all the guesses.
   */
  @Override
  public String toString() {
    // CALCULATE GAME TIEM USING HOURS : MINUTES : SECONDS
    long timeInMillis = this.getTimeOfGame();
    long hours = timeInMillis / MILLIS_IN_AN_HOUR;
    timeInMillis -= hours * MILLIS_IN_AN_HOUR;
    long minutes = timeInMillis / MILLIS_IN_A_MINUTE;
    timeInMillis -= minutes * MILLIS_IN_A_MINUTE;
    long seconds = timeInMillis / MILLIS_IN_A_SECOND;

    // AND NOW BUILD THE STRING SUMMARY. START WITH THE SECRET WORD
    String text = secretWord;

    // THEN ADD THE TIME OF GAME SUMMARIZED IN PARENTHESES
    String minutesText = "" + minutes;
    if (minutes < 10) {
      minutesText = "0" + minutesText;
    }
    String secondsText = "" + seconds;
    if (seconds < 10) {
      secondsText = "0" + secondsText;
    }
    text += " (" + hours + ":" + minutesText + ":" + secondsText + ") - ";

    // THEN ADD THE GUESSES
    Iterator<String> guessIt = guesses.iterator();
    int counter = 0;
    while (guessIt.hasNext()) {
      String guess = guessIt.next();

      // WE'LL ADD A COMMA BEFORE EACH GUESS IN THE LIST, 
      // EXCEPT THE FIRST ONE OF COURSE
      if (counter == 0) {
        text += guess;
      } else {
        text += ", " + guess;
      }
      counter++;
    }
    return text;
  }
}