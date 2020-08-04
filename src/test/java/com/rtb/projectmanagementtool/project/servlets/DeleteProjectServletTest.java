package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.*;
import java.util.Collections;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class DeleteProjectServletTest extends Mockito {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private DatastoreService datastore;
  private ProjectController projectController;
  private DeleteProjectServlet servlet;

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
    servlet = new DeleteProjectServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void deleteProject() throws Exception {
    projectController.addProject(new ProjectData("Project", "Desc", 0l));

    // Assert that the project was added
    Assert.assertEquals(1, projectController.getAllProjects().size());

    when(request.getParameter("id")).thenReturn("1");

    // Run servlet
    servlet.doPost(request, response);

    Assert.assertEquals(Collections.emptyList(), projectController.getAllProjects());
  }
}
