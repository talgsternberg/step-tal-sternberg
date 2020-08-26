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
import org.apache.commons.lang3.SerializationUtils;

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
    String projectName = (String) request.getParameter("projectName");
    long taskID = Long.parseLong(request.getParameter("blocked"));
    long blockerID = Long.parseLong(request.getParameter("blocker"));

    // Create TaskBlockerData object
    TaskBlockerData taskBlocker = new TaskBlockerData(taskID, blockerID);
    TaskBlockerController taskBlockerController =
        new TaskBlockerController(datastore, taskController);

    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    String key = Long.toString(projectID);
    byte[] value;
    String graph = "";
    value = (byte[]) syncCache.get(key);
    if (value == null) {
      graph = taskBlockerController.buildGraph(projectID);
    } else {
      graph = SerializationUtils.deserialize(value);
    }
    graph = taskBlockerController.addEdge(graph, taskID, blockerID);
    value = SerializationUtils.serialize(graph);
    syncCache.put(key, value);
    System.out.println("Graph: " + graph);

    // Add taskBlocker to datastore
    String alert = "";
    try {
      taskBlockerController.addTaskBlocker(taskBlocker);
    } catch (Exception e) {
      alert = e.getMessage();
    }

    // If adding a task blocker failed, return back to the add-task-blocker page with a message
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
