/** Servlet responsible for deleting a project */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.task.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-project")
public class DeleteProjectServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // initialize controllers
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ProjectController projectController = new ProjectController(datastore);
    TaskController taskController = new TaskController(datastore);

    // get request parameters
    Long projectId = Long.parseLong(request.getParameter("project"));

    // Delete project tasks
    taskController.deleteTasks(
        taskController.getTaskIDsFromTasks(taskController.getTasksByProjectID(projectId)));

    // Delete project
    projectController.removeProject(projectController.getProjectById(projectId));

    // Redirect to /home
    response.sendRedirect("/home");
  }
}
