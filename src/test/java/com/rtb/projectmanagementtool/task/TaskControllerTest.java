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
  //   private static final ArrayList<Long> subtasks1 = new ArrayList<>(Arrays.asList(3l));

  // Task 2 attributes
  private static final long projectID2 = 1l;
  private static final String name2 = "Task 2";
  private static final String description2 = "Task 2 description...";
  private static final Status status2 = Status.COMPLETE;
  private static final ArrayList<Long> users2 = new ArrayList<>(Arrays.asList(1l, 3l));
  //   private static final ArrayList<Long> subtasks2 = new ArrayList<>();

  // Task 3 attributes
  private static final long projectID3 = 1l;
  private static final String name3 = "Task 3";
  private static final String description3 = "Task 3 description...";
  private static final Status status3 = Status.INCOMPLETE;
  private static final ArrayList<Long> users3 = new ArrayList<>(Arrays.asList(3l));
  //   private static final ArrayList<Long> subtasks3 = new ArrayList<>();

  // TaskData objects
  //   private static final TaskData task1 =
  //       new TaskData(projectID1, name1, description1, status1, users1, subtasks1);
  //   private static final TaskData task2 =
  //       new TaskData(projectID2, name2, description2, status2, users2, subtasks2);
  //   private static final TaskData task3 =
  //       new TaskData(projectID3, name3, description3, status3, users3, subtasks3);
  //   private static final TaskData task1 =
  //       new TaskData(projectID1, name1, description1, status1, users1);
  //   private static final TaskData task2 =
  //       new TaskData(projectID2, name2, description2, status2, users2);
  //   private static final TaskData task3 =
  //       new TaskData(projectID3, name3, description3, status3, users3);

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

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

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
    // TaskData task2 =
    //     new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);
    // TaskData task2 = new TaskData(taskID2, 0, projectID2, name2, description2, status2, users2);
    task2.setTaskID(taskID2);

    // Assert task retrieved is correct
    Assert.assertEquals("getTask", task2, getTask);
  }

  @Test
  public void testGetTasksFromEmptyDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Get task entities with TaskController
    TaskController taskController = new TaskController(ds);
    ArrayList<TaskData> tasks = taskController.getTasks(3, "description", "ascending");

    // Assert no entities were retrieved
    Assert.assertEquals("getTasks", new ArrayList<>(), tasks);
  }

  @Test
  public void testGetTasksFromDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task entities to ds
    long taskID1 = ds.put(task1.toEntity()).getId();
    long taskID2 = ds.put(task2.toEntity()).getId();
    long taskID3 = ds.put(task3.toEntity()).getId();

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // TaskData task1 =
    //     new TaskData(taskID1, projectID1, name1, description1, status1, users1, subtasks1);
    // TaskData task2 =
    //     new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);
    // TaskData task3 =
    //     new TaskData(taskID3, projectID3, name3, description3, status3, users3, subtasks3);
    // TaskData task1 = new TaskData(taskID1, 0, projectID1, name1, description1, status1, users1);
    // TaskData task2 = new TaskData(taskID2, 0, projectID2, name2, description2, status2, users2);
    // TaskData task3 = new TaskData(taskID3, 0, projectID3, name3, description3, status3, users3);

    // Add taskID's to tasks
    task1.setTaskID(taskID1);
    task2.setTaskID(taskID2);
    task3.setTaskID(taskID3);

    // Create ArrayList of TaskData objects
    ArrayList<TaskData> tasks = new ArrayList<>(Arrays.asList(task1, task2, task3));
    // tasks.add(task1);
    // tasks.add(task2);
    // tasks.add(task3);

    // Get task entities with TaskController
    TaskController taskController = new TaskController(ds);
    ArrayList<TaskData> getTasks = taskController.getTasks(5, "name", "ascending");

    // Assert all entities were retrieved
    Assert.assertEquals("getTask", tasks, getTasks);
  }

  @Test
  public void testAddTasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task entities with TaskController
    TaskController taskController = new TaskController(ds);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1, task2, task3)));

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));
  }

  @Test
  public void testAddSubtasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

    // Add task1 and add task2 and task3 as its subtasks with TaskController
    ArrayList<TaskData> subtasks = new ArrayList<>(Arrays.asList(task2, task3));
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1)));
    taskController.addSubtasks(task1, subtasks);

    // Get subtasks
    // Query query =
    //     new Query("Task")
    //         .addFilter("__key__", FilterOperator.IN, taskController.getKeysFromTasks(subtasks));
    Query query =
        new Query("Task").addFilter("parentTaskID", FilterOperator.EQUAL, task1.getTaskID());
    PreparedQuery results = ds.prepare(query);

    // // Get parent task
    // TaskData parentTask = taskController.getTaskByID(task1.getTaskID());

    // Assert correct subtasks were found in datastore and in the subtasks of the parent task
    TaskData task;
    for (Entity entity : results.asIterable()) {
      task = new TaskData(entity);
      Assert.assertTrue("addSubtasks", subtasks.contains(task));
      //   Assert.assertTrue("addSubtasks", task1.getSubtasks().contains(task.getTaskID()));
    }

    // Assert correct amount of subtasks were found
    Assert.assertEquals(2, results.countEntities(withLimit(10)));
  }

  @Test
  public void testGetSubtasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

    // Add task1 to datastore and add task2 and task3 as its subtasks with TaskController
    ArrayList<TaskData> subtasks = new ArrayList<>(Arrays.asList(task2, task3));
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1)));
    taskController.addSubtasks(task1, subtasks);

    // Get subtasks with TaskController
    ArrayList<TaskData> getSubtasks = taskController.getSubtasks(task1);

    // Sort lists
    Collections.sort(subtasks);
    Collections.sort(getSubtasks);

    // Assert subtasks retrieved are accurate
    Assert.assertEquals("getSubtasks", subtasks, getSubtasks);
  }

  @Test
  public void testDeleteTasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

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
    // TaskData task2 =
    //     new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);
    // TaskData task2 = new TaskData(taskID2, 0, projectID2, name2, description2, status2, users2);
    task2.setTaskID(taskID2);

    // Assert the correct task entity remains
    Assert.assertEquals(
        "deleteTask", task2, new TaskData(ds.prepare(new Query("Task")).asSingleEntity()));
  }

  @Test
  public void testDeleteTaskWithSubtasksRecursively() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);
    TaskData task4 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task5 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task6 = new TaskData(projectID3, name3, description3, status3, users3);

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task1 to ds
    long taskID1 = ds.put(task1.toEntity()).getId();

    // Set task2 and task3 as subtasks of task1
    task2.setParentTaskID(taskID1);
    task3.setParentTaskID(taskID1);

    // Add task2 and task3 to ds
    long taskID2 = ds.put(task2.toEntity()).getId();
    long taskID3 = ds.put(task3.toEntity()).getId();

    // Set task4 and task5 as subtasks of task3
    task4.setParentTaskID(taskID3);
    task5.setParentTaskID(taskID3);

    // Add task4 and task5 and task6 to ds
    long taskID4 = ds.put(task4.toEntity()).getId();
    long taskID5 = ds.put(task5.toEntity()).getId();
    long taskID6 = ds.put(task6.toEntity()).getId();

    // Assert 6 entities were added
    Assert.assertEquals(6, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Delete task1 and its subtasks recursively with TaskController
    TaskController taskController = new TaskController(ds);
    taskController.deleteTasks(new ArrayList<>(Arrays.asList(taskID1)));

    // Assert 1 task entity remains
    Assert.assertEquals(1, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Expected remaining task
    // TaskData task2 =
    //     new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);
    // TaskData task2 = new TaskData(taskID2, 0, projectID2, name2, description2, status2, users2);
    task6.setTaskID(taskID6);

    // Assert the correct task entity remains
    Assert.assertEquals(
        "deleteTask", task6, new TaskData(ds.prepare(new Query("Task")).asSingleEntity()));
  }
}
