<%@ page import="com.rtb.projectmanagementtool.project.*" %>
<%@ page import="com.rtb.projectmanagementtool.user.*" %>
<%@ page import="com.rtb.projectmanagementtool.task.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashSet" %>

<%
    Long userId = (Long) request.getAttribute("userId");
    ProjectData project = (ProjectData) request.getAttribute("project");
    UserData creator = (UserData) request.getAttribute("creator");
    HashSet<UserData> admins =  (HashSet<UserData>) request.getAttribute("admins");
    HashSet<UserData> members =  (HashSet<UserData>) request.getAttribute("members");
    ArrayList<TaskData> tasks = (ArrayList<TaskData>) request.getAttribute("tasks");
%>

<html>
  <head>
    <meta charset="UTF-8">
    <title><%=project.getName()%></title>
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.14.0/css/all.css">
    <script defer src="scripts/project.js"></script>
  </head>
  <body onload="initEventListeners()">
    <!-- Include navigation bar -->
    <jsp:include page="navigation-bar.jsp" />

    <!-- Page content -->
    <div class="project-page-top-bar">
    <div class="project-page-header">
        <div class="page-header-title-and-actions">
        <h1 id="main-project-name"><%=project.getName()%></h1>
        <div class="page-header-actions-selector" >
            <a href="#"><i id="angle-down" class="fas fa-angle-down"></i></a>
            <div class="page-header-actions">
            <ul>
                <li class="action-list-item"><a href="#"><i class="fas fa-info"></i><p>Toggle description</p></a></li>
                <% if (project.isCreator(userId) || project.hasAdmin(userId)) { %>
                <li class="action-list-item"><a href="#"><i class="fas fa-edit"></i><p>Edit project details</p></a></li>
                <% } %>
                <% if (project.isCreator(userId)) { %>
                <li class="action-list-item"><a href="#"><i class="fas fa-check"></i><p id="set-project">Complete Project</p></a></li>
                <li class="action-list-item"><a href="#"><i class="far fa-trash-alt"></i><p>Delete project</p></a></li>
                <% } %>
            </ul>
            </div>
        </div>
        </div>
        <div class="page-header-description">
        <p id="main-project-description"><%=project.getDescription()%></p>
        </div>
        <div class="page-header-nav">
        <div class="project-header-tab tasks active">
            <i class="fas fa-tasks"></i>
            <p>Tasks</p>
        </div>
        <div class="project-header-tab users">
            <i class="fas fa-users"></i>
            <p>Users</p>
        </div>
        </div>
    </div>
    </div>
    
    <div id="project-tasks-container">
    <h2>Tasks</h2>
    <%request.setAttribute("tasks", tasks);%>
    <jsp:include page="list-tasks.jsp"/>
    </div>
    <div id="project-addtask-container" class="inline">
    <button type="button" class="deep-button" onclick="location.href='add-task.jsp?projectID=<%=project.getId()%>&projectName=<%=project.getName()%>&taskID=0&taskName=null'">
        Add Task
    </button>
    </div>
    <div id="project-tasktree-button-container" class="inline">
    <button id="tasktree-button" class="deep-button">View Task Tree</button>
    </div>
    <div id="project-tasktree-container" class="popup">
    <div class="popup-content">
        <span class="close">&times;</span>
        <h2>Task Tree</h2>
        <%request.setAttribute("projectID", project.getId());%>
        <%request.setAttribute("select", false);%>
        <jsp:include page="/task-tree"/>
    </div>
    </div>
    <div id="project-addtaskblocker-container" class="inline">
    <button id="addtaskblocker-button" class="deep-button" onclick="location.href='add-task-blocker.jsp?projectID=<%=project.getId()%>&projectName=<%=project.getName()%>'">
        Add Task Blocker
    </button>
    </div>

  </body>
</html>
