package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.comment.*;
import com.rtb.projectmanagementtool.project.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/task")
public class TaskServlet extends HttpServlet {

  DatastoreService datastore;

  public TaskServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // For testing only
  public TaskServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Authenticate
    AuthOps auth = new AuthOps(datastore);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == /*No user found*/ -1l) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // Get current user
    UserController userController = new UserController(datastore);
    UserData user = userController.getUserByID(userLoggedInId);

    // Temporary/Default task to display
    long projectID1 = -1l;
    String name1 = "Default Task Name";
    String description1 = "Default task description...";
    TaskData task = new TaskData(projectID1, name1, description1);

    // Get Task
    long taskID;
    TaskController taskController = new TaskController(datastore);
    try {
      taskID = Long.parseLong(request.getParameter("taskID"));
      task = taskController.getTaskByID(taskID);
    } catch (NullPointerException | IllegalArgumentException e) {
      taskID = 0l;
    }

    // Get Parent Task
    TaskData parentTask = null;
    if (taskID != 0 && task.getParentTaskID() != 0) {
      parentTask = taskController.getTaskByID(task.getParentTaskID());
    }

    // Get Parent Project
    ProjectController projectController = new ProjectController(datastore);
    ProjectData project = projectController.getProjectById(task.getProjectID());

    // Get Subtasks
    ArrayList<TaskData> subtasks = taskController.getSubtasks(task);

    // Get Task Users
    ArrayList<UserData> users = new ArrayList<>();
    if (taskID != 0) {
      // users = userController.getUsers(task.getUsers());
      for (long userID : task.getUsers()) {
        UserData member = userController.getUserByID(userID);
        if (member != null) {
          users.add(member);
        } else {
          System.out.println("No user exists for userID: " + userID);
        }
      }
    }

    // Get Comments
    // int quantity = Integer.parseInt(request.getParameter("quantity"));
    // String sortBy = request.getParameter("sortBy");
    // String sortDirection = request.getParameter("sortDirection");
    CommentController commentController = new CommentController(datastore);
    ArrayList<CommentData> comments = new ArrayList<>();
    HashMap<CommentData, String> commentsMap = new HashMap<>();
    try {
      comments = commentController.getCommentsByTaskID(taskID);
      for (CommentData comment : comments) {
        String username = "Default Username";
        try {
          username = userController.getUserByID(comment.getUserID()).getUserName();
        } catch (NullPointerException | IllegalArgumentException e) {
          System.out.println("UserID doesn't exist. Default username will be used.");
        }
        commentsMap.put(comment, username);
      }
    } catch (NullPointerException | IllegalArgumentException e) {
      System.out.println("TaskID doesn't exist. Cannot fetch comments.");
    }

    // Send data to task.jsp
    request.setAttribute("user", user);
    request.setAttribute("task", task);
    request.setAttribute("parentTask", parentTask);
    request.setAttribute("project", project);
    request.setAttribute("subtasks", subtasks);
    request.setAttribute("users", users);
    request.setAttribute("comments", commentsMap);
    request.getRequestDispatcher("task.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Get and create parameters
    long parentTaskID = Long.parseLong(request.getParameter("parentTaskID"));
    long projectID = Long.parseLong(request.getParameter("projectID"));
    String name = request.getParameter("name").trim();
    String description = request.getParameter("description").trim();

    // Create TaskData object
    TaskData task = new TaskData(parentTaskID, projectID, name, description);

    // Add task to datastore
    TaskController taskController = new TaskController(datastore);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task)));

    // Redirect back to the parent task's task page
    response.sendRedirect("/task?taskID=" + parentTaskID);
  }
}
