package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

/** Controller class for the chat view. */
public class ChatController {
  @FXML private TextArea chatTextArea;
  @FXML private TextField inputText;
  @FXML private Button sendButton;

  private ChatCompletionRequest chatCompletionRequest;
  private static Timer tauntTimer;

  /**
   * Initializes the chat view, loading the riddle.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() {

    Task<Void> initTask =
        new Task<>() {
          @Override
          protected Void call() throws ApiProxyException {

            chatCompletionRequest =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);

            ChatMessage initialText =
                runGpt(
                    new ChatMessage("user", GptPromptEngineering.createCharacter()),
                    chatCompletionRequest);

            String responseText = initialText.getContent();

            // Speak the taunt message using text-to-speech
            App.getTextToSpeech().speak(responseText);
            return null;
          }
        };

    // Start the initialization task in a new thread
    Thread thread = new Thread(initTask);
    thread.setDaemon(true);
    thread.start();
    startTauntTimer();
  }

  public void startTauntTimer() {
    tauntTimer = new Timer();
    tauntTimer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            if (GameState.allowTaunting == false) {
              tauntTimer.cancel();
              App.getTextToSpeech().speak("Good work partner!");
              return;
            }

            if (GameState.riddleGenerated == false && GameState.shelfClickedWithCipher == true) {
              GameState.riddleGenerated = true;
              generateRiddle();
              return;
            }

            if (GameState.allowTaunting
                && RoomController.getRoomCountdown().getSecondsLeft() <= 0) {
              GameState.allowTaunting = false;
              tauntTimer.cancel();
              App.getTextToSpeech().speak("Oh no, the pirates caught us!");
              return;
            }

            int secondsLeft = RoomController.getRoomCountdown().getSecondsLeft();
            if (secondsLeft > 0
                && secondsLeft % 30 == 0
                && secondsLeft < 120) { // Check if seconds is a multiple of 30
              try {
                tauntPlayer();
              } catch (ApiProxyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }
        },
        0,
        1000); // Run every 1 second
  }

  private void tauntPlayer() throws ApiProxyException {
    if (!GameState.allowTaunting || RoomController.getRoomCountdown().getSecondsLeft() <= 0) {
      return;
    }
    Task<Void> tauntTask =
        new Task<>() {
          @Override
          protected Void call() throws ApiProxyException {
            ChatCompletionRequest tauntCompletionRequest =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);
            ChatMessage tauntText =
                runGpt(
                    new ChatMessage(
                        "user",
                        "Complain to user that they are taking too long and might get caught by the"
                            + " pirates. Keep it short, 1 sentence at most, yet variate."),
                    tauntCompletionRequest);

            String responseText = tauntText.getContent();

            // Speak the taunt message using text-to-speech
            App.getTextToSpeech().speak(responseText);

            return null;
          }
        };

    // Start the taunt task in a new thread
    Thread thread = new Thread(tauntTask);
    thread.setDaemon(true);
    thread.start();
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    chatTextArea.appendText(msg.getRole() + ": " + msg.getContent() + "\n\n");
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg, ChatCompletionRequest request)
      throws ApiProxyException {
    // Add the current user message to the conversation history
    request.addMessage(msg);

    // Remove earlier conversation history to keep only the last 3 messages
    while (request.getMessages().size() > 3) {
      request.getMessages().remove(1);
    }

    try {
      ChatCompletionResult chatCompletionResult = request.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();

      // Add the model-generated response to the conversation history
      request.addMessage(result.getChatMessage());

      // Append the model-generated response to display
      appendChatMessage(result.getChatMessage());

      // Print the usage prompt tokens for monitoring
      System.out.println(chatCompletionResult.getUsageTotalTokens());

      return result.getChatMessage();
    } catch (ApiProxyException e) {
      // TODO handle exception appropriately
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) {
    // No time response
    if (RoomController.getRoomCountdown().getSecondsLeft() <= 0) {
      appendChatMessage(new ChatMessage("assistant", "It's over!!! The pirates found us!"));
      return;
    }
    // No need for more hints, the riddle is done.
    if (GameState.isRiddleResolved == true) {
      appendChatMessage(
          new ChatMessage(
              "assistant", "Alright, now we know where to get that key to the treasure!"));
      return;
    }

    String message = inputText.getText();
    if (message.trim().isEmpty()) {
      return;
    }
    inputText.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);

    // Create a Task<ChatMessage> to run the GPT API call in a separate thread
    Task<ChatMessage> gptTask =
        new Task<ChatMessage>() {
          @Override
          protected ChatMessage call() throws ApiProxyException {
            Platform.runLater(
                () -> appendChatMessage(new ChatMessage("assistant", "Let me think..")));
            ;
            try {
              ChatMessage response = runGpt(msg, chatCompletionRequest);
              return response;
            } catch (ApiProxyException e) {
              // TODO handle exception appropriately
              e.printStackTrace();
              return null;
            }
          }
        };

    // Start the task in a new thread
    Thread thread = new Thread(gptTask);
    thread.setDaemon(true);
    thread.start();

    // After the task is completed, you can optionally add code to run
    // when the task is finished using the setOnSucceeded method.
    gptTask.setOnSucceeded(
        e -> {
          ChatMessage response = gptTask.getValue(); // Get the GPT-generated response
          if (response != null && response.getContent().toLowerCase().contains("hazzuh")) {
            GameState.isRiddleResolved = true;
          }
        });
  }

  public static void stopTauntTimer() {
    if (tauntTimer != null) {
      tauntTimer.cancel();
      tauntTimer = null;
    }
  }

  public void generateRiddle() {
    GameState.allowTaunting = false;
    Task<Void> riddleTask =
        new Task<>() {
          @Override
          protected Void call() throws ApiProxyException {
            chatCompletionRequest.getMessages().clear();
            chatCompletionRequest =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);
            runGpt(
                new ChatMessage("user", GptPromptEngineering.getRiddle()), chatCompletionRequest);
            return null;
          }
        };
    Thread thread = new Thread(riddleTask);
    thread.setDaemon(true);
    thread.start();
    GameState.allowTaunting = true;
  }
}
