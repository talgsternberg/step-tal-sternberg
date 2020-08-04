package com.rtb.projectmanagementtool.task;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns task data */
@WebServlet("/task")
public class TaskServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Temporary/Default task to display
    long taskID1 = 1l;
    long projectID1 = 1l;
    String name1 = "Task 1";
    String description1 = "Task 1 description...";
    Status status1 = Status.INCOMPLETE;
    ArrayList<Long> users1 = new ArrayList<>(Arrays.asList(1l, 2l));
    ArrayList<Long> subtasks1 = new ArrayList<>(Arrays.asList(3l));
    TaskData task1 = new TaskData(projectID1, name1, description1, status1, users1, subtasks1);

    // Get Task
    long taskID = Long.parseLong(request.getParameter("taskID"));
    TaskController taskController = new TaskController(datastore);
    // TaskData task = taskController.getTaskByID(taskID);
    // ArrayList is for HashMap below. Is there a better way to do this?
    ArrayList<TaskData> taskInArrayList = new ArrayList<>(Arrays.asList(task1));

    // // Get Parent Project
    // ProjectController projectController = new ProjectController(datastore);
    // ProjectData project = projectController.getProjectByID(task.getProjectID);
    // ArrayList<ProjectData> projectInArrayList = new ArrayList<>(Arrays.asList(project));

    // // Get Subtasks
    // ArrayList<TaskData> subtasks = taskController.getSubtasks(task);

    // // Get Comments
    // int quantity = Integer.parseInt(request.getParameter("quantity"));
    // String sortBy = request.getParameter("sortBy");
    // String sortDirection = request.getParameter("sortDirection");

    // // Get Task Users
    // UserController userController = new UserController(datastore);
    // ArrayList<UserData> users = userController.getUsers(task.getUsers());

    // Convert data to JSON
    Gson gson = new Gson();
    response.setContentType("application/json;");
    HashMap<String, ArrayList> data = new HashMap<>();
    data.put("task", taskInArrayList);
    // data.put("project", projectInArrayList);
    // data.put("subtasks", subtasks);
    // data.put("comments", comments);
    // data.put("users", users);
    response.getWriter().println(gson.toJson(data));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long projectID = Long.parseLong(request.getParameter("projectID"));
    String name = request.getParameter("name").trim();
    String description = request.getParameter("description").trim();
    Status status = Status.valueOf(request.getParameter("status").toUpperCase());
    ArrayList<Long> users = new ArrayList<>();
    ArrayList<Long> subtasks = new ArrayList<>();
    TaskData task = new TaskData(projectID, name, description, status, users, subtasks);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(datastore);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task)));
  }

  public void doPost(
      HttpServletRequest request, HttpServletResponse response, DatastoreService datastore)
      throws IOException {
    long projectID = Long.parseLong(request.getParameter("projectID"));
    String name = request.getParameter("name").trim();
    String description = request.getParameter("description").trim();
    Status status = Status.valueOf(request.getParameter("status").toUpperCase());
    ArrayList<Long> users = new ArrayList<>();
    ArrayList<Long> subtasks = new ArrayList<>();
    TaskData task = new TaskData(projectID, name, description, status, users, subtasks);
    TaskController taskController = new TaskController(datastore);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task)));
  }
}
