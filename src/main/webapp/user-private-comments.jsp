<%--Class Imports--%>
<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="com.rtb.projectmanagementtool.privatecomment.*"%>
<%@ page import="java.util.*"%>



<%--Get variables--%>
<%
    List<TaskData> tasks = (List<TaskData>)(List<?>) request.getAttribute("tasks");
    HashMap<Long, PrivateCommentData> privateCommentsMap = (HashMap<Long, PrivateCommentData>) request.getAttribute("privateCommentsMap");
    boolean currUser = (boolean) request.getAttribute("currentUser");
%>

<%--HTML--%>
<ul>
  <%if (currUser) {%>
    <h1 id="pc-header">Your Private Task Comments:</h1>
    <%for (TaskData task : tasks) {%>
      <li class="task-with-pc">
        <button class="inline deep-button" type="button" onclick="location.href='task?taskID=<%=task.getTaskID()%>'">
          <%=task.getName()%>
        </button>
        <%request.setAttribute("task", task);%>
        <%request.setAttribute("clickable", false);%>
        <jsp:include page="task-status-checkmark.jsp"/>
        <p class="inline"><%=task.getDescription()%></p>
        <form action="/user-profile" method="POST">
          <input type="hidden" name="taskID" value="<%=task.getTaskID()%>">
          <br>
          <textarea style="width:300px; height:100px;" type="text" name="message-<%=task.getTaskID()%>" rows="6" cols="50"><%=(privateCommentsMap.get(task.getTaskID())).getMessage().trim()%></textarea>
          <br>
          <button class="deep-button" style="font-size: 12px;" type="submit" class="deep-button">Update Comment</button>
        </form>
        <br><br>
      </li>
    <%}%>
  <%}%>
</ul>