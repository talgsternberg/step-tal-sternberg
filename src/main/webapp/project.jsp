<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.project.*,java.util.ArrayList" %>

<%--Get variables--%>
<%
    ProjectData project = (ProjectData) request.getAttribute("project");
    ArrayList<Long> tasks = (ArrayList<Long>) request.getAttribute("projectTasks");
%>

<%--HTML--%>
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
        <p>(Creator) User with id #<%=project.getCreatorId()%></p>
        <%for (Long userId : project.getAdmins()) {%>
          <p>(Admin) User with id #<%=userId%></p>
        <%}%>

        <%for (Long userId : project.getMembers()) {%>
          <p>(Member) User with id #<%=userId%></p>
        <%}%>
      </div>
      
      <div id="project-tasks-container">
        <h2>Tasks</h2>
        <%for (Long taskId : tasks) {%>
          <p>- Task with id #<%=taskId%></p>
        <%}%>
      </div>
    </div>
  </body>
</html>
