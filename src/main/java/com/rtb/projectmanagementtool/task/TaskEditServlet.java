package com.rtb.projectmanagementtool.task;

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
@WebServlet("/task-edit")
public class TaskEditServlet extends HttpServlet {

  DatastoreService datastore;

  public TaskEditServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public TaskEditServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Get and create parameters
    long taskID = Long.parseLong(request.getParameter("taskID"));
    String description = null;
    if (request.getParameter("description") != null) {
      description = request.getParameter("description").trim();
    }

    // Get TaskData object
    TaskController taskController = new TaskController(datastore);
    TaskData task = taskController.getTaskByID(taskID);

    // Update task
    if (description != null) {
      task.setDescription(description);
    }

    // Add task to datastore
    taskController.addTasks(new ArrayList<>(Arrays.asList(task)));

    // Redirect back to the task's task page
    response.sendRedirect("/task?taskID=" + taskID);
  }
}
