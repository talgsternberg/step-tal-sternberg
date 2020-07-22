<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Home page</title>
  </head>
  <body>
      <jsp:include page="templates/header_template.jsp" />
      <jsp:include page="templates/content_layout.jsp">
        <jsp:param name="content" value="SOME OTHER PAGE" />
      </jsp:include>
      <jsp:include page="templates/footer_template.jsp" />
  </body>
</html>