package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.task.TaskData.Status;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/task-set-status")
public class TaskSetStatusServlet extends HttpServlet {

  DatastoreService datastore;

  public TaskSetStatusServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public TaskSetStatusServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get parameters
    long taskID = Long.parseLong(request.getParameter("taskID"));
    Status status = Status.valueOf(request.getParameter("status").trim().toUpperCase());

    // Set status
    TaskController taskController = new TaskController(datastore);
    if (status == Status.COMPLETE) {
      taskController.setComplete(taskID);
    } else if (status == Status.INCOMPLETE) {
      taskController.setIncomplete(taskID);
    }

    response.sendRedirect("/task?taskID=" + taskID);
  }
}
