<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.project.*,java.util.ArrayList" %>

<%--Get variables from servlet--%>
<%
  ArrayList<ProjectData> userProjects = (ArrayList<ProjectData>) request.getAttribute("userProjects");
%>

<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Home page</title>
    <link rel="stylesheet" href="style.css">
  </head>

  <body>
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp" />

    <p>Hello ${user}</p>
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
