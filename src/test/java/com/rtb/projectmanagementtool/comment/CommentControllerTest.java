package com.rtb.projectmanagementtool.comment;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Class testing CommentController */
public class CommentControllerTest {

  // Comment 1 attributes
  private static final long commentID1 = 1l;
  private static final long taskID1 = 1l;
  private static final long userID1 = 1l;
  private static final String title1 = "Comment 1";
  private static final String message1 = "Comment 1 message...";
  private static final Date timestamp1 = new Date();

  // Comment 2 attributes
  private static final long commentID2 = 2l;
  private static final long taskID2 = 1l;
  private static final long userID2 = 2l;
  private static final String title2 = "Comment 2";
  private static final String message2 = "Comment 2 message...";
  private static final Date timestamp2 = new Date();

  // Comment 3 attributes
  private static final long commentID3 = 3l;
  private static final long taskID3 = 2l;
  private static final long userID3 = 2l;
  private static final String title3 = "Comment 3";
  private static final String message3 = "Comment 3 message...";
  private static final Date timestamp3 = new Date();

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
  public void testAddTasks() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    CommentController commentController = new CommentController(ds);

    // Create comments
    CommentData comment1 = new CommentData(taskID1, userID1, title1, message1);
    CommentData comment2 = new CommentData(taskID2, userID2, title2, message2);
    CommentData comment3 = new CommentData(taskID3, userID3, title3, message3);

