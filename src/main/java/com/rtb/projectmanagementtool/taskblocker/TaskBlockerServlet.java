package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.rtb.projectmanagementtool.task.*;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/task-blocker")
public class TaskBlockerServlet extends HttpServlet {

  DatastoreService datastore;
  MemcacheService cache;
  TaskController taskController;

  public TaskBlockerServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    taskController = new TaskController(datastore);
    cache = MemcacheServiceFactory.getMemcacheService();
    cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
  }

  // For testing only
  public TaskBlockerServlet(
      DatastoreService datastore, MemcacheService cache, TaskController taskController) {
    this.datastore = datastore;
    this.taskController = taskController;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Get and create parameters
    long projectID = Long.parseLong(request.getParameter("projectID"));
    String projectName = (String) request.getParameter("projectName");
    long taskID = Long.parseLong(request.getParameter("blocked"));
    long blockerID = Long.parseLong(request.getParameter("blocker"));

    // Get graph
    TaskBlockerController taskBlockerController =
        new TaskBlockerController(datastore, cache, taskController);
    TaskBlockerGraph graph = taskBlockerController.getGraph(projectID);

    // Add taskBlocker to graph and datastore
    String alert = "";
    try {
      taskBlockerController.addEdge(graph, taskID, blockerID);
    } catch (TaskBlockerException e) {
      alert = e.getMessage();
    }

    // Serialize graph and put it back into the cache
    taskBlockerController.cacheGraph(graph, projectID);

    // If adding a task blocker failed due to user input,
    // return back to the add-task-blocker page with a message
    if (alert != "") {
      response.sendRedirect(
          "add-task-blocker.jsp?projectID="
              + projectID
              + "&projectName="
              + projectName
              + "&alert="
              + alert);
      return;
    }

    // Redirect back to project page
    response.sendRedirect("/project?id=" + projectID);
  }
}
