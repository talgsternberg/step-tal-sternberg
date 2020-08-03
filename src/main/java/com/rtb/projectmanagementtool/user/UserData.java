package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.Entity;
import java.util.*;

/** Enum containing skills for user. */
enum Skills { // ignore for now. conflict w/ testing
  LEADERSHIP,
  ORGANIZATION,
  WRITING,
  ART,
  WEBDEV,
  OOP;
}

/** Class for User data. */
public final class UserData {

  private long userID;
  private long AuthID; // this will be the ID from API
  private String userName;
  private long userYear;
  private ArrayList<String> userMajors;
  // private Skills skills;
  // private String skillsString;
  private long userTotalCompTasks;

  public UserData(
      long userID,
      long AuthID,
      String userName,
      long userYear,
      ArrayList<String> userMajors,
      // Skills skills,
      // String skillString,
      long userTotalCompTasks) {
    this.userID = userID;
    this.AuthID = AuthID;
    this.userName = userName;
    this.userYear = userYear;
    this.userMajors = userMajors;
    // this.skills = skills;
    // this.skillsString = skillsString;
    this.userTotalCompTasks = userTotalCompTasks;
  }

  public UserData(Entity entity) {
    userID = (long) entity.getKey().getId();
    System.out.println("userID() in UserData:");
    System.out.println(userID);
    AuthID = (long) entity.getProperty("AuthID");
    userName = (String) entity.getProperty("userName");
    userYear = (long) entity.getProperty("userYear");
    userMajors = (ArrayList<String>) entity.getProperty("userMajors");
    // skills = Skills.valueOf((String) entity.getProperty("skills"));
    userTotalCompTasks = (long) entity.getProperty("userTotalCompTasks");
  }

  public Entity toEntity() {
    Entity entity;
    if (userID != 0) {
      entity = new Entity("User", userID);
    } else {
      entity = new Entity("User");
    }
    entity.setProperty("userID", entity.getKey().getId());
    entity.setProperty("AuthID", AuthID);
    entity.setProperty("userName", userName);
    entity.setProperty("userYear", userYear);
    entity.setProperty("userMajors", userMajors);
    // entity.setProperty("skills", skills.name());
    entity.setProperty("userTotalCompTasks", userTotalCompTasks);
    return entity;
  }

  public long getUserID() {
    return userID;
  }

  public long getAuthID() {
    return AuthID;
  }

  public String getUserName() {
    return userName;
  }

  public long getUserYear() {
    return userYear;
  }

  public ArrayList<String> getUserMajors() {
    return userMajors;
  }

  /* public Skills getUserSkills() {
    return skills;
  } */

  public long getUserTotal() {
    return userTotalCompTasks;
  }

  public void setUserID(long userID) {
    this.userID = userID;
  }

  public void setUserEmail(long AuthID) {
    this.AuthID = AuthID;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setUserYear(long userYear) {
    this.userYear = userYear;
  }

  public void setUserMajors(ArrayList<String> userMajors) {
    this.userMajors = userMajors;
  }

  /* public void setUserSkills(Skills skills) {
    this.skills = skills;
  } */

  public void setUserTotal(long userTotalCompTasks) {
    this.userTotalCompTasks = userTotalCompTasks;
  }
}
