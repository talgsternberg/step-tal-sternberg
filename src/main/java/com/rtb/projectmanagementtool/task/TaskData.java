package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import java.util.HashSet;

/** Enum containing status options for a task. */
enum Status {
  COMPLETE,
  INCOMPLETE
}

/** Class containing task data. */
public final class TaskData implements Comparable<TaskData> {

  private long taskID;
  private long projectID;
  private String name;
  private String description;
  private Status status;
  private HashSet<Long> users;
  private HashSet<Long> subtasks;

  public TaskData(
      long taskID,
      long projectID,
      String name,
      String description,
      Status status,
      HashSet<Long> users,
      HashSet<Long> subtasks) {
    this.taskID = taskID;
    this.projectID = projectID;
    this.name = name;
    this.description = description;
    this.status = status;
    this.users = users;
    this.subtasks = subtasks;
  }

  public TaskData(Entity entity) {
    taskID = (long) entity.getKey().getId();
    projectID = (long) entity.getProperty("projectID");
    name = (String) entity.getProperty("name");
    description = (String) entity.getProperty("description");
    status = Status.valueOf((String) entity.getProperty("status"));
    try {
      users = new HashSet<Long>((ArrayList<Long>) entity.getProperty("users"));
    } catch (ClassCastException e) {
      users = (HashSet<Long>) entity.getProperty("users");
    } catch (NullPointerException e) {
      users = new HashSet<Long>();
    }
    try {
      subtasks = new HashSet<Long>((ArrayList<Long>) entity.getProperty("subtasks"));
    } catch (ClassCastException e) {
      subtasks = (HashSet<Long>) entity.getProperty("subtasks");
    } catch (NullPointerException e) {
      subtasks = new HashSet<Long>();
    }
  }

  public Entity toEntity() {
    Entity entity = new Entity("Task", taskID);
    entity.setProperty("projectID", projectID);
    entity.setProperty("name", name);
    entity.setProperty("description", description);
    entity.setProperty("status", status.name());
    entity.setProperty("users", users);
    entity.setProperty("subtasks", subtasks);
    return entity;
  }

  public long getTaskID() {
    return taskID;
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

  public HashSet<Long> getUsers() {
    return users;
  }

  public HashSet<Long> getSubtasks() {
    return subtasks;
  }

  public void setTaskID(long taskID) {
    this.taskID = taskID;
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

  public void setUsers(HashSet<Long> users) {
    this.users = users;
  }

  public void setSubtasks(HashSet<Long> subtasks) {
    this.subtasks = subtasks;
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
    returnString += "Project ID: " + projectID + "\n";
    returnString += "Name: " + name + "\n";
    returnString += "Description: " + description + "\n";
    returnString += "Status: " + status.name() + "\n";
    returnString += "Users: " + users.toString() + "\n";
    returnString += "Subtasks: " + subtasks.toString() + "\n}";
    return returnString;
  }

  private boolean equals(TaskData a, TaskData b) {
    return a.taskID == b.taskID
        && a.projectID == b.projectID
        && a.name.equals(b.name)
        && a.description.equals(b.description)
        && a.status == b.status
        && a.users.equals(b.users)
        && a.subtasks.equals(b.subtasks);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TaskData && equals(this, (TaskData) other);
  }
}
