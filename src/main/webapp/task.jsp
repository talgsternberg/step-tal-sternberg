<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.comment.*"%>
<%@ page import="com.rtb.projectmanagementtool.project.*"%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="com.rtb.projectmanagementtool.task.TaskData.Status"%>
<%@ page import="com.rtb.projectmanagementtool.user.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.ArrayList"%>

<%--Get variables--%>
<%
    UserData user = (UserData) request.getAttribute("user");
    TaskData task = (TaskData) request.getAttribute("task");
    ArrayList<TaskData> ancestors = (ArrayList<TaskData>) request.getAttribute("ancestors");
    boolean canSetComplete = (boolean) request.getAttribute("canSetComplete");
    boolean canSetIncomplete = (boolean) request.getAttribute("canSetIncomplete");
    ProjectData project = (ProjectData) request.getAttribute("project");
    ArrayList<TaskData> subtasks = (ArrayList<TaskData>) request.getAttribute("subtasks");
    ArrayList<UserData> users = (ArrayList<UserData>) request.getAttribute("users");
    ArrayList<CommentDisplayData> commentsDisplay = (ArrayList<CommentDisplayData>) request.getAttribute("comments");
%>



<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title><%=task.getName()%></title>
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <!-- <script src="scripts/main.js"></script> -->
    <!-- <script src="scripts/task-old.js"></script> -->
    <script src="scripts/task.js"></script>
  </head>

  <body>
    <jsp:include page="navigation-bar.jsp"/>

    <div id="content">
      <div id="task-ancestors-container">
        <p>
          <b><a href="project?id=<%=project.getId()%>"><%=project.getName()%></a></b> / 
          <%for (TaskData ancestor : ancestors) {%>
          <a href="task?taskID=<%=ancestor.getTaskID()%>"><%=ancestor.getName()%></a> / 
          <%}%>
          <b><%=task.getName()%></b>
        </p>
      </div>
      <div id="task-title-container" class="center">
        <h1 class="inline"><%=task.getName()%></h1>
        <%request.setAttribute("task", task);%>
        <%request.setAttribute("clickable", true);%>
        <%request.setAttribute("canSetComplete", canSetComplete);%>
        <%request.setAttribute("canSetIncomplete", canSetIncomplete);%>
        <jsp:include page="task-status-checkmark.jsp"/>
      </div>
      <div id="task-description-container" class="description">
        <div id="task-description">
          <%=task.getDescription()%>
        </div>
        <div id="edit-description-container">
          <button type="button" id="edit-description-button" class="has-hover-text flat-button" onclick="editDescription(<%=task.getTaskID()%>)">
            <span class="fa fa-edit" aria-hidden="true"></span>
            <span class="hover-text small">Edit</span>
          </button>
        </div>
      </div>

      <h2>Subtasks</h2>
      <div id="task-subtasks-container">
        <%request.setAttribute("tasks", subtasks);%>
        <jsp:include page="list-tasks.jsp"/>
      </div>
      <div id="task-addsubtask-container">
        <%
            long projectID;
            String projectName;
            if (project != null) { // Use real project
                projectID = project.getId();
                projectName = project.getName();
            } else { // Use default values
                projectID = 1;
                projectName = "Default Project Name";
            }
            if (task.getStatus() != Status.COMPLETE) {
        %>
        <button type="button" class="deep-button" onclick="location.href='add-task.jsp?projectID=<%=projectID%>&projectName=<%=projectName%>&taskID=<%=task.getTaskID()%>&taskName=<%=task.getName()%>'">
          Add Subtask
        </button>
        <%}%>
      </div>

      <h2>Members</h2>
      <div id="task-assignuser-container"></div>
        <%
            if (task.getStatus() != Status.COMPLETE) {
                String servletPage;
                String userButtonText;
                if (!task.getUsers().contains(user.getUserID())) {
                    servletPage = "/task-add-user";
                    userButtonText = "Assign me to this task";
                } else {
                    servletPage = "/task-remove-user";
                    userButtonText = "Remove me from this task";
                }
        %>
        <form id="toggle-user-assignment-post-form" action="<%=servletPage%>" method="POST">
          <input type="hidden" name="taskID" value="<%=task.getTaskID()%>"/>
          <input type="hidden" name="userID" value="<%=user.getUserID()%>"/>
          <button type="submit" class="deep-button" id="toggle-user-assignment"><%=userButtonText%></button>
        </form>
        <%}%>
      <div id="task-users-container">
        <%request.setAttribute("users", users);%>
        <jsp:include page="list-users.jsp"/>
      </div>

      <h2>Comments</h2>
      <% if (task.getTaskID() != 0 && task.getStatus() != Status.COMPLETE) { %>
      <div id="task-addcomments-container">
        <form id="add-comment-post-form" action="/comment" method="POST">
          <input type="hidden" id="add-comment-task-input" name="taskID" value="<%=task.getTaskID()%>">
          <input type="hidden" id="add-comment-user-input" name="userID" value="<%=user.getUserID()%>">
          <h3>Write a Comment</h3>
          <br>
          <input type="text" name="title" required placeholder="Enter title of comment..." maxlength="40">
          <br>
          <br>
          <textarea type="text" name="message" required placeholder="Enter comment message..."></textarea>
          <br>
          <button type="submit" class="deep-button">Post Comment</button>
        </form>
      </div>
      <% } %>
      <ul id="task-comments-container">
        <%
            for (CommentDisplayData commentDisplay : commentsDisplay) {
                CommentData comment = commentDisplay.getComment();
                String username = commentDisplay.getUsername();

                // Get timestamp
                String datePattern = "MMM d, yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
                String date = dateFormat.format(comment.getTimestamp());
                String timePattern = "HH:mm";
                SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
                String time = timeFormat.format(comment.getTimestamp());

                // Get isEdited
                String edit = "";
                if (comment.getIsEdited()) {
                    edit = "(edited)";
                }

                // Get commentID
                long cID = comment.getCommentID();
        %>
        <li class="comment">
          <div id="comment-container-<%=cID%>">
            <div id="comment-title-container-<%=cID%>"><h3><%=comment.getTitle()%></h3></div>
            <div id="comment-postinfo-container-<%=cID%>">
              <h5>
                Posted on <%=date%> at <%=time%> <%=edit%>
                <br>
                Posted by <%=username%>
              </h5>   
            </div>       
            <div id="comment-message-container-<%=cID%>"><p id="comment-message-<%=cID%>"><%=comment.getMessage()%></p></div>
            <% if (comment.getUserID() == user.getUserID() && task.getStatus() != Status.COMPLETE) {%>
            <div id="edit-comment-container-<%=cID%>" class="inline">
              <button type="button" id="edit-comment-button" class="has-hover-text flat-button" onclick="editComment(<%=cID%>, <%=task.getTaskID()%>)">
                <span class="fa fa-edit" aria-hidden="true"></span>
                <span class="hover-text small">Edit</span>
              </button>
            </div>
            <div id="delete-comment-container-<%=cID%>" class="inline">
              <form id="delete-comment-post-form-<%=cID%>" action="/comment-delete" method="POST" class="inline">
                <input type="hidden" id="delete-comment-commentID-input-<%=cID%>" name="commentID" value="<%=cID%>">
                <input type="hidden" id="delete-comment-taskID-input-<%=cID%>" name="taskID" value="<%=task.getTaskID()%>">
                <button type="submit" class="has-hover-text flat-button">
                  <span class="fa fa-trash-o" aria-hidden="true"></span>
                  <span class="hover-text small">Delete</span>
                </button>
              </form>
            </div>
            <% } %>
          </div>
        </li>
        <% } %>
      </ul>
      <div id="task-delete-container" class="center">
        <form id="task-delete-post-form" action="/task-delete" method="POST">
          <input type="hidden" name="taskID" value="<%=task.getTaskID()%>"/>
          <button type="submit" class="deep-button" id="task-delete-button">Delete Task</button>
        </form>
      </div>
    </div>
  </body>
</html>