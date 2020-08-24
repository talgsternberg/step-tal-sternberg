package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.task.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/task-blocker")
public class TaskBlockerServlet extends HttpServlet {

  DatastoreService datastore;
  TaskController taskController;

  public TaskBlockerServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    taskController = new TaskController(datastore);
  }

  // For testing only
  public TaskBlockerServlet(DatastoreService datastore, TaskController taskController) {
    this.datastore = datastore;
    this.taskController = taskController;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Get and create parameters
    long projectID = Long.parseLong(request.getParameter("projectID"));
    long taskID = Long.parseLong(request.getParameter("blocked"));
    long blockerID = Long.parseLong(request.getParameter("blocker"));

    // Create TaskBlockerData object
    TaskBlockerData taskBlocker = new TaskBlockerData(taskID, blockerID);

    // Add taskBlocker to datastore
    TaskBlockerController taskBlockerController =
        new TaskBlockerController(datastore, taskController);
    taskBlockerController.addTaskBlocker(taskBlocker);

    // Redirect back to project page
    response.sendRedirect("/project?id=" + projectID);
  }
}
