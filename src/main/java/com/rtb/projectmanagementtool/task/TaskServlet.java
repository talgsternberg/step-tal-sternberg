package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    // Temporary/Default task to display
    long taskID1 = 1l;
    long projectID1 = 1l;
    String name1 = "Task 1";
    String description1 = "Task 1 description...";
    TaskData task1 = new TaskData(projectID1, name1, description1);

    // Get Task
    long taskID = Long.parseLong(request.getParameter("taskID"));
    TaskController taskController = new TaskController(datastore);
    if (taskID != 0 && taskID != 1) {
      task1 = taskController.getTaskByID(taskID);
    }
    // // ArrayList is for HashMap below. Is there a better way to do this?
    // ArrayList<TaskData> taskInArrayList = new ArrayList<>(Arrays.asList(task1));

    // // Get Parent Task
    // if (taskID != 0 && taskID != 1) {
    //   TaskData parentTask = taskController.getTaskByID(task1.getParentTaskID());
    // }

    // // Get Parent Project
    // ProjectController projectController = new ProjectController(datastore);
    // ProjectData project = projectController.getProjectByID(task.getProjectID);
    // ArrayList<ProjectData> projectInArrayList = new ArrayList<>(Arrays.asList(project));

    // Get Subtasks
    ArrayList<TaskData> subtasks = taskController.getSubtasks(task1);

    // // Get Comments
    // int quantity = Integer.parseInt(request.getParameter("quantity"));
    // String sortBy = request.getParameter("sortBy");
    // String sortDirection = request.getParameter("sortDirection");

    // Get Task Users
    ArrayList<UserData> users = new ArrayList<>();
    if (taskID != 0 && taskID != 1) {
      UserController userController = new UserController(datastore);
      // users = userController.getUsers(task1.getUsers());
      for (long userID : task1.getUsers()) {
        try {
          users.add(userController.getUserByID(userID));
        } catch (NullPointerException e) {
          System.out.println("No user exists for that userID.");
        }
      }
    }

    System.out.println("TaskServlet");
    System.out.println(task1);
    System.out.println(subtasks);

    // Send data to task.jsp
    request.setAttribute("task", task1);
    // request.setAttribute("parentTask", parentTask);
    // request.setAttribute("project", projectInArrayList);
    request.setAttribute("subtasks", subtasks);
    // request.setAttribute("comments", comments);
    request.setAttribute("users", users);
    request.getRequestDispatcher("task.jsp").forward(request, response);

    // // Convert data to JSON
    // Gson gson = new Gson();
    // response.setContentType("application/json;");
    // HashMap<String, ArrayList> data = new HashMap<>();
    // data.put("task", taskInArrayList);
    // // data.put("project", projectInArrayList);
    // data.put("subtasks", subtasks);
    // // data.put("comments", comments);
    // data.put("users", users);
    // response.getWriter().println(gson.toJson(data));
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

    // // Redirect to newly created task page
    // HttpServletRequest request2 = new HttpServletRequest();
    // request2.setParameter("taskID", task.getTaskID());
    // request.getRequestDispatcher("task").forward(request, response);
  }
}
