package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.user.UserData.Skills;
import java.util.ArrayList;

public class UserController {

  private DatastoreService datastore;

  public UserController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  // Only way to find users to add to projects currently
  // is with their name; means that we'd need to make sure there
  // are no duplicate names in database
  public UserData getUserByName(String userName) {
    Query query = new Query("User").addFilter("userName", FilterOperator.EQUAL, userName);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity != null) {
      UserData user = new UserData(entity);
      return user;
    }
    return null;
  }

  public UserData getUserByID(long userID) {
    // Query query = new Query("User").addFilter("userID", FilterOperator.EQUAL, userID);
    Query query =
        new Query("User")
            .addFilter("__key__", FilterOperator.EQUAL, KeyFactory.createKey("User", userID));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity != null) {
      UserData user = new UserData(entity);
      return user;
    }
    return null;
  }

  public UserData getUserByAuthID(String AuthID) {
    Query query = new Query("User").addFilter("AuthID", FilterOperator.EQUAL, AuthID);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity != null) {
      UserData user = new UserData(entity);
      return user;
    }
    return null;
  }

  public UserData getUserByEmail(String email) {
    Query query = new Query("User").addFilter("email", FilterOperator.EQUAL, email);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity != null) {
      UserData user = new UserData(entity);
      return user;
    }
    return null;
  }

  // get users+data to direct/display user's profiles later
  public ArrayList<UserData> getEveryUser() {
    Query query = new Query("User");
    ArrayList<UserData> users = new ArrayList<>();
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      UserData user = new UserData(entity);
      users.add(user);
    }
    return users;
  }

  public void updateUser(UserData user) {
    Entity entity = user.toEntity();
    datastore.put(entity);
  }

  public long addUser(UserData user) {
    Entity entity = user.toEntity();
    Key key = datastore.put(entity);
    long userID = key.getId();
    user.setUserID(userID);
    return userID;
  }

  public UserData createNewUser() {
    ArrayList<String> majors = new ArrayList<>();

    // create new UserData object with UserID
    UserData newUser = new UserData("", "", "", 0, majors, Skills.NONE, 0);
    return newUser;
  }

  public ArrayList<Long> getUserIDs() {
    Query query = new Query("User");
    ArrayList<Long> userIDs = new ArrayList<Long>();
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      UserData user = new UserData(entity);
      Long userID = (Long) user.getUserID();
      userIDs.add(userID);
    }
    return userIDs;
  }
}
