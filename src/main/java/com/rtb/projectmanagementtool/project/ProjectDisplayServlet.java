/**
 * Servlet responsible for getting data to display projects on home page and for each project page.
 *
 * <p>"page" parameter in the request determines what page projects are being loaded on Considering
 * having separate servlets for loading projects on main hub/home page and for loading a specific
 * project page
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/project-display")
public class ProjectDisplayServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ProjectController projectController =
        new ProjectController(DatastoreServiceFactory.getDatastoreService());

    String page = request.getParameter("page");

    response.setContentType("application/json;");

    switch (page) {
        // Get projects for a specific project page
      case "project":
        ProjectData project =
            projectController.getProjectById(Long.parseLong(request.getParameter("projectId")));
        response.getWriter().println(project);
        break;
        // Get projects for the home page
      case "home":
        ArrayList<ProjectData> userProjects =
            projectController.getProjectsWithUser(Long.parseLong(request.getParameter("userId")));
        response.getWriter().println(userProjects);
        break;
    }
  }
}
