package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.ArrayList;

public final class UserController {

  private DatastoreService datastore;

  public UserController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  public UserData getUserByID(long userID) {
    Query query = new Query("User").addFilter("userID", FilterOperator.EQUAL, userID);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    UserData user = new UserData(entity);
    return user;
  }

  // get users+data to direct/display user's profiles later
  public ArrayList<UserData> getEveryUser() {
    ArrayList<UserData> users = new ArrayList<>();
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);
    // create new user object and add to users
    for (Entity entity : results.asIterable()) {
      UserData user = new UserData(entity);
      users.add(user);
    }
    return users;
  }

  public void addUser(UserData user) {
    Entity entity = user.toEntity();
    datastore.put(entity);
  }
}
