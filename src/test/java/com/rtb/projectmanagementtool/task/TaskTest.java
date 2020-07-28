package com.rtb.projectmanagementtool.task;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
//import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.HashSet;
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
    Assert.assertEquals(0, ds.prepare(new Query("task")).countEntities(withLimit(10)));
    
    // Build task entity 1
    Entity entity1 = new Entity("task");
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
    Entity entity2 = new Entity("task");
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
    Assert.assertEquals(2, ds.prepare(new Query("task")).countEntities(withLimit(10)));
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
  public void testCreateFromConstructor() {
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
    Assert.assertEquals(task.getTaskID(), taskID);
    Assert.assertEquals(task.getProjectID(), projectID);
    Assert.assertEquals(task.getName(), name);
    Assert.assertEquals(task.getDescription(), description);
    Assert.assertEquals(task.getStatus(), Status.INCOMPLETE);
    Assert.assertEquals(task.getUsers(), users);
    Assert.assertEquals(task.getSubtasks(), subtasks);
  }

  @Test
  public void testCreateFromEntity() {
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

    // Build task entity
    Entity entity = new Entity("task", taskID);
    entity.setProperty("projectID", projectID);
    entity.setProperty("name", name);
    entity.setProperty("description", description);
    entity.setProperty("status", status.name());
    entity.setProperty("users", users);
    entity.setProperty("subtasks", subtasks);
    TaskData task = new TaskData(entity);
    
    // Assert TaskData parameters were stored correctly
    Assert.assertEquals(task.getTaskID(), taskID);
    Assert.assertEquals(task.getProjectID(), projectID);
    Assert.assertEquals(task.getName(), name);
    Assert.assertEquals(task.getDescription(), description);
    Assert.assertEquals(task.getStatus(), status);
    Assert.assertEquals(task.getUsers(), users);
    Assert.assertEquals(task.getSubtasks(), subtasks);
  }
}