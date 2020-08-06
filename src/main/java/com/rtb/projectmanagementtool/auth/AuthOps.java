package com.rtb.projectmanagementtool.auth;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.users.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public final class AuthOps {

  private DatastoreService datastore;
  public String cookieName;
  public String cookieValue;
  public Cookie currCookie;

  public AuthOps(DatastoreService datastore) {
    this.datastore = datastore;
    cookieName = "sessionUserID";
    cookieValue = "out"; // "out" if not logged in
  }

  public void loginUser(HttpServletRequest request, HttpServletResponse response) {

    // get all cookies from request
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("sessionUserID")) {
          // if we find cookie w/ name, set currCookie equal
          currCookie = cookie;
        }
      }
    } else {
      // if no cookie found, create a new one
      currCookie = new Cookie(cookieName, cookieValue);
    }

    // if not logged in, call auth service
    if (currCookie.getValue() == "out") {
      // call auth service
      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
        // get AuthID
        String AuthID = userService.getCurrentUser().getUserId();
        // find AuthID in DataStore
        UserController controller = new UserController(datastore);
        ArrayList<UserData> users = controller.getEveryUser();
        for (UserData user : users) {
          if (user.getAuthID() == AuthID) {
            currCookie.setValue(AuthID);
          }
        }
      }
    }
    // send back cookie to response
    response.addCookie(currCookie);
  }
}
