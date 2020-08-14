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
    this.controller = new UserController(ds);
  }

  public String getAuthID() {
    // call auth service to generate AuthID
    UserService userService = UserServiceFactory.getUserService();
    String AuthID = userService.getCurrentUser().getUserId();
    return AuthID;
  }

  // get/create cookie and set value to userID
  public void createAndSetCookieNewUser(HttpServletRequest request, long userID) {
    Cookie cookie = getCurrCookie(request);
    String userIDString = Long.toString(userID);
    cookie.setValue(userIDString);
  }

  // gets cookie from request. Used in most methods.
  public Cookie getCurrCookie(HttpServletRequest request) {
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
    currCookie = getCurrCookie(request);
    // if not logged in, call auth service
    if (currCookie.getValue().equals(NO_LOGGED_IN_USER)) {
      // call auth service
      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
        // get AuthID
        String AuthID = userService.getCurrentUser().getUserId();
        System.out.println("AuthID: " + AuthID);
        // find AuthID in DataStore
        UserData user = controller.getUserByAuthID(AuthID);
        if (user != null) {
          String userIDString = String.valueOf(user.getUserID());
          currCookie.setValue(userIDString);
        }
      }
      // send back cookie to response
      response.addCookie(currCookie);
    }
  }

  // returns the userID long (or -1) associated with the user logged in
  public long whichUserIsLoggedIn(HttpServletRequest request, HttpServletResponse response) {
    currCookie = getCurrCookie(request);

    // get cookie value for user
    String currUserIDString = currCookie.getValue();
    return Long.parseLong(currUserIDString);
  }

  // logs out user by setting cookie value to -1
  public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
    // make currCookie val = -1
    Cookie currCookie = new Cookie(COOKIENAME, NO_LOGGED_IN_USER);

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
