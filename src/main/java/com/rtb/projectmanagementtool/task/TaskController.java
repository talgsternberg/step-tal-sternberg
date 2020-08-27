package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.task.TaskData.Status;
import com.rtb.projectmanagementtool.taskblocker.*;
import com.rtb.projectmanagementtool.user.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Class controlling the TaskData object. */
public final class TaskController {

  private DatastoreService datastore;
  private static final Filter NO_QUERY_FILTER = null;
  private static final int NO_QUERY_LIMIT = Integer.MAX_VALUE;
  private static final SortPredicate NO_QUERY_SORT = null;

  public TaskController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  // Add methods

  public void addTasks(ArrayList<TaskData> tasks) {
    ArrayList<Entity> taskEntities = new ArrayList<>();
    for (TaskData task : tasks) {
      taskEntities.add(task.toEntity());
    }
    addKeysToTasks(tasks, new ArrayList<>(datastore.put(taskEntities)));
  }

  public void addSubtasks(TaskData task, ArrayList<TaskData> subtasks) {
    for (TaskData subtask : subtasks) {
      subtask.setParentTaskID(task.getTaskID());
    }
    addTasks(subtasks);
  }

  private void addKeysToTasks(ArrayList<TaskData> tasks, ArrayList<Key> keys) {
    if (tasks.size() == keys.size()) {
      for (int i = 0; i < keys.size(); i++) {
        tasks.get(i).setTaskID(keys.get(i).getId());
      }
    }
  }

  // Update methods

  public void addUser(long taskID, long userID) {
    addUser(getTaskByID(taskID), userID);
  }

  public void addUser(TaskData task, long userID) {
    if (task.getStatus() != Status.COMPLETE && !task.getUsers().contains(userID)) {
      task.getUsers().add(userID);
      if (task.getTaskID() != 0) {
        datastore.put(task.toEntity());
      }
    }
  }

  public void removeUser(long taskID, long userID) {
    removeUser(getTaskByID(taskID), userID);
  }

  public void removeUser(TaskData task, long userID) {
    if (task.getStatus() != Status.COMPLETE && task.getUsers().contains(userID)) {
      task.getUsers().remove(userID);
      if (task.getTaskID() != 0) {
        datastore.put(task.toEntity());
      }
    }
  }

  public boolean setComplete(long taskID) {
    return setComplete(getTaskByID(taskID));
  }

  public boolean setComplete(TaskData task) {
    if (!allSubtasksAreComplete(task)) {
      return false;
    }
    TaskBlockerController taskBlockerController = new TaskBlockerController(datastore, this);
    if (!taskBlockerController.getTaskBlockers(task.getTaskID()).isEmpty()) {
      return false;
    }
    completeTask(task);
    return true;
  }

  public boolean allSubtasksAreComplete(TaskData task) {
    ArrayList<TaskData> subtasks = getSubtasks(task);
    for (TaskData subtask : subtasks) {
      if (subtask.getStatus() != Status.COMPLETE) {
        return false;
      }
    }
    return true;
  }

  private void completeTask(TaskData task) {
    if (task.getStatus() != Status.COMPLETE) {
      TransactionOptions options = TransactionOptions.Builder.withXG(true);
      Transaction transaction = datastore.beginTransaction(options);
      try {
        task.setStatus(Status.COMPLETE);
        if (task.getTaskID() != 0) {
          datastore.put(task.toEntity());
          UserController userController = new UserController(datastore);
          ArrayList<Entity> users = new ArrayList<>();
          UserData user;
          for (long userID : task.getUsers()) {
            try {
              user = userController.getUserByID(userID);
              user.setUserTotal(user.getUserTotal() + 1);
              users.add(user.toEntity());
            } catch (NullPointerException e) {
              System.out.println("User ID: " + userID + " cannot be found.");
            }
          }
          datastore.put(users);
        }
        transaction.commit();
      } finally {
        if (transaction.isActive()) {
          transaction.rollback();
        }
      }
    }
  }

  public boolean setIncomplete(long taskID) {
    return setIncomplete(getTaskByID(taskID));
  }

