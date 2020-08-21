package com.rtb.projectmanagementtool.comment;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.Date;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Class testing Comment */
public class CommentDataTest {

  // Comment 1 attributes
  private static final long commentID1 = 1l;
  private static final long taskID1 = 1l;
  private static final long userID1 = 1l;
  private static final String title1 = "Comment 1";
  private static final String message1 = "Comment 1 message...";
  private static final Date timestamp1 = new Date();
  private static final boolean isEdited1 = true;

  // Comment 2 attributes
  private static final long commentID2 = 2l;
  private static final long taskID2 = 1l;
  private static final long userID2 = 2l;
  private static final String title2 = "Comment 2";
  private static final String message2 = "Comment 2 message...";
  private static final Date timestamp2 = new Date();
  private static final boolean isEdited2 = false;

  // Comment 3 attributes
  private static final long commentID3 = 3l;
  private static final long taskID3 = 2l;
  private static final long userID3 = 2l;
  private static final String title3 = "Comment 3";
  private static final String message3 = "Comment 3 message...";
  private static final Date timestamp3 = new Date();
  private static final boolean isEdited3 = false;

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

  private void DsInserts() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // Assert no comment entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Build task entity 1
    Entity entity1 = new Entity("Comment");
    entity1.setProperty("taskID", taskID1);
    entity1.setProperty("userID", userID1);
    entity1.setProperty("title", title1);
    entity1.setProperty("message", message1);
    entity1.setProperty("timestamp", timestamp1);
    entity1.setProperty("isEdited", isEdited1);

    // Build task entity 2
    Entity entity2 = new Entity("Comment");
    entity2.setProperty("taskID", taskID2);
    entity2.setProperty("userID", userID2);
    entity2.setProperty("title", title2);
    entity2.setProperty("message", message2);
    entity2.setProperty("timestamp", timestamp2);
    entity1.setProperty("isEdited", isEdited2);

    // Add task entities to ds
    ds.put(entity1);
    ds.put(entity2);

