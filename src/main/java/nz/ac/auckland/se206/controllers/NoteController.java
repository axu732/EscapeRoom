package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.Countdown;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class NoteController {
  private static Countdown noteCountdown;

  public static Countdown getNoteCountdown() {
    return noteCountdown;
  }

  @FXML private Label codeText;
  @FXML private Button back;

  // Constructor
  public void initialize() {
    noteCountdown = new Countdown(121);
    GameState.timerCreated = true;
    noteCountdown.setOnUpdate(
        secondsLeft -> {
          if (secondsLeft <= 0) {
            Scene roomScene = codeText.getScene();
            roomScene.setRoot(SceneManager.getUiRoot(AppUi.FAIL));
          }
        });
    noteCountdown.start();
    // Generate the random numbers and display them
    int[] randomNumbers = GameState.generateRandomNumbers();
    displayRandomNumbers(randomNumbers);
  }

  // Method to display the random numbers on the label
  public void displayRandomNumbers(int[] numbers) {
    StringBuilder sb = new StringBuilder();
    for (int number : numbers) {
      sb.append(number);
    }
    codeText.setText(sb.toString());
  }

  public void clickBack(MouseEvent event) {
    System.out.println("back button pressed");
    Button button = (Button) event.getSource();
    Scene roomScene = button.getScene();
    roomScene.setRoot(SceneManager.getUiRoot(AppUi.ROOM_CHAT));
  }
}
