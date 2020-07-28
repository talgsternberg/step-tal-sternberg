package com.rtb.projectmanagementtool.task;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
//import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;

/** Class testing tasks */
// @RunWith(JUnit4.class)
public class TaskTest {
  
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

  private void doTest() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    
    // Assert no task entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Task")).countEntities(withLimit(10)));
    
    // Build task entity 1
    Entity entity1 = new Entity("Task");
    entity1.setProperty("projectID", 1l);
    entity1.setProperty("name", "Task 1");
    entity1.setProperty("description", "Task 1 description...");
    entity1.setProperty("status", Status.INCOMPLETE.toString());
    HashSet<Long> users1 = new HashSet<>();
    users1.add(1l);
    users1.add(2l);
    entity1.setProperty("users", users1);
    HashSet<Long> subtasks1 = new HashSet<>();
    subtasks1.add(3l);
    entity1.setProperty("subtasks", subtasks1);
    ds.put(entity1);
    
    // Build task entity 2
    Entity entity2 = new Entity("Task");
    entity2.setProperty("projectID", 1l);
    entity2.setProperty("name", "Task 2");
    entity2.setProperty("description", "Task 2 description...");
    entity2.setProperty("status", Status.COMPLETE.toString());
    HashSet<Long> users2 = new HashSet<>();
    users2.add(1l);
    users2.add(3l);
    entity2.setProperty("users", users2);
    HashSet<Long> subtasks2 = new HashSet<>();
    entity2.setProperty("subtasks", subtasks2);
    ds.put(entity2);
    
    // Assert 2 entities were added
    Assert.assertEquals(2, ds.prepare(new Query("Task")).countEntities(withLimit(10)));
  }

  @Test
  public void testInsertIntoDs1() {
    doTest();
  }

  @Test
  public void testInsertIntoDs2() {
    doTest();
  }

  @Test
  public void testCreateTaskFromConstructor() {
    // Create task attributes
    long taskID = 1l;
    long projectID = 1l;
    String name = "Task 1";
    String description = "Task 1 description...";
    Status status = Status.INCOMPLETE;
    HashSet<Long> users = new HashSet<>();
    users.add(1l);
    users.add(2l);
    HashSet<Long> subtasks = new HashSet<>();
    subtasks.add(3l);

    // Build TaskData object
    TaskData task = new TaskData(taskID, projectID, name, description, status, users, subtasks);
    
    // Assert TaskData parameters were stored correctly
    Assert.assertEquals("taskID", taskID, task.getTaskID());
    Assert.assertEquals("projectID", projectID, task.getProjectID());
    Assert.assertEquals("name", name, task.getName());
    Assert.assertEquals("description", description, task.getDescription());
    Assert.assertEquals("status", status, task.getStatus());
    Assert.assertEquals("users", users, task.getUsers());
    Assert.assertEquals("subtasks", subtasks, task.getSubtasks());
  }

  @Test
  public void testCreateTaskFromEntity() {
    // Create task attributes
    long taskID = 2l;
    long projectID = 1l;
    String name = "Task 2";
    String description = "Task 2 description...";
    Status status = Status.COMPLETE;
    HashSet<Long> users = new HashSet<>();
    users.add(1l);
    users.add(3l);
    HashSet<Long> subtasks = new HashSet<>();

    // Get entity attributes
    Entity entity = new Entity("Task", taskID);
    entity.setProperty("projectID", projectID);
    entity.setProperty("name", name);
    entity.setProperty("description", description);
    entity.setProperty("status", status.name());
    entity.setProperty("users", users);
    entity.setProperty("subtasks", subtasks);

    // Build TaskData object from entity
    TaskData task = new TaskData(entity);
    
    // Assert TaskData parameters were stored correctly
    Assert.assertEquals("taskID", taskID, task.getTaskID());
    Assert.assertEquals("projectID", projectID, task.getProjectID());
    Assert.assertEquals("name", name, task.getName());
    Assert.assertEquals("description", description, task.getDescription());
    Assert.assertEquals("status", status, task.getStatus());
    Assert.assertEquals("users", users, task.getUsers());
    Assert.assertEquals("subtasks", subtasks, task.getSubtasks());
  }

  @Test
  public void testCreateEntityFromTask() {
    // Create task attributes
    long taskID = 3l;
    long projectID = 1l;
    String name = "Task 3";
    String description = "Task 3 description...";
    Status status = Status.INCOMPLETE;
    HashSet<Long> users = new HashSet<>();
    users.add(3l);
    HashSet<Long> subtasks = new HashSet<>();

    // Build TaskData object
    TaskData task = new TaskData(taskID, projectID, name, description, status, users, subtasks);
    
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
    Assert.assertEquals("taskID", taskID, entityTaskID);
    Assert.assertEquals("projectID", projectID, entityProjectID);
    Assert.assertEquals("name", name, entityName);
    Assert.assertEquals("description", description, entityDescription);
    Assert.assertEquals("status", status, entityStatus);
    Assert.assertEquals("users", users, entityUsers);
    Assert.assertEquals("subtasks", subtasks, entitySubtasks);
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
    
    // Create task 1 attributes
    long taskID1 = 1l;
    long projectID1 = 1l;
    String name1 = "Task 1";
    String description1 = "Task 1 description...";
    Status status1 = Status.INCOMPLETE;
    HashSet<Long> users1 = new HashSet<>();
    users1.add(1l);
    users1.add(2l);
    HashSet<Long> subtasks1 = new HashSet<>();
    subtasks1.add(3l);

    // Create task 2 attributes
    long taskID2 = 2l;
    long projectID2 = 1l;
    String name2 = "Task 2";
    String description2 = "Task 2 description...";
    Status status2 = Status.COMPLETE;
    HashSet<Long> users2 = new HashSet<>();
    users2.add(1l);
    users2.add(3l);
    HashSet<Long> subtasks2 = new HashSet<>();

    // Create task 3 attributes
    long taskID3 = 3l;
    long projectID3 = 1l;
    String name3 = "Task 3";
    String description3 = "Task 3 description...";
    Status status3 = Status.INCOMPLETE;
    HashSet<Long> users3 = new HashSet<>();
    users3.add(3l);
    HashSet<Long> subtasks3 = new HashSet<>();

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
    TaskData task1 = new TaskData(taskID1, projectID1, name1, description1, status1, users1, subtasks1);
    TaskData task2 = new TaskData(taskID2, projectID2, name2, description2, status2, users2, subtasks2);
    TaskData task3 = new TaskData(taskID3, projectID3, name3, description3, status3, users3, subtasks3);

    // Create ArrayList of TaskData objects
    ArrayList<TaskData> tasks = new ArrayList<>();
    tasks.add(task1);
    tasks.add(task2);
    tasks.add(task3);

    // Get task entities with TaskController
    TaskController taskController = new TaskController();
    HashSet<TaskData> getTasks = taskController.getTasks(5, "name", "ascending");

    // Sort getTasks by taskID
    ArrayList<TaskData> t = new ArrayList<>();
    t.addAll(getTasks);
    Collections.sort(t);

    // Assert all 3 entities were retrieved
    for (int i = 0; i < 3; i++) {
      Assert.assertTrue("getTask", t.get(i).equals(tasks.get(i)));
    } 
  }
}