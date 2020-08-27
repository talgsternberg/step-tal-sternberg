package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.task.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskBlockerControllerTest {

  private DatastoreService datastore;
  private MemcacheService cache;
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
          new LocalMemcacheServiceTestConfig(),
          new LocalDatastoreServiceTestConfig()
              .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    cache = MemcacheServiceFactory.getMemcacheService();
    cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    taskController = new TaskController(datastore);
    taskController.addTasks(
        new ArrayList<>(Arrays.asList(task1, task2, task3, task4, task5, task6)));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testAddTaskBlockers() throws TaskBlockerException {
    TaskBlockerController ctl = new TaskBlockerController(datastore, cache, taskController);

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
  public void testCycleDetectionSmall() throws TaskBlockerException {
    TaskBlockerController ctl = new TaskBlockerController(datastore, cache, taskController);

    // Create 2 task blockers that block each other
    TaskBlockerData tb1 = new TaskBlockerData(task1.getTaskID(), task2.getTaskID());
    TaskBlockerData tb2 = new TaskBlockerData(task2.getTaskID(), task1.getTaskID());

    // Add task blockers to ds
    ctl.addTaskBlocker(tb1);
    try {
      ctl.addTaskBlocker(tb2);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    // Assert only the first task blocker was added successfully
    Assert.assertTrue(tb1.getTaskBlockerID() != 0);
    Assert.assertEquals(0, tb2.getTaskBlockerID());
    Assert.assertEquals(1, ctl.getAllTaskBlockers().size());
    Assert.assertEquals(tb1, ctl.getAllTaskBlockers().iterator().next());
  }

  @Test
  public void testCycleDetectionLarge() throws TaskBlockerException {
    TaskBlockerController ctl = new TaskBlockerController(datastore, cache, taskController);

    // Create 2 task blockers that block each other
    TaskBlockerData tb1 = new TaskBlockerData(task1.getTaskID(), task2.getTaskID());
    TaskBlockerData tb2 = new TaskBlockerData(task1.getTaskID(), task3.getTaskID());
    TaskBlockerData tb3 = new TaskBlockerData(task2.getTaskID(), task3.getTaskID());
    TaskBlockerData tb4 = new TaskBlockerData(task2.getTaskID(), task4.getTaskID());
    TaskBlockerData tb5 = new TaskBlockerData(task4.getTaskID(), task5.getTaskID());
    TaskBlockerData tb6 = new TaskBlockerData(task4.getTaskID(), task6.getTaskID());

    // Add task blockers to ds
    ctl.addTaskBlocker(tb1);
    ctl.addTaskBlocker(tb2);
    ctl.addTaskBlocker(tb3);
    ctl.addTaskBlocker(tb4);
    ctl.addTaskBlocker(tb5);
    ctl.addTaskBlocker(tb6);

    // Assert 6 task blockers were added successfully
    Assert.assertEquals(6, ctl.getAllTaskBlockers().size());

    // Attempt to add task blockers that would cause a cycle
    TaskBlockerData tb7 = new TaskBlockerData(task5.getTaskID(), task2.getTaskID());
    TaskBlockerData tb8 = new TaskBlockerData(task6.getTaskID(), task1.getTaskID());
    TaskBlockerData tb9 = new TaskBlockerData(task4.getTaskID(), task4.getTaskID());
    try {
      ctl.addTaskBlocker(tb7);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    try {
      ctl.addTaskBlocker(tb8);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    try {
      ctl.addTaskBlocker(tb9);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    // Assert only the first set of task blocker were added successfully
    Assert.assertEquals(6, ctl.getAllTaskBlockers().size());
  }

  @Test
  public void testGetTaskBlockerByID() throws TaskBlockerException {
    TaskBlockerController ctl = new TaskBlockerController(datastore, cache, taskController);

    // Create 3 task blockers
    TaskBlockerData tb1 = new TaskBlockerData(task1.getTaskID(), task2.getTaskID());
    TaskBlockerData tb2 = new TaskBlockerData(task2.getTaskID(), task3.getTaskID());
    TaskBlockerData tb3 = new TaskBlockerData(task2.getTaskID(), task4.getTaskID());

    // Add task blockers to ds
    ctl.addTaskBlocker(tb1);
    ctl.addTaskBlocker(tb2);
    ctl.addTaskBlocker(tb3);

    // Assert 3 task blockers were added
    HashSet<TaskBlockerData> blockers = ctl.getAllTaskBlockers();
    Assert.assertEquals(3, blockers.size());

    // Get id's from task blocker objects
    long taskBlockerID1 = tb1.getTaskBlockerID();
    long taskBlockerID2 = tb2.getTaskBlockerID();
    long taskBlockerID3 = tb3.getTaskBlockerID();

    // Get task blockers by id via controller
    TaskBlockerData getTaskBlocker1 = ctl.getTaskBlockerByID(taskBlockerID1);
    TaskBlockerData getTaskBlocker2 = ctl.getTaskBlockerByID(taskBlockerID2);
    TaskBlockerData getTaskBlocker3 = ctl.getTaskBlockerByID(taskBlockerID3);

    // Assert correct task blockers were found
    Assert.assertEquals(tb1, getTaskBlocker1);
    Assert.assertEquals(tb2, getTaskBlocker2);
    Assert.assertEquals(tb3, getTaskBlocker3);
  }

  @Test
  public void testGetBlockersByTaskId() throws TaskBlockerException {
    TaskBlockerController ctl = new TaskBlockerController(datastore, cache, taskController);

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
  public void testDeleteBlockersByBlockerId() throws TaskBlockerException {
    TaskBlockerController ctl = new TaskBlockerController(datastore, cache, taskController);

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
