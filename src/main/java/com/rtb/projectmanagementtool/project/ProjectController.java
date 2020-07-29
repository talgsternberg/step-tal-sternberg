/**
 * ProjectController.java - this file implements ProjectController, which controls the logic of
 * working with Project objects.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class ProjectController {
  private final String PROPERTY_USER_IDS = "userIds";

  DatastoreService datastore;

  public ProjectController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /**
   * Gets all the projects in datastore that all specified users are in
   *
   * @param userIds the collection of userIds whose projects are to be retrieved
   * @return HashSet containing desired projects
   */
  public HashSet<ProjectData> getProjects(Collection userIds) {

    HashSet<ProjectData> projectContainer = new HashSet<ProjectData>();

    PreparedQuery results = datastore.prepare(new Query("Project"));
    for (Entity entity : results.asIterable()) {
      if (entityContainsUser(entity, userIds)) {
        ProjectData project = new ProjectData(entity);
        projectContainer.add(project);
      }
    }

    return projectContainer;
  }

  /**
   * Checks if the Project entity has at least one userId contained in userIds
   *
   * @param entity the project entity
   * @param userIds the collection of userIds
   * @return true if userIds param and entity's userIds property share at least one user
   */
  private boolean entityContainsUser(Entity entity, Collection userIds) {
    // userIds is empty, so get all projects in database
    if (userIds.size() == 0) {
      return true;
    }

    ArrayList<Long> entityUserIds = new ArrayList<Long>();
    EmbeddedEntity ee = (EmbeddedEntity) entity.getProperty(PROPERTY_USER_IDS);
    if (ee != null) {
      for (String key : ee.getProperties().keySet()) {
        entityUserIds.add(Long.parseLong(key));
      }
    }

    // Collections.disjoint() returns true if the two specified
    // collections have no elements in common.
    return !Collections.disjoint(entityUserIds, userIds);
  }

  /**
   * Saves a project to datastore.
   *
   * @param project the project to save
   */
  public void saveProject(ProjectData project) {
    Entity projectEntity = project.toEntity();
    datastore.put(projectEntity);
  }

  /**
   * Removes a Project from database.
   *
   * @param project the project to remove
   */
  public void removeProject(ProjectData project) {
    removeProject(project.getId());
  }

  /**
   * Removes a Project from database given its projectIdd
   *
   * @param projectId the id of the project to remove
   */
  public void removeProject(long projectId) {
    Key projectEntityKey = KeyFactory.createKey("Project", projectId);
    datastore.delete(projectEntityKey);
  }
}
