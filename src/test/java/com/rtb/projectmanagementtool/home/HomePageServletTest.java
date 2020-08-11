package com.rtb.projectmanagementtool.home;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.rtb.projectmanagementtool.project.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

// import com.rtb.projectmanagementtool.auth.*;

public class HomePageServletTest extends Mockito {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private DatastoreService datastore;
  private ProjectController projectController;
  private TaskController taskController;
  private UserController userController;
  private HomePageServlet servlet;

  ProjectData project;
  UserData user;
  TaskData task1;

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
    userController = new UserController(datastore);
    taskController = new TaskController(datastore);
    servlet = new HomePageServlet();

    // Create user used in test
    createUser();

    // Create project used in test
    project = new ProjectData("Project1", "Desc", 2l);
    project.setId(1);
    projectController.addProject(project);

    // Create task used in test
    createTask();
  }

  // Same code used to create one UserData in UserControllerTest.java
  private void createUser() {
    long userID1 = 2l;
    String AuthID1 = "abc";
    String name1 = "Anna";
    long year1 = 2023;
    ArrayList<String> majors1 = new ArrayList<>(Arrays.asList("Biology", "Gov"));
    long total1 = 3;
    Skills skill = Skills.WRITING;
    user = new UserData(userID1, AuthID1, name1, year1, majors1, skill, total1);
    userController.addUser(user);
  }

  // Same code used to create one TaskData in TaskControllerTest.java
  private void createTask() {
    long projectID1 = 1l;
    String name1 = "Task 1";
    String description1 = "Task 1 description...";
    Status status = Status.INCOMPLETE;
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));
    task1 = new TaskData(projectID1, name1, description1, status, users1);
    taskController.addTasks(new ArrayList<TaskData>(Arrays.asList(task1)));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void homePageData() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    when(request.getParameter("userId")).thenReturn("2");

    // Call getJsonForResponse() method in HomePageServlet to
    // generate expected response string
    String expectedOutput =
        servlet
            .getJsonForResponse(
                true,
                user,
                new ArrayList<ProjectData>(Arrays.asList(project)),
                new ArrayList<TaskData>(Arrays.asList(task1)))
            .trim();

    // Run Servlet
    servlet.doGet(request, response);

    // Get actual response string
    String actualOutput = stringWriter.getBuffer().toString().trim();

    Gson gson = new Gson();
    Assert.assertEquals(gson.toJson(expectedOutput), gson.toJson(actualOutput));

    // Response looks something like this:
    //
    // {"user":"{"userID":2,"AuthID":"abc","userName":"Anna","userYear":2023,
    //  "userMajors":["Biology","Gov"],"skills":"WRITING","userTotalCompTasks":3}",
    //
    // "userProjects":"[{"PROPERTY_NAME":"name","PROPERTY_CREATOR":"creator",
    // "PROPERTY_DESCRIPTION":"description","PROPERTY_TASKS":"tasks",
    // "PROPERTY_ADMINS":"admins","PROPERTY_MEMBERS":"members","id":1,"creatorId":2,
    // "name":"Project1","description":"Desc","members":[],"admins":[]}]",
    //
    // "userTasks":"[{"taskID":2,"parentTaskID":0,"projectID":1,"name":"Task 1",
    // "description":"Task 1 description...","status":"INCOMPLETE","users":[1,2]}]"}
    //
    // Will be left to javascript to use whatever variables are needed
  }

  @Test
  public void returnUserNotLoggedIn() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    when(request.getParameter("userId")).thenReturn("-1");

    String expectedOutput =
        servlet
            .getJsonForResponse(
                /*userLoggedIn*/ false, /*user*/ null, /*userProjects*/ null, /*userTasks*/ null)
            .trim();

    // Run Servlet
    servlet.doGet(request, response);

    // Get actual response string
    String actualOutput = stringWriter.getBuffer().toString().trim();

    Gson gson = new Gson();
    Assert.assertEquals(gson.toJson(expectedOutput), gson.toJson(actualOutput));
  }
}
