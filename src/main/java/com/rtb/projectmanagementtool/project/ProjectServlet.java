/** Servlet responsible for loading project page */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/project")
public class ProjectServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Authentication goes here
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    AuthOps auth = new AuthOps(datastore);

    // Authenticate
    auth.loginUser(request, response);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == AuthOps.NO_LOGGED_IN_USER) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // Get project object
    ProjectController projectController = new ProjectController(datastore);
    Long projectId = Long.parseLong(request.getParameter("id"));
    ProjectData project = projectController.getProjectById(projectId);

    // If the user is not a part of the project, redirect to home page
    if (!project.hasUser(userLoggedInId)) {
      response.sendRedirect("/home");
      return;
    }

    // Get project tasks
    TaskController taskController = new TaskController(datastore);
    ArrayList<TaskData> tasks = taskController.getTasksByProjectID(projectId);

    // Get project users
    UserController userController = new UserController(datastore);

    // Get project creator
    UserData creator = userController.getUserByID(project.getCreatorId());

    // Get project admins
    HashSet<UserData> admins = new HashSet<UserData>();
    for (Long userId : project.getAdmins()) {
      admins.add(userController.getUserByID(userId));
    }

    // Get project members
    HashSet<UserData> members = new HashSet<UserData>();
    for (Long userId : project.getMembers()) {
      members.add(userController.getUserByID(userId));
    }

    // Set attributes
    request.setAttribute("userId", userLoggedInId);
    request.setAttribute("project", project);
    request.setAttribute("creator", creator);
    request.setAttribute("admins", admins);
    request.setAttribute("members", members);
    request.setAttribute("tasks", tasks);

    // Load jsp for project page
    request.getRequestDispatcher("project.jsp").forward(request, response);
  }
}
