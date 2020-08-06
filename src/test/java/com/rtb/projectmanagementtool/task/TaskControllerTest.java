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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Class testing TaskController */
public class TaskControllerTest {

  // Task 1 attributes
  private static final long projectID1 = 1l;
  private static final String name1 = "Task 1";
  private static final String description1 = "Task 1 description...";
  private static final Status status1 = Status.INCOMPLETE;
  private static final ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));
  private static final ArrayList<Long> subtasks1 = new ArrayList<>(Arrays.asList(3l));

  // Task 2 attributes
  private static final long projectID2 = 1l;
  private static final String name2 = "Task 2";
  private static final String description2 = "Task 2 description...";
  private static final Status status2 = Status.COMPLETE;
  private static final ArrayList<Long> users2 = new ArrayList<>(Arrays.asList(1l, 3l));
  private static final ArrayList<Long> subtasks2 = new ArrayList<>();

  // Task 3 attributes
  private static final long projectID3 = 1l;
  private static final String name3 = "Task 3";
  private static final String description3 = "Task 3 description...";
  private static final Status status3 = Status.INCOMPLETE;
  private static final ArrayList<Long> users3 = new ArrayList<>(Arrays.asList(3l));
  private static final ArrayList<Long> subtasks3 = new ArrayList<>();

  // TaskData objects
  private static final TaskData task1 =
      new TaskData(projectID1, name1, description1, status1, users1, subtasks1);
  private static final TaskData task2 =
      new TaskData(projectID2, name2, description2, status2, users2, subtasks2);
  private static final TaskData task3 =
      new TaskData(projectID3, name3, description3, status3, users3, subtasks3);

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

    // Add task entities to ds
    long taskID1 = ds.put(task1.toEntity()).getId();
    long taskID2 = ds.put(task2.toEntity()).getId();
    long taskID3 = ds.put(task3.toEntity()).getId();

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Get task with TaskController
    TaskController taskController = new TaskController(ds);
    TaskData getTask = taskController.getTaskByID(taskID2);

    // Build expected task
    TaskData task2 =
        new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);

    // Assert task retrieved is correct
    Assert.assertEquals("getTask", task2, getTask);
  }

  @Test
  public void testAddSubtasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Add task1 and add task2 and task3 as its subtasks with TaskController
    long taskID1 = taskController.addTasks(new ArrayList<>(Arrays.asList(task1))).get(0).getId();
    ArrayList<Long> subtaskIDs =
        taskController.getTaskIDsFromKeys(
            taskController.addSubtasks(task1, new ArrayList<>(Arrays.asList(task2, task3))));

    // Get subtasks
    Query query =
        new Query("Task")
            .addFilter("__key__", FilterOperator.IN, taskController.getKeysFromTaskIDs(subtaskIDs));
    PreparedQuery results = ds.prepare(query);

    // Assert correct subtasks were found
    for (Entity entity : results.asIterable()) {
      long taskID = entity.getKey().getId();
      Assert.assertTrue("addSubtasks", subtaskIDs.contains(taskID));
    }

    // Assert correct amount of subtasks were found
    Assert.assertEquals(2, results.countEntities(withLimit(10)));
  }

  @Test
  public void testGetSubtasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Add task1 and add task2 and task3 as its subtasks with TaskController
    long taskID1 = taskController.addTasks(new ArrayList<>(Arrays.asList(task1))).get(0).getId();
    ArrayList<Key> subtaskKeys =
        taskController.addSubtasks(task1, new ArrayList<>(Arrays.asList(task2, task3)));

    // Build expected subtasks
    ArrayList<TaskData> subtasks = new ArrayList<>();
    subtasks.add(
        new TaskData(
            subtaskKeys.get(0).getId(),
            projectID2,
            name2,
            description2,
            status2,
            users2,
            subtasks2));
    subtasks.add(
        new TaskData(
            subtaskKeys.get(1).getId(),
            projectID3,
            name3,
            description3,
            status3,
            users3,
            subtasks3));

    // Get subtasks with TaskController
    ArrayList<TaskData> getSubtasks = taskController.getSubtasks(task1);

    // Sort lists
    Collections.sort(subtasks);
    Collections.sort(getSubtasks);

    // Assert subtasks retrieved are accurate
    Assert.assertEquals("getSubtasks", subtasks, getSubtasks);
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
    Assert.assertEquals("getTasks", new ArrayList<>(), tasks);
  }

  @Test
  public void testGetTasksFromDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task entities to ds
    long taskID1 = ds.put(task1.toEntity()).getId();
    long taskID2 = ds.put(task2.toEntity()).getId();
    long taskID3 = ds.put(task3.toEntity()).getId();

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

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

    // Assert all entities were retrieved
    Assert.assertEquals("getTask", tasks, getTasks);
  }

  @Test
  public void testAddTasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task entities with TaskController
    TaskController taskController = new TaskController(ds);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1, task2, task3)));

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));
  }

  @Test
  public void testDeleteTasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task entities to ds
    long taskID1 = ds.put(task1.toEntity()).getId();
    long taskID2 = ds.put(task2.toEntity()).getId();
    long taskID3 = ds.put(task3.toEntity()).getId();

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Delete task entities with TaskController
    TaskController taskController = new TaskController(ds);
    taskController.deleteTasks(new ArrayList<>(Arrays.asList(taskID1, taskID3)));

    // Assert 1 task entity remains
    Assert.assertEquals(1, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Expected remaining task
    TaskData task2 =
        new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);

    // Assert the correct task entity remains
    Assert.assertEquals(
        "deleteTask", task2, new TaskData(ds.prepare(new Query("Task")).asSingleEntity()));
  }
}