  public boolean setIncomplete(TaskData task) {
    if (parentTaskIsComplete(task)) {
      return false;
    }
    if (task.getStatus() != Status.INCOMPLETE) {
      TransactionOptions options = TransactionOptions.Builder.withXG(true);
      Transaction transaction = datastore.beginTransaction(options);
      try {
        task.setStatus(Status.INCOMPLETE);
        if (task.getTaskID() != 0) {
          datastore.put(task.toEntity());
          UserController userController = new UserController(datastore);
          ArrayList<Entity> users = new ArrayList<>();
          UserData user;
          for (long userID : task.getUsers()) {
            try {
              user = userController.getUserByID(userID);
              user.setUserTotal(user.getUserTotal() - 1);
              users.add(user.toEntity());
            } catch (NullPointerException e) {
              System.out.println("User ID: " + userID + " cannot be found.");
            }
          }
          datastore.put(users);
        }
        transaction.commit();
      } finally {
        if (transaction.isActive()) {
          transaction.rollback();
        }
      }
    }
    return true;
  }

  public boolean parentTaskIsComplete(TaskData task) {
    Long parentTaskID = task.getParentTaskID();
    return parentTaskID != 0 && getTaskByID(parentTaskID).getStatus() == Status.COMPLETE;
  }

  // Get methods

  public TaskData getTaskByID(long taskID) {
    Query query =
        new Query("Task")
            .addFilter("__key__", FilterOperator.EQUAL, KeyFactory.createKey("Task", taskID));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    TaskData task = new TaskData(entity);
    return task;
  }

  public ArrayList<TaskData> getTasksByIDs(ArrayList<Long> taskIDs) {
    ArrayList<Key> keys = getKeysFromTaskIDs(taskIDs);
    if (keys.isEmpty()) {
      return new ArrayList<>();
    }
    Filter filter = new FilterPredicate("__key__", FilterOperator.IN, keys);
    return getTasks(filter, NO_QUERY_LIMIT, NO_QUERY_SORT);
  }

  public ArrayList<TaskData> getTasksByUserID(long userID) {
    Filter filter = new FilterPredicate("users", FilterOperator.EQUAL, userID);
    return getTasks(filter, NO_QUERY_LIMIT, NO_QUERY_SORT);
  }

  public ArrayList<TaskData> getTasksByProjectID(long projectID) {
    Filter filter =
        new CompositeFilter(
            CompositeFilterOperator.AND,
            Arrays.asList(
                FilterOperator.EQUAL.of("projectID", projectID),
                FilterOperator.EQUAL.of("parentTaskID", 0)));
    return getTasks(filter, NO_QUERY_LIMIT, NO_QUERY_SORT);
  }

  public ArrayList<TaskData> getTasks(int limit, String sortBy, String sortDirection) {
    SortPredicate sort =
        new SortPredicate(sortBy, SortDirection.valueOf(sortDirection.toUpperCase()));
    return getTasks(NO_QUERY_FILTER, limit, sort);
  }

  public ArrayList<TaskData> getSubtasks(TaskData task) {
    if (task.getTaskID() != 0) {
      Filter filter = new FilterPredicate("parentTaskID", FilterOperator.EQUAL, task.getTaskID());
      return getTasks(filter, NO_QUERY_LIMIT, NO_QUERY_SORT);
    }
    return new ArrayList<>();
  }

  public ArrayList<TaskData> getAncestors(TaskData task) {
    ArrayList<TaskData> ancestors = new ArrayList<>();
    long parentTaskID = task.getParentTaskID();
    while (parentTaskID != 0) {
      task = getTaskByID(parentTaskID);
      ancestors.add(task);
      parentTaskID = task.getParentTaskID();
    }
    Collections.reverse(ancestors);
    return ancestors;
  }

  public ArrayList<TaskTreeNode> getTaskTree(long projectID) {
    ArrayList<TaskTreeNode> taskTree = new ArrayList<>();
    Filter filter = new FilterPredicate("projectID", FilterOperator.EQUAL, projectID);
    ArrayList<TaskData> tasks = getTasks(filter, NO_QUERY_LIMIT, NO_QUERY_SORT);
    buildTree(taskTree, tasks);
    return taskTree;
  }

