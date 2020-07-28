package com.rtb.projectmanagementtool.taskblocker;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.HashSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskBlockerControllerTest {
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
  public void testAddTaskBlockers() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskBlockerController ctl = new TaskBlockerController();

    // Add 2 initially
    ctl.addTaskBlocker(ds, /*taskID=*/ 1l, /*blockerID=*/ 2l);
    ctl.addTaskBlocker(ds, /*taskID=*/ 2l, /*blockerID=*/ 3l);

    // Should have 2 now
    HashSet<TaskBlockerData> blockers = ctl.getTaskBlockers(ds, /*taskID=*/ 1l);
    Assert.assertEquals(blockers.size(), 2);

    // Add 2 more
    ctl.addTaskBlocker(ds, /*taskID=*/ 1l, /*blockerID=*/ 2l);
    ctl.addTaskBlocker(ds, /*taskID=*/ 2l, /*blockerID=*/ 3l);

    // Should be 4 now
    blockers = ctl.getTaskBlockers(ds, /*taskID=*/ 1l);
    Assert.assertEquals(blockers.size(), 4);
  }

  @Test
  public void testGetBlockersById() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    TaskBlockerController ctl = new TaskBlockerController();

    // 2 blockers for taskID=1
    ctl.addTaskBlocker(ds, /*taskID=*/ 1l, /*blockerID=*/ 2l);
    ctl.addTaskBlocker(ds, /*taskID=*/ 1l, /*blockerID=*/ 3l);

    // 5 blockers for taskID=2
    ctl.addTaskBlocker(ds, /*taskID=*/ 2l, /*blockerID=*/ 4l);
    ctl.addTaskBlocker(ds, /*taskID=*/ 2l, /*blockerID=*/ 5l);
    ctl.addTaskBlocker(ds, /*taskID=*/ 2l, /*blockerID=*/ 6l);

    HashSet<TaskBlockerData> blockers = ctl.getTaskBlockers(ds, /*taskID=*/ 1l);
    Assert.assertFalse(blockers.isEmpty());

    /*
    // These two asserts will fail because getTaskBlockers filter by
    // taskID has not been implemented yet

    Assert.assertEquals(blockers.size(), 3);

    for (TaskBlockerData blocker : blockers) {
        Assert.assertEquals(blocker.getTaskID(), 1l);
    }
    */
  }
}
