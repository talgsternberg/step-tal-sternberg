<%--Class Imports--%>
<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.List"%>

<%--Get variables--%>
<%
    List<TaskData> tasks = (List<TaskData>)(List<?>) request.getAttribute("tasks");
%>

<%--HTML--%>
<ul>
  <%for (TaskData task : tasks) {%>
    <li class="task">
      <button type="button" class="inline deep-button" onclick="location.href='task?taskID=<%=task.getTaskID()%>'">
        <%=task.getName()%>
      </button>
      <p class="inline"><%=task.getDescription()%></p>
    </li>
  <%}%>
</ul>