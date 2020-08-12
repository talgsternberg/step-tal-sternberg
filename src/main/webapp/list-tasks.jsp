<%--Class Imports--%>
<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="java.util.ArrayList"%>

<%--Get variables--%>
<%
    Gson gson = new Gson();
    ArrayList<TaskData> tasks = (ArrayList<TaskData>) gson.fromJson(request.getParameter("tasks"), ArrayList.class);
%>

<%--HTML--%>
<ul>
  <%for (TaskData task : tasks) {%>
    <li class="task">
      <button type="button" class="inline" href="task.jsp?taskID=<%=task.getTaskID()%>">
        <%=task.getName()%>
      </button>
      <p class="inline"><%=task.getDescription()%></p>
    </li>
  <%}%>
</ul>