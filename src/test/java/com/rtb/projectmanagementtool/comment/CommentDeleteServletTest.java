package com.rtb.projectmanagementtool.comment;

import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.user.*;
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

public class CommentDeleteServletTest extends Mockito {

  private DatastoreService datastore;
  private CommentDeleteServlet servlet;
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
    servlet = new CommentDeleteServlet(datastore);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoPost() throws IOException, ServletException {

    // Create CommentData object
    CommentData comment = new CommentData(0l, 1l, "title", "message");

    // Add task to datastore with CommentController
    CommentController commentController = new CommentController(datastore);
    commentController.addComments(new ArrayList<>(Arrays.asList(comment)));

    // Create cookie array to authenticate
    Cookie[] cookies = {new Cookie("sessionUserID", Long.toString(comment.getUserID()))};

    // When parameters are requested, return test values
    when(request.getParameter("commentID")).thenReturn(Long.toString(comment.getCommentID()));
    when(request.getParameter("taskID")).thenReturn(Long.toString(0l));
    when(request.getCookies()).thenReturn(cookies);

    // Create writer
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Get quantity of tasks before posting
    int quantityBefore = datastore.prepare(new Query("Comment")).countEntities();

    // Run doPost()
    servlet.doPost(request, response);

    // Get quantity of tasks after posting
    int quantityAfter = datastore.prepare(new Query("Comment")).countEntities();

    // Assert a task entity was added to datastore
    Assert.assertEquals("doPost", quantityAfter, quantityBefore - 1);
  }
}
