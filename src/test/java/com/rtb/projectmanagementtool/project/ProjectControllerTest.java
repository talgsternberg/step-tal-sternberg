import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.project.ProjectController;
import com.rtb.projectmanagementtool.project.ProjectData;
import java.util.Collections;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProjectControllerTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void noProjects() {
    ProjectController projectController = new ProjectController();
    Assert.assertEquals(projectController.getProjects(), Collections.emptySet());
  }

  @Test
  public void addProjects() {
    ProjectController projectController = new ProjectController();
    projectController.createProject("1", "1-desc", 1l);

    Assert.assertEquals(projectController.getProjects().size(), 1);

    projectController.createProject("2", "2-desc", 0l);

    Assert.assertEquals(projectController.getProjects().size(), 2);
  }

  @Test
  public void removeProjects() {
    ProjectController projectController = new ProjectController();
    projectController.createProject("1", "1-desc", 1l);
    projectController.createProject("2", "2-desc", 1l);

    for (ProjectData project : projectController.getProjects()) {
      projectController.removeProject(project.getId());
      break;
    }

    Assert.assertEquals(projectController.getProjects().size(), 1);
  }

  // Remove project:
  // projectController.removeProject(Long.parseLong("projectid"))
  // Add project:
  // projectController.createProject("name", "description", Long.parseLong("creatorId"));
  // Get project:
  // projectController.getProject(Long.parseLong("projectid"))
  // Print Project:
}
