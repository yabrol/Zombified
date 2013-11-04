package jotto.game;

import jotto.Jotto.JottoPropertyType;
import properties_manager.PropertiesManager;

/**
 * This exception class represents the situation where an attempt
 * is made to use a word that is not legal in Jotto. For example,
 * a word not in the dictionary.
 * 
 * @author Richard McKenna, Yukti Abrol
 */
public class WrongGuessException extends Exception
{
    // THIS WILL STORE INFORMATION ABOUT THE ILLEGAL
    // WORD THAT CAUSED THIS EXCEPTION
    private String wrongGuessWord;

    /**
     * This constructor will keep the illegal word information
     * so that whoever catches this exception may use it in
     * providing informative feedback.
     * 
     * @param initWrongGuessWord The illegal word that 
     * caused the exception.
     */
    public WrongGuessException(String initWrongGuessWord)
    {
        // STORE THE ILLEGAL WORD SO THAT WE MAY
        // PROVIDE FEEDBACK IF WE WISH
        wrongGuessWord = initWrongGuessWord;
    }
    
    /**
     * This method returns a textual summary of this exception.
     * 
     * @return A textual summary of this exception, which can
     * be used to provide feedback.
     */
    @Override
    public String toString()
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String illegalWordFeedback = props.getProperty(JottoPropertyType.WRONG_WORD_ERROR_TEXT);
        return wrongGuessWord + illegalWordFeedback;
    }
}