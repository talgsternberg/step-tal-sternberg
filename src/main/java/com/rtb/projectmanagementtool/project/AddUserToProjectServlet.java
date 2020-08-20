/** Servlet responsible for adding users to a project */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/add-user-to-project")
public class AddUserToProjectServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // initialize controllers
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ProjectController projectController = new ProjectController(datastore);
    UserController userController = new UserController(datastore);

    // get request parameters
    Long projectId = Long.parseLong(request.getParameter("projectId"));
    String userName = request.getParameter("user-name");
    String userRole = request.getParameter("user-role");

    // create objects
    ProjectData project = projectController.getProjectById(projectId);
    UserData user = userController.getUserByName(userName);

    // if user is not in database or they are already in the project,
    // redirect back to project page
    if (user == null || project.hasUser(user.getUserID())) {
      response.sendRedirect("/project?id=" + project.getId());
      return;
    }

    // user found and not in the project; add them
    switch (userRole) {
      case "admin":
        project.addAdminUser(user.getUserID());
        break;
      case "member":
        project.addMemberUser(user.getUserID());
        break;
    }

    // save project to database
    projectController.addProject(project);

    // Redirect to project page
    response.sendRedirect("/project?id=" + project.getId());
  }
}
