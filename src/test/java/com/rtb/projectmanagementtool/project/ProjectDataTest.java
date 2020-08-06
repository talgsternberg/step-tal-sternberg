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
    Assert.assertEquals("no admins in project", Collections.emptySet(), project1.getAdmins());
    Assert.assertEquals("no members in project", Collections.emptySet(), project1.getMembers());
  }

  // Test project creation from entity
  @Test
  public void createProjectFromEntity() {
    // Create entity
    Entity entity = new Entity("Project");
    entity.setProperty(PROPERTY_NAME, PROJECT1_NAME);
    entity.setProperty(PROPERTY_CREATOR, USER2);
    entity.setProperty(PROPERTY_DESCRIPTION, PROJECT1_DESC);
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

    HashSet<Long> projectAdmins = project.getAdmins();
    HashSet<Long> projectMembers = project.getMembers();

    Assert.assertEquals("project admins match", entity.getProperty(PROPERTY_ADMINS), projectAdmins);
    Assert.assertEquals(
        "project members match", entity.getProperty(PROPERTY_MEMBERS), projectMembers);
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
    project.addMemberUser(USER3);
    project.addAdminUser(USER4);

    Assert.assertTrue("user in project", project.hasUser(USER3));
    Assert.assertTrue("user in project", project.hasUser(USER4));
    Assert.assertFalse("user not in project", project.hasUser(USER2));
    Assert.assertTrue("user is admin", project.hasAdmin(USER4));
    Assert.assertTrue("user is creator", project.isCreator(USER1));
  }

  @Test
  public void removeUser() {
    ProjectData project = new ProjectData(PROJECT1_NAME, PROJECT1_DESC, USER1);
    project.addMemberUser(USER3);
    project.addAdminUser(USER4);
    project.addAdminUser(USER2);

    // Assert users are in project
    Assert.assertEquals("1 member in project", 1, project.getMembers().size());

    Assert.assertEquals("2 admins in project", 2, project.getAdmins().size());

    // Remove users
    project.removeMember(USER2);
    project.removeMember(USER3);
    project.removeMember(USER4);

    // Assert users have been removed
    Assert.assertEquals("0 members in project", 0, project.getMembers().size());
    Assert.assertEquals("0 admins in project", 0, project.getAdmins().size());
  }
}
