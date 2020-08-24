<%--Class Imports--%>
<%@ page import="com.rtb.projectmanagementtool.task.*"%>
<%@ page import="java.util.List"%>

<%--Get variables--%>
<%
    List<TaskTreeData> taskTree = (List<TaskTreeData>)(List<?>) request.getAttribute("taskTree");
%>

<%--HTML--%>
<ul class="task-tree">
  <%for (TaskTreeData taskTreeNode : taskTree) {%>
    <li>
      <% if (!taskTreeNode.getSubtasks().isEmpty()) { %>
      <span class="task-tree-node"><%=taskTreeNode.getTask().getName()%></span>
      <%request.setAttribute("taskTree", taskTreeNode.getSubtasks());%>
      <jsp:include page="task-tree.jsp"/>
      <% } else { %>
      <span class="task-tree-leaf-node"><%=taskTreeNode.getTask().getName()%></span>
      <% } %>
    </li>
  <% } %>
</ul>