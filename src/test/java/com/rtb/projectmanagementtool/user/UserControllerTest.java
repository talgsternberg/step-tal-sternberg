package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.user.UserData.Skills;
import java.util.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserControllerTest {

  // User 1
  private static final long userID1 = 1l;
  private static final String AuthID1 = "abc";
  private static final String name1 = "Anna";
  private static final long year1 = 2023;
  private static final ArrayList<String> majors1 = new ArrayList<>(Arrays.asList("Biology", "Gov"));
  private static final Skills skills1 = Skills.ORGANIZATION;
  private static final long totalCompTasks1 = 3;

  // User 2
  private static final long userID2 = 2l;
  private static final String AuthID2 = "def";
  private static final String name2 = "Tal";
  private static final long year2 = 2023;
  private static final ArrayList<String> majors2 =
      new ArrayList<>(Arrays.asList("Comp Sci", "Earth Sciences"));
  private static final Skills skills2 = Skills.LEADERSHIP;
  private static final long totalCompTasks2 = 0;

  // User 3
  private static final long userID3 = 3l;
  private static final String AuthID3 = "ghi";
  private static final String name3 = "Eddie";
  private static final long year3 = 2022;
  private static final ArrayList<String> majors3 = new ArrayList<>(Arrays.asList("Film", "Econ"));
  private static final Skills skills3 = Skills.WRITING;
  private static final long totalCompTasks3 = 10;
  private HashSet<UserData> testUsers;

  // UserData
  private static final UserData user1 =
      new UserData(userID1, AuthID1, /*email*/ "", name1, year1, majors1, skills1, totalCompTasks1);
  private static final UserData user2 =
      new UserData(userID2, AuthID2, /*email*/ "", name2, year2, majors2, skills2, totalCompTasks2);
  private static final UserData user3 =
      new UserData(userID3, AuthID3, /*email*/ "", name3, year3, majors3, skills3, totalCompTasks3);

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testGetUserByID() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Add user entities
    ds.put(user1.toEntity());
    ds.put(user2.toEntity());
    ds.put(user3.toEntity());

    // Get user with UserController
    UserController userController = new UserController(ds);
    UserData getUser = userController.getUserByID(userID1);

    // Assert task retrieved is correct
    Assert.assertEquals("getUser", user1, getUser);
  }

  @Test
  public void testAddUsers() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Add user entities with UserController
    UserController userController = new UserController(ds);
    userController.addUser(user1);
    userController.addUser(user2);

    // Assert an entity was added
    ArrayList<UserData> users = userController.getEveryUser();
    Assert.assertEquals(users.size(), 2);
  }

  @Test
  public void testGetUserIDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Add user entities with UserController
    UserController userController = new UserController(ds);
    userController.addUser(user1);
    userController.addUser(user2);

    ArrayList<Long> generatedIDList = new ArrayList<>();
    generatedIDList.add((Long) userID1);
    generatedIDList.add((Long) userID2);

    ArrayList<Long> userIDList = userController.getUserIDs();
    Assert.assertEquals(userIDList, generatedIDList);
  }
}
