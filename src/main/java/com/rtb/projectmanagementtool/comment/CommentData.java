package com.rtb.projectmanagementtool.comment;

import com.google.appengine.api.datastore.Entity;
import java.util.Date;

/** Class containing comments. */
public final class CommentData {

  private final long commentID;
  private final long taskID;
  private final long userID;
  private final String title;
  private final String message;
  private final Date timestamp;

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
}
