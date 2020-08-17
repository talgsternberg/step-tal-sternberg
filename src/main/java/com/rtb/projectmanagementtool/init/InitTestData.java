/** class initiates objects to test mvp on test servers */
package com.rtb.projectmanagementtool.project;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.task.TaskData.Status;
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
  private Long task1;
  private Long task2;

  private ArrayList<Key> entityKeys; // arrays to store entity keys for batch delete

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    deleteEntitiesFromPreviousRun(datastore);

    entityKeys = new ArrayList<Key>();

    // Create UserDataObjects
    creatUsers(datastore);

    // Create ProjectDataObjects
    creatProjects(datastore);

    // Create TaskDataObjects
    creatTasks(datastore);

    addKeys(datastore);
    // Load jsp for project page
    AuthOps authOps = new AuthOps(datastore);
    authOps.setLoggedInCookie(request, response, -1);
    response.sendRedirect("/home");
  }

  private void deleteEntitiesFromPreviousRun(DatastoreService datastore) {
    // delete all entities from previous run
    Entity keysEntity = datastore.prepare(new Query("Keys")).asSingleEntity();
    if (keysEntity != null) {
      entityKeys = (ArrayList<Key>) keysEntity.getProperty("entityKeys");
      datastore.delete(entityKeys);
      datastore.delete(keysEntity.getKey()); // also delete the keysEntity entity
    }
  }

  private void addKeys(DatastoreService datastore) {
    // add keys to datastore
    Entity entity = new Entity("Keys");
    entity.setProperty("entityKeys", entityKeys);
    datastore.put(entity);
  }

  private void creatUsers(DatastoreService datastore) {
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
    Key entityKey = datastore.put(entity);
    Long userId = entityKey.getId();
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

    entityKeys.add(entityKey);
  }

  private void creatProjects(DatastoreService datastore) {
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

    Key entityKey = datastore.put(entity);
    Long projectId = entityKey.getId();
    entityKeys.add(entityKey);

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

  private void creatTasks(DatastoreService datastore) {
    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ englishProject,
        /*name*/ "Write paragraph two",
        /*description*/ "description for 'write paragraph two' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Pearl)));
    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ englishProject,
        /*name*/ "Write paragraph four",
        /*description*/ "description for 'Write paragraph four' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Garry)));
    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ englishProject,
        /*name*/ "Review paragraph three",
        /*description*/ "description for 'Review paragraph three' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Garry)));

    createTask(
        datastore,
        /*parentTaskId*/ task1,
        /*projectId*/ englishProject,
        /*name*/ "Include annotations in paragraph",
        /*description*/ "description for 'Include annotations in paragraph'",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Pearl)));

    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ csProject,
        /*name*/ "Fix bugs",
        /*description*/ "description for 'fix bugs' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Garry, Patrick)));

    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ csProject,
        /*name*/ "Write test class",
        /*description*/ "description for 'Write test class' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Garry)));

    createTask(
        datastore,
        /*parentTaskId*/ task2,
        /*projectId*/ csProject,
        /*name*/ "Figure out what ERROR 551 means",
        /*description*/ "description for 'Figure out what ERROR 551 means' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Patrick)));

    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ historyProject,
        /*name*/ "Gather resources",
        /*description*/ "description for 'Gather resources' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Pearl)));

    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ historyProject,
        /*name*/ "Meet with professor",
        /*description*/ "description for 'Meet with professor' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Patrick)));

    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ historyProject,
        /*name*/ "Finish reading the book",
        /*description*/ "description for 'Finish reading the book' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Patrick, Pearl, Garry)));

    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ spanishProject,
        /*name*/ "Create slide #5",
        /*description*/ "description for 'Create slide #5' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Pearl)));

    createTask(
        datastore,
        /*parentTaskId*/ 0l,
        /*projectId*/ spanishProject,
        /*name*/ "Create slide #6",
        /*description*/ "description for 'Create slide #6' task",
        /*status*/ Status.INCOMPLETE,
        /*users*/ new ArrayList(Arrays.asList(Sandy)));
  }

  private void createTask(
      DatastoreService datastore,
      long parentTaskId,
      long projectId,
      String name,
      String description,
      Status status,
      ArrayList<Long> users) {
    Entity entity = new Entity("Task");
    entity.setProperty("parentTaskID", parentTaskId);
    entity.setProperty("projectID", projectId);
    entity.setProperty("name", name);
    entity.setProperty("description", description);
    entity.setProperty("status", status.name());
    entity.setProperty("users", users);

    Key entityKey = datastore.put(entity);
    Long entityId = entityKey.getId();
    entityKeys.add(entityKey);

    switch (name) {
      case "Write paragraph two":
        this.task1 = entityId;
        break;
      case "Fix bugs":
        this.task2 = entityId;
    }
  }
}
