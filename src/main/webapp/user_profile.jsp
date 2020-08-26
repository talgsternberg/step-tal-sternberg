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
    String userMajorsString = (String) request.getAttribute("userMajorsString");
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
        <%if (currUser) {%>
          <div id="user-page-container"><h1>Your User Profile</h1></div>
        <%} else {%>
          <div id="user-page-container"><h1><%=user.getUserName()%>'s User Profile</h1></div>
        <%}%>
      </div>
      <div class="user-description">
        <ul class="user-desc-list">
          <li id="user-inviteCode-container"><p>Invite Code: <%=user.getInviteCode()%></p></li><br>
          <li id="user-name-container"><p>Name: <%=user.getUserName()%></p></li><br>
          <li id="user-year-container"><p>Graduation Year: <%=user.getUserYear()%></p></li><br>
          <li id="user-majors-container"><p>Majors: <%=userMajorsString %></p></li><br>
          <li id="user-skills-container"><p>Top Skill: <%=user.getUserSkills()%></p></li><br>
           <li id="user-total-tasks-container">
              <p>Total Completed Tasks: <%=user.getUserTotal()%></p>
          </li>
        </ul>
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
