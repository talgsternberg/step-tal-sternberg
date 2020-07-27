/**
 * ProjectData.java - this file implements the Project class, which contains details about a particular
 * project, including the members and tasks within it.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;

enum User {
  CREATOR,
  ADMIN,
  REGULAR;
}

public class ProjectData {
  /**
   * constant variables used to specify the names of each property of the Project entity. Used to
   * minimize redundancy and also make renaming properties easy if needed
   */
  private final String PROPERTY_NAME = "name";

  private final String PROPERTY_DESCRIPTION = "description";
  private final String PROPERTY_USER_IDS = "userIds";
  private final String PROPERTY_TASK_IDS = "taskIds";

  private DatastoreService datastore;

  private long id;
  private String name;
  private String description;
  private HashMap<Long, User> userIds;
  private HashSet<Long> taskIds;

  /**
   * Class constructor with the minimum requirements for creating a project.
   *
   * @param name the name of the project
   * @param description the description of the project
   * @param creatorId the id of the user who created the project
   */
  public ProjectData(String name, String description, long creatorId) {
    datastore = DatastoreServiceFactory.getDatastoreService();
    this.id = datastore.put(new Entity("Project")).getId();
    this.name = name;
    this.description = description;

    this.userIds = new HashMap<Long, User>();
    this.userIds.put(creatorId, User.CREATOR);

    this.taskIds = new HashSet<Long>();
    toEntity();
  }

  /**
   * Class constructor to create Project from existing datastore entity
   *
   * @param entity the datastore entity to parse object from
   */
  public ProjectData(Entity entity) {
    datastore = DatastoreServiceFactory.getDatastoreService();

    this.id = (Long) entity.getKey().getId();
    this.name = (String) entity.getProperty(PROPERTY_NAME);
    this.description = (String) entity.getProperty(PROPERTY_DESCRIPTION);

    Gson gson = new Gson();

    // sets the type of containers to store parsed JSON in
    Type mapType = new TypeToken<HashMap<Long, User>>() {}.getType();
    Type setType = new TypeToken<HashSet<Long>>() {}.getType();

    this.userIds = gson.fromJson((String) entity.getProperty(PROPERTY_USER_IDS), mapType);
    this.taskIds = gson.fromJson((String) entity.getProperty(PROPERTY_TASK_IDS), setType);
  }

  /** Saves the object as a datastore entity. */
  private void toEntity() {
    Gson gson = new Gson();
    try {
      Key entityKey = KeyFactory.createKey("Project", this.id);
      Entity entity = datastore.get(entityKey);

      entity.setProperty(PROPERTY_NAME, this.name);
      entity.setProperty(PROPERTY_DESCRIPTION, this.description);
      entity.setProperty(PROPERTY_USER_IDS, gson.toJson(this.userIds));
      entity.setProperty(PROPERTY_TASK_IDS, gson.toJson(this.taskIds));

      datastore.put(entity);
    } catch (Exception exception) {
      System.out.println("Error: Project not found.");
    }
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

  /** @return returns user ids */
  public HashMap<Long, User> getUsers() {
    return this.userIds;
  }

  /** @return task ids */
  public HashSet<Long> getTaskIds() {
    return this.taskIds;
  }

  /**
   * Changes the name of this project
   *
   * @param name the new name of the project
   */
  public void setName(String name) {
    this.name = name;
    toEntity();
  }

  /**
   * Changes the description of this project
   *
   * @param description the new description of the project
   */
  public void setDescription(String description) {
    this.description = description;
    toEntity();
  }

  /**
   * Adds a user to the project ? separate this into addAdminUser and addRegularUser?
   *
   * @param isAdmin is this user an admin?
   * @param userId id of the user to add
   */
  public void addUser(boolean isAdmin, long userId) {
    if (isAdmin) {
      this.userIds.put(userId, User.ADMIN);
    } else {
      this.userIds.put(userId, User.REGULAR);
    }
    toEntity();
  }

  /**
   * Removes a user from the project
   *
   * @param userId id of the user to remove
   */
  public void removeUser(long userId) {
    this.userIds.remove(userId);
    toEntity();
  }

  /**
   * Adds a task to the project
   *
   * @param taskId id of the task to add
   */
  public void addTask(long taskId) {
    this.taskIds.add(taskId);
    toEntity();
  }

  /**
   * Removes a task from the project
   *
   * @param taskId id of the task to remove
   */
  public void removeTask(long taskId) {
    this.taskIds.remove(taskId);
    toEntity();
  }

  /** @return the string representation of this project. */
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
