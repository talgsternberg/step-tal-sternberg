<%--Class Imports--%>
<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="com.rtb.projectmanagementtool.user.*"%>
<%@ page import="com.rtb.projectmanagementtool.privatecomment.*"%>
<%@ page import="java.util.*"%>

<%--Get variables--%>
<%
    UserData user = (UserData) request.getAttribute("UserData");
    ArrayList<TaskData> userTasks = (ArrayList<TaskData>) request.getAttribute("UserTasks");
    HashMap<Long, PrivateCommentData> privateCommentsMap = (HashMap <Long, PrivateCommentData>) request.getAttribute("privateCommentsMap");
    boolean currUser = (boolean) request.getAttribute("currentUser");
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
        <div id="user-page-container"><h1><%=user.getUserName()%></h1></div>
      </div>
      <div id="user-name-container"><p>Name: <%=user.getUserName()%></p></div>
      <div id="user-year-container"><p>Graduation Year: <%=user.getUserYear()%></p></div>
      <div id="user-majors-container"><p>Majors: <%=user.getUserMajors()%></p></div>
      <div id="user-skills-container"><p>Top Skill: <%=user.getUserSkills()%></p></div>
       <div id="user-total-tasks-container">
          <p>Total Completed Tasks: <%=user.getUserTotal()%></p>
      </div>
      <div id="task-subtasks-container">
        <%request.setAttribute("tasks", userTasks);%>
        <%request.setAttribute("privateCommentsMap", privateCommentsMap);%>
        <%request.setAttribute("currUser", currUser);%>
        <jsp:include page="user-private-comments.jsp"/>
      </div>
    </div>
  </body>
</html>
