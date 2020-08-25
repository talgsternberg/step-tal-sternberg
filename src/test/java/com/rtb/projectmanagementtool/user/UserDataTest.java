package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.rtb.projectmanagementtool.user.UserData.Skills;
import java.util.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserDataTest {
  // User 1
  private static final long userID1 = 1l;
  private static final String AuthID1 = "abc";
  private static final String name1 = "Anna";
  private static final long year1 = 2023;
  private static final ArrayList<String> majors1 = new ArrayList<>(Arrays.asList("Biology", "Gov"));
  private static final Skills skills1 = Skills.ORGANIZATION;
  private static final long total1 = 3;

  // User 2
  private static final long userID2 = 2l;
  private static final String AuthID2 = "def";
  private static final String name2 = "Tal";
  private static final long year2 = 2023;
  private static final ArrayList<String> majors2 =
      new ArrayList<>(Arrays.asList("Comp Sci", "Earth Sciences"));
  private static final Skills skills2 = Skills.LEADERSHIP;
  private static final long total2 = 0;

  // User 3
  private static final long userID3 = 3l;
  private static final String AuthID3 = "ghi";
  private static final String name3 = "Eddie";
  private static final long year3 = 2022;
  private static final ArrayList<String> majors3 = new ArrayList<>(Arrays.asList("Film", "Econ"));
  private static final Skills skills3 = Skills.WRITING;
  private static final long total3 = 10;
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
  public void testCreateFromConstructor() {
    // create user by passing paramets to constructor
    UserData user1 =
        new UserData(userID1, AuthID1, /*email*/ "", name1, year1, majors1, skills1, total1);

    // ensure values are stored correct
    Assert.assertEquals("userID", userID1, user1.getUserID());
    Assert.assertEquals("AuthID", AuthID1, user1.getAuthID());
    Assert.assertEquals("userName", name1, user1.getUserName());
    Assert.assertEquals("userYear", year1, user1.getUserYear());
    Assert.assertEquals("userMajors", majors1, user1.getUserMajors());
    Assert.assertEquals("skills", skills1, user1.getUserSkills());
    Assert.assertEquals("userTotalCompTasks", total1, user1.getUserTotal());
  }

  @Test
  public void testCreateFromEntity() {
    // create entity
    Entity entity = new Entity("UserData", userID2);
    entity.setProperty("AuthID", AuthID2);
    entity.setProperty("userName", name2);
    entity.setProperty("userYear", year2);
    entity.setProperty("userMajors", majors2);
    entity.setProperty("skills", skills2.toString());
    entity.setProperty("userTotalCompTasks", total2);

    // create user by passing entity
    UserData user2 = new UserData(entity);

    // ensure values are stored correct
    Assert.assertEquals("userID", userID2, user2.getUserID());
    Assert.assertEquals("AuthID", AuthID2, user2.getAuthID());
    Assert.assertEquals("userName", name2, user2.getUserName());
    Assert.assertEquals("userYear", year2, user2.getUserYear());
    Assert.assertEquals("userMajors", majors2, user2.getUserMajors());
    Assert.assertEquals("skills", skills2, user2.getUserSkills());
    Assert.assertEquals("userTotalCompTasks", total2, user2.getUserTotal());
  }

  @Test
  public void testCreateEntityFromUser() {
    // Build UserData object
    UserData user3 =
        new UserData(userID3, AuthID3, /*email*/ "", name3, year3, majors3, skills3, total3);

    // Create user entity
    Entity entity2 = user3.toEntity();

    // Get user entity attributes
    long entityUserID = (long) entity2.getKey().getId();
    String entityAuthID = (String) entity2.getProperty("AuthID");
    String entityName = (String) entity2.getProperty("userName");
    long entityYear = (long) entity2.getProperty("userYear");
    ArrayList<String> entityMajors = (ArrayList<String>) entity2.getProperty("userMajors");
    Skills entitySkills = Skills.valueOf((String) entity2.getProperty("skills"));
    long entityTotal = (long) entity2.getProperty("userTotalCompTasks");

    // Assert user entity attributes equal UserData attributes
    Assert.assertEquals("userID", userID3, entityUserID);
    Assert.assertEquals("AuthID", AuthID3, entityAuthID);
    Assert.assertEquals("userName", name3, entityName);
    Assert.assertEquals("userName", year3, entityYear);
    Assert.assertEquals("userMajors", majors3, entityMajors);
    Assert.assertEquals("skills", skills3, entitySkills);
    Assert.assertEquals("userTotalCompTasks", total3, entityTotal);
  }
}