    // Assert 2 entities were added
    Assert.assertEquals(2, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));
  }

  @Test
  public void testInsertIntoDs1() {
    DsInserts();
  }

  @Test
  public void testInsertIntoDs2() {
    DsInserts();
  }

  @Test
  public void testCreateCommentFromLongConstructor() {
    // Build CommentData object
    CommentData comment =
        new CommentData(commentID1, taskID1, userID1, title1, message1, timestamp1, isEdited1);

    // Assert CommentData parameters were stored correctly
    Assert.assertEquals("commentID", commentID1, comment.getCommentID());
    Assert.assertEquals("taskID", taskID1, comment.getTaskID());
    Assert.assertEquals("userID", userID1, comment.getUserID());
    Assert.assertEquals("title", title1, comment.getTitle());
    Assert.assertEquals("message", message1, comment.getMessage());
    Assert.assertEquals("timestamp", timestamp1, comment.getTimestamp());
    Assert.assertEquals("isEdited", isEdited1, comment.getIsEdited());
  }

  @Test
  public void testCreateCommentFromShortConstructor() {
    // Build CommentData object
    CommentData comment = new CommentData(taskID1, userID1, title1, message1);

    // Assert CommentData parameters were stored correctly
    Assert.assertEquals("commentID", 0l, comment.getCommentID());
    Assert.assertEquals("taskID", taskID1, comment.getTaskID());
    Assert.assertEquals("userID", userID1, comment.getUserID());
    Assert.assertEquals("title", title1, comment.getTitle());
    Assert.assertEquals("message", message1, comment.getMessage());
  }

  @Test
  public void testCreateTaskFromEntityWithCommentID() {
    // Build entity
    Entity entity = new Entity("Comment", commentID2);
    entity.setProperty("taskID", taskID2);
    entity.setProperty("userID", userID2);
    entity.setProperty("title", title2);
    entity.setProperty("message", message2);
    entity.setProperty("timestamp", timestamp2);
    entity.setProperty("isEdited", isEdited2);

    // Build CommentData object from entity
    CommentData comment = new CommentData(entity);

    // Assert CommentData parameters were stored correctly
    Assert.assertEquals("commentID", commentID2, comment.getCommentID());
    Assert.assertEquals("taskID", taskID2, comment.getTaskID());
    Assert.assertEquals("userID", userID2, comment.getUserID());
    Assert.assertEquals("title", title2, comment.getTitle());
    Assert.assertEquals("message", message2, comment.getMessage());
    Assert.assertEquals("timestamp", timestamp2, comment.getTimestamp());
    Assert.assertEquals("isEdited", isEdited2, comment.getIsEdited());
  }

  @Test
  public void testCreateTaskFromEntityWithoutCommentID() {
    // Build entity
    Entity entity = new Entity("Comment");
    entity.setProperty("taskID", taskID2);
    entity.setProperty("userID", userID2);
    entity.setProperty("title", title2);
    entity.setProperty("message", message2);
    entity.setProperty("timestamp", timestamp2);
    entity.setProperty("isEdited", isEdited2);

    // Build CommentData object from entity
    CommentData comment = new CommentData(entity);

    // Assert CommentData parameters were stored correctly
    Assert.assertEquals("commentID", 0l, comment.getCommentID());
    Assert.assertEquals("taskID", taskID2, comment.getTaskID());
    Assert.assertEquals("userID", userID2, comment.getUserID());
    Assert.assertEquals("title", title2, comment.getTitle());
    Assert.assertEquals("message", message2, comment.getMessage());
    Assert.assertEquals("timestamp", timestamp2, comment.getTimestamp());
    Assert.assertEquals("isEdited", isEdited2, comment.getIsEdited());
  }

  @Test
  public void testCreateEntityFromComment() {
    // Build CommentData object
    CommentData comment =
        new CommentData(commentID3, taskID3, userID3, title3, message3, timestamp3, isEdited3);

    // Create comment entity from CommentData object
    Entity entity = comment.toEntity();

    // Get comment entity attributes
    long entityCommentID = (long) entity.getKey().getId();
    long entityTaskID = (long) entity.getProperty("taskID");
    long entityUserID = (long) entity.getProperty("userID");
    String entityTitle = (String) entity.getProperty("title");
    String entityMessage = (String) entity.getProperty("message");
    Date entityTimestamp = (Date) entity.getProperty("timestamp");
    boolean entityIsEdited = (boolean) entity.getProperty("isEdited");

    // Assert comment entity attributes equal CommentData attributes
    Assert.assertEquals("commentID", commentID3, entityCommentID);
    Assert.assertEquals("taskID", taskID3, entityTaskID);
    Assert.assertEquals("userID", userID3, entityUserID);
    Assert.assertEquals("title", title3, entityTitle);
    Assert.assertEquals("message", message3, entityMessage);
    Assert.assertEquals("timestamp", timestamp3, entityTimestamp);
    Assert.assertEquals("isEdited", isEdited3, entityIsEdited);
  }

  @Test
  public void testCreateEntityFromCommentWithoutCommentIDAndWithoutTimestamp() {
    // Build CommentData object
    CommentData comment = new CommentData(taskID3, userID3, title3, message3);

    // Create comment entity from CommentData object
    Entity entity = comment.toEntity();

    // Get comment entity attributes
    long entityCommentID = (long) entity.getKey().getId();
    long entityTaskID = (long) entity.getProperty("taskID");
    long entityUserID = (long) entity.getProperty("userID");
    String entityTitle = (String) entity.getProperty("title");
    String entityMessage = (String) entity.getProperty("message");

    // Assert comment entity attributes equal CommentData attributes
    Assert.assertEquals("commentID", 0l, entityCommentID);
    Assert.assertEquals("taskID", taskID3, entityTaskID);
    Assert.assertEquals("userID", userID3, entityUserID);
    Assert.assertEquals("title", title3, entityTitle);
    Assert.assertEquals("message", message3, entityMessage);
  }
}
