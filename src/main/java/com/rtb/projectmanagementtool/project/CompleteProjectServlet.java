/** Servlet responsible for marking a project as complete */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.task.TaskData.Status;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/complete-project")
public class CompleteProjectServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");

    // initialize controllers
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ProjectController projectController = new ProjectController(datastore);
    TaskController taskController = new TaskController(datastore);

    // get request parameters
    Long projectId = Long.parseLong(request.getParameter("project"));

    // create objects
    ProjectData project = projectController.getProjectById(projectId);

    boolean setComplete = request.getParameter("setComplete").equals("true") ? true : false;

    // if servlet is called to set project as incomplete
    if (!setComplete) {
      project.setIncomplete();
      projectController.addProject(project);
      response.getWriter().println(generateResponse(/*message*/ "Project marked as incomplete."));
      return;
    }

    // Get all tasks in project to ensure they're completed
    ArrayList<TaskData> projectTasks = taskController.getTasksByProjectID(projectId);
    for (TaskData task : projectTasks) {
      // if a task is incomplete, return
      if (task.getStatus() == Status.INCOMPLETE) {
        response
            .getWriter()
            .println(
                generateResponse(/*message*/ "Task(s) incomplete. Unable to complete project."));
        return;
      }
    }

    // All project tasks are complete
    project.setComplete();
    projectController.addProject(project);

    // Redirect to project page
    response.getWriter().println(generateResponse(/*message*/ "Project marked as complete."));
  }

  // Method generates a json for servlet response
  public String generateResponse(String message) {
    String response = "{";
    response += "\"message\": ";
    response += "\"" + message + "\"";
    response += "}";
    return response;
  }
}
