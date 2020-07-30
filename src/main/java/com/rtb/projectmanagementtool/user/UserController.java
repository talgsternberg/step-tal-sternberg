package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.HashSet;

public final class UserController {

  private DatastoreService datastore;

  public UserController(Datastore datastore) {
    this.datastore = datastore;
  }

  public UserData getUserByID(long userID) {
    Query query = new Query("User").addFilter("userID", FilterOperator.EQUAL, taskID);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    UserData user = new UserData(entity);
  }

  // get users+data to direct/display user's profiles later
  public HashSet<UserData> getEveryUser() {
    HashSet<UserData> users = new HashSet<>();
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
    datastore.put(user.toEntity());
  }
}
