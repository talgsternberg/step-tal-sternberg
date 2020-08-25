package com.rtb.projectmanagementtool.auth;

import com.google.appengine.api.datastore.*;
import com.rtb.projectmanagementtool.user.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    AuthOps auth = new AuthOps(datastore);

    String authID = auth.getAuthID();
    // If user has no auth id,
    if (authID != null) {
      auth.loginUser(request, response); // try to log in
      UserController userController = new UserController(datastore);
      UserData user = userController.getUserByAuthID(authID);

      if (userController.getUserByAuthID(authID)
          == null) { // If user doesn't exist, redirect to /create-user
        response.sendRedirect("/create-user");
      } else { // If user exists, redirect to /home
        response.sendRedirect("/home");
      }
    }

    // Get login URL
    request.setAttribute("loginUrl", auth.getLoginLink(/*Return URL*/ "/login"));

    // Forward to login page
    request.getRequestDispatcher("login.jsp").forward(request, response);
    return;
  }
}
