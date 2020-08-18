<%--Class Imports--%>
<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.rtb.projectmanagementtool.user.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.List"%>

<%--Get variables--%>
<%
    List<UserData> users = (List<UserData>)(List<?>) request.getAttribute("users");
%>

<%--HTML--%>
<ul>
  <%for (UserData user : users) {%>
    <li class="user inline">
      <button type="button" class = "flat-button" onclick="location.href='user-profile'">
        <h3><%=user.getUserName()%></h3>
        <p>Major: <%=user.getUserMajors()%></p>
        <p>Graduation Year: <%=user.getUserYear()%></p>
      </button>
    </li>
  <%}%>
</ul>