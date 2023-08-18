package nz.ac.auckland.se206;

import java.util.Random;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isKeyFound = false;

  // Indicates the timer is created
  public static boolean timerCreated = false;

  public static boolean allowTaunting = true;

  public static boolean safeOpened = false;

  public static int[] randomNumbers;

  public static boolean riddleGenerated = false;

  public static boolean shelfClickedWithCipher = false;

  // Method to generate random numbers
  public static int[] generateRandomNumbers() {
    int[] randomNumbers = new int[4];
    Random random = new Random();

    for (int i = 0; i < randomNumbers.length; i++) {
      randomNumbers[i] = random.nextInt(9) + 1; // Generate numbers between 1 and 9 (inclusive)
    }
    setRandomNumbers(randomNumbers);
    return randomNumbers;
  }

  private static void setRandomNumbers(int[] num) {
    randomNumbers = num;
  }

  public static int[] getRandomNumbers() {
    return randomNumbers;
  }
}
