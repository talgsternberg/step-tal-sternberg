/** Servlet responsible for user_settings */
package com.rtb.projectmanagementtool.user;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.rtb.projectmanagementtool.auth.*;
import com.rtb.projectmanagementtool.task.*;
import com.rtb.projectmanagementtool.user.UserData.Skills;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-settings")
public class UserSettingsServlet extends HttpServlet {
  DatastoreService datastore;
  AuthOps auth;

  public UserSettingsServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    auth = new AuthOps(datastore);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Authenticate
    // shouldn't need this line
    // auth.loginUser(request, response);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == AuthOps.NO_LOGGED_IN_USER) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // NON TESTING: ONCE EVERYTHING IS SET UP

    // new UserController
    UserController userController = new UserController(datastore);
    // get user by ID long
    long userID = auth.whichUserIsLoggedIn(request, response);
    UserData user = userController.getUserByID(userID);

    // make a string of majors
    String majorsString = "";
    for (String major : user.getUserMajors()) {
      if (majorsString == "") {
        majorsString = major;
      } else {
        majorsString = majorsString + "," + major;
      }
    }
    majorsString = majorsString.replaceAll("\\s", "");

    // pre checked buttons
    String[] checkedStatus = {"", "", "", "", "", "", ""};
    Skills getSkills = user.getUserSkills();
    String skillsAsString = getSkills.name();

    // currently not using: look up "switch-case"
    if (skillsAsString.equals("NONE")) {
      checkedStatus[0] = "checked";
    } else if (skillsAsString.equals("LEADERSHIP")) {
      checkedStatus[1] = "checked";
    } else if (skillsAsString.equals("ORGANIZATION")) {
      checkedStatus[2] = "checked";
    } else if (skillsAsString.equals("WRITING")) {
      checkedStatus[3] = "checked";
    } else if (skillsAsString.equals("ART")) {
      checkedStatus[4] = "checked";
    } else if (skillsAsString.equals("WEBDEV")) {
      checkedStatus[5] = "checked";
    } else {
      checkedStatus[6] = "checked";
    }

    // Set attributes of request; retrieve in jsp with
    request.setAttribute("settings", user);
    request.setAttribute("checkedStatus", checkedStatus);
    request.setAttribute("majorsSettings", majorsString);

    // Load jsp for user page
    request.getRequestDispatcher("user-settings.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Authenticate
    auth.loginUser(request, response);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);

    if (userLoggedInId == /*No user found*/ AuthOps.NO_LOGGED_IN_USER) { // changed from -1l
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // NON TESTING: ONCE EVERYTHING IS SET UP

    // new UserController
    UserController userController = new UserController(datastore);

    // get user by ID long
    long userID = auth.whichUserIsLoggedIn(request, response);
    UserData user = userController.getUserByID(userID);

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
    user.setUserName(userName);
    user.setUserYear(userYear);
    user.setUserMajors(userMajors);
    user.setUserSkills(skills);

    // update in datastore
    userController.updateUser(user);

    UserData updatedUser = userController.getUserByID(userID);

    response.sendRedirect("/user-profile");
  }
}
