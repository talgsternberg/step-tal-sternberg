<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="com.rtb.projectmanagementtool.taskblocker.*"%>
<%@ page import="java.util.ArrayList" %>

<%--Get variables--%>
<%
    long projectID = Long.parseLong(request.getParameter("projectID"));
    String projectName = request.getParameter("projectName");
    String alert = request.getParameter("alert");
%>

<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Add Task Blocker</title>
    <link rel="stylesheet" href="style.css">
    <script src="scripts/task.js"></script>
  </head>

  <body onload="initTaskBlockerEventListeners();<% if (alert != null) { %> alert('<%=alert%>') <% } %>">
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp"/>

    <div id="content">

      <h1>Add Task Blocker</h1>

      <form id="addtaskblocker-post-form" action="/task-blocker" method="POST">
        <div id="addtaskblocker-post-container">
          <input type="hidden" id="addtaskblocker-projectID-input" name="projectID" value="<%=projectID%>">
          <input type="hidden" id="addtaskblocker-projectName-input" name="projectName" value="<%=projectName%>">
          
          <div id="taskblocker-project-container">
            <p>This task blocker will be under project: <b><a href="project?id=<%=projectID%>"><%=projectName%></a></b></p>
          </div>
          <div id="taskblocker-instructions-container"><p>Double-click to select a task.</p></div>
          <div id="taskblocker-blocker-container">
            <h2>Choose a Blocker Task: <div id="addtaskblocker-blocker-name" class="inline"></div></h2>
            <div id="taskblocker-blockertree-container">
            <%request.setAttribute("projectID", projectID);%>
            <%request.setAttribute("select", true);%>
            <%request.setAttribute("taskType", "blocker");%>
            <jsp:include page="/task-tree"/>
            </div>
          </div>

          <div id="taskblocker-blocked-container">
            <h2>Choose a Task to be Blocked: <div id="addtaskblocker-blocked-name" class="inline"></div></h2>
            <div id="taskblocker-blockedtree-container">
            <%request.setAttribute("projectID", projectID);%>
            <%request.setAttribute("select", true);%>
            <%request.setAttribute("taskType", "blocked");%>
            <jsp:include page="/task-tree"/>
            </div>
          </div>
          
          <br>
          <br>
          <button type="submit" class="deep-button">Add Task Blocker</button>
        </div>
        <br>
      </form>

    </div>

  </body>
</html>