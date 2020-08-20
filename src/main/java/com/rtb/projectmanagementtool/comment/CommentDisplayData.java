package com.rtb.projectmanagementtool.comment;

/** Class containing comments. */
public final class CommentDisplayData {

  private CommentData comment;
  private String username;

  public CommentDisplayData(CommentData comment, String username) {
    this.comment = comment;
    this.username = username;
  }

  public CommentData getComment() {
    return comment;
  }

  public String getUsername() {
    return username;
  }

  public void setComment(CommentData comment) {
    this.comment = comment;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Comment: " + comment + "\n";
    returnString += "Username: " + username + "\n}";
    return returnString;
  }

  private boolean equals(CommentDisplayData a, CommentDisplayData b) {
    return a.comment.equals(b.comment) && a.username.equals(b.username);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof CommentDisplayData && equals(this, (CommentDisplayData) other);
  }
}
