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

    // simple way to have majors without []
    String userMajorsString = "";
    for (String major : user.getUserMajors()) {
      if (userMajorsString.equals("")) {
        userMajorsString = major;
      } else {
        userMajorsString = userMajorsString + ", " + major;
      }
    }

    // get this user's tasks
    ArrayList<TaskData> tasks = taskController.getTasksByUserID(userID);

    // build list of private comments
    ArrayList<PrivateCommentData> privateComments = new ArrayList<>();

    // get private comments
    privateComments = privateCommentController.getPrivateCommentsForUser(userID);

    // build map of taskID to pcomment
    Map<Long, PrivateCommentData> privateCommentsMap = new HashMap<Long, PrivateCommentData>();

    // preset each comment if every task is empty
    if (privateCommentsMap.isEmpty()) {
      for (TaskData task : tasks) {
        privateCommentsMap.put(
            task.getTaskID(), new PrivateCommentData(task.getTaskID(), userID, ""));
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
    request.setAttribute("currentUser", currentUser);

    // Load jsp for user page
    request.getRequestDispatcher("user_profile.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // get parameters
    TaskData task = (TaskData) request.getParameter("task")
    long taskID = Long.parseLong(request.getParameter("taskID-" + task.getName()));
    String message = request.getParameter("message-" + task.getName());

    // new controller for methods
    PrivateCommentController pcController = new PrivateCommentController(datastore);

    // if a pcomment already exists with this taskID. Get it and update it.
    if (pcController.getPrivateCommentByTaskID(taskID) != null) {
      System.out.println("THIS PC EXISTS. THIS PC EXISTS.");
      PrivateCommentData pcData = pcController.getPrivateCommentByTaskID(taskID);
      // update message
      pcData.setMessage(message);

      // Add updates to datastore
      pcController.updatePrivateComment(pcData);
    }

    // otherwise, create a new pcData and put that in ds
    else {
      System.out.println(
          "THIS PC DOESN'T EXIST AND IS BEING CREATED. THIS PC DOESN'T EXIST AND IS BEING CREATED.");
      PrivateCommentData pcData = new PrivateCommentData(userID, taskID, message);
      pcController.addPrivateComment(pcData);
    }

    // Redirect back to the user profile servlet
    response.sendRedirect("/user-profile");
  }
}
