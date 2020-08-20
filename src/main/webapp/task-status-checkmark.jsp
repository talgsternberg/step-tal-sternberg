<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="com.rtb.projectmanagementtool.task.TaskData.Status"%>

<%--Get variables--%>
<%
    TaskData task = (TaskData) request.getAttribute("task");
    boolean clickable = (boolean) request.getAttribute("clickable");
%>

<%--HTML--%>
<%
    if (clickable) {
        String check;
        String status;
        String availability = "";
        String text;
        if (task.getStatus() != Status.COMPLETE) {
            check = "unchecked";
            status = Status.COMPLETE.name();
            text = "Click to set status as Complete";
            if (!(boolean) request.getAttribute("canSetComplete")) {
                availability = "unavailable";
                text = "Cannot set task's status to complete until all of its subtasks are set to complete.";
            }
        } else {
            check = "checked";
            status = Status.INCOMPLETE.name();
            text = "Click to set status as Incomplete";
            if (!(boolean) request.getAttribute("canSetIncomplete")) {
                availability = "unavailable";
                text = "Cannot set task's status to incomplete if its parent task is set to incomplete.";
            }
        } 
%>
<form id="toggle-status-post-form" action="/task-set-status" method="POST" class="inline">
  <input type="hidden" name="taskID" value="<%=task.getTaskID()%>"/>
  <input type="hidden" name="status" value="<%=status%>"/>
  <button type="submit" id="task-toggle-status" class="checkmark-button">
    <h1 class="has-hover-text <%=check%> <%=availability%>">
      &#10003
      <span class="hover-text"><%=text%></span>
    </h1>
  </button>
</form>
<%
    } else {
        String check;
        String text;
        if (task.getStatus() != Status.COMPLETE) {
            check = "unchecked";
            text = "Task status is Incomplete";
        } else {
            check = "checked";
            text = "Task status is Complete";
        }
%>
<h2 class="has-hover-text inline no-margin <%=check%>">
  &#10003
  <span class="hover-text"><%=text%></span>
</h2>
<% } %>