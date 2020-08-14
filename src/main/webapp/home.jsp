<%@ page import="com.rtb.projectmanagementtool.project.*" %>
<%@ page import="com.rtb.projectmanagementtool.user.*" %>
<%@ page import="java.util.ArrayList" %>

<%
  ArrayList<ProjectData> userProjects = (ArrayList<ProjectData>) request.getAttribute("userProjects");
  UserData user = (UserData) request.getAttribute("user");
%>

<html>
  <head>
    <meta charset="UTF-8">
    <title>Home page</title>
    <link rel="stylesheet" href="style.css">
  </head>

  <body>
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp" />

    <p>Hi, <%=user.getUserName() %></p>
    <div id="project-container">
      <%for (ProjectData project : userProjects) {%>
        <a href="/project?id=<%=project.getId()%>">
          <button><%=project.getName()%></button>
        </a>
      <%}%>
    <button>Create Project</button>
    </div>
  </body>
</html>
