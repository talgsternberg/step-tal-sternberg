<%--Class Imports--%>
<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="com.rtb.projectmanagementtool.privatecomment.*"%>
<%@ page import="java.util.*"%>

<%--Get variables--%>
<%
    List<TaskData> tasks = (List<TaskData>)(List<?>) request.getAttribute("tasks");
    ArrayList<PrivateCommentData> privateComments = (ArrayList<PrivateCommentData>) request.getAttribute("privateComments");
%>

<%--HTML--%>
<ul>
  <%for (TaskData task : tasks) {%>
    <li class="task">
      <%if (task.getStatus().name().equals("COMPLETE")) {%>
        <button class="complete" type="button" onclick="location.href='task?taskID=<%=task.getTaskID()%>'">
      <%} else if (task.getStatus().name().equals("INCOMPLETE")){%>
        <button class="incomplete" type="button" onclick="location.href='task?taskID=<%=task.getTaskID()%>'">
      <%}%>
        <%=task.getName()%>
      </button>
      <td class="inline"><input type="text" name="<%=task.getName()%>" value=<%=privateComments.get(something)%> /></td>
      <br><br>
      <%request.setAttribute("task", task);%>
    </li>
  <%}%>
</ul>