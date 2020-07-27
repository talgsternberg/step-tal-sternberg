package com.rtb.projectmanagementtool.user;

import java.util.HashSet;

/** Enum containing skills for user. */
enum Skills {
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
  private int userYear;
  private HashSet<String> userMajors;
  private Skills skills;
  private int userTotalCompTasks;

  public UserData(
      long userID,
      long AuthID,
      String userName,
      int userYear,
      HashSet<String> userMajors,
      Skills skills,
      int userTotalCompTasks) {
    this.userID = userID;
    this.AuthID = AuthID;
    this.userName = userName;
    this.userYear = userYear;
    this.userMajors = userMajors;
    this.skills = skills;
    this.userTotalCompTasks = userTotalCompTasks;
  }

  public UserData(Entity entity) {
    userID = (long) entity.getKey().getId();
    AuthID = (long) entity.getProperty("AuthID");
    userName = (String) entity.getProperty("userName");
    userYear = (int) entity.getProperty("userYear");
    userMajors = (HashSet<String>) entity.getProperty("userMajors");
    skills = (Skills) entity.getProperty("skills");
    userTotalCompTasks = (int) entity.getProperty("userTotalCompTasks")
  }

  public Entity toEntity() {
    Entity entity = new Entity("User", userID);
    entity.setProperty("AuthID", AuthID);
    entity.setProperty("userName", userName);
    entity.setProperty("userYear", userYear);
    entity.setProperty("userMajors", userMajors);
    entity.setProperty("skills", skills);
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

  public int getUserYear() {
    return userYear;
  }

  public HashSet<String> getUserMajors() {
    return userMajors;
  }

  public Skills getUserSkills() {
    return skills;
  }

  public int getUserTotal() {
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

  public void setUserYear(int userYear) {
    this.userYear = userYear;
  }

  public void setUserSkills(Skills skills) {
    this.skills = skills;
  }

  public void setUserTotal(int userTotalCompTasks) {
    this.userTotalCompTasks = userTotalCompTasks;
  }
}
