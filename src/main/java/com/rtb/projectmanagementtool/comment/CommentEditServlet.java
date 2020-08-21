package com.rtb.projectmanagementtool.comment;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.project.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/comment-edit")
public class CommentEditServlet extends HttpServlet {

  DatastoreService datastore;

  public CommentEditServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public CommentEditServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Get and create parameters
    long commentID = Long.parseLong(request.getParameter("commentID"));
    long taskID = Long.parseLong(request.getParameter("taskID"));
    String title = request.getParameter("title").trim();
    String message = request.getParameter("message").trim();

    // Create CommentData object
    CommentController commentController = new CommentController(datastore);
    CommentData comment = commentController.getCommentByID(commentID);

    // Authenticate before making any changes
    AuthOps auth = new AuthOps(datastore);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == comment.getUserID()) {
      // Update comment
      if (!title.equals(comment.getTitle()) || !message.equals(comment.getMessage())) {
        comment.setIsEdited(true);
      }
      comment.setTitle(title);
      comment.setMessage(message);
      // Add comment to datastore
      commentController.addComments(new ArrayList<>(Arrays.asList(comment)));
    } else {
      System.out.println("User authentication to edit comment failed.");
    }

    // Redirect back to the task's task page
    response.sendRedirect("/task?taskID=" + taskID);
  }
}
