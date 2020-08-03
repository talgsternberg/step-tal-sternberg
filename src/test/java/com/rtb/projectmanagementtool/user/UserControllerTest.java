package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserControllerTest {

  // User 1
  private static final long userID1 = 1l;
  private static final long AuthID1 = 1l;
  private static final String name1 = "Anna";
  private static final long year1 = 2023;
  private static final ArrayList<String> majors1 = new ArrayList<>(Arrays.asList("Biology", "Gov"));
  private static final long total1 = 3;

  // User 2
  private static final long userID2 = 2l;
  private static final long AuthID2 = 2l;
  private static final String name2 = "Tal";
  private static final long year2 = 2023;
  private static final ArrayList<String> majors2 =
      new ArrayList<>(Arrays.asList("Comp Sci", "Earth Sciences"));
  private static final long total2 = 0;

  // User 3
  private static final long userID3 = 3l;
  private static final long AuthID3 = 3l;
  private static final String name3 = "Eddie";
  private static final long year3 = 2022;
  private static final ArrayList<String> majors3 = new ArrayList<>(Arrays.asList("Film", "Econ"));
  private static final long total3 = 10;
  private HashSet<UserData> testUsers;

  // UserData
  private static final UserData user1 =
      new UserData(userID1, AuthID1, name1, year1, majors1, total1);
  private static final UserData user2 =
      new UserData(userID2, AuthID2, name2, year2, majors2, total2);
  private static final UserData user3 =
      new UserData(userID3, AuthID3, name3, year3, majors3, total3);

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

    System.out.println("in test: getUser");
    System.out.println(getUser);
    System.out.println("in test: user1");
    System.out.println(user1);

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
}
