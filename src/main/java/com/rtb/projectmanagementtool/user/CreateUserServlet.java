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

  DatastoreService datastore;
  Cookie cookie;
  UserService userService;

  public CreateUserServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
  }

  // for testing
  // public CreateUserServlet(DatastoreService datastore) {
  // this.datastore = datastore;
  // }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // new AuthOps object
    AuthOps auth = new AuthOps(datastore);

    // new UserController object
    UserController controller = new UserController(datastore);

    // create new "blank" UserData object
    UserData newUser = controller.createNewUser();

    // get AuthID
    String AuthID = auth.getAuthID();

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

    // create entity and put in datastore. Get userID
    long userID = controller.addUser(newUser);

    // get/create cookie and set value to userID
    auth.setLoggedInCookie(request, response, userID);

    // redirect them to their new profile
    response.sendRedirect("/user_profile.jsp");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get login URL
    String urlToRedirectToAfterUserLogsIn = "/create-new-user";
    String loginUrlNewUser = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

    // send to request
    request.setAttribute("loginUrlNewUser", loginUrlNewUser);
    request.getRequestDispatcher("login.jsp").forward(request, response);

    // redirect them to their new form
    response.sendRedirect("/create-new-user.jsp");
  }
}
