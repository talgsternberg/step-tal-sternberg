<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.user.*,java.util.ArrayList" %>

<%--Get variables--%>
<%
    UserData user = (UserData) request.getAttribute("settings");
    ArrayList<String> skills = (ArrayList<String>) request.getAttribute("skillsSettings");
    String majors = (String) request.getAttribute("majorsSettings");
%>

<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title><%=user.getUserName()%>'s Settings</title>
    <link rel="stylesheet" href="style.css">
  </head>

  <body>
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp" />

    <!-- Page content -->
    <div id="content">
      <div id="title">
        <div id="user-page-container"><h1><%=user.getUserName()%>'s Settings</h1></div>
      </div>
      <form action="/user-settings" method="post">
        <tr>
          <td>Name:</td>
          <td><input type="text" name="userName" value=<%=user.getUserName()%> /></td>
        </tr><br><br>
        <tr>
          <td>Class Year:</td>
          <td><input type="text" name="userYear" value=<%=user.getUserYear()%> /></td>
        </tr><br><br>
        <tr>
          <td>Majors (separate by commas no spaces)</td>
          <td><input type="text" name="userYear" value=<%=majors%> /></td>
        </tr><br><br>
        <tr>
          <td>Skills</td><br>
          <td>
            <input type="checkbox" name="NONE">
            <label for="NONE">None</label><br>
            <input type="checkbox" name="LEADERSHIP">
            <label for="LEADERSHIP">Leadership</label><br>
            <input type="checkbox" name="ORGANIZATION">
            <label for="ORGANIZATION">Organization</label><br>
            <input type="checkbox" name="WRITING">
            <label for="WRITING">Writing</label><br>
            <input type="checkbox" name="ART">
            <label for="ART">Art</label><br>
            <input type="checkbox" name="WEBDEV">
            <label for="WEBDEV">WebDev</label><br>
            <input type="checkbox" name="OOP">
            <label for="OOP">OOP</label><br>
          </td>
        </tr><br><br>
        <input type="submit" value="Submit" />
      </form>
    </div>
  </body>
</html>
