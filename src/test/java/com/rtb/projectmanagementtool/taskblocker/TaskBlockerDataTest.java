package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskBlockerDataTest {
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
        TaskBlockerData blocker = new TaskBlockerData(/*taskID=*/ 1l, /*blockerID=*/ 2l);
        Assert.assertEquals(blocker.getTaskID(), 1l);
        Assert.assertEquals(blocker.getBlockerID(), 2l);
    }

    @Test
    public void testCreateFromEntity() {
        Entity entity = new Entity("TaskBlocker");
        entity.setProperty("taskID", /*taskID=*/ 1l);
        entity.setProperty("blockerID", /*blockerID=*/ 2l);
        TaskBlockerData blocker = new TaskBlockerData(entity);
        Assert.assertEquals(blocker.getTaskID(), 1l);
        Assert.assertEquals(blocker.getBlockerID(), 2l);
    }
}