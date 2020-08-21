package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.auth.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/create-project")
public class CreateProjectServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Authenticate
    AuthOps auth = new AuthOps(datastore);
    auth.loginUser(request, response);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == AuthOps.NO_LOGGED_IN_USER) {
      response.sendRedirect("/login");
      return;
    }

    // userId attribute set here so a new AuthOps object is not
    // created in doPost() method
    request.setAttribute("userId", userLoggedInId);

    // Forward to create project page
    request.getRequestDispatcher("create-project.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ProjectController projectController = new ProjectController(datastore);

    // TODO: make sure arguments are valid before attempting to add to database
    Long userId = Long.parseLong(request.getParameter("userId"));
    String name = request.getParameter("project-name");
    String description = request.getParameter("project-desc");

    ProjectData newProject = new ProjectData(name, description, userId);
    long projectId = projectController.addProject(newProject);

    // If project created successfully, redirect to its page
    response.sendRedirect("/project?id=" + projectId);
  }
}
