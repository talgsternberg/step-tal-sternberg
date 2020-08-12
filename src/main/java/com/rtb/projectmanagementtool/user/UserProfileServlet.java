/** Servlet responsible for loading user_profile */
package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
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

@WebServlet("/user-profile")
public class UserProfileServlet extends HttpServlet {
  DatastoreService datastore;

  public UserProfileServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // taken from @Godsfavour. Will implement once auth is ready
    // Authentication goes here
    // if (something something something) {
    //   // Redirect to /login servlet if authentication fails
    //   request.getRequestDispatcher("/login").forward(request, response);
    //   return;
    // }

    // HARDCODE FOR TESTING

    // initialize/fill hardcoded userMajors
    ArrayList<String> majors = new ArrayList<>();
    majors.add("Chemistry");
    majors.add("Studio Art");

    // Create hardcoded user
    long userID = Long.parseLong(request.getParameter("userID"));
    String AuthID = "abc";
    String userName = "Name 1";
    long userYear = 2023;
    ArrayList<String> userMajors = majors;
    Skills skills = Skills.OOP;
    long userTotalCompTasks = 3;
    UserData user =
        new UserData(AuthID, userName, userYear, userMajors, skills, userTotalCompTasks);
    user.setUserID(userID);

    // hard code task array
    long taskID1 = 1l;
    long projectID1 = 1l;
    String name1 = "Task 1";
    String description1 = "Task 1 description...";
    Status status1 = Status.INCOMPLETE;
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));
    long taskID2 = 2l;
    long projectID2 = 2l;
    String name2 = "Task 2";
    String description2 = "Task 2 description...";
    Status status2 = Status.COMPLETE;
    ArrayList<Long> users2 = new ArrayList<>(Arrays.asList(userID));
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1);
    TaskData task2 = new TaskData(projectID2, name2, description2, status2, users2);
    ArrayList<TaskData> tasks = new ArrayList<>();
    tasks.add(task1);
    tasks.add(task2);

    // NON TESTING: ONCE EVERYTHING IS SET UP

    /**
     * // new UserController UserController userController = new UserController(datastore);
     *
     * <p>// get user by ID long userID = Long.parseLong(request.getParameter("userID")); user =
     * userController.getUserByID(userID);
     *
     * <p>// new TaskController TaskController taskController = new TaskController(datastore);
     *
     * <p>// get this user's tasks ArrayList<TaskData> tasks =
     * taskController.getTasksByUserID(userID);
     */

    // Set attributes of request; retrieve in jsp with
    request.setAttribute("UserProfile", user);
    request.setAttribute("UserTasks", tasks);

    // Load jsp for user page
    request.getRequestDispatcher("user_profile.jsp").forward(request, response);
  }
}
