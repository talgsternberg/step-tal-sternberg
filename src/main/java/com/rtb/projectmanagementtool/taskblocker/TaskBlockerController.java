package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.task.TaskData.Status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

/** Class controlling the TaskBlockerData object. */
public final class TaskBlockerController {
  private DatastoreService datastore;
  private TaskController taskController;
  private Filter NO_FILTER = null;

  public TaskBlockerController(DatastoreService datastore, TaskController taskController) {
    this.datastore = datastore;
    this.taskController = taskController;
  }

  // Add methods

  public void addTaskBlocker(long taskID, long blockerID) throws Exception {
    addTaskBlocker(new TaskBlockerData(taskID, blockerID));
  }

  public void addTaskBlocker(TaskBlockerData taskBlocker) throws Exception {
    // Ensure the tasks exist
    ArrayList<TaskData> tasks =
        taskController.getTasksByIDs(
            new ArrayList<>(Arrays.asList(taskBlocker.getTaskID(), taskBlocker.getBlockerID())));
    if (tasks.size() < 2) {
      throw new Exception("Cannot find tasks with provided taskID or blockerID");
    }
    // Ensure the blocked task isn't already set to COMPLETE
    if (tasks.get(0).getStatus() == Status.COMPLETE
        || tasks.get(1).getStatus() == Status.COMPLETE) {
      throw new Exception("One or more tasks are already set to COMPLETE");
    }
    // Ensure a cycle wouldn't be created if the TaskBlocker is added
    if (containsPath(taskBlocker.getBlockerID(), taskBlocker.getTaskID())) {
      throw new Exception("Cannot block a task if it would create a cycle");
    }
    // Add the TaskBlocker
    taskBlocker.setTaskBlockerID(datastore.put(taskBlocker.toEntity()).getId());
  }

  private boolean containsPath(long start, long end) {
    // Get taskBlockers that belong to the current project
    long projectID = taskController.getTaskByID(start).getProjectID();
    HashSet<TaskBlockerData> taskBlockers = getTaskBlockersByProjectID(projectID);
    // Initialize visited and queue
    HashSet<Long> visited = new HashSet<>();
    LinkedList<Long> queue = new LinkedList<>();
    visited.add(start);
    queue.add(start);
    Long blockerID;
    while (queue.size() != 0) {
      start = queue.poll();
      for (TaskBlockerData taskBlocker : taskBlockers) {
        if (taskBlocker.getTaskID() == start) {
          blockerID = taskBlocker.getBlockerID();
          if (blockerID == end) {
            return true;
          }
          if (!visited.contains(blockerID)) {
            visited.add(blockerID);
            queue.add(blockerID);
          }
        }
      }
    }
    return false;
  }

  // Build methods

  public String buildGraph(long projectID) {
    return "This is a map for " + Long.toString(projectID);
  }

  public String addEdge(String graph, long taskID, long blockerID) {
    return graph + ": " + Long.toString(taskID) + ", " + Long.toString(blockerID);
  }

  // Get methods

  public TaskBlockerData getTaskBlockerByID(long taskBlockerID) {
    Query query =
        new Query("TaskBlocker")
            .addFilter(
                "__key__",
                FilterOperator.EQUAL,
                KeyFactory.createKey("TaskBlocker", taskBlockerID));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    TaskBlockerData taskBlocker = new TaskBlockerData(entity);
    return taskBlocker;
  }

  public HashSet<TaskBlockerData> getAllTaskBlockers() {
    return getTaskBlockers(NO_FILTER);
  }

  public HashSet<TaskBlockerData> getTaskBlockersByProjectID(long projectID) {
    ArrayList<Long> taskIDs = new ArrayList<>();
    for (TaskData task : getTasksFromTaskBlockers(getAllTaskBlockers())) {
      if (task.getProjectID() == projectID) {
        taskIDs.add(task.getTaskID());
      }
    }
    if (taskIDs.isEmpty()) {
      return new HashSet<>();
    }
    Filter filter = new FilterPredicate("taskID", FilterOperator.IN, taskIDs);
    return getTaskBlockers(filter);
  }

