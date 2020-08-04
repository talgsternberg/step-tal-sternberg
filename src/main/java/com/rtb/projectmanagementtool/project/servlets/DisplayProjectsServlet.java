// Servlet for displaying projects, primarily in home page

package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for displaying projects on home page. */
@WebServlet("/display-projects")
public class DisplayProjectsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ProjectController projectController = new ProjectController(datastore);

    // TODO: remove hard-coding when user authentication is finalized
    Long userId = 2l;

    // Get the query from request parameter
    ArrayList<ProjectData> userProjects = new ArrayList<ProjectData>();
    switch (request.getParameter("query")) { // isIn, isAdmin, isCreator, etc.
      case "isCreator":
        userProjects = projectController.getProjectsByCreator(userId);
        break;
      case "isAdmin":
        userProjects = projectController.getProjectsWithAdmin(userId);
        break;
      case "isIn":
        userProjects = projectController.getProjectsWithUser(userId);
        break;
    }

    // Use project names for now to represent each project on home page
    ArrayList<String> projectNames = new ArrayList<String>();
    for (ProjectData project : userProjects) {
      projectNames.add(project.getName());
    }

    // Convert to Json
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(projectNames));
  }
}
