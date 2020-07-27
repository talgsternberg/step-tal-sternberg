package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import java.util.HashSet;

/** Class controlling the TaskData object. */
public final class TaskController {

  private HashSet<TaskData> tasks;

  public TaskController(HashSet<TaskData> tasks) {
    this.tasks = tasks;
  }

  public HashSet<TaskData> getTasks(int quantity, String sortBy, String sortDirection) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    HashSet<TaskData> tasks = new HashSet<>();
    Query query;
    if (sortDirection.equals("descending")){
      query = new Query("Task").addSort(sortBy, SortDirection.DESCENDING);
    } else {
      query = new Query("Task").addSort(sortBy, SortDirection.ASCENDING);
    }
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

  public void addTasks(HashSet<TaskData> tasks) {
    HashSet<Entity> taskEntities = new HashSet<>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    for (TaskData task : tasks) {
      Entity entity = task.toEntity();
      taskEntities.add(entity);
    }
    datastore.put(taskEntities);
  }

  public void deleteTasks(HashSet<Long> taskIDs) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    HashSet<Key> keys = new HashSet<>();
    Query query = new Query("Comment").setKeysOnly();
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      if (taskIDs.contains(entity.getProperty("id"))) {
        keys.add(entity.getKey());
      }
    }
    datastore.delete(keys);
  }

}
