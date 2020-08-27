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

    // Deserialize graph from cache or build graph
    MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    TaskBlockerController taskBlockerController =
        new TaskBlockerController(datastore, cache, taskController);
    String key = Long.toString(projectID);
    byte[] value;
    TaskBlockerGraph graph;
    value = (byte[]) cache.get(key);
    if (value == null) {
      graph = taskBlockerController.buildGraph(projectID);
    } else {
      graph = SerializationUtils.deserialize(value);
    }

    // Add taskBlocker to graph and datastore
    String alert = "";
    try {
      taskBlockerController.addEdge(graph, taskID, blockerID);
    } catch (Exception e) {
      alert = e.getMessage();
      e.printStackTrace();
      System.out.println(e);
    }

    // Serialize graph and put it back into the cache
    value = SerializationUtils.serialize(graph);
    cache.put(key, value);
    System.out.println("Graph: " + graph);

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
