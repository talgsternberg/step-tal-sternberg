import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.rtb.projectmanagementtool.project.ProjectData;
import com.rtb.projectmanagementtool.project.UserProjectRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProjectDataTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  // Projects
  private final String PROJECT1_NAME = "Project 1";
  private final String PROJECT1_DESC = "Description of Project 1";

  private final String PROJECT2_NAME = "Project 2";
  private final String PROJECT2_DESC = "Description of Project 2";

  private final String PROJECT3_NAME = "Project 3";
  private final String PROJECT3_DESC = "Description of Project 3";

  private final String PROJECT4_NAME = "Project 4";
  private final String PROJECT4_DESC = "Description of Project 4";

  // Project Creators
  private final Long USER1 = 1l;
  private final Long USER2 = 2l;
  private final Long USER3 = 3l;
  private final Long USER4 = 4l;

  // Project Tasks
  private final Long TASK1 = 1l;
  private final Long TASK2 = 2l;
  private final Long TASK3 = 3l;
  private final Long TASK4 = 4l;

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  // Test project creation given name, desc, and creator
  @Test
  public void testCreateProject() {
    ProjectData project1 = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);
    Assert.assertEquals(project1.getName(), PROJECT1_NAME);
    Assert.assertEquals(project1.getDescription(), PROJECT1_DESC);

    HashSet<Long> expectedProject1Tasks = new HashSet<Long>();
    HashMap<Long, UserProjectRole> expectedProject1Users = new HashMap<Long, UserProjectRole>();
    expectedProject1Users.put(2l, UserProjectRole.CREATOR);

    Assert.assertEquals(expectedProject1Tasks, project1.getTasks());
    Assert.assertEquals(expectedProject1Users, project1.getUsers());
  }

  // Test project creation from entity
  @Test
  public void testCreateProjectFromEntity() {
    // Create entity
    Entity entity = new Entity("Project");
    entity.setProperty("name", PROJECT1_NAME);
    entity.setProperty("description", PROJECT1_DESC);
    entity.setProperty("taskIds", new ArrayList<Long>());

    // HashMap -> json
    Gson gson = new Gson();
    HashMap<Long, UserProjectRole> expectedUsers = new HashMap<Long, UserProjectRole>();
    expectedUsers.put(2l, UserProjectRole.CREATOR);
    entity.setProperty("userIds", gson.toJson(expectedUsers));

    ProjectData project = new ProjectData(entity);
    Assert.assertEquals(PROJECT1_NAME, project.getName());
    Assert.assertEquals(PROJECT1_DESC, project.getDescription());
    Assert.assertEquals(new HashSet<Long>(), project.getTasks());
    Assert.assertEquals(expectedUsers, project.getUsers());
  }

  // Test toEntity() method
  @Test
  public void testToEntity() {
    // Create expected entity
    Entity expectedEntity = new Entity("Project");
    expectedEntity.setProperty("name", PROJECT1_NAME);
    expectedEntity.setProperty("description", PROJECT1_DESC);
    expectedEntity.setProperty("taskIds", new HashSet<Long>());

    // HashMap -> json
    Gson gson = new Gson();
    HashMap<Long, UserProjectRole> expectedUsers = new HashMap<Long, UserProjectRole>();
    expectedUsers.put(2l, UserProjectRole.CREATOR);
    expectedEntity.setProperty("userIds", gson.toJson(expectedUsers));

    ProjectData project1 = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);
    Entity actualEntity = project1.toEntity();

    // Exclude projectId in check
    Assert.assertEquals(actualEntity.getProperty("name"), expectedEntity.getProperty("name"));
    Assert.assertEquals(
        actualEntity.getProperty("description"), expectedEntity.getProperty("description"));
    Assert.assertEquals(actualEntity.getProperty("userIds"), expectedEntity.getProperty("userIds"));
    Assert.assertEquals(actualEntity.getProperty("taskIds"), expectedEntity.getProperty("taskIds"));
  }

  // Test ProjectData functionality of checking user types
  @Test
  public void testHasUserByType() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);

    project.addRegularUser(USER1);
    project.addAdminUser(USER3);
    project.addRegularUser(USER4);

    // hasUser() works for all users, regardless of type
    Assert.assertTrue(project.hasUser(USER1));
    Assert.assertTrue(project.hasUser(USER2));
    Assert.assertTrue(project.hasUser(USER3));
    Assert.assertTrue(project.hasUser(USER4));

    // hasAmin() works correctly
    Assert.assertTrue(project.hasAdmin(USER3));
    Assert.assertFalse(project.hasAdmin(USER4));

    // isCreator() works correctly
    Assert.assertTrue(project.isCreator(USER2));
  }

  // Test removing a user
  @Test
  public void testRemoveUser() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);

    project.addRegularUser(USER1);

    // hasUser() works for all users, regardless of type
    Assert.assertTrue(project.getUsers().size() == 2); // 2 because project creator is in

    project.removeUser(USER1);

    Assert.assertTrue(project.getUsers().size() == 1);
  }

  // Test unable to remove creator of project
  @Test
  public void testCantRemoveCreator() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);

    Assert.assertFalse(project.removeUser(USER2));
  }

  // Test adding tasks to project (by task id)
  @Test
  public void testAddTasks() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);
    ArrayList<Long> expectedTasks = new ArrayList<Long>(Arrays.asList(TASK1, TASK2, TASK3, TASK4));

    for (Long taskId : expectedTasks) {
      project.addTask(taskId);
    }

    ArrayList<Long> actualTasks = new ArrayList<Long>(project.getTasks());
    Assert.assertEquals(expectedTasks, actualTasks);

    testRemoveTasks(project);
  }

  // Test removing tasks from project
  public void testRemoveTasks(ProjectData project) {
    int currentSize = project.getTasks().size();

    project.removeTask(TASK1);
    currentSize--;
    Assert.assertEquals(project.getTasks().size(), currentSize);
    project.removeTask(TASK4);
    currentSize--;
    Assert.assertEquals(project.getTasks().size(), currentSize);
    project.removeTask(TASK4); // should return false, so size won't change
    Assert.assertEquals(project.getTasks().size(), currentSize);
  }
}
