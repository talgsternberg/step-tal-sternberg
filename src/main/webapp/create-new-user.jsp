<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.user.*,java.util.ArrayList" %>


<%--HTML--%>
<html>
  <head>
    <meta charset="UTF-8">
    <title>New User</title>
    <link rel="stylesheet" href="style.css">
  </head>

  <body>
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp" />

    <!-- Page content -->
    <div id="content">
      <div id="title">
        <div id="user-page-container"><h1>Hi, New User!</h1></div>
      </div>
      <form action="/create-user" method="post">
        <tr>
          <td>Name:</td>
          <td><input type="text" name="userName" value="ex: Sarah Burke" /></td>
        </tr><br><br>
        <tr>
          <td>Class Year:</td>
          <td><input type="text" name="userYear" value="ex: 2020" /></td>
        </tr><br><br>
        <tr>
          <td>Majors (separate by commas no spaces)</td>
          <td><input type="text" name="userMajors" value="ex: Chemistry,Math" /></td>
        </tr><br><br>
        <tr>
          <td>Top Skill:</td><br>
          <td>
            <input type="radio" value="NONE" name="skills">
            <label>None</label><br>
            <input type="radio" value="LEADERSHIP" name="skills">
            <label>Leadership</label><br>
            <input type="radio" vlaue="ORGANIZATION" name="skills">
            <label>Organization</label><br>
            <input type="radio" value="WRITING" name="skills">
            <label>Writing</label><br>
            <input type="radio" value="ART" name="skills">
            <label>Art</label><br>
            <input type="radio" value="WEBDEV" name="skills">
            <label>WebDev</label><br>
            <input type="radio" value="OOP" name="skills">
            <label>OOP</label><br>
          </td>
        </tr><br><br>
        <input type="submit" value="Submit" />
      </form>
    </div>
  </body>
</html>
