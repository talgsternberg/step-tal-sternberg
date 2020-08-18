package com.rtb.projectmanagementtool.comment;

import com.google.appengine.api.datastore.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/comment-delete")
public class CommentDeleteServlet extends HttpServlet {

  DatastoreService datastore;

  public CommentDeleteServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public CommentDeleteServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get parameters
    long commentID = Long.parseLong(request.getParameter("commentID"));
    long taskID = Long.parseLong(request.getParameter("taskID"));

    // Delete comment
    CommentController commentController = new CommentController(datastore);
    commentController.deleteComments(new ArrayList<>(Arrays.asList(commentID)));

    // Redirect to comment's task page
    response.sendRedirect("/task?taskID=" + taskID);
  }
}
