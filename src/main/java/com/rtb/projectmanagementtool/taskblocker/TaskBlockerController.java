package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.HashSet;

/** Class controlling the TaskBlockerData object. */
public final class TaskBlockerController {
  public TaskBlockerController() {}

  public HashSet<TaskBlockerData> getTaskBlockers(DatastoreService datastore, long taskID) {
    Query query = new Query("TaskBlocker");
    // Filter by taskID here
    PreparedQuery results = datastore.prepare(query);
    
    HashSet<TaskBlockerData> blockers = new HashSet<>();
    for (Entity entity : results.asIterable()) {
      TaskBlockerData blocker = new TaskBlockerData(entity);
      blockers.add(blocker);
    }
    return blockers;
  }

  public void addTaskBlocker(DatastoreService datastore, long taskID, long blockerID) {
    TaskBlockerData blocker = new TaskBlockerData(taskID, blockerID);
    datastore.put(blocker.toEntity());
  }
}
