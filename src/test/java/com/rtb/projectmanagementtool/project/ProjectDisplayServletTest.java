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

public class ProjectDisplayServletTest extends Mockito {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private DatastoreService datastore;
  private ProjectController projectController;
  private ProjectDisplayServlet servlet;

  ProjectData newProject1;
  ProjectData newProject2;
  ProjectData newProject3;

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
    servlet = new ProjectDisplayServlet();

    // Create projects used in tests
    newProject1 = new ProjectData("Project1", "Desc", 2l);
    projectController.addProject(newProject1);

    newProject2 = new ProjectData("Project2", "Desc2", 3l);
    projectController.addProject(newProject2);

    newProject3 = new ProjectData("Project3", "Desc3", 4l);
    newProject3.addAdminUser(2l);
    projectController.addProject(newProject3);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getDataForProjectPage() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    when(request.getParameter("page")).thenReturn("project");
    when(request.getParameter("projectId")).thenReturn("3"); // hard-coded

    // Run Servlet
    servlet.doGet(request, response);

    String expectedOutput = newProject3.toString().replaceAll(" ", "");
    String actualOutput = stringWriter.getBuffer().toString().trim();

    Assert.assertEquals(expectedOutput, actualOutput);
  }

  @Test
  public void getDataForHomePage() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    when(request.getParameter("page")).thenReturn("home");
    when(request.getParameter("userId")).thenReturn("2"); // hard-coded

    // Run Servlet
    servlet.doGet(request, response);

    ArrayList<ProjectData> expectedListOutput =
        new ArrayList<ProjectData>(Arrays.asList(newProject1, newProject3));

    String expectedOutput = expectedListOutput.toString();
    String actualOutput = stringWriter.getBuffer().toString().trim();

    Assert.assertEquals(expectedOutput, actualOutput);
  }
}
