/** Servlet responsible for editing project details */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/edit-project-details")
public class EditProjectDetailsServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");

    // initialize controllers
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ProjectController projectController = new ProjectController(datastore);

    // get request parameters
    Long projectId = Long.parseLong(request.getParameter("project"));
    String projectName = request.getParameter("projectName");
    String projectDescription = request.getParameter("projectDesc");

    // create objects
    ProjectData project = projectController.getProjectById(projectId);

    boolean updatedName = false;
    boolean updatedDesc = false;

    if (!project.getName().equals(projectName)) {
      project.setName(projectName);
      updatedName = true;
    }

    if (!project.getDescription().equals(projectDescription)) {
      project.setDescription(projectDescription);
      updatedDesc = true;
    }

    // save project to database
    if (updatedName || updatedDesc) {
      projectController.addProject(project);
    }

    // Redirect to project page
    response.getWriter().println(generateResponse(updatedName, updatedDesc));
  }

  // Method generates a json for servlet response
  public String generateResponse(boolean updatedName, boolean updatedDesc) {
    String response = "{";
    response += "\"message\": ";
    response += "\"";
    if (!updatedName && !updatedDesc) {
      response += "Nothing to update.";
    } else if (updatedName || updatedDesc) {
      if (updatedName && updatedDesc) {
        response += "Updated project name and description.";
      } else if (updatedName) {
        response += "Updated project name.";
      } else if (updatedDesc) {
        response += "Updated project description.";
      }
    }
    response += "\"";
    response += "}";
    return response;
  }
}
