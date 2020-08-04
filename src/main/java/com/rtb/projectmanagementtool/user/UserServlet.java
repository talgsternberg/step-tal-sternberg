package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Test user
    long userID1 = 3l;
    long AuthID1 = 3l;
    String userName1 = "Sarah";
    long userYear1 = 2023;
    ArrayList<String> userMajors1 = new ArrayList<>(Arrays.asList("Psychology"));
    Skills skills1 = Skills.OOP;
    long userTotalCompTasks1 = 3;
    UserData user1 =
        new UserData(
            userID1, AuthID1, userName1, userYear1, userMajors1, skills1, userTotalCompTasks1);

    // get the User
    long userID = Long.parseLong(request.getParameter("userID"));
    UserController userController = new UserController(datastore);

    // UserData user = userController.getUserByID(userID);

    // add to list of users
    // ArrayList<UserData> usersList = new ArrayList<>(Arrays.asList(user));
    ArrayList<UserData> usersList = new ArrayList<>(Arrays.asList(user1));

    // convert to JSON
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(usersList));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity entity = new Entity("User");
    System.out.println("no error yet: 1");

    // params from request
    long userID = (long) entity.getKey().getId();
    long AuthID = Long.parseLong(request.getParameter("AuthID"));
    String userName = request.getParameter("userName").trim();
    long userYear = Long.parseLong(request.getParameter("userYear"));

    // error is here
    // request.getParameterValues(userMajors) is null
    ArrayList<String> userMajors =
        (ArrayList<String>) Arrays.asList(request.getParameterValues("userMajors"));

    Skills skills = Skills.valueOf(request.getParameter("skills").toUpperCase());
    long userTotal = Long.parseLong(request.getParameter("userTotalCompTasks"));

    // create new user. Set up datastore
    UserData user = new UserData(userID, AuthID, userName, userYear, userMajors, skills, userTotal);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // new UserController. Add UserData to it.
    UserController userController = new UserController(datastore);
    userController.addUser(user);
  }
}
