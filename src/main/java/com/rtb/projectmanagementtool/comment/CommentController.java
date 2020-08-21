package com.rtb.projectmanagementtool.comment;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.ArrayList;
import java.util.List;

/** Class controlling the CommentData object. */
public final class CommentController {

  private DatastoreService datastore;
  private static final Filter NO_QUERY_FILTER = null;
  private static final int NO_QUERY_LIMIT = Integer.MAX_VALUE;
  private static final SortPredicate NO_QUERY_SORT = null;

  public CommentController(DatastoreService datastore) {
    this.datastore = datastore;
  }

  // Add methods

  public void addComments(ArrayList<CommentData> comments) {
    ArrayList<Entity> commentEntities = new ArrayList<>();
    for (CommentData comment : comments) {
      commentEntities.add(comment.toEntity());
    }
    addKeysToComments(comments, new ArrayList<>(datastore.put(commentEntities)));
  }

  private void addKeysToComments(ArrayList<CommentData> comments, ArrayList<Key> keys) {
    if (comments.size() == keys.size()) {
      for (int i = 0; i < keys.size(); i++) {
        comments.get(i).setCommentID(keys.get(i).getId());
      }
    }
  }

  // Get methods

  public CommentData getCommentByID(long commentID) {
    Query query =
        new Query("Comment")
            .addFilter("__key__", FilterOperator.EQUAL, KeyFactory.createKey("Comment", commentID));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    CommentData comment = new CommentData(entity);
    return comment;
  }

  public ArrayList<CommentData> getCommentsByTaskID(long taskID) {
    Filter filter = new FilterPredicate("taskID", FilterOperator.EQUAL, taskID);
    return getComments(filter, NO_QUERY_LIMIT, NO_QUERY_SORT);
  }

  public ArrayList<CommentData> getComments(
      long taskID, int limit, String sortBy, String sortDirection) {
    Filter filter = new FilterPredicate("taskID", FilterOperator.EQUAL, taskID);
    SortPredicate sort =
        new SortPredicate(sortBy, SortDirection.valueOf(sortDirection.toUpperCase()));
    return getComments(filter, limit, sort);
  }

  public ArrayList<CommentData> getComments(int limit, String sortBy, String sortDirection) {
    SortPredicate sort =
        new SortPredicate(sortBy, SortDirection.valueOf(sortDirection.toUpperCase()));
    return getComments(NO_QUERY_FILTER, limit, sort);
  }

  private ArrayList<CommentData> getComments(Filter filter, int limit, SortPredicate sort) {
    ArrayList<CommentData> comments = new ArrayList<>();
    Query query = new Query("Comment");
    if (filter != null) {
      query.setFilter(filter);
    }
    if (sort != null) {
      query.addSort(sort.getPropertyName(), sort.getDirection());
    }
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(limit));
    for (Entity entity : results) {
      comments.add(new CommentData(entity));
    }
    return comments;
  }

  // Delete methods

  public void deleteComments(ArrayList<Long> commentIDs) {
    datastore.delete(getKeysFromCommentIDs(commentIDs));
  }

  public void deleteCommentsByTaskID(long taskID) {
    datastore.delete(getKeysFromComments(getCommentsByTaskID(taskID)));
  }

  // Conversion methods

  public ArrayList<Long> getCommentIDsFromKeys(ArrayList<Key> keys) {
    ArrayList<Long> commentIDs = new ArrayList<>();
    for (Key key : keys) {
      commentIDs.add(key.getId());
    }
    return commentIDs;
  }

  public ArrayList<Long> getCommentIDsFromComments(ArrayList<CommentData> comments) {
    ArrayList<Long> commentIDs = new ArrayList<>();
    for (CommentData comment : comments) {
      commentIDs.add(comment.getCommentID());
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

  public ArrayList<Key> getKeysFromComments(ArrayList<CommentData> comments) {
    ArrayList<Key> keys = new ArrayList<>();
    for (CommentData comment : comments) {
      keys.add(KeyFactory.createKey("Comment", comment.getCommentID()));
    }
    return keys;
  }
}
