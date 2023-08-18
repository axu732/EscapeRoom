package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.controllers.ChatController;
import nz.ac.auckland.se206.controllers.NoteController;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.controllers.SafeController;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {
  private static TextToSpeech textToSpeech; // Declare the instance here

  public static void main(final String[] args) {
    launch();
  }

  public static void setRoot(String fxml) throws IOException {
    Scene currentScene = new Scene(SceneManager.getUiRoot(AppUi.ROOM_CHAT), 680, 470);
    getCurrentStage().setScene(currentScene);
    getCurrentStage().show();
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    textToSpeech = new TextToSpeech();
    setCurrentStage(stage);
    SceneManager.addUi(AppUi.ROOM_CHAT, loadFxml("roomchat"));
    SceneManager.addUi(AppUi.SAFE, loadFxml("safe"));
    SceneManager.addUi(AppUi.NOTE, loadFxml("note"));
    SceneManager.addUi(AppUi.FAIL, loadFxml("fail"));
    SceneManager.addUi(AppUi.ESCAPE, loadFxml("escape"));
    setRoot("roomchat"); // Load the "roomchat" FXML, which contains the SplitPane with "room" and
    // "chat"

    stage.setOnCloseRequest(
        event -> {
          if (GameState.timerCreated == true) {
            RoomController.getRoomCountdown().stop();
            SafeController.getSafeCountdown().stop();
            NoteController.getNoteCountdown().stop();
            GameState.timerCreated = false;
            ChatController.stopTauntTimer(); // Call the method to stop taunt timer
            textToSpeech.terminate(); // Terminate text to speech service
          }
        });
  }

  // Helper methods to handle stage and scene management
  private static Stage currentStage;

  private static void setCurrentStage(Stage stage) {
    currentStage = stage;
  }

  public static Stage getCurrentStage() {
    return currentStage;
  }

  public static TextToSpeech getTextToSpeech() {
    return textToSpeech;
  }
}
