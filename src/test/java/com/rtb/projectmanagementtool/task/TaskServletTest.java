package com.rtb.projectmanagementtool.task;

import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class TaskServletTest extends Mockito {

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
    servlet = new TaskServlet();
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

    // Expected results
    String expectedResult =
        "{\"task\":[{\"taskID\":1,\"projectID\":1,\"name\":\"Task 1\",\"description\":\"Task 1"
            + " description...\",\"status\":\"INCOMPLETE\",\"users\":[1,2],\"subtasks\":[3]}]}";

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

    // Run doGet()
    servlet.doPost(request, response);
  }
}
