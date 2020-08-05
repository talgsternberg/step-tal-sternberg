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
    addTasks(subtasks);
    ArrayList<Long> subtaskIDs = task.getSubtasks();
    for (TaskData subtask : subtasks) {
      subtaskIDs.add(subtask.getTaskID());
    }
    task.setSubtasks(subtaskIDs);
  }

  public void addKeysToTasks(ArrayList<TaskData> tasks, ArrayList<Key> keys) {
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
    ArrayList<TaskData> tasks = new ArrayList<>();
    Query query;
    if (sortDirection.equals("descending")) {
      query = new Query("Task").addSort(sortBy, SortDirection.DESCENDING);
    } else {
      query = new Query("Task").addSort(sortBy, SortDirection.ASCENDING);
    }
    return getTasks(query, quantity);
  }

  public ArrayList<TaskData> getSubtasks(TaskData task) {
    ArrayList<Key> subtaskKeys = getKeysFromTaskIDs(task.getSubtasks());
    if (!task.getSubtasks().isEmpty()) {
      Query query = new Query("Task").addFilter("__key__", FilterOperator.IN, subtaskKeys);
      return getTasks(query, Integer.MAX_VALUE);
    }
    return new ArrayList<>();
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
    ArrayList<Key> keys = getKeysFromTaskIDs(taskIDs);
    datastore.delete(keys);
  }

  public ArrayList<Long> getTaskIDsFromKeys(ArrayList<Key> keys) {
    ArrayList<Long> taskIDs = new ArrayList<>();
    for (Key key : keys) {
      taskIDs.add(key.getId());
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
