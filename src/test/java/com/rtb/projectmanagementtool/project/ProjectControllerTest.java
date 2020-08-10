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
    PROJECT2.addMemberUser(USER4);
    PROJECT2.addAdminUser(USER3);
    PROJECT1.addAdminUser(USER4);
    PROJECT4.addMemberUser(USER2);
    PROJECT3.addMemberUser(USER3);

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

  @Test
  public void addProjects() {
    projectController.addProject(PROJECT1);
    projectController.addProject(PROJECT2);
    projectController.addProject(PROJECT3);
    projectController.addProject(PROJECT4);

    Assert.assertTrue("four projects in database", projectController.getAllProjects().size() == 4);
  }

  @Test
  public void removeProjects() {
    projectController.addProject(PROJECT1);
    projectController.addProject(PROJECT2);
    projectController.addProject(PROJECT3);
    projectController.addProject(PROJECT4);

    Assert.assertTrue("four projects in database", projectController.getAllProjects().size() == 4);

    projectController.removeProject(PROJECT1);
    projectController.removeProject(PROJECT2);

    Assert.assertTrue(
        "two projects in database after removing two",
        projectController.getAllProjects().size() == 2);

    projectController.removeProject(PROJECT3);
    projectController.removeProject(PROJECT4);
    Assert.assertTrue("0 projects in database", projectController.getAllProjects().size() == 0);
  }

  @Test
  public void getProjectsThatUserIsIn() {
    projectController.addProject(PROJECT1);
    projectController.addProject(PROJECT2);
    projectController.addProject(PROJECT3);
    projectController.addProject(PROJECT4);

    ArrayList<ProjectData> expectedUser1Projects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT1, PROJECT2));
    ArrayList<ProjectData> actualUser1Projects = projectController.getProjectsWithUser(USER1);

    ArrayList<ProjectData> expectedUser2Projects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT2, PROJECT3, PROJECT4));
    ArrayList<ProjectData> actualUser2Projects = projectController.getProjectsWithUser(USER2);

    ArrayList<ProjectData> expectedUser3Projects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT2, PROJECT3, PROJECT4));
    ArrayList<ProjectData> actualUser3Projects = projectController.getProjectsWithUser(USER3);

    ArrayList<ProjectData> expectedUser4Projects =
        new ArrayList<ProjectData>(Arrays.asList(PROJECT1, PROJECT2));
    ArrayList<ProjectData> actualUser4Projects = projectController.getProjectsWithUser(USER4);

    Assert.assertEquals("correct projects for user 1", expectedUser1Projects, actualUser1Projects);
    Assert.assertEquals("correct projects for user 2", expectedUser2Projects, actualUser2Projects);
    Assert.assertEquals("correct projects for user 3", expectedUser3Projects, actualUser3Projects);
    Assert.assertEquals("correct projects for user 4", expectedUser4Projects, actualUser4Projects);
  }
}
