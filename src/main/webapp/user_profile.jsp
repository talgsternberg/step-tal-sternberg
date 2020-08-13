<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.user.*,java.util.ArrayList,
  com.rtb.projectmanagementtool.task.*" %>

<%--Get variables--%>
<%
    UserData user = (UserData) request.getAttribute("UserProfile");
    ArrayList<TaskData> userTasks = (ArrayList<TaskData>) request.getAttribute("UserTasks");
%>

<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title><%=user.getUserName()%></title>
    <link rel="stylesheet" href="style.css">
  </head>

  <body>
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp" />

    <!-- Page content -->
    <div id="content">
      <div id="title">
        <div id="user-page-container"><h1>#<%=user.getUserName()%></h1></div>
      </div>
      <div id="user-name-container"><p>Name: <%=user.getUserName()%></p></div>
      <div id="user-year-container"><p>Graduation Year: <%=user.getUserYear()%></p></div>
      <div id="user-majors-container"><p>Majors: <%=user.getUserMajors()%></p></div>
      <div id="user-skills-container"><p>Skills: <%=user.getUserSkills()%></p></div>
      <div id="user-total-tasks-container">
          <p>Total Completed Tasks: <%=user.getUserTotal()%></p>
      </div>
      
      <div id="user-tasks-container">
        <h2><%=user.getUserName()%>'s Tasks:</h2>
        <%for (TaskData task : userTasks) {%>
          <p>Task Name: <%=task.getName()%></p>
          <p>Task Status: <%=task.getStatus()%></p>
        <%}%>
      </div>
    </div>
  </body>
</html>
