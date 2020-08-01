import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.project.ProjectController;
import com.rtb.projectmanagementtool.project.ProjectData;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
    PROJECT2.addAdminUser(USER2);
    PROJECT2.addRegularUser(USER4);
    PROJECT2.addAdminUser(USER3);
    PROJECT1.addAdminUser(USER4);
    PROJECT4.addRegularUser(USER2);
    PROJECT3.addRegularUser(USER3);
    PROJECT3.addRegularUser(USER1);

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

  // One test method because I need tests to run in this order
  @Test
  public void runProjectControllerTest() {
    testAddProjectsToDatabase();
    testSaveProjectsToDatabase();
    testGetProjectWithUserAndCreator();
    testRemoveProjects();
  }

  // Test saving projects to database
  // Projects added here are used in subsequent test methods
  public void testAddProjectsToDatabase() {
    // Add projects to database to use get projects later
    projectController.addProject(PROJECT1);
    Assert.assertEquals(1, projectController.getAllProjects().size());

    projectController.addProject(PROJECT2);
    Assert.assertEquals(2, projectController.getAllProjects().size());

    projectController.addProject(PROJECT3);
    Assert.assertEquals(3, projectController.getAllProjects().size());

    projectController.addProject(PROJECT4);
    Assert.assertEquals(4, projectController.getAllProjects().size());
  }

  // Test saving projects to database
  public void testSaveProjectsToDatabase() {
    ArrayList<ProjectData> expectedProjects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT1, PROJECT2, PROJECT3, PROJECT4));

    ArrayList<ProjectData> actualProjects = projectController.getAllProjects();

    // comparing the arraylists to each other directly without toString() returns false,
    // not entirely sure why
    Assert.assertEquals(expectedProjects.toString(), actualProjects.toString());
  }

  // Test querying for specific user's projects, both as owner and admin/regular user
  public void testGetProjectWithUserAndCreator() {
    // Only projects that USER1 created
    // USER1: creates PROJECT1, creates PROJECT2, regularUser in PROJECT3
    ArrayList<ProjectData> expectedUser1CreatorProjects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT1, PROJECT2));

    // Only projects that USER2 is in
    // USER2: creates PROJECT3, admin in PROJECT2, regularUser in PROJECT4
    ArrayList<ProjectData> expectedUser2Projects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT2, PROJECT3, PROJECT4));

    // Test retrieving projects USER1 created
    ArrayList<ProjectData> actualUser1CreatorProjects = projectController.getCreatorProjects(USER1);
    System.out.println("PROJECTS: " + actualUser1CreatorProjects);
    Assert.assertEquals(
        expectedUser1CreatorProjects.toString(), actualUser1CreatorProjects.toString());

    // Test retrieving projects USER2 is in
    ArrayList<ProjectData> actualUser2Projects = projectController.getUserProjects(USER2);
    Assert.assertEquals(expectedUser2Projects.toString(), actualUser2Projects.toString());
  }

  // Test project removal from database
  public void testRemoveProjects() {
    // Check that number of projects = 4
    Assert.assertEquals(4, projectController.getAllProjects().size());

    projectController.removeProject(PROJECT1);
    Assert.assertEquals(3, projectController.getAllProjects().size());

    projectController.removeProject(PROJECT2);
    Assert.assertEquals(2, projectController.getAllProjects().size());

    projectController.removeProject(PROJECT3);
    Assert.assertEquals(1, projectController.getAllProjects().size());

    projectController.removeProject(PROJECT4);
    Assert.assertEquals(0, projectController.getAllProjects().size());
  }
}
