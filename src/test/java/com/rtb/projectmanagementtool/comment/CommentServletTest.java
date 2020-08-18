package com.rtb.projectmanagementtool.comment;

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

public class CommentServletTest extends Mockito {

  private DatastoreService datastore;
  private CommentServlet servlet;
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
    servlet = new CommentServlet(datastore);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoPost() throws IOException, ServletException {

    // Create comment
    CommentData comment = new CommentData(1l, 1l, "Comment 1", "Comment 1 message...");

    // When parameters are requested, return test values
    when(request.getParameter("taskID")).thenReturn(Long.toString(comment.getTaskID()));
    when(request.getParameter("userID")).thenReturn(Long.toString(comment.getUserID()));
    when(request.getParameter("title")).thenReturn(comment.getTitle());
    when(request.getParameter("message")).thenReturn(comment.getMessage());

    // Create writer
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Get quantity of comments before posting
    int quantityBefore = datastore.prepare(new Query("Comment")).countEntities();

    // Run doPost()
    servlet.doPost(request, response);

    // Get quantity of comments after posting
    int quantityAfter = datastore.prepare(new Query("Comment")).countEntities();

    // Assert a comment entity was added to datastore
    Assert.assertEquals("doPost", quantityAfter, quantityBefore + 1);

    // Get added comment from datastore
    CommentData getComment =
        new CommentData(datastore.prepare(new Query("Comment")).asSingleEntity());

    // Assert correct comment values we're added
    Assert.assertEquals("taskID", comment.getTaskID(), getComment.getTaskID());
    Assert.assertEquals("userID", comment.getUserID(), getComment.getUserID());
    Assert.assertEquals("title", comment.getTitle(), getComment.getTitle());
    Assert.assertEquals("message", comment.getMessage(), getComment.getMessage());
  }
}
