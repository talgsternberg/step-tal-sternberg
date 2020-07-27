package com.rtb.projectmanagementtool.user;

import java.util.HashSet;

/** Enum containing skills for user. */
enum Skills {
  LEADERSHIP (false);
  ORGANIZATION (false);
  ANALYTICAL/CREATIVE WRITING (false);
  ARTISTIC ABILITY (false);
  WEB DEVELOPMENT (false);
  OBJECT ORIENTED PROGRAMMING (false);
}

/** Class for User data. */
public final class UserData {

  private long userID;
  private String userName;
  private int userYear;
  private HashSet<String> userMajors;
  private Skills skills;
  private int userTotalCompTasks;

  public UserData(
      long userID,
      String userName,
      int userYear,
      HashSet<String> userMajors,
      Skills skills,
      int userTotalCompTasks) {
    this.userID = userID;
    this.userName = userName;
    this.userYear = userYear;
    this.userMajors = userMajors;
    this.skills = skills;
    this.userTotalCompTasks = userTotalCompTasks;
  }

  public long getUserID() {
    return userID;
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
