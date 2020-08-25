package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.Entity;

/** Class containing task blocker data. */
public final class TaskBlockerData {

  private long taskBlockerID;
  private long taskID;
  private long blockerID;

  public TaskBlockerData(long taskID, long blockerID) {
    this.taskID = taskID;
    this.blockerID = blockerID;
  }

  public TaskBlockerData(Entity entity) {
    taskBlockerID = entity.getKey().getId();
    taskID = (long) entity.getProperty("taskID");
    blockerID = (long) entity.getProperty("blockerID");
  }

  public Entity toEntity() {
    Entity entity;
    if (taskBlockerID != 0) {
      entity = new Entity("TaskBlocker", taskBlockerID);
    } else {
      entity = new Entity("TaskBlocker");
    }
    entity.setProperty("taskID", taskID);
    entity.setProperty("blockerID", blockerID);
    return entity;
  }

  public long getTaskBlockerID() {
    return taskBlockerID;
  }

  public long getTaskID() {
    return taskID;
  }

  public long getBlockerID() {
    return blockerID;
  }

  public void setTaskBlockerID(long taskBlockerID) {
    this.taskBlockerID = taskBlockerID;
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "TaskBlocker ID: " + taskBlockerID + "\n";
    returnString += "Task ID: " + taskID + "\n";
    returnString += "Blocker ID: " + blockerID + "\n}";
    return returnString;
  }

  private boolean equals(TaskBlockerData a, TaskBlockerData b) {
    return a.taskBlockerID == b.taskBlockerID && a.taskID == b.taskID && a.blockerID == b.blockerID;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TaskBlockerData && equals(this, (TaskBlockerData) other);
  }
}
