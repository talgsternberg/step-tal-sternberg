/**
 * ProjectController.java - this file implements ProjectController, which controls the logic of
 * working with Project objects.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.HashSet;

public class ProjectController {
  private DatastoreService datastore;
  private HashSet<ProjectData> projects;

  /** Class constructor */
  public ProjectController() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    projects = new HashSet<ProjectData>();
    loadProjects();
  }

  /** Initializes the projects HashSet by retrieving all datastore entities of type "Project" */
  private void loadProjects() {
    PreparedQuery results = datastore.prepare(new Query("Project"));

    for (Entity entity : results.asIterable()) {
      ProjectData project = new ProjectData(entity);
      projects.add(project);
    }
  }

  /**
   * Creates a new project First creates the Project entity and then creates the project object with
   * the projectId returned by createProjectEntity()
   *
   * @param name the name of the project
   * @param description the description of the project
   */
  public void createProject(String name, String description, long creatorId) {
    ProjectData project = new ProjectData(name, description, creatorId);
    projects.add(project);
  }

  /**
   * Removes a Project from web application; calls removeProjectEntity to remove the project from
   * datastore. If successful, removes the project from HashSet
   *
   * @param projectId the id of the project to remove
   * @return true if the removal is successful
   */
  public boolean removeProject(long projectId) {
    if (removeProjectEntity(projectId)) {
      projects.remove(getProject(projectId));
      return true;
    }
    return false;
  }

  /**
   * Removes a Project Entity from datastore (?) should this be done in the Data layer?
   *
   * @param projectId the id of the project to remove
   * @return true if the removal is successful
   */
  private boolean removeProjectEntity(long projectId) {
    try {
      Key projectEntityKey = KeyFactory.createKey("Project", projectId);
      datastore.delete(projectEntityKey);
      return true;
    } catch (Exception exception) {
      System.out.println(
          "Error: Project with projectId " + projectId + " does not exist. Key invalid");
      return false;
    }
  }

  /**
   * Retrieves the Project object with the matching projectId from HashSet, or nothing if the
   * project is not found
   *
   * @param projectId the id of the project to retrieve
   * @return the project if found, or null if not found
   */
  public ProjectData getProject(long projectId) {
    for (ProjectData project : projects) {
      if (project.getId() == projectId) {
        return project;
      }
    }
    return null;
  }

  public HashSet<ProjectData> getProjects() {
    return this.projects;
  }
}
