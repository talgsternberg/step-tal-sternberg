package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.task.TaskData.Status;
import java.util.HashSet;

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

  public void addTaskBlocker(long taskID, long blockerID) {
    addTaskBlocker(new TaskBlockerData(taskID, blockerID));
  }

  public void addTaskBlocker(TaskBlockerData taskBlocker) {
    // Get task
    TaskData task;
    try {
      task = taskController.getTaskByID(taskBlocker.getTaskID());
    } catch (NullPointerException e) {
      System.out.println("Cannot find task with provided taskID");
      return;
    }
    // Get blocker
    TaskData blocker;
    try {
      blocker = taskController.getTaskByID(taskBlocker.getBlockerID());
    } catch (NullPointerException e) {
      System.out.println("Cannot find task with provided blockerID");
      return;
    }
    // Add taskBlocker
    if (task.getStatus() != Status.COMPLETE) {
      TransactionOptions options = TransactionOptions.Builder.withXG(true);
      Transaction transaction = datastore.beginTransaction(options);
      try {
        datastore.put(taskBlocker.toEntity());
        assert containsCycle() == false;
        transaction.commit();
      } finally {
        if (transaction.isActive()) {
          transaction.rollback();
        }
      }
    } else {
      System.out.println("Cannot block a task set to COMPLETE.");
    }
  }

  private boolean containsCycle() {
    // TODO: Implement cycle detection algorithm
    return false;
  }

  // Get methods

  public HashSet<TaskBlockerData> getAllTaskBlockers() {
    return getTaskBlockers(NO_FILTER);
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
}
