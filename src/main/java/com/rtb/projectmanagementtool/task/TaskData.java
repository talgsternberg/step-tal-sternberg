package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.Entity;
import java.util.HashSet;

/** Enum containing status options for a task. */
enum Status {
  COMPLETE,
  INCOMPLETE
}

/** Class containing task data. */
public final class TaskData {

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
    status = (Status) entity.getProperty("status");
    users = (HashSet<Long>) entity.getProperty("users");
    subtasks = (HashSet<Long>) entity.getProperty("subtasks");
  }

  public Entity toEntity() {
    Entity entity = new Entity("Task", taskID);
    entity.setProperty("projectID", projectID);
    entity.setProperty("name", name);
    entity.setProperty("description", description);
    entity.setProperty("status", status);
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
}
