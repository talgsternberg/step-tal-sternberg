<%@ page import="com.rtb.projectmanagementtool.project.*" %>
<%@ page import="com.rtb.projectmanagementtool.user.*" %>
<%@ page import="com.rtb.projectmanagementtool.task.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashSet" %>

<%
    Long userId = (Long) request.getAttribute("userId");
    ProjectData project = (ProjectData) request.getAttribute("project");
    UserData creator = (UserData) request.getAttribute("creator");
    HashSet<UserData> admins =  (HashSet<UserData>) request.getAttribute("admins");
    HashSet<UserData> members =  (HashSet<UserData>) request.getAttribute("members");
    ArrayList<TaskData> tasks = (ArrayList<TaskData>) request.getAttribute("tasks");
%>

<html>
  <head>
    <meta charset="UTF-8">
    <title><%=project.getName()%></title>
    <link rel="stylesheet" href="style.css">
  </head>

  <body>
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp" />

    <!-- Page content -->
    <div id="content">
      <div id="project-title-container"><h1><%=project.getName()%></h1></div>
      <div id="project-description-container">
        <p><%=project.getDescription()%></p>
      </div>
      
      <div id="project-users-container">
        <h2>Users</h2>
        <p>Creator: 
            <%if (userId == creator.getUserID()) { %> 
              <mark>
            <% } %>
            <%=creator.getUserName() %>
            <%if (userId == creator.getUserID()) { %> 
              </mark>
            <% } %>
        </p>
        <%for (UserData user : admins) {%>
          <p>Admin: 
            <%if (userId == user.getUserID()) { %> 
              <mark>
            <% } %>
              <%=user.getUserName() %>
            <%if (userId == user.getUserID()) { %> 
              </mark>
            <% } %>
          </p>
        <%}%>
        <%for (UserData user : members) {%>
          <p>Member: 
            <%if (userId == user.getUserID()) { %> 
              <mark>
            <% } %>
              <%=user.getUserName() %>
            <%if (userId == user.getUserID()) { %> 
              </mark>
            <% } %>
          </p>
        <%}%>
      </div>
      
      <div id="project-tasks-container">
        <h2>Tasks</h2>
        <%for (TaskData task : tasks) {%>
          <p><%=task.getName()%></p>
        <%}%>
      </div>
    </div>
  </body>
</html>
