<%@ page import="com.rtb.projectmanagementtool.project.*" %>

<%
    Long userId = (Long) request.getAttribute("userId");
    ProjectData project = (ProjectData) request.getAttribute("project");
%>

<form action="/add-user-to-project" method="POST">
  <input type="hidden" name="projectId" value=<%=project.getId()%>>
  <ul>
    <li>
      <input type="text" name="user-name" placeholder="UserName" /> 
    </li>
    <li>
      <label>Role</label>
      <select name="user-role" class="field-select">
      <% if (project.isCreator(userId)) { %>
        <option value="admin">admin</option>
      <% } %>
      <option value="member">member</option>
      </select>
    </li>
    <li>
        <input type="submit" value="Add user" />
    </li>
  </ul>
</form>