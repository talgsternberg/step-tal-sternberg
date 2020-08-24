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
    <%for (TaskData task : tasks) {%>
      <li class="task">
        <%if (task.getStatus().name().equals("COMPLETE")) {%>
          <button class="complete" type="button" onclick="location.href='task?taskID=<%=task.getTaskID()%>'">
        <%} else if (task.getStatus().name().equals("INCOMPLETE")){%>
          <button class="incomplete" type="button" onclick="location.href='task?taskID=<%=task.getTaskID()%>'">
        <%}%>
          <%=task.getName()%>
        </button>
        <form class="inline" id="add-private-comment-post-form" action="/user-profile" method="POST">
          <input type="hidden" id="add-private-comment-task-input" name="taskID" value="<%=task.getTaskID()%>">
          <br>
          <input class="private-comment" type="text" name="message" value="<%=(privateCommentsMap.get(task.getTaskID())).getMessage().trim()%>"></input>
          <br>
          <button class="deep-button" type="submit" class="deep-button">Update</button>
        </form>
        <%request.setAttribute("task", task);%>
        <br><br>
      </li>
    <%}%>
  <%}%>
</ul>