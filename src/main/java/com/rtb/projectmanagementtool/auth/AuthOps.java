package com.rtb.projectmanagementtool.auth;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.users.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AuthOps {

  // private DatastoreService datastore;
  private UserController controller;
  // public String cookieValue;
  public Cookie currCookie;
  private final String COOKIENAME = "sessionUserID";
  private final String NO_LOGGED_IN_USER = "-1"; // -1 = logged out

  // for testing purposes (with mock controllers)
  public AuthOps(UserController controller) {
    this.controller = controller;
  }

  public AuthOps(DatastoreService ds) {
    UserController controller = new UserController(ds);
  }

  // generates cookie. Used in most methods.
  public Cookie generateCurrCookie(HttpServletRequest request, HttpServletResponse response) {
    Cookie currCookie = new Cookie(COOKIENAME, NO_LOGGED_IN_USER);
    // get all cookies from request
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("sessionUserID")) {
          // if we find cookie w/ name, set currCookie equal
          currCookie.setValue(cookie.getValue());
        }
      }
    }
    return currCookie;
  }

  // logs in user and adds cookie with value of String userID to response
  public void loginUser(HttpServletRequest request, HttpServletResponse response) {
    currCookie = generateCurrCookie(request, response);
    // if not logged in, call auth service
    if (currCookie.getValue() == NO_LOGGED_IN_USER) {
      // call auth service
      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
        // get AuthID
        String AuthID = userService.getCurrentUser().getUserId();
        // find AuthID in DataStore
        ArrayList<UserData> users = controller.getEveryUser();
        for (UserData user : users) {
          if (user.getAuthID() == AuthID) {
            String userIDString = String.valueOf(user.getUserID());
            currCookie.setValue(userIDString);
          }
        }
      }
      // send back cookie to response
      response.addCookie(currCookie);
    }
  }

  // returns the userID long (or -1) associated with the user logged in
  public long whichUserLoggedIn(HttpServletRequest request, HttpServletResponse response) {
    currCookie = generateCurrCookie(request, response);

    // get cookie value for user
    String currUserIDString = currCookie.getValue();
    return Long.parseLong(currUserIDString);
  }

  // logs out user by setting cookie value to -1
  public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
    currCookie = generateCurrCookie(request, response);
    // set cookie value to logged out
    currCookie.setValue(NO_LOGGED_IN_USER);

    // write cookie back
    response.addCookie(currCookie);
  }

  // returns the AuthID to go in the UserData object (for later association)
  public String createUserHelper(HttpServletRequest request, HttpServletResponse response) {
    UserService userService = UserServiceFactory.getUserService();
    String AuthID = userService.getCurrentUser().getUserId();
    return AuthID;
  }
}
