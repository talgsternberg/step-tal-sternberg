package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class NewProjectServletTest extends Mockito {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private DatastoreService datastore;
  private ProjectController projectController;
  private NewProjectServlet servlet;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

  @Before
  public void setUp() {
    helper.setUp();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    datastore = DatastoreServiceFactory.getDatastoreService();
    projectController = new ProjectController(datastore);
    servlet = new NewProjectServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void addNewProject() throws Exception {
    when(request.getParameter("name")).thenReturn("Project");
    when(request.getParameter("description")).thenReturn("Desc");

    // Run Servlet
    servlet.doPost(request, response);

    // Create the project
    ProjectData newProject = new ProjectData("Project", "Desc", 0l);
    newProject.setId(1l);

    ArrayList<ProjectData> expectedProjects = new ArrayList<ProjectData>(Arrays.asList(newProject));
    ArrayList<ProjectData> actualProjects = projectController.getAllProjects();

    Assert.assertEquals(expectedProjects.toString(), actualProjects.toString());
  }
}
