import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.project.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ProjectControllerTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  DatastoreService ds;
  ProjectController projectController;

  // Project Creators
  private final Long USER1 = 1l;
  private final Long USER2 = 2l;
  private final Long USER3 = 3l;
  private final Long USER4 = 4l;

  private ProjectData PROJECT1 = new ProjectData("Project 1", "Project 1 Description", USER1);
  private ProjectData PROJECT2 = new ProjectData("Project 2", "Project 2 Description", USER1);
  private ProjectData PROJECT3 = new ProjectData("Project 3", "Project 3 Description", USER2);
  private ProjectData PROJECT4 = new ProjectData("Project 4", "Project 4 Description", USER3);

  @Before
  public void setUp() {
    helper.setUp();

    ds = DatastoreServiceFactory.getDatastoreService();
    projectController = new ProjectController(ds);

    // Add users to projects
    PROJECT2.addUser(USER2, UserProjectRole.ADMIN);
    PROJECT2.addUser(USER4, UserProjectRole.MEMBER);
    PROJECT2.addUser(USER3, UserProjectRole.ADMIN);
    PROJECT1.addUser(USER4, UserProjectRole.ADMIN);
    PROJECT4.addUser(USER2, UserProjectRole.MEMBER);
    PROJECT3.addUser(USER3, UserProjectRole.MEMBER);
    PROJECT3.addUser(USER1, UserProjectRole.MEMBER);

    // Set hardcoded ids to mimic expected behavior
    PROJECT1.setId(1l);
    PROJECT2.setId(2l);
    PROJECT3.setId(3l);
    PROJECT4.setId(4l);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  // Test saving projects to database
  // Projects added here are used in subsequent test methods
  @Ignore
  public void addProjects() {
    ArrayList<ProjectData> expectedProjects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT1, PROJECT2, PROJECT3, PROJECT4));
    projectController.addProject(PROJECT1);
    projectController.addProject(PROJECT2);

    projectController.addProject(PROJECT3);
    projectController.addProject(PROJECT4);
    Assert.assertEquals(
        expectedProjects.toString(), projectController.getProjects(null).toString());
  }

  // Test querying for specific user's projects, both as owner and admin/regular user
  @Test
  public void getProjectWithCreator() {
    projectController.addProject(PROJECT1);
    projectController.addProject(PROJECT2);
    projectController.addProject(PROJECT3);
    projectController.addProject(PROJECT4);

    ArrayList<ProjectData> expectedUserProjects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT1, PROJECT2));

    ArrayList<String> queryList = new ArrayList<String>();
    String userString = ProjectData.createUserString(USER1, UserProjectRole.CREATOR);
    queryList.add(userString);

    ArrayList<ProjectData> actualUserProjects = projectController.getProjects(queryList);

    Assert.assertEquals(expectedUserProjects.toString(), actualUserProjects.toString());
  }

  @Test
  public void getProjectsWithUser() {
    projectController.addProject(PROJECT1);
    projectController.addProject(PROJECT2);
    projectController.addProject(PROJECT3);
    projectController.addProject(PROJECT4);

    ArrayList<ProjectData> expectedUserProjects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT3, PROJECT2, PROJECT4));

    ArrayList<String> queryList = new ArrayList<String>();
    queryList.add(ProjectData.createUserString(USER2, UserProjectRole.CREATOR));
    queryList.add(ProjectData.createUserString(USER2, UserProjectRole.ADMIN));
    queryList.add(ProjectData.createUserString(USER2, UserProjectRole.MEMBER));

    ArrayList<ProjectData> actualUserProjects = projectController.getProjects(queryList);
    Assert.assertEquals(expectedUserProjects.toString(), actualUserProjects.toString());
  }

  // Test project removal from database
  @Test
  public void removeProjects() {
    projectController.addProject(PROJECT1);
    projectController.addProject(PROJECT2);
    projectController.addProject(PROJECT3);
    projectController.addProject(PROJECT4);

    ArrayList<ProjectData> expectedProjects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT1, PROJECT2, PROJECT3, PROJECT4));

    Assert.assertEquals(
        expectedProjects.toString(), projectController.getProjects(null).toString());

    expectedProjects.remove(PROJECT2);
    projectController.removeProject(PROJECT2);
    Assert.assertEquals(
        expectedProjects.toString(), projectController.getProjects(null).toString());

    expectedProjects.remove(PROJECT3);
    expectedProjects.remove(PROJECT1);
    projectController.removeProject(PROJECT1);
    projectController.removeProject(PROJECT3);
    Assert.assertEquals(
        expectedProjects.toString(), projectController.getProjects(null).toString());
  }
}
