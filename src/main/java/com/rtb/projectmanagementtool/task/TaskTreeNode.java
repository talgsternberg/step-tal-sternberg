package com.rtb.projectmanagementtool.task;

import java.util.ArrayList;

/** Class containing task tree nodes. */
public final class TaskTreeNode {

  private TaskData parentTask;
  private ArrayList<TaskTreeNode> subtasks;

  public TaskTreeNode(TaskData parentTask, ArrayList<TaskTreeNode> subtasks) {
    this.parentTask = parentTask;
    this.subtasks = subtasks;
  }

  public TaskTreeNode(TaskData parentTask) {
    this.parentTask = parentTask;
    this.subtasks = new ArrayList<>();
  }

  public TaskData getParentTask() {
    return parentTask;
  }

  public ArrayList<TaskTreeNode> getSubtasks() {
    return subtasks;
  }

  public void setParentTask(TaskData parentTask) {
    this.parentTask = parentTask;
  }

  public void setSubtasks(ArrayList<TaskTreeNode> subtasks) {
    this.subtasks = subtasks;
  }

  public TaskTreeNode addSubtask(TaskData task) {
    TaskTreeNode subtask = new TaskTreeNode(task);
    this.subtasks.add(subtask);
    return subtask;
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Parent Task: " + parentTask + "\n";
    returnString += "Subtasks: " + subtasks.toString() + "\n}";
    return returnString;
  }

  private boolean equals(TaskTreeNode a, TaskTreeNode b) {
    return a.parentTask.equals(b.parentTask) && a.subtasks.equals(b.subtasks);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TaskTreeNode && equals(this, (TaskTreeNode) other);
  }
}
