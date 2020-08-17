package com.rtb.projectmanagementtool.task;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.task.TaskData.Status;
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
  public void testAddUserBeforePuttingIntoDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create users list
    ArrayList<Long> users = new ArrayList<>(Arrays.asList(1l, 2l));

    // Create task: { task users: [1, 2] }
    TaskData task = new TaskData(projectID1, name1, description1, status1, users);

    // Create user
    long user = 4l;

    // Add user to task with TaskController with TaskData object
    taskController.addUser(task, user);

    // Update expected users
    users.add(user);

    // Assert task has correct userIDs
    Assert.assertEquals("addUser", users, task.getUsers());

    // Attempt to add user to task with TaskController again
    taskController.addUser(task, user);

    // Assert the user wasn't added to task again
    Assert.assertEquals("addUser", users, task.getUsers());
  }

  @Test
  public void testAddUserAfterPuttingIntoDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create users
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));

    // Create task
    TaskData task = new TaskData(projectID1, name1, description1, Status.COMPLETE, users1);

    // Add task to ds with TaskController
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task)));

    // Create user
    long user = 4l;

    // Add user to task with TaskController with TaskData object
    taskController.addUser(task, user);

    // Create expected users
    ArrayList<Long> users = new ArrayList<>(Arrays.asList(1l, 2l));

    // Get users from ds with TaskController
    ArrayList<Long> getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();
    Status getStatus = taskController.getTaskByID(task.getTaskID()).getStatus();

    // Assert no users were added while task status is complete
    Assert.assertEquals("taskIsComplete", Status.COMPLETE, getStatus);
    Assert.assertEquals("addUserWhenComplete", users, getUsers);

    // Change task status to incomplete
    task.setStatus(Status.INCOMPLETE);
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task)));

    // Add user to task with TaskController with TaskData object
    taskController.addUser(task, user);

    // Create expected users
    users.add(user);

    // Get users from ds with TaskController
    getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();
    getStatus = taskController.getTaskByID(task.getTaskID()).getStatus();

    // Assert task has correct userIDs
    Assert.assertEquals("taskIsIncomplete", Status.INCOMPLETE, getStatus);
    Assert.assertEquals("addUserWhenIncomplete", users, getUsers);

    // Attempt to add user to task with TaskController again with TaskID
    taskController.addUser(task.getTaskID(), user);

    // Get users from ds with TaskController
    getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();

    // Assert the user wasn't added to task again
    Assert.assertEquals("addUser", users, task.getUsers());
  }

  @Test
  public void testRemoveUserBeforePuttingIntoDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create users list
    ArrayList<Long> users = new ArrayList<>(Arrays.asList(1l, 2l));

    // Create task: { task users: [1, 2] }
    TaskData task = new TaskData(projectID1, name1, description1, status1, users);

    // Create user
    long user = 2l;

    // Remove user from task with TaskController with TaskData object
    taskController.removeUser(task, user);

    // Update expected users
    users.remove(user);

    // Assert task has correct userIDs
    Assert.assertEquals("addUser", users, task.getUsers());

    // Attempt to remove user from task with TaskController again
    taskController.removeUser(task, user);

    // Assert users weren't changed
    Assert.assertEquals("addUser", users, task.getUsers());
  }

  @Test
  public void testRemoveUserAfterPuttingIntoDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create users
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));

    // Create task
    TaskData task = new TaskData(projectID1, name1, description1, Status.COMPLETE, users1);

    // Add task to ds with TaskController
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task)));

    // Create user
    long user = 2l;

    // Remove user from task with TaskController with taskID
    taskController.removeUser(task.getTaskID(), user);

    // Create expected users
    ArrayList<Long> users = new ArrayList<>(Arrays.asList(1l, 2l));

    // Get users from ds with TaskController
    ArrayList<Long> getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();
    Status getStatus = taskController.getTaskByID(task.getTaskID()).getStatus();

    // Assert task has correct userIDs
    Assert.assertEquals("taskIsComplete", Status.COMPLETE, getStatus);
    Assert.assertEquals("removeUserWhenComplete", users, getUsers);

    // Change task status to incomplete
    task.setStatus(Status.INCOMPLETE);
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task)));

    // Remove user from task with TaskController with taskID
    taskController.removeUser(task.getTaskID(), user);

    // Create expected users
    users.remove(user);

    // Get users from ds with TaskController
    getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();
    getStatus = taskController.getTaskByID(task.getTaskID()).getStatus();

    // Assert task has correct userIDs
    Assert.assertEquals("taskIsIncomplete", Status.INCOMPLETE, getStatus);
    Assert.assertEquals("removeUser", users, getUsers);

    // Attempt to remove user from task with TaskController again
    taskController.removeUser(task.getTaskID(), user);

    // Get users from ds with TaskController
    getUsers = taskController.getTaskByID(task.getTaskID()).getUsers();

    // Assert users weren't changed
    Assert.assertEquals("removeUser", users, getUsers);
  }

  @Test
  public void testSetStatusComplete() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create task
    TaskData task = new TaskData(projectID1, name1, description1, status1, users1);

    // Assert status is INCOMPLETE
    Assert.assertEquals("status", Status.INCOMPLETE, task.getStatus());

    // Set status as complete with TaskController
    boolean isSuccessful = taskController.setComplete(task);

    // Assert status was set successfully
    Assert.assertTrue("isSuccessful", isSuccessful);

    // Assert status is COMPLETE
    Assert.assertEquals("status", Status.COMPLETE, task.getStatus());
  }

  @Test
  public void testSetStatusCompleteWithCompleteSubtasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, Status.INCOMPLETE, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, Status.INCOMPLETE, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, Status.INCOMPLETE, users3);

    // Add task1 with TaskController
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1)));

    // Add task2 and task3 as subtasks of task1 with TaskController
    ArrayList<TaskData> subtasks = new ArrayList<>(Arrays.asList(task2, task3));
    taskController.addSubtasks(task1, subtasks);

    // Assert status is correct
    Assert.assertEquals("status 1", Status.INCOMPLETE, task1.getStatus());
    Assert.assertEquals("status 2", Status.INCOMPLETE, task2.getStatus());
    Assert.assertEquals("status 3", Status.INCOMPLETE, task3.getStatus());

    // Set status of subtasks as complete with TaskController (with id or object as parameter)
    boolean isSuccessful2 = taskController.setComplete(task2.getTaskID());
    boolean isSuccessful3 = taskController.setComplete(task3);

    // Assert status was set successfully
    Assert.assertTrue("isSuccessful 2", isSuccessful2);
    Assert.assertTrue("isSuccessful 3", isSuccessful3);

    // Get status of tasks from datastore
    Status status1 = taskController.getTaskByID(task1.getTaskID()).getStatus();
    Status status2 = taskController.getTaskByID(task2.getTaskID()).getStatus();
    Status status3 = taskController.getTaskByID(task3.getTaskID()).getStatus();

    // Assert status is correct
    Assert.assertEquals("status 1", Status.INCOMPLETE, status1);
    Assert.assertEquals("status 2", Status.COMPLETE, status2);
    Assert.assertEquals("status 3", Status.COMPLETE, status3);

    // Set status of subtasks as complete with TaskController
    boolean isSuccessful1 = taskController.setComplete(task1);

    // Assert status was set successfully
    Assert.assertTrue("isSuccessful 1", isSuccessful1);

    // Get status of tasks from datastore
    status1 = taskController.getTaskByID(task1.getTaskID()).getStatus();
    status2 = taskController.getTaskByID(task2.getTaskID()).getStatus();
    status3 = taskController.getTaskByID(task3.getTaskID()).getStatus();

    // Assert status is correct
    Assert.assertEquals("status 1", Status.COMPLETE, status1);
    Assert.assertEquals("status 2", Status.COMPLETE, status2);
    Assert.assertEquals("status 3", Status.COMPLETE, status3);
  }

  @Test
  public void testSetStatusCompleteWithIncompleteSubtasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, Status.INCOMPLETE, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, Status.INCOMPLETE, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, Status.INCOMPLETE, users3);

    // Add task1 with TaskController
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1)));

    // Add task2 and task3 as subtasks of task1 with TaskController
    ArrayList<TaskData> subtasks = new ArrayList<>(Arrays.asList(task2, task3));
    taskController.addSubtasks(task1, subtasks);

    // Assert status is correct
    Assert.assertEquals("status 1", Status.INCOMPLETE, task1.getStatus());
    Assert.assertEquals("status 2", Status.INCOMPLETE, task2.getStatus());
    Assert.assertEquals("status 3", Status.INCOMPLETE, task3.getStatus());

    // Set status of only one subtask as complete with TaskController
    boolean isSuccessful2 = taskController.setComplete(task2);

    // Assert status was set successfully
    Assert.assertTrue("isSuccessful 2", isSuccessful2);

    // Get status of tasks from datastore
    Status status1 = taskController.getTaskByID(task1.getTaskID()).getStatus();
    Status status2 = taskController.getTaskByID(task2.getTaskID()).getStatus();
    Status status3 = taskController.getTaskByID(task3.getTaskID()).getStatus();

    // Assert status is correct
    Assert.assertEquals("status 1", Status.INCOMPLETE, status1);
    Assert.assertEquals("status 2", Status.COMPLETE, status2);
    Assert.assertEquals("status 3", Status.INCOMPLETE, status3);

    // Set status of subtasks as complete with TaskController (should not set as complete)
    boolean isSuccessful1 = taskController.setComplete(task1);

    // Assert status was not set
    Assert.assertFalse("isSuccessful 1", isSuccessful1);

    // Get status of tasks from datastore
    status1 = taskController.getTaskByID(task1.getTaskID()).getStatus();
    status2 = taskController.getTaskByID(task2.getTaskID()).getStatus();
    status3 = taskController.getTaskByID(task3.getTaskID()).getStatus();

    // Assert status is correct
    Assert.assertEquals("status 1", Status.INCOMPLETE, status1);
    Assert.assertEquals("status 2", Status.COMPLETE, status2);
    Assert.assertEquals("status 3", Status.INCOMPLETE, status3);
  }

  @Test
  public void testSetStatusIncomplete() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create task
    TaskData task = new TaskData(projectID1, name1, description1, Status.COMPLETE, users1);

    // Assert status is COMPLETE
    Assert.assertEquals("status", Status.COMPLETE, task.getStatus());

    // Set status as incomplete with TaskController
    boolean isSuccessful = taskController.setIncomplete(task);

    // Assert status was set successfully
    Assert.assertTrue("isSuccessful", isSuccessful);

    // Assert status is INCOMPLETE
    Assert.assertEquals("status", Status.INCOMPLETE, task.getStatus());
  }

  @Test
  public void testSetStatusIncompleteWithIncompleteParentTask() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, Status.INCOMPLETE, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, Status.COMPLETE, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, Status.INCOMPLETE, users3);

    // Add task1 with TaskController
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1)));

    // Add task2 and task3 as subtasks of task1 with TaskController
    ArrayList<TaskData> subtasks = new ArrayList<>(Arrays.asList(task2, task3));
    taskController.addSubtasks(task1, subtasks);

    // Assert status is correct
    Assert.assertEquals("status 1", Status.INCOMPLETE, task1.getStatus());
    Assert.assertEquals("status 2", Status.COMPLETE, task2.getStatus());
    Assert.assertEquals("status 3", Status.INCOMPLETE, task3.getStatus());

    // Set status of subtasks as complete with TaskController
    boolean isSuccessful2 = taskController.setIncomplete(task2.getTaskID());

    // Assert status was set successfully
    Assert.assertTrue("isSuccessful 2", isSuccessful2);

    // Get status of tasks from datastore
    Status status1 = taskController.getTaskByID(task1.getTaskID()).getStatus();
    Status status2 = taskController.getTaskByID(task2.getTaskID()).getStatus();
    Status status3 = taskController.getTaskByID(task3.getTaskID()).getStatus();

    // Assert status is correct
    Assert.assertEquals("status 1", Status.INCOMPLETE, status1);
    Assert.assertEquals("status 2", Status.INCOMPLETE, status2);
    Assert.assertEquals("status 3", Status.INCOMPLETE, status3);
  }

  @Test
  public void testSetStatusIncompleteWithCompleteParentTask() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(ds);

    // Create tasks
    TaskData task1 = new TaskData(projectID1, name1, description1, Status.COMPLETE, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, Status.COMPLETE, users2);
    TaskData task3 = new TaskData(projectID3, name3, description3, Status.INCOMPLETE, users3);

    // Add task1 with TaskController
    taskController.addTasks(new ArrayList<>(Arrays.asList(task1)));

    // Add task2 and task3 as subtasks of task1 with TaskController
    ArrayList<TaskData> subtasks = new ArrayList<>(Arrays.asList(task2, task3));
    taskController.addSubtasks(task1, subtasks);

    // Assert status is correct
    Assert.assertEquals("status 1", Status.COMPLETE, task1.getStatus());
    Assert.assertEquals("status 2", Status.COMPLETE, task2.getStatus());
    Assert.assertEquals("status 3", Status.INCOMPLETE, task3.getStatus());

    // Set status of subtasks as complete with TaskController (should not set as incomplete)
    boolean isSuccessful2 = taskController.setIncomplete(task2.getTaskID());

    // Assert status was not set
    Assert.assertFalse("isSuccessful 2", isSuccessful2);

    // Get status of tasks from datastore
    Status status1 = taskController.getTaskByID(task1.getTaskID()).getStatus();
    Status status2 = taskController.getTaskByID(task2.getTaskID()).getStatus();
    Status status3 = taskController.getTaskByID(task3.getTaskID()).getStatus();

    // Assert status is correct
    Assert.assertEquals("status 1", Status.COMPLETE, status1);
    Assert.assertEquals("status 2", Status.COMPLETE, status2);
    Assert.assertEquals("status 3", Status.INCOMPLETE, status3);
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
