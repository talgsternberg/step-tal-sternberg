/** Servlet responsible for privatecomments */
package com.rtb.projectmanagementtool.privatecomment;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/private-comment")
public class PrivateCommentListServlet extends HttpServlet {
  DatastoreService datastore;
  AuthOps auth;

  public PrivateCommentListServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    auth = new AuthOps(datastore);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Long userID = Long.parseLong(request.getParameter("userID"));
    Long currentUser = auth.whichUserIsLoggedIn(request, response);

    // controllers
    UserController userController = new UserController(datastore);
    TaskController taskController = new TaskController(datastore);
    PrivateCommentController privateCommentController = new PrivateCommentController(datastore);

    // get user
    UserData user = userController.getUserByID(userID);

    // get this user's tasks
    ArrayList<TaskData> tasks = taskController.getTasksByUserID(userID);

    // build list of private comments and colors
    ArrayList<PrivateCommentData> privateComments = new ArrayList<>();

    // build dictionary of color per task
    Map<TaskData, String> colors = new HashMap<TaskData, String>();

    // only fill if the user is viewing their own profile
    if (userID == currentUser) {
      // build private comments
      privateComments = privateCommentController.getPrivateCommentsForUser(userID);

      // add color to dict for every task
      for (TaskData task : tasks) {
        String statusString = task.getStatus().name();
        if (statusString.equals("COMPLETE")) {
          colors.put(task, "green");
        } else {
          colors.put(task, "red");
        }
      }
    }

    // Set attributes of request; retrieve in jsp with
    // request.setAttribute("userTasks", tasks); I don't need since I can pass from /user-profile
    request.setAttribute("privateComments", privateComments);
    request.setAttribute("colors", colors);

    // Load jsp for user page
    request.getRequestDispatcher("user-private-comments.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Long userID = Long.parseLong(request.getParameter("userID"));
    Long currentUser = auth.whichUserIsLoggedIn(request, response);

    // only change user's own private comments
    if (userID == currentUser) {

      // Get and create parameters
      long taskID = Long.parseLong(request.getParameter("taskID"));
      String message = request.getParameter("message").trim();

      // Create PrivateCommentData object
      PrivateCommentData privateComment = new PrivateCommentData(taskID, userID, message);

      // Add private comment to datastore
      PrivateCommentController privateCommentController = new PrivateCommentController(datastore);
      privateCommentController.addPrivateComment(privateComment);

      // Redirect back to the user profile servlet
      response.sendRedirect("/user-profile");
    }
  }
}
