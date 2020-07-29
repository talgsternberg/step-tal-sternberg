package com.rtb.projectmanagementtool.task;

// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.PreparedQuery;
// import com.google.appengine.api.datastore.QueryResults;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.api.datastore.Query.SortDirection;
// import com.google.appengine.api.datastore.Query.newGqlQueryBuilder;
// import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.HashSet;

/** Class controlling the TaskData object. */
public final class TaskController {

  private DatastoreService datastore;

  public TaskController(datastore) {
    this.datastore = datastore;
  }

  public HashSet<TaskData> getTasks(int quantity, String sortBy, String sortDirection) {
    HashSet<TaskData> tasks = new HashSet<>();
    Query query;
    if (sortDirection.equals("descending")) {
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
    for (TaskData task : tasks) {
      Entity entity = task.toEntity();
      taskEntities.add(entity);
    }
    datastore.put(taskEntities);
  }

  public void deleteTasks(HashSet<Long> taskIDs) {
    HashSet<Key> keys = new HashSet<>();
    for (long taskID : taskIDs) {
      Key key = KeyFactory.createKey("Task", taskID);
      keys.add(key);
    }
    datastore.delete(keys);
  }
}
