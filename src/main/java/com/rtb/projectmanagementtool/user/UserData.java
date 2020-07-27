package com.rtb.projectmanagementtool.user;

import java.util.HashSet;

/** Enum containing skills for user. */
enum Skills {
  LEADERSHIP(false),
  ORGANIZATION(false),
  WRITING(false),
  ART(false),
  WEBDEV(false),
  OOP(false);

  private final boolean isPriority;

  private Skills(boolean isPriority) {
    this.isPriority = isPriority;
  }
}

/** Class for User data. */
public final class UserData {

  private long userID;
  private String userName;
  private int userYear;
  private HashSet<String> userMajors;
  private HashSet<Skills> skills;
  private HashSet<Skills> prioritySkills;
  private int userTotalCompTasks;

  public UserData(
      long userID,
      String userName,
      int userYear,
      HashSet<String> userMajors,
      HashSet<Skills> skills,
      HashSet<Skills> prioritySkills,
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

  public HashSet<Skills> getUserSkills() {
    return skills;
  }

  public HashSet<Skills> getUserPrSkills() {
    return prioritySkills;
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

  public void setUserSkills(HashSet<Skills> skills) {
    this.skills = skills;
  }

  public void setUserPrSkills(HashSet<Skills> prioritySkills) {
    this.prioritySkills = prioritySkills;
  }

  public void setUserTotal(int userTotalCompTasks) {
    this.userTotalCompTasks = userTotalCompTasks;
  }
}
