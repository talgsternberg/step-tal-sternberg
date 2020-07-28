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

/** Class testing tasks */
public class TaskTest {

  // Task 1 attributes
  private static final long taskID1 = 1l;
  private static final long projectID1 = 1l;
  private static final String name1 = "Task 1";
  private static final String description1 = "Task 1 description...";
  private static final Status status1 = Status.INCOMPLETE;
  private static final HashSet<Long> users1 = new HashSet<>(Arrays.asList(1l, 2l));
  private static final HashSet<Long> subtasks1 = new HashSet<>(Arrays.asList(3l));

  // Task 2 attributes
  private static final long taskID2 = 2l;
  private static final long projectID2 = 1l;
  private static final String name2 = "Task 2";
  private static final String description2 = "Task 2 description...";
  private static final Status status2 = Status.COMPLETE;
  private static final HashSet<Long> users2 = new HashSet<>(Arrays.asList(1l, 3l));
  private static final HashSet<Long> subtasks2 = new HashSet<>();

  // Task 3 attributes
  private static final long taskID3 = 3l;
  private static final long projectID3 = 1l;
  private static final String name3 = "Task 3";
  private static final String description3 = "Task 3 description...";
  private static final Status status3 = Status.INCOMPLETE;
  private static final HashSet<Long> users3 = new HashSet<>(Arrays.asList(3l));
  private static final HashSet<Long> subtasks3 = new HashSet<>();

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

  private void DsInserts() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Build task entity 1
    Entity entity1 = new Entity("Task", taskID1);
    entity1.setProperty("projectID", projectID1);
    entity1.setProperty("name", name1);
    entity1.setProperty("description", description1);
    entity1.setProperty("status", status1.toString());
    entity1.setProperty("users", users1);
    entity1.setProperty("subtasks", subtasks1);

    // Build task entity 2
    Entity entity2 = new Entity("Task", taskID2);
    entity2.setProperty("projectID", projectID2);
    entity2.setProperty("name", name2);
    entity2.setProperty("description", description2);
    entity2.setProperty("status", status2.toString());
    entity2.setProperty("users", users2);
    entity2.setProperty("subtasks", subtasks2);

    // Add task entities to ds
    ds.put(entity1);
    ds.put(entity2);

    // Assert 2 entities were added
    Assert.assertEquals(2, ds.prepare(new Query("Task")).countEntities(withLimit(10)));
  }

  @Test
  public void testInsertIntoDs1() {
    DsInserts();
  }

  @Test
  public void testInsertIntoDs2() {
    DsInserts();
  }

  @Test
  public void testCreateTaskFromConstructor() {
    // Build TaskData object
    TaskData task =
        new TaskData(taskID1, projectID1, name1, description1, status1, users1, subtasks1);

    // Assert TaskData parameters were stored correctly
    Assert.assertEquals("taskID", taskID1, task.getTaskID());
    Assert.assertEquals("projectID", projectID1, task.getProjectID());
    Assert.assertEquals("name", name1, task.getName());
    Assert.assertEquals("description", description1, task.getDescription());
    Assert.assertEquals("status", status1, task.getStatus());
    Assert.assertEquals("users", users1, task.getUsers());
    Assert.assertEquals("subtasks", subtasks1, task.getSubtasks());
  }

  @Test
  public void testCreateTaskFromEntity() {
    // Build entity
    Entity entity = new Entity("Task", taskID2);
    entity.setProperty("projectID", projectID2);
    entity.setProperty("name", name2);
    entity.setProperty("description", description2);
    entity.setProperty("status", status2.name());
    entity.setProperty("users", users2);
    entity.setProperty("subtasks", subtasks2);

    // Build TaskData object from entity
    TaskData task = new TaskData(entity);

    // Assert TaskData parameters were stored correctly
    Assert.assertEquals("taskID", taskID2, task.getTaskID());
    Assert.assertEquals("projectID", projectID2, task.getProjectID());
    Assert.assertEquals("name", name2, task.getName());
    Assert.assertEquals("description", description2, task.getDescription());
    Assert.assertEquals("status", status2, task.getStatus());
    Assert.assertEquals("users", users2, task.getUsers());
    Assert.assertEquals("subtasks", subtasks2, task.getSubtasks());
  }

  @Test
  public void testCreateEntityFromTask() {
    // Build TaskData object
    TaskData task =
        new TaskData(taskID3, projectID3, name3, description3, status3, users3, subtasks3);

    // Create task entity from TaskData object
    Entity entity = task.toEntity();

    // Get task entity attributes
    long entityTaskID = (long) entity.getKey().getId();
    long entityProjectID = (long) entity.getProperty("projectID");
    String entityName = (String) entity.getProperty("name");
    String entityDescription = (String) entity.getProperty("description");
    Status entityStatus = Status.valueOf((String) entity.getProperty("status"));
    HashSet<Long> entityUsers = (HashSet<Long>) entity.getProperty("users");
    HashSet<Long> entitySubtasks = (HashSet<Long>) entity.getProperty("subtasks");

    // Assert task entity attributes equal TaskData attributes
    Assert.assertEquals("taskID", taskID3, entityTaskID);
    Assert.assertEquals("projectID", projectID3, entityProjectID);
    Assert.assertEquals("name", name3, entityName);
    Assert.assertEquals("description", description3, entityDescription);
    Assert.assertEquals("status", status3, entityStatus);
    Assert.assertEquals("users", users3, entityUsers);
    Assert.assertEquals("subtasks", subtasks3, entitySubtasks);
  }

  @Test
  public void testGetTasksFromEmptyDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Get task entities with TaskController
    TaskController taskController = new TaskController();
    HashSet<TaskData> tasks = taskController.getTasks(3, "taskID", "ascending");

    // Assert no entities were retrieved
    Assert.assertEquals("getTasks", new HashSet<TaskData>(), tasks);
  }

  @Test
  public void testGetTasksFromDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));

    // Build task entity 1
    Entity entity1 = new Entity("Task", taskID1);
    entity1.setProperty("projectID", projectID1);
    entity1.setProperty("name", name1);
    entity1.setProperty("description", description1);
    entity1.setProperty("status", status1.toString());
    entity1.setProperty("users", users1);
    entity1.setProperty("subtasks", subtasks1);

    // Build task entity 2
    Entity entity2 = new Entity("Task", taskID2);
    entity2.setProperty("projectID", projectID2);
    entity2.setProperty("name", name2);
    entity2.setProperty("description", description2);
    entity2.setProperty("status", status2.toString());
    entity2.setProperty("users", users2);
    entity2.setProperty("subtasks", subtasks2);

    // Build task entity 2
    Entity entity3 = new Entity("Task", taskID3);
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
    TaskController taskController = new TaskController();
    HashSet<TaskData> getTasks = taskController.getTasks(5, "name", "ascending");

    // Sort getTasks by taskID
    ArrayList<TaskData> getTasksArrayList = new ArrayList<>();
    getTasksArrayList.addAll(getTasks);
    Collections.sort(getTasksArrayList);

    // Assert all 3 entities were retrieved
    for (int i = 0; i < 3; i++) {
      Assert.assertTrue("getTask", getTasksArrayList.get(i).equals(tasks.get(i)));
    }
  }
}
