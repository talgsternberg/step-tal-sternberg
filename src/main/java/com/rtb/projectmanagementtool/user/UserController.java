package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import java.util.HashSet;


public final class UserController {
  private HashSet<UserData> users;

  public UserController(HashSet<UserData> users) {
    this.users = users;
  }

  //get users+data to direct/display user's profiles later
  public HashSet<UserData> getEveryUser() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    HashSet<UserData> users = new HashSet<>();
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);
    //create new user object and add to users
    for (Entity entity : results.asIterable()) {
      long userID = (long) entity.getKey().getId();
      String userEmail = (String) entity.getProperty("userEmail");
      String userName = (String) entity.getProperty("userName");
      int userYear = (int) entity.getProperty("userYear");
      HashSet<String> userMajors = (HashSet<String>) entity.getProperty("userMajors");
      Skills skills = (Skills) entity.getProperty("skills");
      int userTotalCompTasks = (int) entity.getProperty("userTotalCompTasks");
      UserData user = 
          new UserData(
              userID, userEmail, userName, userYear, userMajors, skills, userTotalCompTasks);
      users.add(user);
    }
    return users;
  }
}
