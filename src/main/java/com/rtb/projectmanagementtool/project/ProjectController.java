/**
 * ProjectController.java - this file implements ProjectController, which controls the logic of
 * working with Project objects.
 */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ProjectController {
  DatastoreService datastore;

  public ProjectController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** @return ArrayList containing all projects in database * Only for testing right now */
  public ArrayList<ProjectData> getAllProjects() {
    return getProjects(/* filter */ null);
  }

  /**
   * Gets all projects in database with a user
   *
   * @param userId the id of the user
   * @return ArrayList containing desired projects
   */
  public ArrayList<ProjectData> getProjectsWithUser(Long userId) {
    // Create the sub-filters
    ArrayList<FilterPredicate> subFilters = new ArrayList<FilterPredicate>();
    subFilters.add(new FilterPredicate("creator", FilterOperator.EQUAL, userId));
    subFilters.add(new FilterPredicate("admins", FilterOperator.IN, Arrays.asList(userId)));
    subFilters.add(new FilterPredicate("members", FilterOperator.IN, Arrays.asList(userId)));
    CompositeFilter filter =
        new Query.CompositeFilter(CompositeFilterOperator.OR, (Collection) subFilters);

    return getProjects(filter);
  }

  private ArrayList<ProjectData> getProjects(CompositeFilter filter) {
    // Only retrieve filtered projects
    Query query = new Query("Project");
    if (filter != null) {
      query.setFilter(filter);
    }

    ArrayList<ProjectData> projectContainer = new ArrayList<ProjectData>();

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
