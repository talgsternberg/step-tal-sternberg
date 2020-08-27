<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.user.*,java.util.ArrayList" %>

<%--Get variables--%>
<%
    UserData user = (UserData) request.getAttribute("settings");
    String majors = (String) request.getAttribute("majorsSettings");
    String[] checkedStatus = (String[]) request.getAttribute("checkedStatus");
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
        <div id="user-page-container"><h1>Your Settings</h1></div>
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
            <input type="radio" value="NONE" name="skills" <%=checkedStatus[0]%>>
            <label>None</label><br>
            <input type="radio" value="LEADERSHIP" name="skills" <%=checkedStatus[1]%>>
            <label>Leadership</label><br>
            <input type="radio" value="ORGANIZATION" name="skills" <%=checkedStatus[2]%>>
            <label>Organization</label><br>
            <input type="radio" value="WRITING" name="skills" <%=checkedStatus[3]%>>
            <label>Writing</label><br>
            <input type="radio" value="ART" name="skills" <%=checkedStatus[4]%>>
            <label>Art</label><br>
            <input type="radio" value="WEBDEV" name="skills" <%=checkedStatus[5]%>>
            <label>WebDev</label><br>
            <input type="radio" value="OOP" name="skills" <%=checkedStatus[6]%>>
            <label>OOP</label><br>
          </td>
        </tr><br><br>
        <input type="submit" value="Submit" />
      </form>
    </div>
  </body>
</html>
