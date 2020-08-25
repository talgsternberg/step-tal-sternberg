<%@ page import="com.rtb.projectmanagementtool.project.*" %>

<%
    Long userId = (Long) request.getAttribute("userId");
    ProjectData project = (ProjectData) request.getAttribute("project");
%>

<div class="modal add-user-to-project">
  <div class="modal-content">
    <div class="modal-close add-user-to-project"><i class="fas fa-times"></i></div>
      <h3>Add user to project</h3>
      <input type="hidden" name="projectId" value=<%=project.getId()%>>
      <div>
        <label>Invite Code</label>
        <input title="Enter the user's invite code" type="text" id="user-invite-code" name="user-invite-cide" placeholder="User invite code" /> 
      </div>
      <div>
        <label>Role</label>
        <select title="Select the role to add user as" id="user-role" name="user-role" class="field-select">
          <% if (project.isCreator(userId)) { %>
          <option value="admin">admin</option>
          <% } %>
        <option value="member">member</option>
        </select>
      </div>
    <button onclick="addUserToProject(<%=project.getId()%>)">Add user</button>
  </div>
</div>
