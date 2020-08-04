import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.project.*;
import java.util.ArrayList;
import java.util.Arrays;
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

  // Project Users
  private final Long USER1 = 1l;
  private final String USER1_MEMBER = new String(UserProjectRole.MEMBER + "-" + USER1);
  private final Long USER2 = 2l;
  private final String USER2_CREATOR = new String(UserProjectRole.CREATOR + "-" + USER2);
  private final Long USER3 = 3l;
  private final String USER3_ADMIN = new String(UserProjectRole.ADMIN + "-" + USER3);
  private final Long USER4 = 4l;
  private final String USER4_ADMIN = new String(UserProjectRole.ADMIN + "-" + USER4);

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

    ArrayList<Long> expectedTasks = new ArrayList<Long>();
    ArrayList<String> expectedUsers = new ArrayList<String>();
    expectedUsers.add(USER2_CREATOR);

    Assert.assertEquals(expectedTasks, project1.getTasks());
    Assert.assertEquals(expectedUsers, project1.getUsers());
  }

  // Test project creation from entity
  @Test
  public void createProjectFromEntity() {
    // Create entity
    Entity entity = new Entity("Project");
    entity.setProperty("name", PROJECT1_NAME);
    entity.setProperty("description", PROJECT1_DESC);
    entity.setProperty("tasks", new ArrayList<Long>());
    ArrayList expectedUsers = new ArrayList<String>(Arrays.asList(USER2_CREATOR));
    entity.setProperty("users", expectedUsers);

    // Create project
    ProjectData project = new ProjectData(entity);
    Assert.assertEquals(expectedUsers, project.getUsers());
  }

  @Test
  public void createEntityFromProject() {
    // Create expected entity
    Entity expectedEntity = new Entity("Project");
    expectedEntity.setProperty("name", PROJECT1_NAME);
    expectedEntity.setProperty("description", PROJECT1_DESC);
    expectedEntity.setProperty("tasks", new ArrayList<Long>());
    expectedEntity.setProperty("users", new ArrayList<String>(Arrays.asList(USER2_CREATOR)));

    // Create project and call toEntity()
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);
    Entity actualEntity = project.toEntity();

    Assert.assertEquals(expectedEntity.toString(), actualEntity.toString());
  }

  @Test
  public void hasOwner() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);
    Assert.assertTrue(project.hasUser(USER1) && project.isCreator(USER1));
  }

  @Test
  public void hasAdminAndMemberUsers() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);
    project.addUser(USER2, UserProjectRole.ADMIN);
    project.addUser(USER3, UserProjectRole.MEMBER);
    project.addUser(USER4, UserProjectRole.ADMIN);

    Assert.assertTrue(project.hasUser(USER2) && project.hasAdmin(USER2));
    Assert.assertTrue(project.hasUser(USER4) && project.hasAdmin(USER4));
    Assert.assertFalse(project.hasAdmin(USER3));
    Assert.assertTrue(project.hasUser(USER3));
  }

  @Test
  public void removeUser() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);
    project.addUser(USER1, UserProjectRole.MEMBER);
    project.addUser(USER4, UserProjectRole.ADMIN);

    ArrayList expectedUsers =
        new ArrayList<String>(Arrays.asList(USER2_CREATOR, USER1_MEMBER, USER4_ADMIN));
    ArrayList actualUsers = project.getUsers();
    Assert.assertEquals(expectedUsers, project.getUsers());

    project.removeUser(USER4);
    expectedUsers.remove(USER4_ADMIN);
    Assert.assertEquals(expectedUsers, project.getUsers());

    project.removeUser(USER1);
    expectedUsers.remove(USER1_MEMBER);
    Assert.assertEquals(expectedUsers, project.getUsers());
  }

  @Test
  public void cantRemoveCreator() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);
    Assert.assertFalse(project.removeUser(USER2));
  }

  @Test
  public void testAddTasks() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);
    ArrayList<Long> expectedTasks = new ArrayList<Long>(Arrays.asList(TASK1, TASK2, TASK3, TASK4));

    for (Long taskId : expectedTasks) {
      project.addTask(taskId);
    }

    ArrayList<Long> actualTasks = new ArrayList<Long>(project.getTasks());
    Assert.assertEquals(expectedTasks, actualTasks);
  }

  @Test
  public void testRemoveTasks() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);
    ArrayList<Long> tasks = new ArrayList<Long>(Arrays.asList(TASK1, TASK2, TASK3, TASK4));

    for (Long taskId : tasks) {
      project.addTask(taskId);
    }

    Assert.assertEquals(project.getTasks().size(), 4);
    project.removeTask(TASK1);
    project.removeTask(TASK2);
    project.removeTask(TASK3);
    project.removeTask(TASK4);
    Assert.assertEquals(project.getTasks().size(), 0);
  }
}
