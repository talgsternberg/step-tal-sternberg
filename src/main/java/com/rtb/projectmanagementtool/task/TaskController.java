package com.rtb.projectmanagementtool.task;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.ArrayList;

/** Class controlling the TaskData object. */
public final class TaskController {

  private DatastoreService datastore;

  public TaskController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  public TaskData getTaskByID(long taskID) {
    Query query = new Query("Task").addFilter("taskID", FilterOperator.EQUAL, taskID);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    TaskData task = new TaskData(entity);
    return task;
  }

  public ArrayList<TaskData> getSubtasks(TaskData task) {
    if (!task.getSubtasks().isEmpty()) {
      Query query = new Query("Task").addFilter("taskID", FilterOperator.IN, task.getSubtasks());
      return getTasks(query, Integer.MAX_VALUE);
    }
    return new ArrayList<>();
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

  public void addTasks(ArrayList<TaskData> tasks) {
    ArrayList<Entity> taskEntities = new ArrayList<>();
    for (TaskData task : tasks) {
      Entity entity = task.toEntity();
      taskEntities.add(entity);
    }
    datastore.put(taskEntities);
  }

  public void deleteTasks(ArrayList<Long> taskIDs) {
    ArrayList<Key> keys = new ArrayList<>();
    for (long taskID : taskIDs) {
      Key key = KeyFactory.createKey("Task", taskID);
      keys.add(key);
    }
    datastore.delete(keys);
  }
}
