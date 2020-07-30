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

    // Get Task
    long taskID = Long.parseLong(request.getParameter("taskID"));
    TaskController taskController = new TaskController(datastore);
    TaskData task = taskController.getTaskByID(taskID);
    // ArrayList is for HashMap below. Is there a better way to do this?
    ArrayList<TaskData> taskInArrayList = new ArrayList<>(Arrays.asList(task));

    // Get Subtasks
    ArrayList<TaskData> subtasks = taskController.getSubtasks(task);

    // // Get Comments
    // int quantity = Integer.parseInt(request.getParameter("quantity"));
    // String sortBy = request.getParameter("sortBy");
    // String sortDirection = request.getParameter("sortDirection");

    // Convert data to JSON
    Gson gson = new Gson();
    response.setContentType("application/json;");
    HashMap<String, ArrayList> data = new HashMap<>();
    data.put("task", taskInArrayList);
    data.put("subtasks", subtasks);
    response.getWriter().println(gson.toJson(data));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity entity = new Entity("Task"); // Used to generate taskID, but it's never put in datastore
    long taskID = (long) entity.getKey().getId();
    long projectID = Long.parseLong(request.getParameter("projectID"));
    String name = request.getParameter("name").trim();
    String description = request.getParameter("description").trim();
    Status status = Status.valueOf(request.getParameter("status").toUpperCase());
    ArrayList<Long> users = new ArrayList<>();
    ArrayList<Long> subtasks = new ArrayList<>();
    TaskData task =
        new TaskData(taskID, projectID, name, description, status, users, subtasks);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    TaskController taskController = new TaskController(datastore);
    taskController.addTasks(new ArrayList<>(Arrays.asList(task)));
  }
}
