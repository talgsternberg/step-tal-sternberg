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
          <td><input type="text" name="userMajors" value=<%=majors%> /></td>
        </tr><br><br>
        <tr>
          <td>Top Skill:</td><br>
          <td>
            <input type="radio" value="none" name="skills">
            <label>None</label><br>
            <input type="radio" value="leadership" name="skills">
            <label>Leadership</label><br>
            <input type="radio" vlaue="organization" name="skills">
            <label>Organization</label><br>
            <input type="radio" value="writing" name="skills">
            <label>Writing</label><br>
            <input type="radio" value="art" name="skills">
            <label>Art</label><br>
            <input type="radio" value="webdev" name="skills">
            <label>WebDev</label><br>
            <input type="radio" value="oop" name="skills">
            <label>OOP</label><br>
          </td>
        </tr><br><br>
        <input type="submit" value="Submit" />
      </form>
    </div>
  </body>
</html>
