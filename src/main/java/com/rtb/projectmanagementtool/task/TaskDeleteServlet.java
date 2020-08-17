package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/task-delete")
public class TaskDeleteServlet extends HttpServlet {

  DatastoreService datastore;

  public TaskDeleteServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public TaskDeleteServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get parameters
    long taskID = Long.parseLong(request.getParameter("taskID"));

    // Delete task
    TaskController taskController = new TaskController(datastore);
    try {
      long parentTaskID = taskController.getTaskByID(taskID).getParentTaskID();
      taskController.deleteTasks(new ArrayList<>(Arrays.asList(taskID)));
      response.sendRedirect("/task?taskID=" + parentTaskID);
    } catch (IllegalArgumentException e) {
      System.out.println("Error deleting task.");
      response.sendRedirect("/task?taskID=" + taskID);
    }
  }
}
