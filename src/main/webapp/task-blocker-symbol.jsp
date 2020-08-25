<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="java.util.ArrayList"%>

<%--Get variables--%>
<%
    ArrayList<TaskData> blockers = (ArrayList<TaskData>) request.getAttribute("blockers");
%>

<%--HTML--%>
<%
    String fill;
    String text;
    if (blockers.isEmpty()) {
        fill = "unfilled";
        text = "Task is not blocked";
    } else {
        fill = "filled";
        text = "Task is blocked by";
    }
%>
<h2 class="has-hover-text inline no-margin <%=fill%>">
  <span class="fa fa-ban" aria-hidden="true"></span>
  <span class="hover-text">
    <%=text%>
    <% for (TaskData blocker : blockers) { %>
    <a href="task?taskID=<%=blocker.getTaskID()%>"><%=blocker.getName()%></a>
    <% } %>
  </span>
</h2>