/**
 * ProjectController.java - this file implements ProjectController, which controls the logic of
 * working with Project objects.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.ArrayList;

public class ProjectController {
  private final String PROPERTY_USER_IDS = "userIds";

  DatastoreService datastore;

  public ProjectController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /**
   * Gets all projects in database that has users
   *
   * @param queryList list of users to fetch. ex string: CREATOR-12345 Can be created by calling
   *     ProjectData's static method, createUserString()
   * @return ArrayList containing desired projects
   */
  public ArrayList<ProjectData> getProjects(ArrayList<String> queryList) {
    ArrayList<ProjectData> projectContainer = new ArrayList<ProjectData>();

    Query query = new Query("Project");
    if (queryList != null) {
      query.addFilter("users", FilterOperator.IN, queryList);
    }

    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      ProjectData project = new ProjectData(entity);
      projectContainer.add(project);
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
