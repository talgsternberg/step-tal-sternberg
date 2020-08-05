package com.rtb.projectmanagementtool.task;

import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class TaskServletTest extends Mockito {

  private DatastoreService datastore;
  private TaskServlet servlet;
  private HttpServletRequest request;
  private HttpServletResponse response;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    servlet = new TaskServlet(datastore);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoGet() throws IOException, ServletException {

    // When parameters are requested, return test values
    when(request.getParameter("taskID")).thenReturn("1");

    // Create writer
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Run doGet()
    servlet.doGet(request, response);

    // Get results of doGet()
    String result = stringWriter.getBuffer().toString().trim();

    // Expected default task
    long taskID1 = 1l;
    long projectID1 = 1l;
    String name1 = "Task 1";
    String description1 = "Task 1 description...";
    Status status1 = Status.INCOMPLETE;
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);

    // Create expected results
    ArrayList<TaskData> taskInArrayList = new ArrayList<>(Arrays.asList(task1));
    HashMap<String, ArrayList> data = new HashMap<>();
    data.put("task", taskInArrayList);
    Gson gson = new Gson();
    String expectedResult = gson.toJson(data);

    // Assert results are as expected
    Assert.assertEquals("doGet", result, expectedResult);
  }

  @Test
  public void testDoPost() throws IOException, ServletException {

    // When parameters are requested, return test values
    when(request.getParameter("projectID")).thenReturn("1");
    when(request.getParameter("name")).thenReturn("Task 1");
    when(request.getParameter("description")).thenReturn("Task 1 description...");
    when(request.getParameter("status")).thenReturn("incomplete");

    // Create writer
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Get quantity of tasks before posting
    int quantityBefore = datastore.prepare(new Query("Task")).countEntities();

    // Run doPost()
    servlet.doPost(request, response);

    // Get quantity of tasks after posting
    int quantityAfter = datastore.prepare(new Query("Task")).countEntities();

    // Assert a task entity was added to datastore
    Assert.assertEquals("doPost", quantityAfter, quantityBefore + 1);
  }
}
