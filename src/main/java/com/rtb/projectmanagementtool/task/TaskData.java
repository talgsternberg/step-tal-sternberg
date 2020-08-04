package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;

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
  private ArrayList<Long> users;
  private ArrayList<Long> subtasks;

  public TaskData(
      long taskID,
      long projectID,
      String name,
      String description,
      Status status,
      ArrayList<Long> users,
      ArrayList<Long> subtasks) {
    this.taskID = taskID;
    this.projectID = projectID;
    this.name = name;
    this.description = description;
    this.status = status;
    this.users = users;
    this.subtasks = subtasks;
  }

  public TaskData(
      long projectID,
      String name,
      String description,
      Status status,
      ArrayList<Long> users,
      ArrayList<Long> subtasks) {
    // this.taskID = 0;
    this.projectID = projectID;
    this.name = name;
    this.description = description;
    this.status = status;
    this.users = users;
    this.subtasks = subtasks;
  }

  public TaskData(Entity entity) {
    taskID = entity.getKey().getId();
    projectID = (long) entity.getProperty("projectID");
    name = (String) entity.getProperty("name");
    description = (String) entity.getProperty("description");
    status = Status.valueOf((String) entity.getProperty("status"));
    if (entity.getProperty("users") == null) {
      users = new ArrayList<Long>();
    } else {
      users = (ArrayList<Long>) entity.getProperty("users");
    }
    if (entity.getProperty("subtasks") == null) {
      subtasks = new ArrayList<Long>();
    } else {
      subtasks = (ArrayList<Long>) entity.getProperty("subtasks");
    }
  }

  public Entity toEntity() {
    Entity entity;
    if (taskID != 0) {
      entity = new Entity("Task", taskID);
    } else {
      entity = new Entity("Task");
    }
    // entity.setProperty("taskID", entity.getKey().getId());
    entity.setProperty("projectID", projectID);
    entity.setProperty("name", name);
    entity.setProperty("description", description);
    entity.setProperty("status", status.name());
    if (!users.isEmpty()) {
      entity.setProperty("users", users);
    }
    if (!subtasks.isEmpty()) {
      entity.setProperty("subtasks", subtasks);
    }
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

  public ArrayList<Long> getUsers() {
    return users;
  }

  public ArrayList<Long> getSubtasks() {
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

  public void setUsers(ArrayList<Long> users) {
    this.users = users;
  }

  public void setSubtasks(ArrayList<Long> subtasks) {
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
