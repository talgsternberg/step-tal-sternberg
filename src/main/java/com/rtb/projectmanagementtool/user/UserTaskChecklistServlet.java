/** Servlet responsible for loading task checklist for user */
package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.task.TaskData.Status;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-checklist")
public class UserTaskChecklistServlet extends HttpServlet {
  DatastoreService datastore;
  long userID;
  AuthOps auth;

  public UserTaskChecklistServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    auth = new AuthOps(datastore);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Authenticate
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == Long.parseLong(AuthOps.NO_LOGGED_IN_USER)) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

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

    // build array to track color of task (red or green) and checked status
    String[] color = new String[tasks.size()];
    String[] checked = new String[tasks.size()];
    for (int i = 0; i < tasks.size(); i++) {
      TaskData task = tasks.get(i);
      Status taskStatus = task.getStatus();
      String statusString = taskStatus.name();
      if (statusString.equals("COMPLETE")) {
        color[i] = "green";
        checked[i] = "checked";
      } else {
        color[i] = "red";
        checked[i] = "";
      }
    }

    // send task list, color array, and checked array
    request.setAttribute("userTaskList", tasks);
    request.setAttribute("color", color);
    request.setAttribute("checkedStatus", checked);

    // Load jsp for user page
    request.getRequestDispatcher("task-checklist.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Authenticate
    auth.loginUser(request, response);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);

    if (userLoggedInId == /*No user found*/ -1l) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // new UserController
    UserController userController = new UserController(datastore);

    // get user by ID long
    long userID = auth.whichUserIsLoggedIn(request, response);
    UserData user = userController.getUserByID(userID);

    // new TaskController
    TaskController taskController = new TaskController(datastore);

    // get this user's tasks
    ArrayList<TaskData> tasks = taskController.getTasksByUserID(userID);

    // get task status updates as strings
    for (TaskData task : tasks) {
      String taskStatusString = request.getParameter(task.getName());
      if (taskStatusString.equals("COMPLETE")) {
        taskController.setComplete(task);
      } else {
        taskController.setIncomplete(task);
      }
      // update in datastore
      Entity entity = task.toEntity();
      datastore.put(entity);
    }

    response.sendRedirect("/user-profile");
  }
}
