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
    <title>Home</title>
    <link rel="stylesheet" href="style.css">
  </head>

  <body>
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp" />

    <div class="project-container">
      <%for (ProjectData project : userProjects) { %>
        <div class="project-box">
        <a href="/project?id=<%=project.getId()%>">
          <div class="project-box-content">
            <h3 class="project-name"><%=project.getName()%></h3>
          </div>
        </a>
        </div>
      <%}%>
      <div class="project-box">
      <a href="/create-project">
        <div class="project-box-content">
          <h3>Add Project</h3>
        </div>
      </a>
      </div>
    </div>
  </body>
</html>
