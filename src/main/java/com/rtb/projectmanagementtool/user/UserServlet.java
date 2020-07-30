package com.rtb.projectmanagementtool.user;

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


@WebServlet("/user")
public class UserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("User");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // get the User
    long userID = Long.parseLong(request.getParameter("userID"));
    UserController userController = new UserController(datastore);
    UserData user = UserController.getUserByID(userID);
    ArrayList<UserData> usersList = new ArrayList<>(Arrays.asList(user));

    // convert to JSON
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(usersList));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Entity entity = new Entity("User");
      
      // params from request
      long userID = (long) entity.getKey().getId();
      long AuthID = Long.parseLong(request.getParameter("AuthID"));
      String userName = request.getParameter("userName").trim();
      long userYear = Long.parseLong(request.getParameter("userYear"));
      ArrayList<String> userMajors= (ArrayList<String>) request.getParameter("userMajors").trim();
      long userTotal = Long.parseLong(request.getParameter("userTotalCompTasks"));
      
      //create new user. Set up datastore
      UserData user = new UserData(userID, AuthID, userName, userYear, userMajors, userTotal);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      
      // new UserController. Add UserData to it.
      UserController userController = new UserController(datastore);
      userController.addUser(user);
}
