package com.rtb.projectmanagementtool.task;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Class testing TaskController */
public class TaskControllerTest {

  // Task 1 attributes
  private static final long taskID1 = 1l;
  private static final long projectID1 = 1l;
  private static final String name1 = "Task 1";
  private static final String description1 = "Task 1 description...";
  private static final Status status1 = Status.INCOMPLETE;
  private static final ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));
  private static final ArrayList<Long> subtasks1 = new ArrayList<>(Arrays.asList(3l));

  // Task 2 attributes
  private static final long taskID2 = 2l;
  private static final long projectID2 = 1l;
  private static final String name2 = "Task 2";
  private static final String description2 = "Task 2 description...";
  private static final Status status2 = Status.COMPLETE;
  private static final ArrayList<Long> users2 = new ArrayList<>(Arrays.asList(1l, 3l));
  private static final ArrayList<Long> subtasks2 = new ArrayList<>();

  // Task 3 attributes
  private static final long taskID3 = 3l;
  private static final long projectID3 = 1l;
  private static final String name3 = "Task 3";
  private static final String description3 = "Task 3 description...";
  private static final Status status3 = Status.INCOMPLETE;
  private static final ArrayList<Long> users3 = new ArrayList<>(Arrays.asList(3l));
  private static final ArrayList<Long> subtasks3 = new ArrayList<>();

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testGetTaskByID() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Build task entity 1
    Entity entity1 = new Entity("Task", taskID1);
    entity1.setProperty("taskID", taskID1);
    entity1.setProperty("projectID", projectID1);
    entity1.setProperty("name", name1);
    entity1.setProperty("description", description1);
    entity1.setProperty("status", status1.toString());
    entity1.setProperty("users", users1);
    entity1.setProperty("subtasks", subtasks1);

    // Build task entity 2
    Entity entity2 = new Entity("Task", taskID2);
    entity2.setProperty("taskID", taskID2);
    entity2.setProperty("projectID", projectID2);
    entity2.setProperty("name", name2);
    entity2.setProperty("description", description2);
    entity2.setProperty("status", status2.toString());
    entity2.setProperty("users", users2);
    entity2.setProperty("subtasks", subtasks2);

    // Build task entity 2
    Entity entity3 = new Entity("Task", taskID3);
    entity3.setProperty("taskID", taskID3);
    entity3.setProperty("projectID", projectID3);
    entity3.setProperty("name", name3);
    entity3.setProperty("description", description3);
    entity3.setProperty("status", status3.toString());
    entity3.setProperty("users", users3);
    entity3.setProperty("subtasks", subtasks3);

    // Add task entities to ds
    ds.put(entity1);
    ds.put(entity2);
    ds.put(entity3);

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Build TaskData objects
    TaskData task =
        new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);

    TaskController taskController = new TaskController(ds);
    TaskData getTask = taskController.getTaskByID(taskID2);

    Assert.assertEquals("getTask", task, getTask);

  }

  @Test
  public void testGetTasksFromEmptyDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Get task entities with TaskController
    TaskController taskController = new TaskController(ds);
    ArrayList<TaskData> tasks = taskController.getTasks(3, "taskID", "ascending");

    // Assert no entities were retrieved
    Assert.assertEquals("getTasks", new ArrayList<TaskData>(), tasks);
  }

  @Test
  public void testGetTasksFromDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Build task entity 1
    Entity entity1 = new Entity("Task", taskID1);
    entity1.setProperty("taskID", taskID1);
    entity1.setProperty("projectID", projectID1);
    entity1.setProperty("name", name1);
    entity1.setProperty("description", description1);
    entity1.setProperty("status", status1.toString());
    entity1.setProperty("users", users1);
    entity1.setProperty("subtasks", subtasks1);

    // Build task entity 2
    Entity entity2 = new Entity("Task", taskID2);
    entity2.setProperty("taskID", taskID2);
    entity2.setProperty("projectID", projectID2);
    entity2.setProperty("name", name2);
    entity2.setProperty("description", description2);
    entity2.setProperty("status", status2.toString());
    entity2.setProperty("users", users2);
    entity2.setProperty("subtasks", subtasks2);

    // Build task entity 2
    Entity entity3 = new Entity("Task", taskID3);
    entity3.setProperty("taskID", taskID3);
    entity3.setProperty("projectID", projectID3);
    entity3.setProperty("name", name3);
    entity3.setProperty("description", description3);
    entity3.setProperty("status", status3.toString());
    entity3.setProperty("users", users3);
    entity3.setProperty("subtasks", subtasks3);

    // Add task entities to ds
    ds.put(entity1);
    ds.put(entity2);
    ds.put(entity3);

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Build TaskData objects
    TaskData task1 =
        new TaskData(taskID1, projectID1, name1, description1, status1, users1, subtasks1);
    TaskData task2 =
        new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);
    TaskData task3 =
        new TaskData(taskID3, projectID3, name3, description3, status3, users3, subtasks3);

    // Create ArrayList of TaskData objects
    ArrayList<TaskData> tasks = new ArrayList<>();
    tasks.add(task1);
    tasks.add(task2);
    tasks.add(task3);

    // Get task entities with TaskController
    TaskController taskController = new TaskController(ds);
    ArrayList<TaskData> getTasks = taskController.getTasks(5, "name", "ascending");

    // Assert all 3 entities were retrieved
    for (int i = 0; i < 3; i++) {
      Assert.assertTrue("getTask", getTasks.get(i).equals(tasks.get(i)));
    }
  }
}
