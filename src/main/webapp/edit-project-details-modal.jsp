<%@ page import="com.rtb.projectmanagementtool.project.*" %>

<%
    ProjectData project = (ProjectData) request.getAttribute("project");
%>

<div class="modal edit-project-details">
  <div class="modal-content">
    <div class="modal-close edit-project-details"><i class="fas fa-times"></i></div>
      <h3>Edit project details</h3>
      <input type="hidden" name="projectId" value=<%=project.getId()%>>
      <div>
        Project Name: <input type="text" id="project-name" name="projectName" value="<%=project.getName()%>" title="Update project title" placeholder="Project Name">
      </div>
      <div>
        Project Description:<br>
        <textarea id="project-desc" name="projectDesc" title="Update the description of the project" placeholder="Project Description"><%=project.getDescription()%></textarea> 
      </div>
      
    <button onclick="editProjectDetails(<%=project.getId()%>)">Save</button>
  </div>
</div>