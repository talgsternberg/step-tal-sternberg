package com.rtb.projectmanagementtool.taskblocker;

import com.rtb.projectmanagementtool.task.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** Class containing task blocker graph nodes. */
public final class TaskBlockerGraph implements Serializable {

  private Map<Long, HashSet<Long>> vertices;

  public TaskBlockerGraph() {
    this.vertices = new HashMap<>();
  }

  public void addEdge(long taskID, long blockerID) {
    vertices.putIfAbsent(taskID, new HashSet<>());
    vertices.get(taskID).add(blockerID);
  }

  public void removeEdge(long taskID, long blockerID) {
    vertices.get(taskID).remove(blockerID);
    if (vertices.get(taskID).equals(new HashSet<>())) {
      vertices.remove(taskID);
    }
  }

  public void removeEdges(HashSet<TaskBlockerData> taskBlockers) {
    for (TaskBlockerData taskBlocker : taskBlockers) {
      removeEdge(taskBlocker.getTaskID(), taskBlocker.getBlockerID());
    }
  }

  public HashSet<Long> getBlockerIDs(long taskID) {
    if (!vertices.containsKey(taskID)) {
      return new HashSet<>();
    }
    return vertices.get(taskID);
  }

  public void buildGraph(HashSet<TaskBlockerData> taskBlockers) {
    for (TaskBlockerData taskBlocker : taskBlockers) {
      addEdge(taskBlocker.getTaskID(), taskBlocker.getBlockerID());
    }
  }

  @Override
  public String toString() {
    String returnString = "{\n";
    for (Map.Entry<Long, HashSet<Long>> entry : vertices.entrySet()) {
      returnString += "{\n";
      returnString += "TaskID: " + entry.getKey() + "\n";
      returnString += "BlockerIDs: " + entry.getValue() + "\n}";
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
