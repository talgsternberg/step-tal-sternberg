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
    // new AuthOps object
    AuthOps auth = new AuthOps(datastore);

    // get Auth ID from AuthOps method
    String AuthID = auth.getAuthID();

    // new UserController object
    UserController controller = new UserController(datastore);

    // create new "blank" UserData object to put in datastore
    UserData newUser = controller.createNewUser();

    // set this user's AuthID
    newUser.setAuthID(AuthID);

    // create entity and put in datastore. Get userID
    long userID = controller.addUser(newUser);

    // get/create cookie and set value to userID
    cookie = auth.getCurrCookie(request);
    String userIDString = Long.toString(userID);
    cookie.setValue(userIDString);

    // redirect to user_settings
    response.sendRedirect("/user_settings.html");

    // now how do I update the rest of my user's fields given their input

  }
}
