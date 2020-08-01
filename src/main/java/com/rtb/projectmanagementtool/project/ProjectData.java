/**
 * ProjectData.java - this file implements the Project class, which contains details about a
 * particular project, including the members and tasks within it.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ProjectData {
  private final String PROPERTY_NAME = "name";
  private final String PROPERTY_DESCRIPTION = "description";
  private final String PROPERTY_USER_IDS = "userIds";
  private final String PROPERTY_TASK_IDS = "taskIds";

  private long id;
  private String name;
  private String description;
  private HashMap<Long, UserProjectRole> userIds;
  private HashSet<Long> taskIds;

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
    this.taskIds = new HashSet<Long>();
    this.userIds = new HashMap<Long, UserProjectRole>();
    this.userIds.put(creatorId, UserProjectRole.CREATOR);
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

    Object userIdProperty;
    if ((userIdProperty = entity.getProperty(PROPERTY_TASK_IDS)) != null) {
      this.taskIds = new HashSet<Long>((ArrayList<Long>) userIdProperty);
    } else {
      this.taskIds = new HashSet<Long>();
    }

    // Convert json to HashMap for userIds
    Gson gson = new Gson();
    Type mapType = new TypeToken<HashMap<Long, UserProjectRole>>() {}.getType();
    this.userIds = gson.fromJson((String) entity.getProperty(PROPERTY_USER_IDS), mapType);
  }

  /** @return the entity representation of this class */
  public Entity toEntity() {
    Entity entity = new Entity("Project");
    entity.setProperty(PROPERTY_NAME, this.name);
    entity.setProperty(PROPERTY_DESCRIPTION, this.description);
    entity.setProperty(PROPERTY_TASK_IDS, this.taskIds);
    Gson gson = new Gson();
    entity.setProperty(PROPERTY_USER_IDS, gson.toJson(this.userIds));
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
  public HashMap<Long, UserProjectRole> getUsers() {
    return this.userIds;
  }

  /** @return task ids */
  public HashSet<Long> getTasks() {
    return this.taskIds;
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
  public void addRegularUser(long userId) {
    addUser(userId, false);
  }

  /**
   * Add an admin user to the project
   *
   * @param userId id of the user to add
   */
  public void addAdminUser(long userId) {
    addUser(userId, true);
  }

  /**
   * Helper function for addRegularUser() and addAdminUser() to add any user to the project. Can
   * only add regular/admin users
   *
   * @param userId id of the user to add
   * @param isAdmin is the user an admin?
   */
  private void addUser(long userId, boolean isAdmin) {
    // remove user if they're already in project
    if (hasUser(userId)) {
      removeUser(userId);
    }

    if (isAdmin) {
      this.userIds.put(userId, UserProjectRole.ADMIN);
    } else {
      this.userIds.put(userId, UserProjectRole.REGULAR);
    }
  }

  /**
   * Removes a user from the project
   *
   * @param userId id of the user to remove
   * @return true if operation is successful
   */
  public boolean removeUser(long userId) {
    // don't remove creator
    if (userType(userId) != UserProjectRole.CREATOR.name()) {
      this.userIds.remove(userId);
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
    return this.taskIds.add(taskId);
  }

  /**
   * Removes a task from the project
   *
   * @param taskId id of the task to remove
   * @return true if operation successful
   */
  public boolean removeTask(long taskId) {
    return this.taskIds.remove(taskId);
  }

  /**
   * Check if user is in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public boolean hasUser(long userId) {
    return userType(userId) != null;
  }

  /**
   * Check if user exists as an admin in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public boolean hasAdmin(long userId) {
    return userType(userId) == UserProjectRole.ADMIN.name();
  }

  /**
   * Check if this is the project creator
   *
   * @param userId the userId
   * @return true if the user is the creator
   */
  public boolean isCreator(long userId) {
    return userType(userId) == UserProjectRole.CREATOR.name();
  }

  /**
   * Returns the type of the user
   *
   * @param userId the userId
   * @return the type of user (CREATOR / ADMIN / REGULAR)
   */
  private String userType(long userId) {
    UserProjectRole userRole = this.userIds.get(userId);
    if (userRole != null) {
      return userRole.name();
    }
    return null;
  }

  /** @return the string representation of this class. */
  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Project id: " + this.id + "\n";
    returnString += "Project Name: " + this.name + "\n";
    returnString += "Project Description: " + this.description + "\n";
    returnString += "Project Users: " + this.userIds.toString() + "\n";
    returnString += "Tasks: " + this.taskIds.toString() + "\n}";
    return returnString;
  }
}
