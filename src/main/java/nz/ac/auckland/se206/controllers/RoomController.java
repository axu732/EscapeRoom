package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.Countdown;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

/** Controller class for the room view. */
public class RoomController {

  private static Countdown roomCountdown;

  public static Countdown getRoomCountdown() {
    return roomCountdown;
  }

  @FXML private Rectangle bottle;
  @FXML private Rectangle window;
  @FXML private Rectangle chest;
  @FXML private Rectangle candle;
  @FXML private Rectangle roofCandle;
  @FXML private Rectangle note;
  @FXML private Rectangle shelf;
  @FXML private Rectangle chair;
  @FXML private Rectangle safe;

  @FXML private Label countdownLabel;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Initialization code goes here
    roomCountdown = new Countdown(121);
    GameState.timerCreated = true;
    roomCountdown.setOnUpdate(
        secondsLeft -> {
          Platform.runLater(() -> countdownLabel.setText(roomCountdown.formatTime(secondsLeft)));
          if (secondsLeft <= 0) {
            Scene roomScene = countdownLabel.getScene();
            roomScene.setRoot(SceneManager.getUiRoot(AppUi.FAIL));
          }
        });
    roomCountdown.start();
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("key " + event.getCode() + " released");
  }

  /**
   * Displays a dialog box with the given title, header text, and message.
   *
   * @param title the title of the dialog box
   * @param headerText the header text of the dialog box
   * @param message the message content of the dialog box
   */
  private void showDialog(String title, String headerText, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Handles the click event on the door.
   *
   * @param event the mouse event
   * @throws IOException if there is an error loading the chat view
   */
  @FXML
  public void clickNote(MouseEvent event) throws IOException {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to get the treasure");
      return;
    }
    System.out.println("note clicked");
    Rectangle rectangle = (Rectangle) event.getSource();
    Scene safeScreen = rectangle.getScene();
    safeScreen.setRoot(SceneManager.getUiRoot(AppUi.NOTE));
  }

  /**
   * Handles the click event on the chest.
   *
   * @param event the mouse event
   */
  @FXML
  public void clickChest(MouseEvent event) {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to solve the room");
      return;
    }
    System.out.println("chest clicked");

    if (!GameState.isRiddleResolved && !GameState.isKeyFound) {
      showDialog("Info", "Locked", "We need to find the key first!");
      return;
    }
    if (GameState.isRiddleResolved && !GameState.isKeyFound) {
      showDialog("Info", "Locked", "You know where the key is, now find it!");
      return;
    }
    roomCountdown.stop();
    SafeController.getSafeCountdown().stop();
    NoteController.getNoteCountdown().stop();
    showDialog("You Won!", "", "You unlocked the chest and got all the treasure!");
    Scene roomScene = countdownLabel.getScene();
    roomScene.setCursor(Cursor.DEFAULT);
    roomScene.setRoot(SceneManager.getUiRoot(AppUi.ESCAPE));
    GameState.allowTaunting = false;
  }

  @FXML
  public void hoverChest(MouseEvent event) {
    if (GameState.isKeyFound) {
      Rectangle rectangle = (Rectangle) event.getSource();
      Image image = new Image("images/gold-2024590_640.png");
      rectangle.setCursor(new ImageCursor(image));
    }
  }

  @FXML
  public void clickBottle(MouseEvent event) {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to solve the room");
      return;
    }
    System.out.println("bottle clicked");
    if (!GameState.isRiddleResolved && !GameState.isKeyFound) {
      showDialog(
          "Info",
          "Something's in there...",
          "There's something in the bottle..but you can't make it out");
      return;
    }

    if (GameState.isRiddleResolved && !GameState.isKeyFound) {
      showDialog("Info", "Key Found", "You found a key inside the bottle!");
      GameState.isKeyFound = true;
      Image image = new Image("images/gold-2024590_640.png");
      Scene roomScene = countdownLabel.getScene();
      roomScene.setCursor(new ImageCursor(image));
      return;
    } else {
      showDialog("Info", "Key Found", "You already have the key, now go open the treasure!");
    }
  }

  @FXML
  public void clickCandle(MouseEvent event) {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to solve the room");
      return;
    }
    System.out.println("candle clicked");
    showDialog("Info", "Nothing...", "Nothing seems to be here");
  }

  @FXML
  public void clickSafe(MouseEvent event) {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to solve the room");
      return;
    }
    if (GameState.safeOpened) {
      showDialog("Info", "Safe Opened", "You already opened the safe!");
      return;
    }
    System.out.println("safe clicked");
    Rectangle rectangle = (Rectangle) event.getSource();
    Scene safeScreen = rectangle.getScene();
    safeScreen.setRoot(SceneManager.getUiRoot(AppUi.SAFE));
  }

  @FXML
  public void clickShelf(MouseEvent event) {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to solve the room");
      return;
    }
    System.out.println("shelf clicked");
    if (!GameState.safeOpened) {
      showDialog(
          "Info",
          "Something is written on the books here",
          "But without a cipher you can't read it");
      return;
    }
    GameState.shelfClickedWithCipher = true;
    showDialog(
        "Info",
        "You hand the cipher to the assistant",
        "They translate some symbols on the books for you");
  }

  @FXML
  public void clickChair(MouseEvent event) {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to solve the room");
      return;
    }
    System.out.println("chair clicked");
    showDialog("Info", "Nothing...", "Nothing seems to be here");
  }

  @FXML
  public void clickRoofCandle(MouseEvent event) {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to solve the room");
      return;
    }
    System.out.println("roof candle clicked");
    showDialog("Info", "Nothing...", "Nothing seems to be here");
  }

  /**
   * Handles the click event on the window.
   *
   * @param event the mouse event
   */
  @FXML
  public void clickWindow(MouseEvent event) {
    if (roomCountdown.getSecondsLeft() <= 0) {
      showDialog("Time's Up!", "", "You failed to solve the room");
      return;
    }
    System.out.println("window clicked");
    showDialog(
        "Wow...", "The sea is so beautiful!", "But you can admire it after getting the treasure!");
  }
}
