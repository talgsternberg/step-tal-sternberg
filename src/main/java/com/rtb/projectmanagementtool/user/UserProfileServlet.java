/** Servlet responsible for loading user_profile */
package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.task.*;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-profile")
public class UserProfileServlet extends HttpServlet {
  DatastoreService datastore;
  long userID;

  public UserProfileServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Authentication goes here
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    AuthOps auth = new AuthOps(datastore);

    // Authenticate
    auth.loginUser(request, response);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == Long.parseLong(AuthOps.NO_LOGGED_IN_USER)) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // NON TESTING: ONCE EVERYTHING IS SET UP

    // new UserController
    UserController userController = new UserController(datastore);

    // get user by ID
    if (request.getParameter("userID") == null) {
      userID = auth.whichUserIsLoggedIn(request, response);
    } else {
      userID = Long.parseLong(request.getParameter("userID"));
    }

    UserData user = userController.getUserByID(userID);

    // new TaskController
    TaskController taskController = new TaskController(datastore);

    // get this user's tasks
    ArrayList<TaskData> tasks = taskController.getTasksByUserID(userID);

    // Set attributes of request; retrieve in jsp with
    request.setAttribute("UserData", user);
    request.setAttribute("UserTasks", tasks);

    // Load jsp for user page
    request.getRequestDispatcher("user_profile.jsp").forward(request, response);
  }
}
