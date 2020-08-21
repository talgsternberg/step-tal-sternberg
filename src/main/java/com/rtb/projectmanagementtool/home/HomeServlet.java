/** Servlet responsible for getting data for loading home page */
package com.rtb.projectmanagementtool.home;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.project.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Authenticate
    AuthOps auth = new AuthOps(datastore);

    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);

    if (userLoggedInId == AuthOps.NO_LOGGED_IN_USER) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // Initialize controllers
    UserController userController = new UserController(datastore);
    ProjectController projectController = new ProjectController(datastore);
    TaskController taskController = new TaskController(datastore);

    // Get user object
    UserData user = userController.getUserByID(userLoggedInId);

    // Get user projects
    ArrayList<ProjectData> userProjects = projectController.getProjectsWithUser(userLoggedInId);

    // Get user's tasks
    ArrayList<TaskData> userTasks = taskController.getTasksByUserID(userLoggedInId);

    // Set attributes
    request.setAttribute("user", user);
    request.setAttribute("userProjects", userProjects);
    request.setAttribute("userTasks", userTasks);

    // Load jsp for project page
    request.getRequestDispatcher("home.jsp").forward(request, response);
  }
}
