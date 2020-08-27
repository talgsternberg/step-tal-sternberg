package com.rtb.projectmanagementtool.taskblocker;

import com.rtb.projectmanagementtool.task.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Class containing task blocker graph nodes. */
public final class TaskBlockerGraph implements Serializable {

  private Map<TaskData, ArrayList<TaskData>> vertices;

  public TaskBlockerGraph() {
    this.vertices = new HashMap<>();
  }

  public void addEdge(TaskData task, TaskData blocker) {
    vertices.putIfAbsent(task, new ArrayList<>());
    vertices.get(task).add(blocker);
  }

  public void removeEdge(TaskData task, TaskData blocker) {
    vertices.get(task).remove(blocker);
    if (vertices.get(task).equals(new ArrayList<>())) {
      vertices.remove(task);
    }
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    for (Map.Entry<TaskData, ArrayList<TaskData>> entry : vertices.entrySet()) {
      returnString += "{\n";
      returnString += "Task: " + entry.getKey() + "\n";
      returnString += "Blockers: " + entry.getValue() + "\n}";
    }
    return returnString += "\n}";
  }

  private boolean equals(TaskBlockerGraph a, TaskBlockerGraph b) {
    return a.vertices.equals(b.vertices);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TaskBlockerGraph && equals(this, (TaskBlockerGraph) other);
  }
}
