/**
 * ProjectData.java - this file implements the Project class, which contains details about a
 * particular project, including the members and tasks within it.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

enum User {
  CREATOR, // has admin capabilities and more, like deleting the project and add & remove admins
  ADMIN, // can add users to the project/perform other administrative tasks
  REGULAR;
}

public class ProjectData {
  private final String PROPERTY_NAME = "name";
  private final String PROPERTY_DESCRIPTION = "description";
  private final String PROPERTY_USER_IDS = "userIds";
  private final String PROPERTY_TASK_IDS = "taskIds";

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
    this.name = name;
    this.description = description;
    this.taskIds = new HashSet<Long>();

    this.userIds = new HashMap<Long, User>();
    this.userIds.put(creatorId, User.CREATOR);
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

    this.userIds = new HashMap<Long, User>();
    // can store map in datastore entities using EmbeddedEntity
    EmbeddedEntity ee = (EmbeddedEntity) entity.getProperty(PROPERTY_USER_IDS);
    if (ee != null) {
      for (String key : ee.getProperties().keySet()) {
        this.userIds.put(Long.parseLong(key), User.valueOf((String) ee.getProperty(key)));
      }
    }
  }

  /**
  * @return the entity representation of this class 
  */
  public Entity toEntity() {
    Entity entity = new Entity("Project");
    entity.setProperty(PROPERTY_NAME, this.name);
    entity.setProperty(PROPERTY_DESCRIPTION, this.description);
    entity.setProperty(PROPERTY_TASK_IDS, this.taskIds);

    EmbeddedEntity userIdsEntity = new EmbeddedEntity();
    for (Long key : this.userIds.keySet()) {
      userIdsEntity.setProperty(key.toString(), this.userIds.get(key).name());
    }
    entity.setProperty(PROPERTY_USER_IDS, userIdsEntity);

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

  /** @return returns user ids */
  public HashMap<Long, User> getUsers() {
    return this.userIds;
  }

  /** @return task ids */
  public HashSet<Long> getTaskIds() {
    return this.taskIds;
  }

  /**
   * Changes the id of this project
   *
   * @param id the new id of the project
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Changes the name of this project
   *
   * @param name the new name of the project
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Changes the description of this project
   *
   * @param description the new description of the project
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Adds an admin user to project
   *
   * @param userId id of the user to add
   */
  public void addAdminUser(long userId) {
    // remove user if they're already in project
    if (this.userIds.keySet().contains(userId)) {
      removeUser(userId);
    }
    this.userIds.put(userId, User.ADMIN);
  }

  /**
   * Adds a regular user to the project
   *
   * @param userId id of the user to add
   */
  public void addRegularUser(long userId) {
    // remove user if they're already in project
    if (this.userIds.keySet().contains(userId)) {
      removeUser(userId);
    }
    this.userIds.put(userId, User.REGULAR);
  }

  /**
   * Removes a user from the project
   *
   * @param userId id of the user to remove
   */
  public void removeUser(long userId) {
    this.userIds.remove(userId);
  }

  /**
   * Adds a task to the project
   *
   * @param taskId id of the task to add
   */
  public void addTask(long taskId) {
    this.taskIds.add(taskId);
  }

  /**
   * Removes a task from the project
   *
   * @param taskId id of the task to remove
   */
  public void removeTask(long taskId) {
    this.taskIds.remove(taskId);
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
