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

  // Task 2 attributes
  private static final long projectID2 = 1l;
  private static final String name2 = "Task 2";
  private static final String description2 = "Task 2 description...";
  private static final Status status2 = Status.COMPLETE;
  private static final ArrayList<Long> users2 = new ArrayList<>(Arrays.asList(1l, 3l));

  // Task 3 attributes
  private static final long projectID3 = 1l;
  private static final String name3 = "Task 3";
  private static final String description3 = "Task 3 description...";
  private static final Status status3 = Status.INCOMPLETE;
  private static final ArrayList<Long> users3 = new ArrayList<>(Arrays.asList(3l));

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
  public void testAddTasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task entities with TaskController
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

    // Add task1 with TaskController
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1)));

    // Add task2 and task3 as subtasks of task1 with TaskController
    ArrayList<TaskData> subtasks = new ArrayList<>(Arrays.asList(task2, task3));
    taskController.addSubtasks(task1, subtasks);

    // Get subtasks
    Query query =
        new Query("Task").addFilter("parentTaskID", FilterOperator.EQUAL, task1.getTaskID());
    PreparedQuery results = ds.prepare(query);
    ArrayList<TaskData> getSubtasks = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      getSubtasks.add(new TaskData(entity));
    }

    // Sort lists
    Collections.sort(subtasks);
    Collections.sort(getSubtasks);

    // Assert correct subtasks were found
    Assert.assertEquals("addSubtasks", subtasks, getSubtasks);

    // Assert correct amount of subtasks were found
    Assert.assertEquals(2, results.countEntities(withLimit(10)));
  }

  @Test
  public void testAddUser() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create users
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));

    // Create task
    TaskData task = new TaskData(projectID1, name1, description1, status1, users1);

    // Add task to ds with TaskController
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task)));

    // Create user
    long user = 4l;

    // Add user to task with TaskController with TaskData object
    taskController.addUser(task, user);

    // Create expected users
    ArrayList<Long> users = new ArrayList<>(Arrays.asList(1l, 2l, 4l));

    // Get users from ds with TaskController
    ArrayList<Long> getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();

    // Assert task has correct userIDs
    Assert.assertEquals("addUser", users, getUsers);

    // Attempt to add user to task with TaskController again with TaskID
    taskController.addUser(task.getTaskID(), user);

    // Get users from ds with TaskController
    getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();

    // Assert the user wasn't added to task again
    Assert.assertEquals("addUser", users, task.getUsers());
  }

  @Test
  public void testAddUserRecursive() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create users
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l, 3l));
    ArrayList<Long> users2 = new ArrayList<>(Arrays.asList(1l, 3l));
    ArrayList<Long> users3 = new ArrayList<>(Arrays.asList(3l));

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

    // Add task1 to ds with TaskController
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task1)));

    // Add task2 and task3 as subtasks of task1 to ds with TaskController
    taskController.addSubtasks(task1, new ArrayList<TaskData>(Arrays.asList(task2, task3)));

    // Create user
    long user = 4l;

    // Add user to task3 with TaskController with taskID
    taskController.addUser(task3.getTaskID(), user);

    // Create expected users
    ArrayList<Long> expectedUsers1 = new ArrayList<>(Arrays.asList(1l, 2l, 3l, 4l));
    ArrayList<Long> expectedUsers2 = new ArrayList<>(Arrays.asList(1l, 3l));
    ArrayList<Long> expectedUsers3 = new ArrayList<>(Arrays.asList(3l, 4l));

    // Get users from ds with TaskController
    ArrayList<Long> getUsers1 = taskController.getTaskByID(task1.getTaskID()).getUsers();
    ArrayList<Long> getUsers2 = taskController.getTaskByID(task2.getTaskID()).getUsers();
    ArrayList<Long> getUsers3 = taskController.getTaskByID(task3.getTaskID()).getUsers();

    // Assert task has correct userIDs
    Assert.assertEquals("addUser", expectedUsers1, getUsers1);
    Assert.assertEquals("addUser", expectedUsers2, getUsers2);
    Assert.assertEquals("addUser", expectedUsers3, getUsers3);

    // Attempt to add user to task with TaskController again with TaskData object
    taskController.addUser(task3, user);

    // Get users from ds with TaskController
    getUsers1 = taskController.getTaskByID(task1.getTaskID()).getUsers();
    getUsers2 = taskController.getTaskByID(task2.getTaskID()).getUsers();
    getUsers3 = taskController.getTaskByID(task3.getTaskID()).getUsers();

    // Assert the user wasn't added to task again
    Assert.assertEquals("addUser", expectedUsers1, getUsers1);
    Assert.assertEquals("addUser", expectedUsers2, getUsers2);
    Assert.assertEquals("addUser", expectedUsers3, getUsers3);
  }

  @Test
  public void testRemoveUser() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create users
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));

    // Create task
    TaskData task = new TaskData(projectID1, name1, description1, status1, users1);

    // Add task to ds with TaskController
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task)));

    // Create user
    long user = 2l;

    // Remove user from task with TaskController with taskID
    taskController.removeUser(task.getTaskID(), user);

    // Create expected users
    ArrayList<Long> users = new ArrayList<>(Arrays.asList(1l));

    // Get users from ds with TaskController
    ArrayList<Long> getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();

    // Assert task has correct userIDs
    Assert.assertEquals("removeUser", users, getUsers);

    // Attempt to remove user from task with TaskController again
    taskController.removeUser(task.getTaskID(), user);

    // Get users from ds with TaskController
    getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();

    // Assert users weren't changed
    Assert.assertEquals("removeUser", users, getUsers);
  }

  @Test
  public void testRemoveUserRecursive() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create users
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l, 3l));
    ArrayList<Long> users2 = new ArrayList<>(Arrays.asList(1l, 2l));
    ArrayList<Long> users3 = new ArrayList<>(Arrays.asList(3l));

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);
    TaskData task4 = new TaskData(projectID1, name1, description1, status1, users1);

    // Add task1 and task4 to ds with TaskController
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task1, task4)));

    // Add task2 and task3 as subtasks of task1 to ds with TaskController
    taskController.addSubtasks(task1, new ArrayList<TaskData>(Arrays.asList(task2, task3)));

    // Create user
    long user = 2l;

    // Remove user from task with TaskController with TaskData
    taskController.removeUser(task1, user);

    // Create expected users
    ArrayList<Long> expectedUsers1 = new ArrayList<>(Arrays.asList(1l, 3l));
    ArrayList<Long> expectedUsers2 = new ArrayList<>(Arrays.asList(1l));
    ArrayList<Long> expectedUsers3 = new ArrayList<>(Arrays.asList(3l));
    ArrayList<Long> expectedUsers4 = new ArrayList<>(Arrays.asList(1l, 2l, 3l));

    // Get users from ds with TaskController
    ArrayList<Long> getUsers1 = taskController.getTaskByID(task1.getTaskID()).getUsers();
    ArrayList<Long> getUsers2 = taskController.getTaskByID(task2.getTaskID()).getUsers();
    ArrayList<Long> getUsers3 = taskController.getTaskByID(task3.getTaskID()).getUsers();
    ArrayList<Long> getUsers4 = taskController.getTaskByID(task4.getTaskID()).getUsers();

    // Assert task has correct userIDs
    Assert.assertEquals("removeUser", expectedUsers1, getUsers1);
    Assert.assertEquals("removeUser", expectedUsers2, getUsers2);
    Assert.assertEquals("removeUser", expectedUsers3, getUsers3);
    Assert.assertEquals("removeUser", expectedUsers4, getUsers4);

    // Attempt to remove user to task with TaskController again with taskID
    taskController.removeUser(task1.getTaskID(), user);

    // Get users from ds with TaskController
    getUsers1 = taskController.getTaskByID(task1.getTaskID()).getUsers();
    getUsers2 = taskController.getTaskByID(task2.getTaskID()).getUsers();
    getUsers3 = taskController.getTaskByID(task3.getTaskID()).getUsers();
    getUsers4 = taskController.getTaskByID(task4.getTaskID()).getUsers();

    // Assert users weren't changed
    Assert.assertEquals("removeUser", expectedUsers1, getUsers1);
    Assert.assertEquals("removeUser", expectedUsers2, getUsers2);
    Assert.assertEquals("removeUser", expectedUsers3, getUsers3);
    Assert.assertEquals("removeUser", expectedUsers4, getUsers4);
  }

  @Test
  public void testGetTaskByID() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

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
    TaskData getTask = taskController.getTaskByID(taskID2);

    // Add taskID to task2
    task2.setTaskID(taskID2);

    // Assert task retrieved is correct
    Assert.assertEquals("getTask", task2, getTask);
  }

  @Test
  public void testGetTasksByUserID() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks: { task1 users: [1, 2]; task2 users: [1, 3]; task3 users: [3] }
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task entities with TaskController
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1, task2, task3)));

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Get tasks of each user with TaskController
    ArrayList<TaskData> getUser1Tasks = taskController.getTasksByUserID(1l);
    ArrayList<TaskData> getUser2Tasks = taskController.getTasksByUserID(2l);
    ArrayList<TaskData> getUser3Tasks = taskController.getTasksByUserID(3l);

    // Create expected tasks
    ArrayList<TaskData> user1Tasks = new ArrayList<TaskData>(Arrays.asList(task1, task2));
    ArrayList<TaskData> user2Tasks = new ArrayList<TaskData>(Arrays.asList(task1));
    ArrayList<TaskData> user3Tasks = new ArrayList<TaskData>(Arrays.asList(task2, task3));

    // Assert tasks retrieved are correct
    Assert.assertEquals("getTasksByUserID", user1Tasks, getUser1Tasks);
    Assert.assertEquals("getTasksByUserID", user2Tasks, getUser2Tasks);
    Assert.assertEquals("getTasksByUserID", user3Tasks, getUser3Tasks);
  }

  @Test
  public void testGetTasksByProjectID() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks:
    TaskData task1 = new TaskData(1l, name1, description1, status1, users1);
    TaskData task2 = new TaskData(1l, name2, description2, status2, users2);
    TaskData task3 = new TaskData(2l, name3, description3, status3, users3);
    TaskData task4 = new TaskData(1l, name1, description1, status1, users1);

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add task1, task2, and task3 entities with TaskController
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1, task2, task3)));

    // Add task4 as subtask of task1 with TaskController
    taskController.addSubtasks(task1, new ArrayList<>(Arrays.asList(task4)));

    // Assert 4 entities were added
    Assert.assertEquals(4, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Get tasks of each user with TaskController
    ArrayList<TaskData> getProject1Tasks = taskController.getTasksByProjectID(1l);
    ArrayList<TaskData> getProject2Tasks = taskController.getTasksByProjectID(2l);

    // Create expected tasks
    ArrayList<TaskData> project1Tasks = new ArrayList<TaskData>(Arrays.asList(task1, task2));
    ArrayList<TaskData> project2Tasks = new ArrayList<TaskData>(Arrays.asList(task3));

    // Assert tasks retrieved are correct
    Assert.assertEquals("getTasksByProjectID", project1Tasks, getProject1Tasks);
    Assert.assertEquals("getTasksByProjectID", project2Tasks, getProject2Tasks);
  }

  @Test
  public void testGetTasksFromEmptyDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Get task entities with TaskController
    ArrayList<TaskData> tasks = taskController.getTasks(3, "description", "ascending");

    // Assert no entities were retrieved
    Assert.assertEquals("getTasks", new ArrayList<>(), tasks);
  }

  @Test
  public void testGetTasksFromDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

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

    // Add taskIDs to tasks
    task1.setTaskID(taskID1);
    task2.setTaskID(taskID2);
    task3.setTaskID(taskID3);

    // Create ArrayList of expected TaskData objects
    ArrayList<TaskData> tasks = new ArrayList<>(Arrays.asList(task1, task2, task3));

    // Get all task entities with TaskController
    ArrayList<TaskData> getTasks = taskController.getTasks(5, "name", "ascending");

    // Assert all entities were retrieved
    Assert.assertEquals("getTask", tasks, getTasks);

    // Remove task3 from expected TaskData objects
    tasks.remove(task3);

    // Get some task entities with TaskController
    getTasks = taskController.getTasks(2, "name", "ascending");

    // Assert correct entities were retrieved
    Assert.assertEquals("getTask", tasks, getTasks);
  }

  @Test
  public void testGetSubtasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, status3, users3);

    // Add task1 with TaskController
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1)));

    // Add task2 and task3 as subtasks of task1 with TaskController
    ArrayList<TaskData> subtasks = new ArrayList<>(Arrays.asList(task2, task3));
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
    TaskController taskController = new TaskController(ds);

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
    taskController.deleteTasks(new ArrayList<>(Arrays.asList(taskID1, taskID3)));

    // Assert 1 task entity remains
    Assert.assertEquals(1, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add taskID to task2
    task2.setTaskID(taskID2);

    // Assert the correct task entity remains
    Assert.assertEquals(
        "deleteTask", task2, new TaskData(ds.prepare(new Query("Task")).asSingleEntity()));
  }

  @Test
  public void testDeleteTaskWithSubtasksRecursively() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

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
    taskController.deleteTasks(new ArrayList<>(Arrays.asList(taskID1)));

    // Assert 1 task entity remains
    Assert.assertEquals(1, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Add taskID to task6
    task6.setTaskID(taskID6);

    // Assert the correct task entity remains
    Assert.assertEquals(
        "deleteTask", task6, new TaskData(ds.prepare(new Query("Task")).asSingleEntity()));
  }
}
