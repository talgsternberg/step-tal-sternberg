package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.*;

public final class AuthOps {

  private DatastoreService datastore;
  public String cookieName;
  public String[] cookieValues;
  public Cookie userCookie;

  public AuthOps(DatastoreService datastore) {
    this.datastore = datastore;
    cookieName = "loginStatus";
    cookieValues = {"out", "null"};
  }

  public void loginUser() {
    String AuthID;
    userCookie = new Cookie(cookieName, cookieValues);
    
    // call auth service
    UserService userService = UserServiceFactory.getUserService();

    // if the user has logged in
    if (userService.isUserLoggedIn()) {
      
      // set the first cookie value status to "in"
      userCookie.getValue[0] = "in";
      
      // get AuthID
      AuthID = userService.getCurrentUser().getUserId();
      
      // find AuthID in DataStore
      Query query = new Query("User").addFilter("AuthID", FilterOperator.EQUAL, userID);
      PreparedQuery results = datastore.prepare(query);
      Entity entity = results.asSingleEntity();
      UserData currUser = new UserData(entity);

      // set which user to the user who logged in
      userCookie.getValue[1] = (String) currUser.getUserID();
    }
  }
}
