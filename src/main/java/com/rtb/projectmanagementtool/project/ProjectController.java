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

  /** @return ArrayList containing all projects in database * Only for testing right now */
  public ArrayList<ProjectData> getAllProjects() {
    return getProjectsByQuery(/* queryList */ null);
  }

  /**
   * Gets all projects in database with a user
   *
   * @param userId the id of the user
   * @return ArrayList containing desired projects
   */
  public ArrayList<ProjectData> getProjectsWithUser(Long userId) {
    ArrayList<String> queryList = new ArrayList<String>();
    queryList.add(ProjectData.createUserString(userId, UserProjectRole.CREATOR));
    queryList.add(ProjectData.createUserString(userId, UserProjectRole.ADMIN));
    queryList.add(ProjectData.createUserString(userId, UserProjectRole.MEMBER));
    return getProjectsByQuery(queryList);
  }

  /**
   * Gets all projects in database that has a specific admin
   *
   * @param userId the id of the user
   * @return ArrayList containing desired projects
   */
  public ArrayList<ProjectData> getProjectsWithAdmin(Long userId) {
    ArrayList<String> queryList = new ArrayList<String>();
    queryList.add(ProjectData.createUserString(userId, UserProjectRole.ADMIN));
    return getProjectsByQuery(queryList);
  }

  /**
   * Gets all projects in database created by specific user
   *
   * @param userId the id of the user
   * @return ArrayList containing desired projects
   */
  public ArrayList<ProjectData> getProjectsByCreator(Long userId) {
    ArrayList<String> queryList = new ArrayList<String>();
    queryList.add(ProjectData.createUserString(userId, UserProjectRole.CREATOR));
    return getProjectsByQuery(queryList);
  }

  /**
   * Gets all projects in database that match query list
   *
   * @param queryList list of users to fetch. ex string: CREATOR-12345
   * @return ArrayList containing desired projects
   */
  public ArrayList<ProjectData> getProjectsByQuery(ArrayList<String> queryList) {
    ArrayList<ProjectData> projectContainer = new ArrayList<ProjectData>();

    // Only retrieve filtered projects
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
   * Removes a Project from database given its projectId
   *
   * @param projectId the id of the project to remove
   */
  public void removeProject(long projectId) {
    Key projectEntityKey = KeyFactory.createKey("Project", projectId);
    datastore.delete(projectEntityKey);
  }
}