  public ArrayList<TaskData> getBlockersForTask(long taskID) {
    HashSet<TaskBlockerData> taskBlockers = getTaskBlockers(taskID);
    ArrayList<Long> taskIDs = new ArrayList<>();
    for (TaskBlockerData taskBlocker : taskBlockers) {
      taskIDs.add(taskBlocker.getBlockerID());
    }
    return taskController.getTasksByIDs(taskIDs);
  }

  public HashSet<TaskBlockerData> getTaskBlockers(long taskID) {
    Filter filter = new FilterPredicate("taskID", FilterOperator.EQUAL, taskID);
    return getTaskBlockers(filter);
  }

  public HashSet<TaskBlockerData> getTaskBlockersByBlockerID(long blockerID) {
    Filter filter = new FilterPredicate("blockerID", FilterOperator.EQUAL, blockerID);
    return getTaskBlockers(filter);
  }

  private HashSet<TaskBlockerData> getTaskBlockers(Filter filter) {
    Query query = new Query("TaskBlocker");
    if (filter != NO_FILTER) {
      query.setFilter(filter);
    }
    PreparedQuery results = datastore.prepare(query);
    HashSet<TaskBlockerData> blockers = new HashSet<>();
    for (Entity entity : results.asIterable()) {
      TaskBlockerData blocker = new TaskBlockerData(entity);
      blockers.add(blocker);
    }
    return blockers;
  }

  // Delete methods

  public void deleteByBlockerID(long blockerID) {
    datastore.delete(getKeysFromTaskBlockers(getTaskBlockersByBlockerID(blockerID)));
  }

  // Conversion methods

  public HashSet<Long> getTaskBlockerIDsFromKeys(HashSet<Key> keys) {
    HashSet<Long> taskBlockerIDs = new HashSet<>();
    for (Key key : keys) {
      taskBlockerIDs.add(key.getId());
    }
    return taskBlockerIDs;
  }

  public HashSet<Long> getTaskBlockerIDsFromTaskBlockers(HashSet<TaskBlockerData> taskBlockers) {
    HashSet<Long> taskBlockerIDs = new HashSet<>();
    for (TaskBlockerData taskBlocker : taskBlockers) {
      taskBlockerIDs.add(taskBlocker.getTaskBlockerID());
    }
    return taskBlockerIDs;
  }

  public HashSet<Key> getKeysFromTaskBlockerIDs(HashSet<Long> taskBlockerIDs) {
    HashSet<Key> keys = new HashSet<>();
    for (long taskBlockerID : taskBlockerIDs) {
      keys.add(KeyFactory.createKey("TaskBlocker", taskBlockerID));
    }
    return keys;
  }

  public HashSet<Key> getKeysFromTaskBlockers(HashSet<TaskBlockerData> taskBlockers) {
    HashSet<Key> keys = new HashSet<>();
    for (TaskBlockerData taskBlocker : taskBlockers) {
      keys.add(KeyFactory.createKey("TaskBlocker", taskBlocker.getTaskBlockerID()));
    }
    return keys;
  }

  public ArrayList<TaskData> getTasksFromTaskBlockers(HashSet<TaskBlockerData> taskBlockers) {
    ArrayList<Long> taskIDs = new ArrayList<>();
    for (TaskBlockerData taskBlocker : taskBlockers) {
      taskIDs.add(taskBlocker.getTaskID());
    }
    return taskController.getTasksByIDs(taskIDs);
  }

  public ArrayList<TaskData> getBlockersFromTaskBlockers(HashSet<TaskBlockerData> taskBlockers) {
    ArrayList<Long> blockerIDs = new ArrayList<>();
    for (TaskBlockerData taskBlocker : taskBlockers) {
      blockerIDs.add(taskBlocker.getTaskID());
    }
    return taskController.getTasksByIDs(blockerIDs);
  }
}
