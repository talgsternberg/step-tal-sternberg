package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.Entity;
import java.io.Serializable;
import java.util.ArrayList;

/** Class containing task data. */
public final class TaskData implements Serializable, Comparable<TaskData> {
  /** Enum containing status options for a task. */
  public enum Status {
    COMPLETE,
    INCOMPLETE
  }

  private long taskID;
  private long parentTaskID;
  private long projectID;
  private String name;
  private String description;
  private Status status;
  private ArrayList<Long> users;

  public TaskData(
      long taskID,
      long parentTaskID,
      long projectID,
      String name,
      String description,
      Status status,
      ArrayList<Long> users) {
    this.taskID = taskID;
    this.parentTaskID = parentTaskID;
    this.projectID = projectID;
    this.name = name;
    this.description = description;
    this.status = status;
    this.users = users;
  }

  public TaskData(
      long parentTaskID,
      long projectID,
      String name,
      String description,
      Status status,
      ArrayList<Long> users) {
    this.taskID = 0;
    this.parentTaskID = parentTaskID;
    this.projectID = projectID;
    this.name = name;
    this.description = description;
    this.status = status;
    this.users = users;
  }

  public TaskData(
      long projectID, String name, String description, Status status, ArrayList<Long> users) {
    this.taskID = 0;
    this.parentTaskID = 0;
    this.projectID = projectID;
    this.name = name;
    this.description = description;
    this.status = status;
    this.users = users;
  }

  public TaskData(long parentTaskID, long projectID, String name, String description) {
    this.taskID = 0;
    this.parentTaskID = parentTaskID;
    this.projectID = projectID;
    this.name = name;
    this.description = description;
    this.status = Status.INCOMPLETE;
    this.users = new ArrayList<>();
  }

  public TaskData(long projectID, String name, String description) {
    this.taskID = 0;
    this.parentTaskID = 0;
    this.projectID = projectID;
    this.name = name;
    this.description = description;
    this.status = Status.INCOMPLETE;
    this.users = new ArrayList<>();
  }

  public TaskData(Entity entity) {
    taskID = entity.getKey().getId();
    parentTaskID = (long) entity.getProperty("parentTaskID");
    projectID = (long) entity.getProperty("projectID");
    name = (String) entity.getProperty("name");
    description = (String) entity.getProperty("description");
    status = Status.valueOf((String) entity.getProperty("status"));
    if (entity.getProperty("users") == null) {
      users = new ArrayList<Long>();
    } else {
      users = (ArrayList<Long>) entity.getProperty("users");
    }
  }

  public Entity toEntity() {
    Entity entity;
    if (taskID != 0) {
      entity = new Entity("Task", taskID);
    } else {
      entity = new Entity("Task");
    }
    entity.setProperty("parentTaskID", parentTaskID);
    entity.setProperty("projectID", projectID);
    entity.setProperty("name", name);
    entity.setProperty("description", description);
    entity.setProperty("status", status.name());
    if (!users.isEmpty()) {
      entity.setProperty("users", users);
    }
    return entity;
  }

  public long getTaskID() {
    return taskID;
  }

  public long getParentTaskID() {
    return parentTaskID;
  }

  public long getProjectID() {
    return projectID;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Status getStatus() {
    return status;
  }

  public ArrayList<Long> getUsers() {
    return users;
  }

  public void setTaskID(long taskID) {
    this.taskID = taskID;
  }

  public void setParentTaskID(long parentTaskID) {
    this.parentTaskID = parentTaskID;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setUsers(ArrayList<Long> users) {
    this.users = users;
  }

  @Override
  public int compareTo(TaskData task) {
    long dif = this.taskID - task.taskID;
    return (int) dif;
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Task ID: " + taskID + "\n";
    returnString += "Parent Task ID: " + parentTaskID + "\n";
    returnString += "Project ID: " + projectID + "\n";
    returnString += "Name: " + name + "\n";
    returnString += "Description: " + description + "\n";
    returnString += "Status: " + status.name() + "\n";
    returnString += "Users: " + users.toString() + "\n}";
    return returnString;
  }

  private boolean equals(TaskData a, TaskData b) {
    return a.taskID == b.taskID
        && a.parentTaskID == b.parentTaskID
        && a.projectID == b.projectID
        && a.name.equals(b.name)
        && a.description.equals(b.description)
        && a.status == b.status
        && a.users.equals(b.users);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TaskData && equals(this, (TaskData) other);
  }

  //   @Override
  //   public int hashCode() {
  //     return Long.hashCode(taskID);
  //   }
}
