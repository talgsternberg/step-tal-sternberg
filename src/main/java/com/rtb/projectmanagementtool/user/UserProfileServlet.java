/** Servlet responsible for loading user_profile */
package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.privatecomment.*;
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
  AuthOps auth;

  public UserProfileServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    auth = new AuthOps(datastore);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Authenticate
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == AuthOps.NO_LOGGED_IN_USER) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // new controllers
    UserController userController = new UserController(datastore);
    TaskController taskController = new TaskController(datastore);
    PrivateCommentController privateCommentController = new PrivateCommentController(datastore);

    // user's own page tracker
    boolean currentUser = false;

    // get user ID
    if (request.getParameter("userID") == null) {
      userID = auth.whichUserIsLoggedIn(request, response);
      currentUser = true;
    } else {
      userID = Long.parseLong(request.getParameter("userID"));
    }

    // get user with ID
    UserData user = userController.getUserByID(userID);

    // get this user's tasks
    ArrayList<TaskData> tasks = taskController.getTasksByUserID(userID);

    // this should only be sent load user looking @ own profile

    // build list of private comments and colors
    ArrayList<PrivateCommentData> privateComments = new ArrayList<>();

    // build private comments
    privateComments = privateCommentController.getPrivateCommentsForUser(userID);

    // build map of task to pcomment
    Map<Long, PrivateCommentData> privateCommentsMap = new HashMap<Long, PrivateCommentData>();

    // preset each comment for each task empty
    if (privateCommentsMap.isEmpty()) {
      for (TaskData task : tasks) {
        privateCommentsMap.put(
            task.getTaskID(), new PrivateCommentData(task.getTaskID(), userID, ""));
      }
    }
    String userMajorsString = "";
    for (String major : user.getUserMajors()) {
      if (userMajorsString.equals("")) {
        userMajorsString = major;
      } else {
        userMajorsString = userMajorsString + ", " + major;
      }
    }

    // if the user has private comments, load them into map
    for (int i = 0; i < privateComments.size(); i++) {
      PrivateCommentData commentObject = privateComments.get(i);
      privateCommentsMap.replace(privateComments.get(i).getTaskID(), commentObject);
    }

    // Set attributes of request; retrieve in jsp with
    request.setAttribute("UserData", user);
    request.setAttribute("UserTasks", tasks);
    request.setAttribute("privateCommentsMap", privateCommentsMap);
    request.setAttribute("userMajorsString", userMajorsString);
    // will be used to check and see if we should load private comments
    request.setAttribute("currentUser", currentUser);

    // Load jsp for user page
    request.getRequestDispatcher("user_profile.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Long userID = auth.whichUserIsLoggedIn(request, response);

    // get this user's tasks
    TaskController taskController = new TaskController(datastore);
    ArrayList<TaskData> tasks = taskController.getTasksByUserID(userID);

    // for each task, get params and create private comments
    for (TaskData task : tasks) {
      long taskID = Long.parseLong(request.getParameter("taskID"));
      String message = request.getParameter("message");

      // Create PrivateCommentData object
      PrivateCommentData privateComment = new PrivateCommentData(taskID, userID, message);

      // Add private comment to datastore
      PrivateCommentController privateCommentController = new PrivateCommentController(datastore);
      privateCommentController.addPrivateComment(privateComment);
    }
    // Redirect back to the user profile servlet
    response.sendRedirect("/user-profile");
  }
}
