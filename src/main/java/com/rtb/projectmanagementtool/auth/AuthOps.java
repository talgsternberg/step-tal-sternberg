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
  public static final long NO_LOGGED_IN_USER = -1; // -1 = logged out

  // for testing purposes (with mock controllers)
  public AuthOps(UserController controller) {
    this.controller = controller;
  }

  public AuthOps(DatastoreService ds) {
    this.controller = new UserController(ds);
  }

  public String getLoginLink(String returnUrl) {
    return getLogLink(/*login*/ true, returnUrl);
  }

  public String getLogoutLink(String returnUrl) {
    return getLogLink(/*login*/ false, returnUrl);
  }

  private String getLogLink(boolean login, String returnUrl) {
    UserService userService = UserServiceFactory.getUserService();
    return login ? userService.createLoginURL(returnUrl) : userService.createLogoutURL(returnUrl);
  }

  // only call after Auth
  public String getAuthID() {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.getCurrentUser() != null) {
      return userService.getCurrentUser().getUserId();
    }
    return null;
  }

  public String getEmail() {
    UserService userService = UserServiceFactory.getUserService();
    return userService.getCurrentUser().getEmail();
  }

  // get/create cookie and set value to userID
  public void setLoggedInCookie(
      HttpServletRequest request, HttpServletResponse response, long userID) {
    Cookie currCookie = new Cookie(COOKIENAME, Long.toString(NO_LOGGED_IN_USER));
    String userIDString = Long.toString(userID);
    currCookie.setValue(userIDString);
    // send back cookie to response
    response.addCookie(currCookie);
  }

  // gets cookie from request. Used in most methods.
  public Cookie getCurrCookie(HttpServletRequest request) {
    Cookie currCookie = new Cookie(COOKIENAME, Long.toString(NO_LOGGED_IN_USER));
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
  public long loginUser(HttpServletRequest request, HttpServletResponse response) {
    currCookie = getCurrCookie(request);
    long userId = -1l;

    // if not logged in, call auth service
    if (currCookie.getValue().equals(Long.toString(NO_LOGGED_IN_USER))) {
      // call auth service
      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
        // get AuthID
        String AuthID = userService.getCurrentUser().getUserId();
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
    return userId;
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
    Cookie currCookie = new Cookie(COOKIENAME, Long.toString(NO_LOGGED_IN_USER));

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
