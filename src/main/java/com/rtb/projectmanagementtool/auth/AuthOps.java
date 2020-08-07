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

  private DatastoreService datastore;
  public String cookieName;
  public String cookieValue;
  public Cookie currCookie;

  public AuthOps(DatastoreService datastore) {
    this.datastore = datastore;
    cookieName = "sessionUserID";
    cookieValue = "-1"; // "out" if not logged in, else userID as String
  }

  public void loginUser(HttpServletRequest request, HttpServletResponse response) {
    System.out.println("Checkpoint 1");
    System.out.println("Checkpoint 1");
    System.out.println("Checkpoint 1");
    System.out.println("Checkpoint 1");
    System.out.println("Checkpoint 1");
    System.out.println("Checkpoint 1");

    // get all cookies from request
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("sessionUserID")) {
          // if we find cookie w/ name, set currCookie equal
          currCookie = cookie;
          System.out.println("Checkpoint 2");
        }
      }
    } else {
      // if no cookie found, create a new one
      currCookie = new Cookie(cookieName, cookieValue);
      System.out.println("Checkpoint 3");
    }

    System.out.println("Checkpoint 4");

    // if not logged in, call auth service
    if (currCookie.getValue() == "-1") {
      // call auth service
      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {

        System.out.println("Checkpoint 5");

        // get AuthID
        String AuthID = userService.getCurrentUser().getUserId();
        // find AuthID in DataStore
        UserController controller = new UserController(datastore);
        ArrayList<UserData> users = controller.getEveryUser();
        for (UserData user : users) {
          if (user.getAuthID() == AuthID) {
            String userIDString = String.valueOf(user.getUserID());
            currCookie.setValue(userIDString);
            System.out.println("Checkpoint 6");
          }
        }
      }
    }
    // send back cookie to response

    System.out.println("Checkpoint 7");

    response.addCookie(currCookie);
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
      currCookie = new Cookie(cookieName, cookieValue);
    }

    // get cookie value for user
    String currUserIDString = currCookie.getValue();
    return Long.parseLong(currUserIDString);
  }
}
