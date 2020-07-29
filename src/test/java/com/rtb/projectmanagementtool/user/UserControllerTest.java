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
  private HashSet<UserData> testUsers;
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
  public void testAddUsers() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    UserController ctl = new UserController(testUsers);
    ArrayList<String> user1Majors = new ArrayList<String>();
    ArrayList<String> user2Majors = new ArrayList<String>();
    user1Majors.add("Biology");
    user2Majors.add("English");
    user2Majors.add("Religion");
    
    // Add 2 initially
    ctl.addUser(ds, new UserData(1l, 2l, "Sarah", 2023, user1Majors, 5));
    ctl.addUser(ds, new UserData(2l, 3l, "Joe", 2022, user2Majors, 3));
    
    // Should have 2 now
    HashSet<UserData> users = ctl.getEveryUser(ds);
    Assert.assertEquals(users.size(), 2);
  }
} 
