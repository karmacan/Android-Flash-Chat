package edu.example.af_flash_chat;

// CLASS FOR OBJECTS WITCH STORING IN FIREBASE
public class MessageInstance {

  private String message;
  private String author;

  public MessageInstance(String message, String author) {
    this.message = message;
    this.author = author;
  }

  // FIREBASE REQUIRINGS
  public MessageInstance() {

  }

  public String getMessage() {
    return message;
  }

  public String getAuthor() {
    return author;
  }
}
