package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.users.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.user.UserData.Skills;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Servlet that creates new user */
@WebServlet("/create-user")
public class CreateUserServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    AuthOps auth = new AuthOps(datastore);

    // new UserController object
    UserController controller = new UserController(datastore);

    // create new "blank" UserData object
    UserData newUser = controller.createNewUser();

    // get AuthID
    String AuthID = auth.getAuthID();
    String email = auth.getEmail();

    // get stuff from form update
    String userName = request.getParameter("userName");
    long userYear = Long.parseLong(request.getParameter("userYear"));
    String userMajorsString = request.getParameter("userMajors");
    String skillsString = request.getParameter("skills");

    // update skills
    Skills skills = Skills.valueOf(skillsString);

    // convert userMajors back to ArrayList
    String[] majorsSA = userMajorsString.split(",");
    ArrayList<String> userMajors = new ArrayList(Arrays.asList(majorsSA));

    // reset new values
    newUser.setUserName(userName);
    newUser.setUserYear(userYear);
    newUser.setUserMajors(userMajors);
    newUser.setUserSkills(skills);
    newUser.setAuthID(AuthID);
    newUser.setEmail(email);

    // create entity and put in datastore. Get userID
    long userID = controller.addUser(newUser);

    // get/create cookie and set value to userID
    auth.setLoggedInCookie(request, response, userID);

    // redirect them to home servlet
    response.sendRedirect("/home");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    AuthOps auth = new AuthOps(datastore);

    if (auth.whichUserIsLoggedIn(request, response) != AuthOps.NO_LOGGED_IN_USER) {
      response.sendRedirect("/home");
      return;
    }

    request.getRequestDispatcher("create-new-user.jsp").forward(request, response);
  }
}
