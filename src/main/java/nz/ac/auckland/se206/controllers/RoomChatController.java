package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;

public class RoomChatController {

  @FXML
  private RoomController roomIncludeController; // Inject the RoomController specified in room.fxml

  @FXML
  private ChatController chatIncludeController; // Inject the ChatController specified in chat.fxml

  // Other instance variables and methods for handling interactions between the components
  // ...

  public void initialize() {
    // Perform any initialization logic here, if needed.
  }
}
