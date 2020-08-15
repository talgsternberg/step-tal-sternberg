// Servlet for logging out

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

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Clear cookie
    AuthOps auth = new AuthOps(datastore);
    auth.logoutUser(request, response);

    response.sendRedirect(auth.getLogoutLink(/*Return URL*/ "/login"));
  }
}
