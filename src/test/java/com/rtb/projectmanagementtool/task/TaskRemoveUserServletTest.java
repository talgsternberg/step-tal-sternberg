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

public class TaskRemoveUserServletTest extends Mockito {

  private DatastoreService datastore;
  private TaskRemoveUserServlet servlet;
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
    servlet = new TaskRemoveUserServlet(datastore);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoPost() throws IOException, ServletException {
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
    long user = 1l;
    ArrayList<Long> users = new ArrayList<>(Arrays.asList(user));
    TaskData task = new TaskData(projectID, name, description, status, users);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task)));

    // Get users of task before posting
    ArrayList<Long> usersBefore =
        new TaskData(
                datastore
                    .prepare(
                        new Query("Task")
                            .addFilter(
                                "__key__",
                                FilterOperator.EQUAL,
                                KeyFactory.createKey("Task", task.getTaskID())))
                    .asSingleEntity())
            .getUsers();

    // Assert usersBefore is the correct length
    Assert.assertEquals("length of usersBefore", 1, usersBefore.size());

    // When parameters are requested, return test values
    when(request.getParameter("taskID")).thenReturn(Long.toString(task.getTaskID()));
    when(request.getParameter("userID")).thenReturn(Long.toString(user));

    // Run doPost()
    servlet.doPost(request, response);

    // Get users of task after posting
    ArrayList<Long> usersAfter =
        new TaskData(
                datastore
                    .prepare(
                        new Query("Task")
                            .addFilter(
                                "__key__",
                                FilterOperator.EQUAL,
                                KeyFactory.createKey("Task", task.getTaskID())))
                    .asSingleEntity())
            .getUsers();

    // Assert usersAfter is the correct length
    Assert.assertEquals("length of usersAfter", 0, usersAfter.size());

    // Assert the user was added to the task
    usersBefore.remove(user);
    Assert.assertEquals("addUser", usersBefore, usersAfter);
  }
}
