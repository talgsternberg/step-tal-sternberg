package com.rtb.projectmanagementtool.privatecomment;

import com.google.appengine.api.datastore.Entity;
import java.util.Date;

/** Class containing private comments. */
public final class PrivateCommentData implements Comparable<PrivateCommentData> {

  private long commentID;
  private long taskID;
  private long userID;
  private String message;
  private Date timestamp;

  public PrivateCommentData(
      long commentID, long taskID, long userID, String message, Date timestamp) {
    this.commentID = commentID;
    this.taskID = taskID;
    this.userID = userID;
    this.message = message;
    this.timestamp = timestamp;
  }

  public PrivateCommentData(long taskID, long userID, String message) {
    this.commentID = 0;
    this.taskID = taskID;
    this.userID = userID;
    this.message = message;
    this.timestamp = new Date();
  }

  public PrivateCommentData(Entity entity) {
    commentID = entity.getKey().getId();
    taskID = (long) entity.getProperty("taskID");
    userID = (long) entity.getProperty("userID");
    message = (String) entity.getProperty("message");
    timestamp = (Date) entity.getProperty("timestamp");
  }

  public Entity toEntity() {
    Entity entity;
    if (commentID != 0) {
      entity = new Entity("PrivateComment", commentID);
    } else {
      entity = new Entity("PrivateComment");
    }
    entity.setProperty("taskID", taskID);
    entity.setProperty("userID", userID);
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

  public void setMessage(String message) {
    this.message = message;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public int compareTo(PrivateCommentData privateComment) {
    long dif = this.commentID - privateComment.commentID;
    return (int) dif;
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Private Comment ID: " + commentID + "\n";
    returnString += "Task ID: " + taskID + "\n";
    returnString += "User ID: " + userID + "\n";
    returnString += "Message: " + message + "\n";
    returnString += "Date: " + timestamp + "\n}";
    return returnString;
  }

  private boolean equals(PrivateCommentData a, PrivateCommentData b) {
    return a.commentID == b.commentID
        && a.taskID == b.taskID
        && a.userID == b.userID
        && a.message.equals(b.message)
        && a.timestamp.equals(b.timestamp);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof PrivateCommentData && equals(this, (PrivateCommentData) other);
  }
}