  private void buildTree(ArrayList<TaskTreeNode> taskTree, ArrayList<TaskData> tasks) {
    // No tasks left to add
    if (tasks.isEmpty()) {
      return;
    }
    // Load root tasks
    ArrayList<TaskData> toRemove = new ArrayList<>();
    if (taskTree.isEmpty()) {
      for (TaskData task : tasks) {
        if (task.getParentTaskID() == 0) {
          taskTree.add(new TaskTreeNode(task));
          toRemove.add(task);
        }
      }
      tasks.removeAll(toRemove);
      toRemove.clear();
    }
    // Load subtasks
    ArrayList<TaskTreeNode> nextTreeLayer = new ArrayList<>();
    for (TaskTreeNode taskTreeNode : taskTree) {
      for (TaskData task : tasks) {
        if (task.getParentTaskID() == taskTreeNode.getParentTask().getTaskID()) {
          nextTreeLayer.add(taskTreeNode.addSubtask(task));
          toRemove.add(task);
        }
      }
      tasks.removeAll(toRemove);
      toRemove.clear();
    }
    buildTree(nextTreeLayer, tasks);
  }

  private ArrayList<TaskData> getTasks(Filter filter, int limit, SortPredicate sort) {
    ArrayList<TaskData> tasks = new ArrayList<>();
    Query query = new Query("Task");
    if (filter != null) {
      query.setFilter(filter);
    }
    if (sort != null) {
      query.addSort(sort.getPropertyName(), sort.getDirection());
    }
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(limit));
    for (Entity entity : results) {
      tasks.add(new TaskData(entity));
    }
    return tasks;
  }

  public boolean isSubtask(long parentTaskID, long subtaskID) {
    return isSubtask(getTaskByID(parentTaskID), subtaskID);
  }

  private boolean isSubtask(TaskData parentTask, long subtaskID) {
    ArrayList<TaskData> subtasks = getSubtasks(parentTask);
    for (TaskData subtask : subtasks) {
      if (subtask.getTaskID() == subtaskID || isSubtask(subtask, subtaskID) == true) {
        return true;
      }
    }
    return false;
  }

  // Delete methods

  public void deleteTasks(ArrayList<Long> taskIDs) {
    TransactionOptions options = TransactionOptions.Builder.withXG(true);
    Transaction transaction = datastore.beginTransaction(options);
    try {
      deleteTasksRecursive(taskIDs);
      transaction.commit();
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
  }

  private void deleteTasksRecursive(ArrayList<Long> taskIDs) {
    if (taskIDs.isEmpty()) {
      return;
    }
    datastore.delete(getKeysFromTaskIDs(taskIDs));
    Filter filter = new FilterPredicate("parentTaskID", FilterOperator.IN, taskIDs);
    deleteTasksRecursive(getTaskIDsFromTasks(getTasks(filter, NO_QUERY_LIMIT, NO_QUERY_SORT)));
  }

  // Conversion methods

  public ArrayList<Long> getTaskIDsFromKeys(ArrayList<Key> keys) {
    ArrayList<Long> taskIDs = new ArrayList<>();
    for (Key key : keys) {
      taskIDs.add(key.getId());
    }
    return taskIDs;
  }

  public ArrayList<Long> getTaskIDsFromTasks(ArrayList<TaskData> tasks) {
    ArrayList<Long> taskIDs = new ArrayList<>();
    for (TaskData task : tasks) {
      taskIDs.add(task.getTaskID());
    }
    return taskIDs;
  }

  public ArrayList<Key> getKeysFromTaskIDs(ArrayList<Long> taskIDs) {
    ArrayList<Key> keys = new ArrayList<>();
    for (long taskID : taskIDs) {
      keys.add(KeyFactory.createKey("Task", taskID));
    }
    return keys;
  }

  public ArrayList<Key> getKeysFromTasks(ArrayList<TaskData> tasks) {
    ArrayList<Key> keys = new ArrayList<>();
    for (TaskData task : tasks) {
      keys.add(KeyFactory.createKey("Task", task.getTaskID()));
    }
    return keys;
  }
}
