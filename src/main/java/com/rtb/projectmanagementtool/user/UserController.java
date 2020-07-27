package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.HashSet;

public final class UserController {
  private HashSet<UserData> users;

  public UserController(HashSet<UserData> users) {
    this.users = users;
  }

  // get users+data to direct/display user's profiles later
  public HashSet<UserData> getEveryUser() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
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
}
