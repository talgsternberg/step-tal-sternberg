package com.rtb.projectmanagementtool.privatecomment;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.user.*;
import java.util.ArrayList;
import java.util.List;

/** Class controlling the CommentData object. */
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

  // only 1 private comment per task
  public ArrayList<PrivateCommentData> getPrivateCommentByTaskID(long taskID) {
    Filter filter = new FilterPredicate("taskID", FilterOperator.EQUAL, taskID);
    return getPrivateComments(filter, NO_QUERY_LIMIT, NO_QUERY_SORT);
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

  public void deletePrivateComments(ArrayList<Long> commentIDs) {
    datastore.delete(getKeysFromCommentIDs(commentIDs));
  }

  public void deleteCommentsByTaskID(Long taskID) {
    datastore.delete(getKeysFromComments(getPrivateCommentByTaskID(taskID)));
  }

  // Conversion methods

  public ArrayList<Long> getCommentIDsFromKeys(ArrayList<Key> keys) {
    ArrayList<Long> commentIDs = new ArrayList<>();
    for (Key key : keys) {
      commentIDs.add(key.getId());
    }
    return commentIDs;
  }

  public ArrayList<Long> getCommentIDsFromComments(ArrayList<PrivateCommentData> privateComments) {
    ArrayList<Long> commentIDs = new ArrayList<>();
    for (PrivateCommentData privateComment : privateComments) {
      commentIDs.add(privateComment.getCommentID());
    }
    return commentIDs;
  }

  public ArrayList<Key> getKeysFromCommentIDs(ArrayList<Long> commentIDs) {
    ArrayList<Key> keys = new ArrayList<>();
    for (long commentID : commentIDs) {
      keys.add(KeyFactory.createKey("Comment", commentID));
    }
    return keys;
  }

  public ArrayList<Key> getKeysFromComments(ArrayList<PrivateCommentData> privateComments) {
    ArrayList<Key> keys = new ArrayList<>();
    for (PrivateCommentData privateComment : privateComments) {
      keys.add(KeyFactory.createKey("PrivateComment", privateComment.getCommentID()));
    }
    return keys;
  }
}
