/**
 * ProjectController.java - this file implements ProjectController, which controls the logic of
 * working with Project objects.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;

public class ProjectController {
  private final String PROPERTY_USER_IDS = "userIds";

  DatastoreService datastore;

  public ProjectController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /**
   * Gets all the projects in datastore that all users are in
   *
   * @param userIds the collection of userIds whose projects are to be retrieved
   * @return HashSet containing desired projects
   */
  public ArrayList<ProjectData> getAllProjects() {
    return getProjects(0l, false);
  }

  /**
   * Gets all the projects in datastore that the specified user is in
   *
   * @param userId the id of user whose projects to retrieve
   * @return HashSet containing desired projects
   */
  public ArrayList<ProjectData> getUserProjects(long userId) {
    return getProjects(userId, false);
  }

  /**
   * Gets all the projects in datastore created by user
   *
   * @param userId the id of owner whose projects to retrieve
   * @return HashSet containing desired projects
   */
  public ArrayList<ProjectData> getCreatorProjects(long userId) {
    return getProjects(userId, true);
  }

  /**
   * Helper function for getAllProjects(), getUserProjects(), and getCreatorProjects()
   *
   * @param userId the id of user whose projects to retrieve
   * @param getCreatorProjects boolean representing the user's role in project (owner or otherwise)
   * @return HashSet containing desired projects
   */
  private ArrayList<ProjectData> getProjects(long userId, boolean getCreatorProjects) {
    ArrayList<ProjectData> projectContainer = new ArrayList<ProjectData>();

    PreparedQuery results = datastore.prepare(new Query("Project"));
    for (Entity entity : results.asIterable()) {
      ProjectData project = new ProjectData(entity);

      if (
      // query wants every user
      userId == 0l
          // or query wants projects by a creator
          || getCreatorProjects && project.isCreator(userId)
          // or query wants projects with a user
          || !getCreatorProjects && project.hasUser(userId)) {
        projectContainer.add(project);
      }
    }

    return projectContainer;
  }

  /**
   * Adds a Project to database.
   *
   * @param project the project to add
   */
  public void addProject(ProjectData project) {
    datastore.put(project.toEntity());
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
