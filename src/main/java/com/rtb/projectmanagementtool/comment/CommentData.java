package com.rtb.projectmanagementtool.comment;

import com.google.appengine.api.datastore.Entity;
import java.util.Date;

/** Class containing comments. */
public final class CommentData implements Comparable<CommentData> {

  private long commentID;
  private long taskID;
  private long userID;
  private String title;
  private String message;
  private Date timestamp;

  public CommentData(
      long commentID, long taskID, long userID, String title, String message, Date timestamp) {
    this.commentID = commentID;
    this.taskID = taskID;
    this.userID = userID;
    this.title = title;
    this.message = message;
    this.timestamp = timestamp;
  }

  public CommentData(long taskID, long userID, String title, String message) {
    this.commentID = 0;
    this.taskID = taskID;
    this.userID = userID;
    this.title = title;
    this.message = message;
    this.timestamp = new Date();
  }

  public CommentData(Entity entity) {
    commentID = entity.getKey().getId();
    taskID = (long) entity.getProperty("taskID");
    userID = (long) entity.getProperty("userID");
    title = (String) entity.getProperty("title");
    message = (String) entity.getProperty("message");
    timestamp = (Date) entity.getProperty("timestamp");
  }

  public Entity toEntity() {
    Entity entity;
    if (commentID != 0) {
      entity = new Entity("Comment", commentID);
    } else {
      entity = new Entity("Comment");
    }
    entity.setProperty("taskID", taskID);
    entity.setProperty("userID", userID);
    entity.setProperty("title", title);
    entity.setProperty("message", message);
    entity.setProperty("timestamp", timestamp);
    return entity;
  }

  public long getCommentID() {
    return commentID;
  }

  public long getTaskID() {
    return taskID;
  }

  public long getUserID() {
    return userID;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setCommentID(long commentID) {
    this.commentID = commentID;
  }

  public void setTaskID(long taskID) {
    this.taskID = taskID;
  }

  public void setUserID(long userID) {
    this.userID = userID;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public int compareTo(CommentData comment) {
    long dif = this.commentID - comment.commentID;
    return (int) dif;
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Comment ID: " + commentID + "\n";
    returnString += "Task ID: " + taskID + "\n";
    returnString += "User ID: " + userID + "\n";
    returnString += "Title: " + title + "\n";
    returnString += "Message: " + message + "\n";
    returnString += "Date: " + timestamp + "\n}";
    return returnString;
  }

  private boolean equals(CommentData a, CommentData b) {
    return a.commentID == b.commentID
        && a.taskID == b.taskID
        && a.userID == b.userID
        && a.title.equals(b.title)
        && a.message.equals(b.message)
        && a.timestamp.equals(b.timestamp);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof CommentData && equals(this, (CommentData) other);
  }
}
