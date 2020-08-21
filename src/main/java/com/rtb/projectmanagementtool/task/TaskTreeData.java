package com.rtb.projectmanagementtool.task;

import java.util.ArrayList;

/** Class containing task data. */
public final class TaskTreeData {
  /** Enum containing status options for a task. */
  public enum Status {
    COMPLETE,
    INCOMPLETE
  }

  private TaskData task;
  private ArrayList<TaskTreeData> subtasks;

  public TaskTreeData(TaskData task, ArrayList<TaskTreeData> subtasks) {
    this.task = task;
    this.subtasks = subtasks;
  }

  public TaskTreeData(TaskData task) {
    this.task = task;
    this.subtasks = new ArrayList<>();
  }

  public TaskData getTask() {
    return task;
  }

  public ArrayList<TaskTreeData> getSubtasks() {
    return subtasks;
  }

  public void setTask(TaskData taskID) {
    this.task = task;
  }

  public void setSubtasks(ArrayList<TaskTreeData> subtasks) {
    this.subtasks = subtasks;
  }

  public TaskTreeData addSubtask(TaskData task) {
    TaskTreeData subtask = new TaskTreeData(task);
    this.subtasks.add(subtask);
    return subtask;
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Task: " + task + "\n";
    returnString += "Subtasks: " + subtasks.toString() + "\n}";
    return returnString;
  }

  private boolean equals(TaskTreeData a, TaskTreeData b) {
    return a.task.equals(b.task) && a.subtasks.equals(b.subtasks);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TaskTreeData && equals(this, (TaskTreeData) other);
  }
}
