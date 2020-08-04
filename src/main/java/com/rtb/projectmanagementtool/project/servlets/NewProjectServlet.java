// Servlet for creating new projects

package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for creating new projects. */
@WebServlet("/new-project")
public class NewProjectServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ProjectController projectController = new ProjectController(datastore);

    String name = request.getParameter("name");
    String description = request.getParameter("description");

    // TODO: remove hard-coding when user authentication is finalized
    Long userId = 0l;

    ProjectData newProject = new ProjectData(name, description, userId);
    projectController.addProject(newProject);

    // Redirect to project page
    response.sendRedirect("/project.html?project=" + newProject.getId());
  }
}
