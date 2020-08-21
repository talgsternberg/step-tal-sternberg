package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/task-tree")
public class TaskTreeServlet extends HttpServlet {

  DatastoreService datastore;

  public TaskTreeServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public TaskTreeServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Get tree
    TaskController taskController = new TaskController(datastore);
    long projectID = (long) request.getAttribute("projectID");
    ArrayList<TaskTreeData> taskTree = taskController.getTaskTree(projectID);

    // Send tree to task-tree.jsp
    request.setAttribute("taskTree", taskTree);
    request.getRequestDispatcher("task-tree.jsp").include(request, response);
  }
}
