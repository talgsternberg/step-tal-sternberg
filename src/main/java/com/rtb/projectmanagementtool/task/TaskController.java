package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.ArrayList;

/** Class controlling the TaskData object. */
public final class TaskController {

  private DatastoreService datastore;

  public TaskController(DatastoreService datastore) {
    this.datastore = datastore;
  }

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

  public TaskData getTaskByID(long taskID) {
    Query query =
        new Query("Task")
            .addFilter("__key__", FilterOperator.EQUAL, KeyFactory.createKey("Task", taskID));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    TaskData task = new TaskData(entity);
    return task;
  }

  public ArrayList<TaskData> getTasks(int quantity, String sortBy, String sortDirection) {
    Query query;
    if (sortDirection.equals("descending")) {
      query = new Query("Task").addSort(sortBy, SortDirection.DESCENDING);
    } else {
      query = new Query("Task").addSort(sortBy, SortDirection.ASCENDING);
    }
    return getTasks(query, quantity);
  }

  public ArrayList<TaskData> getSubtasks(TaskData task) {
    Query query =
        new Query("Task").addFilter("parentTaskID", FilterOperator.EQUAL, task.getTaskID());
    return getTasks(query, Integer.MAX_VALUE);
  }

  private ArrayList<TaskData> getTasks(Query query, int quantity) {
    ArrayList<TaskData> tasks = new ArrayList<>();
    PreparedQuery results = datastore.prepare(query);
    int count = 0;
    for (Entity entity : results.asIterable()) {
      if (count++ >= quantity) {
        break;
      }
      TaskData task = new TaskData(entity);
      tasks.add(task);
    }
    return tasks;
  }

  public void deleteTasks(ArrayList<Long> taskIDs) {
    if (taskIDs.isEmpty()) {
      return;
    }
    datastore.delete(getKeysFromTaskIDs(taskIDs));
    Query query = new Query("Task").addFilter("parentTaskID", FilterOperator.IN, taskIDs);
    deleteTasks(getTaskIDsFromTasks(getTasks(query, Integer.MAX_VALUE)));
  }

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
