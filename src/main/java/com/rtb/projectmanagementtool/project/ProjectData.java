/**
 * ProjectData.java - this file implements the Project class, which contains details about a
 * particular project, including the members and tasks within it.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class ProjectData {
  private long id;
  private long creatorId;
  private String name;
  private String description;
  private HashSet<Long> members;
  private HashSet<Long> admins;

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
    this.members = new HashSet<Long>();
    this.admins = new HashSet<Long>();
  }

  /**
   * Class constructor to create Project from existing datastore entity
   *
   * @param entity the datastore entity to parse object from
   */
  public ProjectData(Entity entity) {
    this.id = (Long) entity.getKey().getId();
    this.creatorId = (Long) entity.getProperty("creator");
    this.name = (String) entity.getProperty("name");
    this.description = (String) entity.getProperty("description");

    // Members
    Collection entityProperty = (Collection) entity.getProperty("members");
    if (entityProperty != null) {
      this.members = new HashSet<Long>((ArrayList<Long>) entityProperty);
    } else {
      this.members = new HashSet<Long>();
    }

    // Admins
    entityProperty = (Collection) entity.getProperty("admins");
    if (entityProperty == null) {
      this.admins = new HashSet<Long>();
    } else {
      this.admins = new HashSet<Long>((ArrayList<Long>) entityProperty);
    }
  }

  /** @return the entity representation of this class */
  public Entity toEntity() {
    Entity entity = new Entity("Project");
    entity.setProperty("name", this.name);
    entity.setProperty("creator", this.creatorId);
    entity.setProperty("description", this.description);
    entity.setProperty("admins", this.admins);
    entity.setProperty("members", this.members);
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

  /** @return admin users */
  public HashSet<Long> getAdmins() {
    return this.admins;
  }

  /** @return member users */
  public HashSet<Long> getMembers() {
    return this.members;
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
   * Add an admin user to the project
   *
   * @param userId id of the user to add
   */
  public boolean addAdminUser(long userId) {
    return this.admins.add(userId);
  }

  /**
   * Add a member user to the project
   *
   * @param userId id of the user to add
   */
  public boolean addMemberUser(long userId) {
    return this.members.add(userId);
  }

  /**
   * Removes an admin from the project
   *
   * @param userId id of the user to remove
   * @return true if operation is successful
   */
  public boolean removeAdmin(long userId) {
    return this.admins.remove(userId);
  }

  /**
   * Removes a user from the project
   *
   * @param userId id of the user to remove
   * @return true if operation is successful
   */
  public boolean removeMember(long userId) {
    removeAdmin(userId);
    return this.members.remove(userId);
  }

  /**
   * Check if user is in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public boolean hasUser(long userId) {
    return this.creatorId == userId
        || this.admins.contains(userId)
        || this.members.contains(userId);
  }

  /**
   * Check if user exists as an admin in the project
   *
   * @param userId the userId
   * @return true if the user is in project
   */
  public boolean hasAdmin(long userId) {
    return this.admins.contains(userId);
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
        && a.getAdmins().equals(b.getAdmins())
        && a.getMembers().equals(b.getMembers());
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof ProjectData && equals(this, (ProjectData) other);
  }

  /** @return the string representation of this class in JSON format. */
  @Override
  public String toString() {
    String json = "{";
    json += "\"name\":";
    json += "\"" + this.name + "\",";
    json += "\"description\":";
    json += "\"" + this.description + "\",";
    json += "\"creatorId\":";
    json += "\"" + this.creatorId + "\",";
    json += "\"members\":";
    json += "\"" + this.members.toString() + "\",";
    json += "\"admins\":";
    json += "\"" + this.admins.toString() + "\"";
    json += "}";
    return json;
  }
}
