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
  private UserController CONTROLLER;
  public String COOKIENAME;
  public String cookieValue;
  public Cookie currCookie;

  // for testing purposes (with mock controllers)
  public AuthOps(UserController CONTROLLER) {
    this.CONTROLLER = CONTROLLER;
    COOKIENAME = "sessionUserID";
    cookieValue = "-1"; // "out" if not logged in, else userID as String
  }

  public AuthOps(DatastoreService ds) {
    UserController CONTROLLER = new UserController(ds);
    COOKIENAME = "sessionUserID";
    cookieValue = "-1"; // "out" if not logged in, else userID as String
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
      currCookie = new Cookie(COOKIENAME, cookieValue);
    }

    // if not logged in, call auth service
    if (currCookie.getValue() == "-1") {
      // call auth service
      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
        // get AuthID
        String AuthID = userService.getCurrentUser().getUserId();
        System.out.println("authID from service:");
        System.out.println(AuthID);
        // find AuthID in DataStore
        ArrayList<UserData> users = CONTROLLER.getEveryUser();
        for (UserData user : users) {
          System.out.println(user.getAuthID());
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

  public long whichUserLoggedIn(HttpServletRequest request, HttpServletResponse response) {
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
      currCookie = new Cookie(COOKIENAME, cookieValue);
    }

    // get cookie value for user
    String currUserIDString = currCookie.getValue();
    return Long.parseLong(currUserIDString);
  }
}
