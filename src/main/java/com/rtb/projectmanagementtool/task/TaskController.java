package com.rtb.projectmanagementtool.task;

import java.util.HashSet;

/** Class controlling the TaskData object. */
public final class TaskController {

  private TaskData taskData;

  public TaskController(TaskData taskData) {
    this.taskData = taskData;
  }

  // Is there a way to avoid retyping all of these getters and setters?

  public long getTaskID() {
    return taskData.getTaskID();
  }

  public long getProjectID() {
    return taskData.getProjectID();
  }

  public String getName() {
    return taskData.getName();
  }

  public String getDescription() {
    return taskData.getDescription();
  }

  public Status getStatus() {
    return taskData.getStatus();
  }

  public HashSet<Long> getUsers() {
    return taskData.getUsers();
  }

  public HashSet<Long> getSubtasks() {
    return taskData.getSubtasks();
  }

  public void setName(String name) {
    taskData.setName(name);
  }

  public void setDescription(String description) {
    taskData.setDescription(description);
  }

  public void setStatus(Status status) {
    taskData.setStatus(status);
  }

  // Methods that limit the user's actions

  public void addUser(long userID) {
    HashSet<Long> users = taskData.getUsers();
    users.add(userID);
    taskData.setUsers(users);
  }

  public void removeUser(long userID) {
    HashSet<Long> users = taskData.getUsers();
    users.remove(userID);
    taskData.setUsers(users);
  }

  public void addSubtask(long taskID) {
    HashSet<Long> subtasks = taskData.getSubtasks();
    subtasks.add(taskID);
    taskData.setSubtasks(subtasks);
  }

  public void removeSubtask(long taskID) {
    HashSet<Long> subtasks = taskData.getSubtasks();
    subtasks.remove(taskID);
    taskData.setSubtasks(subtasks);
  }

}
