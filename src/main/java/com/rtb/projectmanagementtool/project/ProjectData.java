/**
 * ProjectData.java - this file implements the Project class, which contains details about a
 * particular project, including the members and tasks within it.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.Entity;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ProjectData {
  private final String PROPERTY_NAME = "name";
  private final String PROPERTY_CREATOR = "creator";
  private final String PROPERTY_DESCRIPTION = "description";
  private final String PROPERTY_TASKS = "tasks";
  private final String PROPERTY_ADMINS = "admins";
  private final String PROPERTY_MEMBERS = "members";

  private long id;
  private long creatorId;
  private String name;
  private String description;
  private HashSet<Long> tasks;
  private HashMap<UserProjectRole, HashSet<Long>> users; // hashmap maps user roles to ids
  // ex. {UserProjectRole.ADMIN={0,1,2}, UserProjectRole.MEMBER={3,4}, ...

  /**
   * Class constructor with the minimum requirements for creating a project.
   *
   * @param name the name of the project
   * @param description the description of the project
   * @param creatorId the id of the user who created the project
   */
  public ProjectData(String name, String description, long creatorId) {
    this.name = name;
    this.creatorId = creatorId;
    this.description = description;
    this.tasks = new HashSet<Long>();
    this.users = new HashMap<UserProjectRole, HashSet<Long>>();

    // add roles to the HashMap with empty id sets
    this.users.put(UserProjectRole.ADMIN, new HashSet<Long>());
    this.users.put(UserProjectRole.MEMBER, new HashSet<Long>());
  }

  /**
   * Class constructor to create Project from existing datastore entity
   *
   * @param entity the datastore entity to parse object from
   */
  public ProjectData(Entity entity) {
    this.id = (Long) entity.getKey().getId();
    this.creatorId = (Long) entity.getProperty(PROPERTY_CREATOR);
    this.name = (String) entity.getProperty(PROPERTY_NAME);
    this.description = (String) entity.getProperty(PROPERTY_DESCRIPTION);

    // Instantiate containers
    this.tasks = new HashSet<Long>();
    this.users = new HashMap<UserProjectRole, HashSet<Long>>();
    this.users.put(UserProjectRole.ADMIN, new HashSet<Long>());
    this.users.put(UserProjectRole.MEMBER, new HashSet<Long>());

    // Update containers with entity properties
    parseEntityHashSets(entity, PROPERTY_TASKS);
    parseEntityHashSets(entity, PROPERTY_ADMINS);
    parseEntityHashSets(entity, PROPERTY_MEMBERS);
  }

  /**
   * Used by ProjectData(Entity) to parse entity properties into respective HashSets of this class
   *
   * @param entity the entity
   * @param propertyName the name of entity property
   */
  private void parseEntityHashSets(Entity entity, String propertyName) {
    Object entityProperty = entity.getProperty(propertyName);
    if (entityProperty == null) {
      return;
    }

    Collection collection = (Collection) entityProperty;
    Iterator<Long> iterator = collection.iterator();

    while (iterator.hasNext()) {
      if (propertyName.equals(PROPERTY_ADMINS)) {
        addUser(UserProjectRole.ADMIN, iterator.next());
      } else if (propertyName.equals(PROPERTY_MEMBERS)) {
        addUser(UserProjectRole.MEMBER, iterator.next());
      } else if (propertyName.equals(PROPERTY_TASKS)) {
        addTask(iterator.next());
      }
    }
  }

  /** @return the entity representation of this class */
  public Entity toEntity() {
    Entity entity = new Entity("Project");
    entity.setProperty(PROPERTY_NAME, this.name);
    entity.setProperty(PROPERTY_CREATOR, this.creatorId);
    entity.setProperty(PROPERTY_DESCRIPTION, this.description);
    entity.setProperty(PROPERTY_TASKS, this.tasks);
    entity.setProperty(PROPERTY_ADMINS, this.users.get(UserProjectRole.ADMIN));
    entity.setProperty(PROPERTY_MEMBERS, this.users.get(UserProjectRole.MEMBER));
    return entity;
  }

  /** @return project id */
  public long getId() {
    return this.id;
  }

  /** @return id of project creator */
  public long getCreatorId() {
    return this.creatorId;
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
  public HashMap<UserProjectRole, HashSet<Long>> getUsers() {
    return this.users;
  }

  /** @return task ids */
  public HashSet<Long> getTasks() {
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
  public boolean addUser(UserProjectRole userRole, long userId) {
    if (getUserRole(userId) == null) { // if user doesn't exist
      this.users.get(userRole).add(userId);
      return true;
    }
    return false;
  }

  /**
   * Removes a user from the project
   *
   * @param userId id of the user to remove
   * @return true if operation is successful
   */
  public boolean removeUser(long userId) {
    UserProjectRole userRole = getUserRole(userId);
    if (userRole != null) {
      users.get(userRole).remove(userId);
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
   * Check if user is in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public UserProjectRole getUserRole(long userId) {
    if (this.creatorId == userId) {
      return UserProjectRole.CREATOR;
    } else if (users.get(UserProjectRole.ADMIN).contains(userId)) {
      return UserProjectRole.ADMIN;
    } else if (users.get(UserProjectRole.MEMBER).contains(userId)) {
      return UserProjectRole.MEMBER;
    } else {
      return null;
    }
  }

  /**
   * Check if user is in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public boolean hasUser(long userId) {
    return getUserRole(userId) != null;
  }

  /**
   * Check if user exists as an admin in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public boolean hasAdmin(long userId) {
    return users.get(UserProjectRole.ADMIN).contains(userId);
  }

  /**
   * Check if this is the project creator
   *
   * @param userId the userId
   * @return true if the user is the creator
   */
  public boolean isCreator(long userId) {
    return this.creatorId == userId;
  }

  public static boolean equals(ProjectData a, ProjectData b) {
    return a.getId() == b.getId()
        && a.getCreatorId() == b.getCreatorId()
        && a.getDescription().equals(b.getDescription())
        && a.getTasks().equals(b.getTasks())
        && a.getUsers().equals(b.getUsers());
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof ProjectData && equals(this, (ProjectData) other);
  }

  /** @return the string representation of this class. */
  @Override
  public String toString() {
    String returnString = "{\n";
    returnString += "Project id: " + this.id + "\n";
    returnString += "Project creator's id: " + this.creatorId + "\n";
    returnString += "Project Name: " + this.name + "\n";
    returnString += "Project Description: " + this.description + "\n";
    returnString += "Project Users: " + this.users.toString() + "\n";
    returnString += "Tasks: " + this.tasks.toString() + "\n}";
    return returnString;
  }
}
