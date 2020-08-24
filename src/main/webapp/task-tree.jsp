<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="java.util.List"%>

<%--Get variables--%>
<%
    List<TaskTreeNode> taskTree = (List<TaskTreeNode>)(List<?>) request.getAttribute("taskTree");
    boolean select = (boolean) request.getAttribute("select");
    String taskType = "";
    if (select == true) {
        taskType = (String) request.getAttribute("taskType");
    }
%>

<%--HTML--%>
<% if (select == true) { %>
<ul class="task-tree">
  <%for (TaskTreeNode taskTreeNode : taskTree) {%>
    <li>
      <% if (!taskTreeNode.getSubtasks().isEmpty()) { %>
      <span class="task-tree-node">
        <label for="<%=taskType%>-<%=taskTreeNode.getParentTask().getTaskID()%>" class="inline <%=taskType%>"><%=taskTreeNode.getParentTask().getName()%></label>
        <input type="radio" id="<%=taskType%>-<%=taskTreeNode.getParentTask().getTaskID()%>" class="hidden" name="<%=taskType%>" value="<%=taskTreeNode.getParentTask().getTaskID()%>" required>
      </span>
      <%request.setAttribute("taskTree", taskTreeNode.getSubtasks());%>
      <jsp:include page="task-tree.jsp"/>
      <% } else { %>
      <span class="task-tree-leaf-node">
        <label for="<%=taskType%>-<%=taskTreeNode.getParentTask().getTaskID()%>" class="inline <%=taskType%>"><%=taskTreeNode.getParentTask().getName()%></label>
        <input type="radio" id="<%=taskType%>-<%=taskTreeNode.getParentTask().getTaskID()%>" class="hidden" name="<%=taskType%>" value="<%=taskTreeNode.getParentTask().getTaskID()%>" required>
      </span>
      <% } %>
    </li>
  <% } %>
</ul>
<% } else { %>
<ul class="task-tree">
  <%for (TaskTreeNode taskTreeNode : taskTree) {%>
    <li>
      <% if (!taskTreeNode.getSubtasks().isEmpty()) { %>
      <span class="task-tree-node"><%=taskTreeNode.getParentTask().getName()%></span>
      <%request.setAttribute("taskTree", taskTreeNode.getSubtasks());%>
      <jsp:include page="task-tree.jsp"/>
      <% } else { %>
      <span class="task-tree-leaf-node"><%=taskTreeNode.getParentTask().getName()%></span>
      <% } %>
    </li>
  <% } %>
</ul>
<% } %>