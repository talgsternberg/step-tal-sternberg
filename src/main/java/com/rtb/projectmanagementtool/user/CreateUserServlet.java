package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.users.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Servlet that returns task data */
@WebServlet("/create-user")
public class CreateUserServlet extends HttpServlet {

  DatastoreService datastore;
  Cookie cookie;

  public CreateUserServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  // for testing
  public CreateUserServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // call auth service to generate AuthID
    UserService userService = UserServiceFactory.getUserService();
    String AuthID = userService.getCurrentUser().getUserId();

    // initialize userMajors
    ArrayList<String> majors = new ArrayList<>();

    // create new UserData object with UserID
    UserData newUser = new UserData("", "", 0, majors, Skills.NONE, 0);

    // to entity
    Entity entity = newUser.toEntity();

    // put in datastore
    datastore.put(entity);

    // get userID for cookie
    long userID = (long) entity.getKey().getId();

    // set AuthID in entity
    entity.setProperty("AuthID", AuthID);

    // new AuthOps object
    AuthOps auth = new AuthOps(datastore);

    // get/create cookie and set value to userID
    cookie = auth.getCurrCookie(request);
    String userIDString = Long.toString(userID);
    cookie.setValue(userIDString);

    // redirect to user_settings
    response.sendRedirect("/user_settings.html");

    // now how do I update the rest of my user's fields given their input

  }
}
