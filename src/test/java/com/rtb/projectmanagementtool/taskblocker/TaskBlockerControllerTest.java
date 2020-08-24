package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.task.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskBlockerControllerTest {

  private DatastoreService datastore;
  private TaskController taskController;

  // Tasks
  private static final TaskData task1 = new TaskData(1l, "Task 1", "Task 1 description...");
  private static final TaskData task2 = new TaskData(1l, "Task 2", "Task 2 description...");
  private static final TaskData task3 = new TaskData(1l, "Task 3", "Task 3 description...");
  private static final TaskData task4 = new TaskData(1l, "Task 4", "Task 4 description...");
  private static final TaskData task5 = new TaskData(1l, "Task 5", "Task 5 description...");
  private static final TaskData task6 = new TaskData(1l, "Task 6", "Task 6 description...");

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    taskController = new TaskController(datastore);
    taskController.addTasks(
        new ArrayList<>(Arrays.asList(task1, task2, task3, task4, task5, task6)));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testAddTaskBlockers() {
    TaskBlockerController ctl = new TaskBlockerController(datastore, taskController);

    // Add 2 initially
    ctl.addTaskBlocker(task1.getTaskID(), task2.getTaskID());
    ctl.addTaskBlocker(task2.getTaskID(), task3.getTaskID());

    // Should have 2 now
    HashSet<TaskBlockerData> blockers = ctl.getAllTaskBlockers();
    Assert.assertEquals(2, blockers.size());

    // Add 2 more
    ctl.addTaskBlocker(task1.getTaskID(), task2.getTaskID());
    ctl.addTaskBlocker(task2.getTaskID(), task3.getTaskID());

    // Should be 4 now
    blockers = ctl.getAllTaskBlockers();
    Assert.assertEquals(4, blockers.size());
  }

  @Test
  public void testGetBlockersByTaskId() {
    TaskBlockerController ctl = new TaskBlockerController(datastore, taskController);

    // 2 blockers for task1
    ctl.addTaskBlocker(task1.getTaskID(), task2.getTaskID());
    ctl.addTaskBlocker(task1.getTaskID(), task3.getTaskID());

    // 3 blockers for task2
    ctl.addTaskBlocker(task2.getTaskID(), task4.getTaskID());
    ctl.addTaskBlocker(task2.getTaskID(), task5.getTaskID());
    ctl.addTaskBlocker(task2.getTaskID(), task6.getTaskID());

    HashSet<TaskBlockerData> blockers = ctl.getTaskBlockers(task1.getTaskID());
    Assert.assertFalse(blockers.isEmpty());
    Assert.assertEquals(2, blockers.size());

    for (TaskBlockerData blocker : blockers) {
      Assert.assertEquals(task1.getTaskID(), blocker.getTaskID());
    }
  }

  @Test
  public void testDeleteBlockersByBlockerId() {
    TaskBlockerController ctl = new TaskBlockerController(datastore, taskController);

    // 2 blockers by task4
    ctl.addTaskBlocker(task1.getTaskID(), task4.getTaskID());
    ctl.addTaskBlocker(task2.getTaskID(), task4.getTaskID());

    // 3 blockers by task5
    ctl.addTaskBlocker(task1.getTaskID(), task5.getTaskID());
    ctl.addTaskBlocker(task2.getTaskID(), task5.getTaskID());
    ctl.addTaskBlocker(task3.getTaskID(), task5.getTaskID());

    // Assert all blockers were added
    HashSet<TaskBlockerData> blockers = ctl.getAllTaskBlockers();
    Assert.assertEquals(5, blockers.size());

    // Delete blockers blocked by task 5
    ctl.deleteByBlockerID(task5.getTaskID());

    // Assert 2 blockers remain
    blockers = ctl.getAllTaskBlockers();
    Assert.assertEquals(2, blockers.size());

    // Assert correct blockers remain
    for (TaskBlockerData blocker : blockers) {
      Assert.assertTrue(blocker.getBlockerID() != task5.getTaskID());
    }
  }
}
