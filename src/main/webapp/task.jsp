<%--Class Imports--%>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.rtb.projectmanagementtool.comment.*"%>
<%@ page import="com.rtb.projectmanagementtool.project.*"%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="com.rtb.projectmanagementtool.task.TaskData.Status"%>
<%@ page import="com.rtb.projectmanagementtool.user.*"%>
<%@ page import="com.rtb.projectmanagementtool.user.UserData.Skills"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>

<%--Get variables--%>
<%
    UserData user = (UserData) request.getAttribute("user");
    TaskData task = (TaskData) request.getAttribute("task");
    TaskData parentTask = (TaskData) request.getAttribute("parentTask");
    ProjectData project = (ProjectData) request.getAttribute("project");
    ArrayList<TaskData> subtasks = (ArrayList<TaskData>) request.getAttribute("subtasks");
    ArrayList<UserData> users = (ArrayList<UserData>) request.getAttribute("users");
    Map<CommentData, String> comments = (HashMap<CommentData, String>) request.getAttribute("comments");
%>



<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title><%=task.getName()%></title>
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <!-- <script src="scripts/main.js"></script> -->
    <!-- <script src="scripts/task.js"></script> -->
  </head>

  <body>
    <jsp:include page="navigation-bar.jsp"/>

    <div id="content">
      <div id="task-title-container"><h1><%=task.getName()%></h1></div>
      <div id="task-description-container"><%=task.getDescription()%></div>
      <div id="task-status-container">
        <p>Status: <%=task.getStatus()%></p>
        <%
            String status;
            String statusButtonText;
            if (task.getStatus() != Status.COMPLETE) {
                status = Status.COMPLETE.name();
                statusButtonText = "Set Task Status as Complete";
            } else {
                status = Status.INCOMPLETE.name();
                statusButtonText = "Set Task Status as Incomplete";
            }
        %>
        <form id="toggle-status-post-form" action="/task-set-status" method="POST">
          <input type="hidden" name="taskID" value="<%=task.getTaskID()%>"/>
          <input type="hidden" name="status" value="<%=status%>"/>
          <button type="submit" id="task-toggle-status"><%=statusButtonText%></button>
        </form>
      </div>
      <div id="task-project-container">
        <%if (project != null) {%>
          <p class="inline">Return to Project: </p>
          <button type="button" class="inline" onclick="location.href='project?id=<%=project.getId()%>'">
            <%=project.getName()%>
          </button>
        <%}%>
      </div>
      <div id="task-parenttask-container">
        <%if (parentTask != null) {%>
          <p class="inline">Return to Parent Task: </p>
          <button type="button" class="inline" onclick="location.href='task?taskID=<%=parentTask.getTaskID()%>'">
            <%=parentTask.getName()%>
          </button>
        <%}%>
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
        %>
        <button type="button" onclick="location.href='add-task.jsp?projectID=<%=projectID%>&projectName=<%=projectName%>&taskID=<%=task.getTaskID()%>&taskName=<%=task.getName()%>'">
          Add Subtask
        </button>
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
          <button type="submit" id="toggle-user-assignment"><%=userButtonText%></button>
        </form>
        <%}%>
      <div id="task-users-container"></div>

      <h2>Comments</h2>
      <div id="task-addcomments-container">
        <%
            if (task.getTaskID() != 0) {
        %>
        <form id="add-comment-post-form" action="/comment" method="POST">
          <input type="hidden" id="add-comment-task-input" name="taskID" value="<%=task.getTaskID()%>">
          <input type="hidden" id="add-comment-user-input" name="userID" value="<%=user.getUserID()%>">
          <input type="text" name="title" required placeholder="Enter title of comment..." maxlength="40">
          <br>
          <br>
          <textarea type="text" name="message" required placeholder="Enter comment message..."></textarea>
          <br>
          <button type="submit">Post Comment</button>
        </form>
        <%}%>
      </div>
      <ul id="task-comments-container">
        <%
            for (Map.Entry<CommentData, String> entry : comments.entrySet()) {
                CommentData comment = entry.getKey();
                String username = entry.getValue();
                
                // Get timestamp
                String datePattern = "MMM d, yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
                String date = dateFormat.format(comment.getTimestamp());
                String timePattern = "HH:mm";
                SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
                String time = timeFormat.format(comment.getTimestamp());
        %>
        <li class="comment">
          <h3><%=comment.getTitle()%></h3>
          <h5>
            Posted on <%=date%> at <%=time%>
            <br>
            Posted by <%=username%>
          </h5>
          <p><%=comment.getMessage()%></p>
          <% if (comment.getUserID() == user.getUserID()) {%>
          <form id="delete-comment-post-form" action="/comment-delete" method="POST">
            <input type="hidden" id="delete-comment-commentID-input" name="commentID" value="<%=comment.getCommentID()%>">
            <input type="hidden" id="delete-comment-taskID-input" name="taskID" value="<%=task.getTaskID()%>">
            <button type="submit"><span class="fa fa-trash-o" aria-hidden="true"></span></button>
          </form>
          <%}%>
        </li>
        <%}%>
      </ul>
      <div id="task-delete-container">
        <form id="task-delete-post-form" action="/task-delete" method="POST">
          <input type="hidden" name="taskID" value="<%=task.getTaskID()%>"/>
          <button type="submit" id="task-delete-button">Delete Task</button>
        </form>
      </div>
    </div>
  </body>
</html>