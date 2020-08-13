<%--Class Imports--%>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="java.util.ArrayList" %>

<%--Get variables--%>
<%
    long projectID = Long.parseLong(request.getParameter("projectID"));
    String projectName = request.getParameter("projectName");
    long parentTaskID = Long.parseLong(request.getParameter("taskID"));
    String parentTaskName = request.getParameter("taskName");
%>

<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Task</title>
    <link rel="stylesheet" href="style.css">
    <script src="scripts/main.js"></script>
    <script src="scripts/task.js"></script>
  </head>

  <body onload="getAddTaskInfo()">
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp"/>

    <div id="content">
      <h1>Add Task</h1>
      <div id="addtask-project-container"><p>This task will be under project: <%=projectName%></p></div>
      <div id="addtask-task-container"><p>This task will be under task: <%=parentTaskName%></p></div>
      <br>
      <form id="addtask-post-form" action="/task" method="POST">
        <div id="addtask-post-container">
          <input type="hidden" id="addtask-project-input" name="projectID" value="<%=projectID%>">
          <input type="hidden" id="addtask-parenttask-input" name="parentTaskID" value="<%=parentTaskID%>">
          <input type="text" name="name" required placeholder="Enter name of task..." maxlength="40">
          <br>
          <br>
          <textarea type="text" name="description" required placeholder="Enter task description..."></textarea>
          <br>
          <button type="submit">Add Task</button>
        </div>
        <br>
      </form>
    </div>
  </body>
</html>