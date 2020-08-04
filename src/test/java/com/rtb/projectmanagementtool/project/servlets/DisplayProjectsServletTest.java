package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class DisplayProjectsServletTest extends Mockito {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private DatastoreService datastore;
  private ProjectController projectController;
  private DisplayProjectsServlet servlet;

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
    servlet = new DisplayProjectsServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void displayProjectsWithUser() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    when(request.getParameter("query")).thenReturn("isIn");

    // Create project - user should be in this one
    ProjectData newProject = new ProjectData("Project1", "Desc", 2l);
    projectController.addProject(newProject);

    // Create project - this project shouldn't be in output, although it is in database
    ProjectData newProject2 = new ProjectData("Project2", "Desc2", 3l);
    projectController.addProject(newProject2);

    // Create project - this project should be included in output
    ProjectData newProject3 = new ProjectData("Project3", "Desc3", 4l);
    newProject3.addUser(2l, UserProjectRole.ADMIN);
    projectController.addProject(newProject3);

    // Run Servlet
    servlet.doPost(request, response);

    ArrayList<String> expectedOutput =
        new ArrayList<String>(Arrays.asList(newProject.getName(), newProject3.getName()));
    String actualOutput = stringWriter.getBuffer().toString().trim();

    // Converting expectedOutput to gson because servlet returns stringWriter as json
    Gson gson = new Gson();
    Assert.assertEquals(gson.toJson(expectedOutput).toString(), actualOutput);
  }

  @Test
  public void displayProjectsWithCreator() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    when(request.getParameter("query")).thenReturn("isCreator"); // get creator

    // Create project - user should be in this one
    ProjectData newProject = new ProjectData("Project1", "Desc", 2l);
    projectController.addProject(newProject);

    // Create project - this project shouldn't be in output
    ProjectData newProject2 = new ProjectData("Project2", "Desc2", 3l);
    projectController.addProject(newProject2);

    // Create project - this project shouldn't be in output
    ProjectData newProject3 = new ProjectData("Project3", "Desc3", 4l);
    newProject3.addUser(2l, UserProjectRole.ADMIN);
    projectController.addProject(newProject3);

    // Call doPost
    servlet.doPost(request, response);

    ArrayList<String> expectedOutput = new ArrayList<String>(Arrays.asList(newProject.getName()));
    String actualOutput = stringWriter.getBuffer().toString().trim();

    // Converting expectedOutput to gson because servlet returns stringWriter as json
    Gson gson = new Gson();
    Assert.assertEquals(gson.toJson(expectedOutput).toString(), actualOutput);
  }
}
