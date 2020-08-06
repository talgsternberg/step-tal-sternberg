package com.rtb.projectmanagementtool.auth;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user")
public class AuthServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    List<Log> Logs = new ArrayList<>();
    String status; // logged in or out
    String userEmail;
    String AuthID; // ID to track user for storing their info in datastore
    String urlToRedirectToAfterUserLogsIn; // go to index (all user data should load)
    String urlToRedirectToAfterUserLogsOut; // go to index with other pages blocked
    String loginUrl;
    String logoutUrl;

    // get all the properties
    if (userService.isUserLoggedIn()) {
      status = "In";
      userEmail = userService.getCurrentUser().getEmail();
      AuthID = userService.getCurrentUser().getUserId();
      urlToRedirectToAfterUserLogsOut = "/";
      urlToRedirectToAfterUserLogsIn = "/"; // temporary
      logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
    } else {
      status = "Out";
      urlToRedirectToAfterUserLogsOut = "/";
      urlToRedirectToAfterUserLogsIn = "/"; // temporary
      loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      userEmail = ""; // empty email
      AuthID = ""; // empty AuthID
    }

    // create new log object
    Log log = new Log(status, loginUrl, logoutUrl, userEmail, AuthID);

    // add to Logs which stores all users
    Logs.add(log);

    // write using gson
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(Logs));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // get each value from the request
    String status = request.getParameter("status");
    String loginUrl = request.getParameter("loginUrl");
    String logoutUrl = request.getParameter("logoutUrl");
    String userEmail = request.getParameter("userEmail");
    String AuthID = request.getParameter("AuthID");

    // setup new Entity to store
    Entity logEntity = new Entity("Log");

    // set properties to be stored
    logEntity.setProperty("status", status);
    logEntity.setProperty("loginUrl", loginUrl);
    logEntity.setProperty("logoutUrl", logoutUrl);
    logEntity.setProperty("userEmail", userEmail);
    logEntity.setProperty("AuthID", AuthID);

    // store data
    // will use AuthID (in both login and UserData to connect info)
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(logEntity);
  }
}