    // Assert no comment entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add comment entities with CommentController
    commentController.addComments(new ArrayList<>(Arrays.asList(comment1, comment2, comment3)));

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));
  }

  @Test
  public void testGetCommentsByID() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    CommentController commentController = new CommentController(ds);

    // Create comments
    CommentData comment1 = new CommentData(taskID1, userID1, title1, message1);
    CommentData comment2 = new CommentData(taskID2, userID2, title2, message2);
    CommentData comment3 = new CommentData(taskID3, userID3, title3, message3);

    // Assert no comment entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add comment entities to ds
    long commentID1 = ds.put(comment1.toEntity()).getId();
    long commentID2 = ds.put(comment2.toEntity()).getId();
    long commentID3 = ds.put(comment3.toEntity()).getId();

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Get comment with CommentController
    CommentData getComment = commentController.getCommentByID(commentID2);

    // Add commentID to comment2
    comment2.setCommentID(commentID2);

    // Assert comment retrieved is correct
    Assert.assertEquals("getComment", comment2, getComment);
  }

  @Test
  public void testGetCommentsByTaskID() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    CommentController commentController = new CommentController(ds);

    // Create comments: { comment1 taskID: 1; comment2 taskID: 1, comment3 taskID: 2 }
    CommentData comment1 = new CommentData(taskID1, userID1, title1, message1);
    CommentData comment2 = new CommentData(taskID2, userID2, title2, message2);
    CommentData comment3 = new CommentData(taskID3, userID3, title3, message3);

    // Assert no comment entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add comment entities with CommentController
    commentController.addComments(new ArrayList<>(Arrays.asList(comment1, comment2, comment3)));

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Get comments of each task with CommentController
    ArrayList<CommentData> getTask1Comments = commentController.getCommentsByTaskID(1l);
    ArrayList<CommentData> getTask2Comments = commentController.getCommentsByTaskID(2l);

    // Create expected comments
    ArrayList<CommentData> task1Comments =
        new ArrayList<CommentData>(Arrays.asList(comment1, comment2));
    ArrayList<CommentData> task2Comments = new ArrayList<CommentData>(Arrays.asList(comment3));

    // Assert comments retrieved are correct
    Assert.assertEquals("getCommentsByTaskID1", task1Comments, getTask1Comments);
    Assert.assertEquals("getCommentsByTaskID2", task2Comments, getTask2Comments);
  }

  @Test
  public void testGetCommentsFromEmptyDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    CommentController commentController = new CommentController(ds);

    // Assert no comment entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Get comment entities with CommentController
    ArrayList<CommentData> comments = commentController.getComments(3, "title", "ascending");

    // Assert no entities were retrieved
    Assert.assertEquals("getComments", new ArrayList<>(), comments);
  }

  @Test
  public void testGetCommentsFromDs() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    CommentController commentController = new CommentController(ds);

    // Create comments
    CommentData comment1 = new CommentData(taskID1, userID1, title1, message1);
    CommentData comment2 = new CommentData(taskID2, userID2, title2, message2);
    CommentData comment3 = new CommentData(taskID3, userID3, title3, message3);

    // Assert no comment entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add comment entities to ds
    long commentID1 = ds.put(comment1.toEntity()).getId();
    long commentID2 = ds.put(comment2.toEntity()).getId();
    long commentID3 = ds.put(comment3.toEntity()).getId();

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add commentIDs to comments
    comment1.setCommentID(commentID1);
    comment2.setCommentID(commentID2);
    comment3.setCommentID(commentID3);

    // Create ArrayList of expected CommentData objects
    ArrayList<CommentData> comments = new ArrayList<>(Arrays.asList(comment1, comment2, comment3));

    // Get all comment entities with CommentController
    ArrayList<CommentData> getComments = commentController.getComments(5, "title", "ascending");

    // Assert all entities were retrieved
    Assert.assertEquals("getComments", comments, getComments);

    // Remove comment3 from expected CommentData objects
    comments.remove(comment3);

    // Get some comment entities with CommentController
    getComments = commentController.getComments(2, "title", "ascending");

    // Assert correct entities were retrieved
    Assert.assertEquals("getComments", comments, getComments);
  }

  @Test
  public void testDeleteComments() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    CommentController commentController = new CommentController(ds);

    // Create comments
    CommentData comment1 = new CommentData(taskID1, userID1, title1, message1);
    CommentData comment2 = new CommentData(taskID2, userID2, title2, message2);
    CommentData comment3 = new CommentData(taskID3, userID3, title3, message3);

    // Assert no comment entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add comment entities to ds
    long commentID1 = ds.put(comment1.toEntity()).getId();
    long commentID2 = ds.put(comment2.toEntity()).getId();
    long commentID3 = ds.put(comment3.toEntity()).getId();

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Delete comment entities with CommentController
    commentController.deleteComments(new ArrayList<>(Arrays.asList(commentID1, commentID3)));

    // Assert 1 comment entity remains
    Assert.assertEquals(1, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add commentID to comment2
    comment2.setCommentID(commentID2);

    // Assert the correct comment entity remains
    Assert.assertEquals(
        "deleteComment",
        comment2,
        new CommentData(ds.prepare(new Query("Comment")).asSingleEntity()));
  }

  @Test
  public void testDeleteCommentsWithTaskID() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    CommentController commentController = new CommentController(ds);

    // Create comments: { comment1 taskID: 1; comment2 taskID: 1, comment3 taskID: 2 }
    CommentData comment1 = new CommentData(taskID1, userID1, title1, message1);
    CommentData comment2 = new CommentData(taskID2, userID2, title2, message2);
    CommentData comment3 = new CommentData(taskID3, userID3, title3, message3);

    // Assert no comment entities are found
    Assert.assertEquals(0, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add comment entities to ds
    long commentID1 = ds.put(comment1.toEntity()).getId();
    long commentID2 = ds.put(comment2.toEntity()).getId();
    long commentID3 = ds.put(comment3.toEntity()).getId();

    // Assert 3 entities were added
    Assert.assertEquals(3, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Delete comment entities with CommentController
    commentController.deleteCommentsByTaskID(taskID1);

    // Assert 1 comment entity remains
    Assert.assertEquals(1, ds.prepare(new Query("Comment")).countEntities(withLimit(10)));

    // Add commentID to comment3
    comment3.setCommentID(commentID3);

    // Assert the correct comment entity remains
    Assert.assertEquals(
        "deleteComment",
        comment3,
        new CommentData(ds.prepare(new Query("Comment")).asSingleEntity()));
  }
}
