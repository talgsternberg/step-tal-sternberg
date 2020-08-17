// Servlet for loading login page

package com.rtb.projectmanagementtool.auth;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.*;
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

    // add cookie w UserID to response
    auth.loginUser(request, response);

    // Don't view login page if user is logged in
    if (auth.whichUserIsLoggedIn(request, response) != Long.parseLong(AuthOps.NO_LOGGED_IN_USER)) {
      response.sendRedirect("/home");
      return;
    }

    // Get login URL
    request.setAttribute("loginUrl", auth.getLoginLink(/*Return URL*/ "/login"));

    // Get login URL for first time users (on submit returns to LoginServlet)
    request.setAttribute(
        "loginUrlNewUser", auth.getLoginLink(/*Return URL*/ "/create-new-user.jsp"));

    // Forward to login page
    request.getRequestDispatcher("login.jsp").forward(request, response);
  }
}
