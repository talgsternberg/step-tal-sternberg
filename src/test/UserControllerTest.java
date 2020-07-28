package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.HashSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserControllerTest {
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
        UserController ctl = new UserController();
        HashSet<String> user1Majors = new HashSet();
        HashSet<String> user2Majors = newHashSet();
        user1Majors.add("Biology");
        user2Majors.add("English");
        user2Majors.add("Religion");
        // Add 2 initially
        ctl.addUserData(
            ds, /*userID=*/ 1l, /*AuthID=*/ 2l,
            /*userName=*/ "Sarah", /*userYear=*/ 2022,
            /*userMajors=*/ user1Majors, /*skills=*/ Skills.LEADERSHIP,
            /*userTotalCompTasks=*/ 3);
        ctl.addUserData(
            ds, /*userID=*/ 2l, /*AuthID=*/ 3l,
            /*userName=*/ "Joe", /*userYear=*/ 2024,
            /*userMajors=*/ user2Majors, /*skills=*/ Skills.ORGANIZATION,
            /*userTotalCompTasks=*/ 5);

        // Should have 2 now
        HashSet<TaskBlockerData> blockers = ctl.getEveryUser();
        Assert.assertEquals(blockers.size(), 2);
    }
} 
