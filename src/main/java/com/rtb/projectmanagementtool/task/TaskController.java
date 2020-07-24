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

  // Optional TODO: Add sort and quantity paramters.
  public HashSet<TaskData> getAllTasks() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    HashSet<TaskData> tasks = new HashSet<>();
    Query query = new Query("Task");
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      // Get task entity data
      long taskID = (long) entity.getKey().getId();
      long projectID = (long) entity.getProperty("projectID");
      String name = (String) entity.getProperty("name");
      String description = (String) entity.getProperty("description");
      Status status = (Status) entity.getProperty("status");
      HashSet<Long> users = (HashSet<Long>) entity.getProperty("users");
      HashSet<Long> subtasks = (HashSet<Long>) entity.getProperty("subtasks");
      // Build TaskData object
      TaskData task = new TaskData(taskID, projectID, name, description, status, users, subtasks);
      tasks.add(task);
    }
    return tasks;
  }

  public void addTasks(HashSet<TaskData> tasks) {
    HashSet<Entity> taskEntities = new HashSet<>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    for (TaskData task : tasks) {
      // Get task data
      long taskID = task.getTaskID();
      long projectID = task.getProjectID();
      String name = task.getName();
      String decription = task.getDescription();
      Status status = task.getStatus();
      HashSet<Long> users = task.getUsers();
      HashSet<Long> subtasks = task.getSubtasks();
      // Build task entity
      Entity entity = new Entity("Task", taskID);
      entity.setProperty("projectID", projectID);
      entity.setProperty("name", name);
      entity.setProperty("description", decription);
      entity.setProperty("status", status);
      entity.setProperty("users", users);
      entity.setProperty("subtasks", subtasks);
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
