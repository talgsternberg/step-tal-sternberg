package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/task-add-user")
public class TaskAddUserServlet extends HttpServlet {

  DatastoreService datastore;

  public TaskAddUserServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public TaskAddUserServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get parameters
    long taskID = Long.parseLong(request.getParameter("taskID"));
    long userID = Long.parseLong(request.getParameter("userID"));

    // Add user to task
    TaskController taskController = new TaskController(datastore);
    taskController.addUser(taskID, userID);
  }
}
