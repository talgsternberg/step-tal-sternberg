<html>
  <head>
    <meta charset="UTF-8">
    <title>Create Project</title>
    <link rel="stylesheet" href="style.css">
  </head>
  <body>
    <jsp:include page="navigation-bar.jsp" />

    <form id="create-project-form" action="/create-project" method="POST">
      <input type="hidden" id="userId" name="userId" value=${userId}>
      <label for="project-name">Project name:</label><br>
      <input type="text" id="project-name" name="project-name"><br>
      <label for="project-desc">Project description:</label><br>
      <textarea id="project-desc" name="project-desc"></textarea><br><br>
      <input type="submit" value="Create Project">
    </form>
  </body>
</html>
