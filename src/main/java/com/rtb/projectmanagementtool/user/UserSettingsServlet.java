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
    auth.loginUser(request, response);
    Long userLoggedInId = auth.whichUserIsLoggedIn(request, response);
    if (userLoggedInId == /*No user found*/ -1l) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // HARDCODE FOR TESTING

    // initialize/fill hardcoded userMajors
    // ArrayList<String> majors = new ArrayList<>();
    // majors.add("Chemistry");
    // majors.add("Studio Art");

    // Create hardcoded user
    // long userID = Long.parseLong(request.getParameter("userID"));
    // String AuthID = "abc";
    // String userName = "Name1";
    // long userYear = 2023;
    // ArrayList<String> userMajors = majors;
    // Skills skills = Skills.OOP;
    // long userTotalCompTasks = 3;
    // UserData user =
    // new UserData(AuthID, userName, userYear, userMajors, skills, userTotalCompTasks);
    // user.setUserID(userID);

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

    // make enum ArrayList<String>
    // Skills userSkills = user.getUserSkills();
    // Skills[] skillsArray = userSkills.values();
    // ArrayList<String> skillsStringList = new ArrayList<>();
    // for (Skills skill : skillsArray) {
    // skillsStringList.add(skill.name());
    // }

    // pre checked buttons
    ArrayList<String> checkedStatus = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));
    Skills getSkills = user.getUserSkills();
    String skillsAsString = getSkills.name();
    if (skillsAsString.equals("NONE")) {
      checkedStatus.set(0, "checked");
    } else if (skillsAsString.equals("LEADERSHIP")) {
      checkedStatus.set(1, "checked");
    } else if (skillsAsString.equals("ORGANIZATION")) {
      checkedStatus.set(2, "checked");
    } else if (skillsAsString.equals("WRITING")) {
      checkedStatus.set(3, "checked");
    } else if (skillsAsString.equals("ART")) {
      checkedStatus.set(4, "checked");
    } else if (skillsAsString.equals("WEBDEV")) {
      checkedStatus.set(5, "checked");
    } else {
      checkedStatus.set(6, "checked");
    }

    // Set attributes of request; retrieve in jsp with
    request.setAttribute("settings", user);
    // request.setAttribute("checkedStatus", checkedStatus);
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
    if (userLoggedInId == /*No user found*/ -1l) {
      // If no user found, redirect to create user servlet
      response.sendRedirect("/login");
      return;
    }

    // TESTING ONLY
    // ArrayList<String> majors = new ArrayList<>();
    // majors.add("Chemistry");
    // majors.add("Studio Art");

    // Create hardcoded user
    // long userID1 = 2l;
    // String AuthID1 = "abc";
    // String userName1 = "Name1";
    // long userYear1 = 2023;
    // ArrayList<String> userMajors1 = majors;
    // Skills skills1 = Skills.OOP;
    // long userTotalCompTasks1 = 3;
    // UserData user =
    // new UserData(AuthID1, userName1, userYear1, userMajors1, skills1, userTotalCompTasks1);
    // user.setUserID(userID1);

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

    response.sendRedirect("/user_profile.jsp");
  }
}
