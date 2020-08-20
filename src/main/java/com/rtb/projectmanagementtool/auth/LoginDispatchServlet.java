// Servlet called after a user logs in with Google. Tt checks to see if
// their authId matches any entities in datastore, in which case it
// would direct the user to the home page. If not, it directs them to
// the page to create a new user.
// Might need a better name than Login'Dispatch'Servlet

package com.rtb.projectmanagementtool.auth;

import com.google.appengine.api.datastore.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login-dispatch")
public class LoginDispatchServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    AuthOps auth = new AuthOps(datastore);

    // log in with AuthOps to set cookies
    long userId = auth.loginUser(request, response);
    // made auth.loginUser return userId because calling
    // auth.whichUserIsLoggedIn() here returns -1 when it shouldn't;
    // I'm guessing this is because in AuthOps, the cookie is being added to the
    // response during loginUser(), but whichUserIsLoggedIn() looks for the cookies
    // in request

    // If user successfully logged in with google but their entity doesn't exist,
    // send them to the create user page
    if (userId == AuthOps.NO_LOGGED_IN_USER) {
      response.sendRedirect("/create-user");
      return;
    }

    // If user successfully logged in with google and their entity exists, send to home page
    response.sendRedirect("/home");
  }
}
