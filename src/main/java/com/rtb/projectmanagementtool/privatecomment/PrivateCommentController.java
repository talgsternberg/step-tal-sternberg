package com.rtb.projectmanagementtool.privatecomment;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.user.*;
import java.util.ArrayList;
import java.util.List;

/** Class controlling the PrivateCommentData object. */
public final class PrivateCommentController {

  private DatastoreService datastore;
  private static final Filter NO_QUERY_FILTER = null;
  private static final int NO_QUERY_LIMIT = Integer.MAX_VALUE;
  private static final SortPredicate NO_QUERY_SORT = null;

  public PrivateCommentController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  public Long addPrivateComment(PrivateCommentData privateComment) {
    Entity entity = privateComment.toEntity();
    Key key = datastore.put(entity);
    long commentID = key.getId();
    privateComment.setCommentID(commentID);
    return commentID;
  }

  public PrivateCommentData getPrivateCommentByID(long commentID) {
    Query query =
        new Query("PrivateComment")
            .addFilter(
                "__key__", FilterOperator.EQUAL, KeyFactory.createKey("PrivateComment", commentID));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    PrivateCommentData privateComment = new PrivateCommentData(entity);
    return privateComment;
  }

  public ArrayList<PrivateCommentData> getPrivateCommentsForUser(long userID) {
    // get a list of all private comments for a user
    Filter filter = new FilterPredicate("userID", FilterOperator.EQUAL, userID);
    return getPrivateComments(filter, NO_QUERY_LIMIT, NO_QUERY_SORT);
  }

  public void updatePrivateComment(PrivateCommentData privateComment) {
    Entity entity = privateComment.toEntity();
    datastore.put(entity);
  }

  // only 1 private comment per task
  public PrivateCommentData getPrivateCommentByTaskID(long taskID) {
    Query query = new Query("PrivateComment").addFilter("taskID", FilterOperator.EQUAL, taskID);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity != null) {
      PrivateCommentData privateComment = new PrivateCommentData(entity);
      return privateComment;
    }
    return null;
  }

  public ArrayList<PrivateCommentData> getPrivateComments(
      int limit, String sortBy, String sortDirection) {
    SortPredicate sort =
        new SortPredicate(sortBy, SortDirection.valueOf(sortDirection.toUpperCase()));
    return getPrivateComments(NO_QUERY_FILTER, limit, sort);
  }

  private ArrayList<PrivateCommentData> getPrivateComments(
      Filter filter, int limit, SortPredicate sort) {
    ArrayList<PrivateCommentData> privateComments = new ArrayList<>();
    Query query = new Query("PrivateComment");
    if (filter != null) {
      query.setFilter(filter);
    }
    if (sort != null) {
      query.addSort(sort.getPropertyName(), sort.getDirection());
    }
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(limit));
    for (Entity entity : results) {
      privateComments.add(new PrivateCommentData(entity));
    }
    return privateComments;
  }

  public void deletePrivateComment(Long commentID) {
    datastore.delete(getKeyFromCommentID(commentID));
  }

  public void deleteCommentByTaskID(Long taskID) {
    datastore.delete(getKeyFromComment(getPrivateCommentByTaskID(taskID)));
  }

  // Conversion methods

  public Long getCommentIDFromKey(Key key) {
    Long commentID = key.getId();
    return commentID;
  }

  public Long getCommentIDFromComment(PrivateCommentData privateComment) {
    Long commentID = privateComment.getCommentID();
    return commentID;
  }

  public Key getKeyFromCommentID(Long commentID) {
    Key key = KeyFactory.createKey("Comment", commentID);
    return key;
  }

  public Key getKeyFromComment(PrivateCommentData privateComment) {
    Key key = KeyFactory.createKey("PrivateComment", privateComment.getCommentID());
    return key;
  }
}
