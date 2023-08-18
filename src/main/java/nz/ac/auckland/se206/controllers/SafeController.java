package nz.ac.auckland.se206.controllers;

import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.Countdown;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class SafeController {
  private static Countdown safeCountdown;

  public static Countdown getSafeCountdown() {
    return safeCountdown;
  }

  @FXML private Button one;
  @FXML private Button two;
  @FXML private Button three;
  @FXML private Button four;
  @FXML private Button five;
  @FXML private Button six;
  @FXML private Button seven;
  @FXML private Button eight;
  @FXML private Button nine;

  @FXML private Button enter;
  @FXML private Button clear;
  @FXML private Button back;

  @FXML private Label code;

  private StringBuilder enteredCode = new StringBuilder();

  public void initialize() {
    safeCountdown = new Countdown(121);
    GameState.timerCreated = true;
    safeCountdown.setOnUpdate(
        secondsLeft -> {
          if (secondsLeft <= 0) {
            Scene roomScene = code.getScene();
            roomScene.setRoot(SceneManager.getUiRoot(AppUi.FAIL));
          }
        });
    safeCountdown.start();
  }

  private void updateLabel() {
    code.setText(enteredCode.toString());
  }

  public void clickOne(MouseEvent event) {
    enteredCode.append("1");
    updateLabel();
  }

  public void clickTwo(MouseEvent event) {
    enteredCode.append("2");
    updateLabel();
  }

  public void clickThree(MouseEvent event) {
    enteredCode.append("3");
    updateLabel();
  }

  public void clickFour(MouseEvent event) {
    enteredCode.append("4");
    updateLabel();
  }

  public void clickFive(MouseEvent event) {
    enteredCode.append("5");
    updateLabel();
  }

  public void clickSix(MouseEvent event) {
    enteredCode.append("6");
    updateLabel();
  }

  public void clickSeven(MouseEvent event) {
    enteredCode.append("7");
    updateLabel();
  }

  public void clickEight(MouseEvent event) {
    enteredCode.append("8");
    updateLabel();
  }

  public void clickNine(MouseEvent event) {
    enteredCode.append("9");
    updateLabel();
  }

  public void clickEnter(MouseEvent event) {
    int[] enteredIntArray = enteredCode.chars().map(Character::getNumericValue).toArray();
    int[] correctCode = GameState.getRandomNumbers();
    // Reversed correctcode array
    int[] reversedCorrectCode = new int[correctCode.length];
    for (int i = 0; i < correctCode.length; i++) {
      reversedCorrectCode[i] = correctCode[correctCode.length - i - 1];
    }

    if (Arrays.equals(enteredIntArray, reversedCorrectCode)) {
      // Code is correct, perform scene transition
      Button enter = (Button) event.getSource();
      Scene roomScene = enter.getScene(); // Assuming 'enter' is the Button you pressed
      GameState.safeOpened = true;
      roomScene.setRoot(SceneManager.getUiRoot(AppUi.ROOM_CHAT));
      Alert cipherGet = new Alert(Alert.AlertType.INFORMATION);
      cipherGet.setTitle("Cipher obtained!");
      cipherGet.setHeaderText(
          "You have obtained a parchment with symbols with a corresponding alphabet.");
      cipherGet.setContentText("But where can we use it?");
      cipherGet.showAndWait();

    } else {
      // Code is incorrect, reset enteredCode and label
      enteredCode.setLength(0);
      code.setText("Wrong Code");
    }
  }

  public void clickClear(MouseEvent event) {
    enteredCode.setLength(0);
    code.setText("Enter Code");
  }

  public void clickBack(MouseEvent event) {
    System.out.println("back button pressed");
    Button button = (Button) event.getSource();
    Scene roomScene = button.getScene();
    roomScene.setRoot(SceneManager.getUiRoot(AppUi.ROOM_CHAT));
  }
}
