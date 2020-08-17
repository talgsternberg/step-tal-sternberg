package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.comment.*;
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
@WebServlet("/comment")
public class CommentServlet extends HttpServlet {

  DatastoreService datastore;

  public CommentServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public CommentServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Get and create parameters
    //   public CommentData(long taskID, long userID, String title, String message)
    long taskID = Long.parseLong(request.getParameter("taskID"));
    long userID = Long.parseLong(request.getParameter("userID"));
    String title = request.getParameter("title").trim();
    String message = request.getParameter("message").trim();

    // Create CommentData object
    CommentData comment = new CommentData(taskID, userID, title, message);

    // Add comment to datastore
    CommentController commentController = new CommentController(datastore);
    commentController.addComments(new ArrayList<>(Arrays.asList(comment)));

    // Redirect back to the task's task page
    response.sendRedirect("/task?taskID=" + taskID);
  }
}
