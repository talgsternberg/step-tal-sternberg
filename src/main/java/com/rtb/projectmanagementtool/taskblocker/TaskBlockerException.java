package com.rtb.projectmanagementtool.taskblocker;

public class TaskBlockerException extends RuntimeException {
  public TaskBlockerException(String message, Throwable error) {
    super(message, error);
  }

  public TaskBlockerException(String message) {
    super(message);
  }
}
