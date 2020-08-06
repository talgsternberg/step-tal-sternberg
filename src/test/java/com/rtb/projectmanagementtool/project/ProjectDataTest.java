import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.project.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProjectDataTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final String PROPERTY_NAME = "name";
  private final String PROPERTY_CREATOR = "creator";
  private final String PROPERTY_DESCRIPTION = "description";
  private final String PROPERTY_TASKS = "tasks";
  private final String PROPERTY_ADMINS = "admins";
  private final String PROPERTY_MEMBERS = "members";

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
  private final long USER1 = 1l;
  private final long USER2 = 2l;
  private final long USER3 = 3l;
  private final long USER4 = 4l;

  // Project Tasks
  private final long TASK1 = 1l;
  private final long TASK2 = 2l;
  private final long TASK3 = 3l;
  private final long TASK4 = 4l;

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
  public void createProject() {
    ProjectData project1 = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);

    Assert.assertEquals("project name is correct", project1.getName(), PROJECT1_NAME);
    Assert.assertTrue("project creator id is correct", project1.getCreatorId() == USER2);
    Assert.assertEquals(
        "project derscription is correct", project1.getDescription(), PROJECT1_DESC);
    Assert.assertEquals("no tasks in project", Collections.emptySet(), project1.getTasks());
    Assert.assertEquals(
        "no admins in project",
        Collections.emptySet(),
        project1.getUsers().get(UserProjectRole.ADMIN));
    Assert.assertEquals(
        "no members in project",
        Collections.emptySet(),
        project1.getUsers().get(UserProjectRole.MEMBER));
  }

  // Test project creation from entity
  @Test
  public void createProjectFromEntity() {
    // Create entity
    Entity entity = new Entity("Project");
    entity.setProperty(PROPERTY_NAME, PROJECT1_NAME);
    entity.setProperty(PROPERTY_CREATOR, USER2);
    entity.setProperty(PROPERTY_DESCRIPTION, PROJECT1_DESC);
    entity.setProperty(PROPERTY_TASKS, new HashSet<Long>());
    entity.setProperty(PROPERTY_ADMINS, new HashSet<Long>(Arrays.asList(USER3, USER4)));
    entity.setProperty(PROPERTY_MEMBERS, new HashSet<Long>(Arrays.asList(USER1)));

    // Create project
    ProjectData project = new ProjectData(entity);

    Assert.assertEquals(
        "project names match", entity.getProperty(PROPERTY_NAME), project.getName());
    Assert.assertEquals(
        "project description match",
        entity.getProperty(PROPERTY_DESCRIPTION),
        project.getDescription());
    Assert.assertEquals(
        "project creators match", entity.getProperty(PROPERTY_CREATOR), project.getCreatorId());
    Assert.assertEquals(
        "project tasks match", entity.getProperty(PROPERTY_TASKS), project.getTasks());

    HashSet<Long> projectAdmins = project.getUsers().get(UserProjectRole.ADMIN);
    HashSet<Long> projectMembers = project.getUsers().get(UserProjectRole.MEMBER);

    Assert.assertEquals("project tasks match", entity.getProperty(PROPERTY_ADMINS), projectAdmins);
    Assert.assertEquals(
        "project tasks match", entity.getProperty(PROPERTY_MEMBERS), projectMembers);
  }

  @Test
  public void createEntityFromProject() {
    // Create project and call toEntity()
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER2);
    Entity entity = project.toEntity();

    Assert.assertEquals(
        "name is correct in entity", PROJECT1_NAME, (String) entity.getProperty(PROPERTY_NAME));
    Assert.assertEquals(
        "description is correct in entity",
        PROJECT1_DESC,
        (String) entity.getProperty(PROPERTY_DESCRIPTION));
    Assert.assertTrue(
        "creator id is correct in entity", USER2 == (Long) entity.getProperty(PROPERTY_CREATOR));
    Assert.assertEquals(
        "tasks is empty in entity",
        Collections.emptySet(),
        (HashSet<Long>) entity.getProperty(PROPERTY_TASKS));
    Assert.assertEquals(
        "admin users container is empty in entity",
        Collections.emptySet(),
        (HashSet<Long>) entity.getProperty(PROPERTY_ADMINS));
    Assert.assertEquals(
        "member users container is empty in entity",
        Collections.emptySet(),
        (HashSet<Long>) entity.getProperty(PROPERTY_MEMBERS));
  }

  @Test
  public void hasSpecificUsers() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);
    project.addUser(UserProjectRole.MEMBER, USER3);
    project.addUser(UserProjectRole.ADMIN, USER4);

    Assert.assertTrue("user in project", project.hasUser(USER3));
    Assert.assertTrue("user in project", project.hasUser(USER4));
    Assert.assertFalse("user not in project", project.hasUser(USER2));
    Assert.assertTrue("user is admin", project.hasAdmin(USER4));
    Assert.assertTrue("user is creator", project.isCreator(USER1));
  }

  @Test
  public void removeUser() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);
    project.addUser(UserProjectRole.MEMBER, USER3);
    project.addUser(UserProjectRole.ADMIN, USER4);
    project.addUser(UserProjectRole.ADMIN, USER2);

    // Assert users are in project
    Assert.assertEquals(
        "1 member in project", 1, project.getUsers().get(UserProjectRole.MEMBER).size());

    Assert.assertEquals(
        "2 admins in project", 2, project.getUsers().get(UserProjectRole.ADMIN).size());

    // Remove users
    project.removeUser(USER2);
    project.removeUser(USER3);
    project.removeUser(USER4);

    // Assert users have been removed
    Assert.assertEquals(
        "0 members in project", 0, project.getUsers().get(UserProjectRole.MEMBER).size());

    Assert.assertEquals(
        "0 admins in project", 0, project.getUsers().get(UserProjectRole.ADMIN).size());
  }

  @Test
  public void addTasks() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);

    // Add tasks
    project.addTask(TASK1);
    project.addTask(TASK2);
    project.addTask(TASK3);
    project.addTask(TASK4);

    // Confirm tasks added
    Assert.assertTrue("project has task1", project.getTasks().contains(TASK1));
    Assert.assertTrue("project has task2", project.getTasks().contains(TASK2));
    Assert.assertTrue("project has task3", project.getTasks().contains(TASK3));
    Assert.assertTrue("project has task4", project.getTasks().contains(TASK4));
  }

  @Test
  public void removeTasks() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);

    // Add tasks
    project.addTask(TASK1);
    project.addTask(TASK2);
    project.addTask(TASK3);
    project.addTask(TASK4);
    Assert.assertEquals("tasks added", project.getTasks().size(), 4);

    // Remove tasks
    project.removeTask(TASK1);
    project.removeTask(TASK2);
    project.removeTask(TASK3);
    project.removeTask(TASK4);
    Assert.assertEquals("all tasks removed", project.getTasks().size(), 0);
  }
}
