package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.Entity;

/** Class containing task blocker data. */
public final class TaskBlockerData {

  private long taskID;
  private long blockerID;

  public TaskBlockerData(long taskID, long blockerID) {
    this.taskID = taskID;
    this.blockerID = blockerID;
  }

  public TaskBlockerData(Entity entity) {
    taskID = (long) entity.getProperty("taskID");
    blockerID = (long) entity.getProperty("blockerID");
  }

  public Entity toEntity() {
    Entity entity = new Entity("TaskBlocker");
    entity.setProperty("taskID", taskID);
    entity.setProperty("blockerID", blockerID);
    return entity;
  }

  public long getTaskID() {
    return taskID;
  }

  public long getBlockerID() {
    return blockerID;
  }
}
