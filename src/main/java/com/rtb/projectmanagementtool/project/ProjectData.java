/**
 * ProjectData.java - this file implements the Project class, which contains details about a
 * particular project, including the members and tasks within it.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;

public class ProjectData {
  private final String PROPERTY_NAME = "name";
  private final String PROPERTY_DESCRIPTION = "description";
  private final String PROPERTY_USERS = "users";
  private final String PROPERTY_TASKS = "tasks";

  private long id;
  private String name;
  private String description;
  private ArrayList<Long> tasks;
  private ArrayList<String> users;

  /**
   * Class constructor with the minimum requirements for creating a project.
   *
   * @param name the name of the project
   * @param description the description of the project
   * @param creatorId the id of the user who created the project
   */
  public ProjectData(String name, String description, long creatorId) {
    this.name = name;
    this.description = description;
    this.tasks = new ArrayList<Long>();
    this.users = new ArrayList<String>();
    this.users.add(createUserString(creatorId, UserProjectRole.CREATOR));
  }

  /**
   * Class constructor to create Project from existing datastore entity
   *
   * @param entity the datastore entity to parse object from
   */
  public ProjectData(Entity entity) {
    this.id = (Long) entity.getKey().getId();
    this.name = (String) entity.getProperty(PROPERTY_NAME);
    this.description = (String) entity.getProperty(PROPERTY_DESCRIPTION);

    // Tasks
    Object entityProperty;
    if ((entityProperty = entity.getProperty(PROPERTY_TASKS)) != null) {
      this.tasks = (ArrayList<Long>) entityProperty;
    } else {
      this.tasks = new ArrayList<Long>();
    }

    // Users
    if ((entityProperty = entity.getProperty(PROPERTY_USERS)) != null) {
      this.users = (ArrayList<String>) entityProperty;
    } else {
      this.users = new ArrayList<String>();
    }
  }

  /** @return the entity representation of this class */
  public Entity toEntity() {
    Entity entity = new Entity("Project");
    entity.setProperty(PROPERTY_NAME, this.name);
    entity.setProperty(PROPERTY_DESCRIPTION, this.description);
    entity.setProperty(PROPERTY_TASKS, this.tasks);
    entity.setProperty(PROPERTY_USERS, this.users);
    return entity;
  }

  /** @return project id */
  public long getId() {
    return this.id;
  }

  /** @return project name */
  public String getName() {
    return this.name;
  }

  /** @return project description */
  public String getDescription() {
    return this.description;
  }

  /** @return users */
  public ArrayList<String> getUsers() {
    return this.users;
  }

  /** @return task ids */
  public ArrayList<Long> getTasks() {
    return this.tasks;
  }

  /**
   * Set the id of this project
   *
   * @param id the new id of the project
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Set the name of this project
   *
   * @param name the new name of the project
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the description of this project
   *
   * @param description the new description of the project
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Add a regular user to the project
   *
   * @param userId id of the user to add
   */
  public boolean addUser(long userId, UserProjectRole userRole) {
    // TODO: add checks for if user already exists
    this.users.add(createUserString(userId, userRole));
    return true;
  }

  /**
   * Removes a user from the project
   *
   * @param userId id of the user to remove
   * @return true if operation is successful
   */
  public boolean removeUser(long userId) {
    if (getUserRole(userId) != UserProjectRole.CREATOR) {
      users.remove(getUser(userId));
      return true;
    }
    return false;
  }

  /**
   * Adds a task to the project
   *
   * @param taskId id of the task to add
   * @return true if operation successful
   */
  public boolean addTask(long taskId) {
    return this.tasks.add(taskId);
  }

  /**
   * Removes a task from the project
   *
   * @param taskId id of the task to remove
   * @return true if operation successful
   */
  public boolean removeTask(long taskId) {
    return this.tasks.remove(taskId);
  }

  /**
   * Get the string representation of a user ex. "CREATOR-12345"
   *
   * @param userId id of the user
   * @return user string
   */
  public String getUser(long userId) {
    for (String user : users) {
      if (getUserId(user) == userId) {
        return user;
      }
    }
    return null;
  }

  /**
   * Get the role of a user given thier id ex. "CREATOR"
   *
   * @param userId id of the user
   * @return user's role
   */
  public UserProjectRole getUserRole(long userId) {
    String user;
    if ((user = getUser(userId)) != null) {
      return getUserRole(user);
    }
    return null;
  }

  /**
   * Get the role of a user given thier user String ex. "CREATOR-12345" -> "CREATOR"
   *
   * @param user the user's string representation
   * @return user's role
   */
  public UserProjectRole getUserRole(String user) {
    String userRole = user.split("-")[0];
    if (userRole != null) {
      return UserProjectRole.valueOf(userRole);
    }
    return null;
  }

  /**
   * Get the id of a user given thier user String ex. "CREATOR-12345" -> 12345
   *
   * @param user the user's string representation
   * @return user's id
   */
  public Long getUserId(String user) {
    return Long.parseLong(user.split("-")[1]);
  }

  /**
   * Static method to create the String representation of user's id. Is static in order to allow
   * easier parsing for external classes that work with the users in this class.
   *
   * @param userId the id of user
   * @param userRole the role of the user
   * @return string representation of user (ex. "MEMBER-3254622")
   */
  public static String createUserString(long userId, UserProjectRole userRole) {
    return new String(userRole.name() + "-" + userId);
  }

  /**
   * Check if user is in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public boolean hasUser(long userId) {
    return getUser(userId) != null;
  }

  /**
   * Check if user exists as an admin in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public boolean hasAdmin(long userId) {
    return getUserRole(userId) == UserProjectRole.ADMIN;
  }

  /**
   * Check if this is the project creator
   *
   * @param userId the userId
   * @return true if the user is the creator
   */
  public boolean isCreator(long userId) {
    return getUserRole(userId) == UserProjectRole.CREATOR;
  }

  /** @return the string representation of this class. */
  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Project id: " + this.id + "\n";
    returnString += "Project Name: " + this.name + "\n";
    returnString += "Project Description: " + this.description + "\n";
    returnString += "Project Users: " + this.users.toString() + "\n";
    returnString += "Tasks: " + this.tasks.toString() + "\n}";
    return returnString;
  }
}
