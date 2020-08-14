/** class initiates objects to test mvp on test servers */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.user.*;
import com.rtb.projectmanagementtool.user.UserData.Skills;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/init-test-data")
public class InitTestData extends HttpServlet {
  // The users
  private Long Sandy;
  private Long Garry;
  private Long Pearl;
  private Long Patrick;

  // The projects
  private Long englishProject;
  private Long csProject;
  private Long historyProject;
  private Long spanishProject;

  // The tasks

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    System.out.println("Initiatializing test data");

    // Authentication goes here
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // remove all entities at start
    // this is mostly done so that the member variables of this
    // class are initialized when the objects are created
    removeAllEntities(datastore);

    // Create UserDataObjects
    createUsers(datastore);

    // Create ProjectDataObjects
    createProjects(datastore);

    // Create TaskDataObjects
    createTasks(datastore);

    // Load jsp for project page
    response.sendRedirect("/logout");
  }

  public void removeAllEntities(DatastoreService datastore) {
    removeAllEntities(datastore, new Query("User"));
    removeAllEntities(datastore, new Query("Project"));
    removeAllEntities(datastore, new Query("Task"));
  }

  public void removeAllEntities(DatastoreService datastore, Query query) {
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      datastore.delete(entity.getKey());
    }
  }

  private void createUsers(DatastoreService datastore) {
    System.out.println("Creating users");
    createUser(
        datastore,
        /*authId*/ "155150131811713716817",
        /*userName*/ "Pearl",
        /*userYear*/ 2023l,
        /*userMajors*/ new ArrayList(Arrays.asList("English", "Music")),
        /*skills*/ Skills.WRITING,
        /*userTotalCompTasks*/ 0l);

    createUser(
        datastore,
        /*authId*/ "125544156533921818610",
        /*userName*/ "Garry",
        /*userYear*/ 2023l,
        /*userMajors*/ new ArrayList(Arrays.asList("History")),
        /*skills*/ Skills.LEADERSHIP,
        /*userTotalCompTasks*/ 0l);

    createUser(
        datastore,
        /*authId*/ "121413416563184106108",
        /*userName*/ "Sandy",
        /*userYear*/ 2022l,
        /*userMajors*/ new ArrayList(Arrays.asList("Mechanical Engineering", "Economics")),
        /*skills*/ Skills.ORGANIZATION,
        /*userTotalCompTasks*/ 0l);

    createUser(
        datastore,
        /*authId*/ "110813220321112249531",
        /*userName*/ "Patrick",
        /*userYear*/ 2022l,
        /*userMajors*/ new ArrayList(Arrays.asList("Computer Science")),
        /*skills*/ Skills.OOP,
        /*userTotalCompTasks*/ 0l);
  }

  private void createUser(
      DatastoreService datastore,
      String authId,
      String userName,
      long userYear,
      ArrayList<String> userMajors,
      Skills skill,
      long userTotalCompTasks) {
    Entity entity = new Entity("User");
    entity.setProperty("userID", datastore.put(entity).getId());
    entity.setProperty("AuthID", authId);
    entity.setProperty("userName", userName);
    entity.setProperty("userYear", userYear);
    entity.setProperty("userMajors", userMajors);
    entity.setProperty("skills", skill.name());
    entity.setProperty("userTotalCompTasks", userTotalCompTasks);
    Long userId = datastore.put(entity).getId();
    switch (userName) {
      case "Sandy":
        this.Sandy = userId;
        break;
      case "Garry":
        this.Garry = userId;
        break;
      case "Pearl":
        this.Pearl = userId;
        break;
      case "Patrick":
        this.Patrick = userId;
        break;
    }
  }

  private void createProjects(DatastoreService datastore) {
    createProject(
        datastore,
        /*projectName*/ "English Project",
        /*projectCreator*/ this.Pearl,
        /*projectDescription*/ "Working on group research paper project",
        /*projectAdmins*/ new ArrayList(Arrays.asList(this.Garry)),
        /*projectMembers*/ new ArrayList(Collections.emptyList()));

    createProject(
        datastore,
        /*projectName*/ "Comp Sci Project",
        /*projectCreator*/ Patrick,
        /*projectDescription*/ "Our heap allocator CS project",
        /*projectAdmins*/ new ArrayList(Arrays.asList(Garry)),
        /*projectMembers*/ new ArrayList(Arrays.asList(Pearl)));

    createProject(
        datastore,
        /*projectName*/ "History Project",
        /*projectCreator*/ Garry,
        /*projectDescription*/ "Working on group research paper project on the civil war",
        /*projectAdmins*/ new ArrayList(Arrays.asList(Patrick, Pearl)),
        /*projectMembers*/ new ArrayList(Collections.emptyList()));

    createProject(
        datastore,
        /*projectName*/ "Spanish Presentation",
        /*projectCreator*/ Sandy,
        /*projectDescription*/ "Project for spanish presentation",
        /*projectAdmins*/ new ArrayList(Arrays.asList(Pearl)),
        /*projectMembers*/ new ArrayList(Arrays.asList(Garry)));
  }

  private void createProject(
      DatastoreService datastore,
      String projectName,
      Long creatorId,
      String projectDescription,
      ArrayList<Long> projectAdmins,
      ArrayList<Long> projectMembers) {
    Entity entity = new Entity("Project");
    entity.setProperty("name", projectName);
    entity.setProperty("creator", creatorId);
    entity.setProperty("description", projectDescription);
    entity.setProperty("admins", projectAdmins);
    entity.setProperty("members", projectMembers);
    Long projectId = datastore.put(entity).getId();

    switch (projectName) {
      case "English Project":
        this.englishProject = projectId;
        break;
      case "Comp Sci Project":
        this.csProject = projectId;
        break;
      case "History Project":
        this.historyProject = projectId;
        break;
      case "Spanish Presentation":
        this.spanishProject = projectId;
        break;
    }
  }

  private void createTasks(DatastoreService datastore) {
    // Use existing projectIds
    System.out.println("Creating tasks");
  }

  private void createTask(DatastoreService datastore) {
    // TO-DO: anyone implement
  }
}
