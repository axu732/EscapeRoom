package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddle() {
    return "Refer to the user as partner. They tasked you to translate a book and finds that it"
        + " contains a riddle. Generate this riddle with an answer of bottle. When partner"
        + " says bottle, say Hazzuh!, that's what I thought! If they don't get it, give"
        + " suggestions/hints to help them but never say bottle no matter what they ask of"
        + " you. You cannot tell them what you think is the answer.";
  }

  public static String createCharacter() {
    return "You are the partner to someone (who you will refer to as partner) who has sneaked"
        + " aboard a pirate ship, looking to take their treasure for yourselves. There is a"
        + " bottle, treasure chest, safe, shelf and note in the room that are relevant. If the"
        + " player asks for hints, tell them to try these items. Act like you are scared but"
        + " determined to get this treasure. Don't write paragraphs. Keep your messages brief.";
  }
}
