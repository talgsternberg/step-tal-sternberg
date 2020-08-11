package com.rtb.projectmanagementtool.task;

import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class TaskSetStatusServletTest extends Mockito {

  private DatastoreService datastore;
  private TaskSetStatusServlet servlet;
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
    servlet = new TaskSetStatusServlet(datastore);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoPostSetComplete() throws IOException, ServletException {
    TaskController taskController = new TaskController(datastore);

    // Create writer
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Add a test task
    long projectID = 1l;
    String name = "Task 1";
    String description = "Task 1 description...";
    Status status = Status.INCOMPLETE;
    ArrayList<Long> users = new ArrayList<>();
    TaskData task = new TaskData(projectID, name, description, status, users);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task)));

    // Get status of task before posting
    Status statusBefore =
        new TaskData(
                datastore
                    .prepare(
                        new Query("Task")
                            .addFilter(
                                "__key__",
                                FilterOperator.EQUAL,
                                KeyFactory.createKey("Task", task.getTaskID())))
                    .asSingleEntity())
            .getStatus();

    // Assert usersBefore is correct
    Assert.assertEquals("Status before", Status.INCOMPLETE, statusBefore);

    // When parameters are requested, return test values
    when(request.getParameter("taskID")).thenReturn(Long.toString(task.getTaskID()));
    when(request.getParameter("status")).thenReturn(Status.COMPLETE.name());

    // Run doPost()
    servlet.doPost(request, response);

    // Get status of task after posting
    Status statusAfter =
        new TaskData(
                datastore
                    .prepare(
                        new Query("Task")
                            .addFilter(
                                "__key__",
                                FilterOperator.EQUAL,
                                KeyFactory.createKey("Task", task.getTaskID())))
                    .asSingleEntity())
            .getStatus();

    // Assert usersBefore is correct
    Assert.assertEquals("Status after", Status.COMPLETE, statusAfter);
  }

  @Test
  public void testDoPostSetIncomplete() throws IOException, ServletException {
    TaskController taskController = new TaskController(datastore);

    // Create writer
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Add a test task
    long projectID = 1l;
    String name = "Task 1";
    String description = "Task 1 description...";
    Status status = Status.COMPLETE;
    ArrayList<Long> users = new ArrayList<>();
    TaskData task = new TaskData(projectID, name, description, status, users);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task)));

    // Get status of task before posting
    Status statusBefore =
        new TaskData(
                datastore
                    .prepare(
                        new Query("Task")
                            .addFilter(
                                "__key__",
                                FilterOperator.EQUAL,
                                KeyFactory.createKey("Task", task.getTaskID())))
                    .asSingleEntity())
            .getStatus();

    // Assert usersBefore is correct
    Assert.assertEquals("Status before", Status.COMPLETE, statusBefore);

    // When parameters are requested, return test values
    when(request.getParameter("taskID")).thenReturn(Long.toString(task.getTaskID()));
    when(request.getParameter("status")).thenReturn(Status.INCOMPLETE.name());

    // Run doPost()
    servlet.doPost(request, response);

    // Get status of task after posting
    Status statusAfter =
        new TaskData(
                datastore
                    .prepare(
                        new Query("Task")
                            .addFilter(
                                "__key__",
                                FilterOperator.EQUAL,
                                KeyFactory.createKey("Task", task.getTaskID())))
                    .asSingleEntity())
            .getStatus();

    // Assert usersBefore is correct
    Assert.assertEquals("Status after", Status.INCOMPLETE, statusAfter);
  }
}
