<%--Class Imports--%>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="java.util.ArrayList" %>

<%--Get variables--%>
<%
    TaskData task = (TaskData) request.getAttribute("task");
    ArrayList<TaskData> subtasks = (ArrayList<TaskData>) request.getAttribute("subtasks");
    System.out.println(task);
    System.out.println(subtasks);
%>

    <!-- TaskData parentTask = (TaskData) request.getAttribute("parentTask"); -->
    <!-- ProjectData project = (ProjectData) request.getAttribute("project"); -->
    <!-- ArrayList<UserData> users = (ArrayList<UserData>) request.getAttribute("users"); -->
    <!-- ArrayList<CommentData> comments = (ArrayList<CommentData>) request.getAttribute("comments"); -->


<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title><%=task.getName()%></title>
    <link rel="stylesheet" href="style.css">
    <!-- <script src="scripts/main.js"></script> -->
    <!-- <script src="scripts/task.js"></script> -->
  </head>

  <body>
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp"/>

    <div id="content">
      <div id="task-title-container"><h1><%=task.getName()%></h1></div>
      <div id="task-description-container"><%=task.getDescription()%></div>
      <div id="task-status-container">Status: <%=task.getStatus()%></div>
      <div id="task-project-container">
        <p>Return to Project: </p>
        <!-- <button type="button" href="project.jsp?projectID="> -->
          <!--  -->
        <!-- </button> -->
      </div>
      <div id="task-parenttask-container">
        <p>Return to Parent Task: </p>
        <!-- <button type="button" href="task.jsp?taskID="> -->
          <!--  -->
        <!-- </button> -->
      </div>

      <h2>Subtasks</h2>
      <div id="task-subtasks-container">
        <%Gson gson = new Gson();%>
        <jsp:include page="list-tasks.jsp">
          <jsp:param name="tasks" value="<%=gson.toJson(subtasks)%>"/>
        </jsp:include>
      </div>
      <div id="task-addsubtask-container"></div>
      <h2>Members</h2>
      <div id="task-assignuser-container"></div>
      <div id="task-users-container"></div>
      <h2>Comments</h2>
      <ul id="task-comments-container">
        <li class="comment">
          <p>Comment 1</p>
        </li>
        <li class="comment">
          <p>Comment 2</p>
        </li>
        <li class="comment">
          <p>Comment 3</p>
        </li>
      </ul>
    </div>
  </body>
</html>