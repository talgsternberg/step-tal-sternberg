import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

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

  @Ignore
  public void noProjects() {}

  @Ignore
  public void getProjectsWithUser() {}

  @Ignore
  public void addProjects() {}

  @Ignore
  public void removeProject() {}

  @Ignore
  public void saveProjects() {}
}
