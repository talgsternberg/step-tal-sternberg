/** Servlet responsible for getting data for loading home page */
package com.rtb.projectmanagementtool.home;

import com.rtb.projectmanagementtool.project.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Mock authentication
    if (!request.getParameterMap().containsKey("user")) {
      // Redirect to /login servlet if authentication fails
      request.getRequestDispatcher("/login").forward(request, response);
      return;
    }

    // Create hard-coded projects
    ArrayList<ProjectData> userProjects = new ArrayList<ProjectData>();
    ProjectData project1 = new ProjectData("CS Project", "Description for the CS project", 1l);
    ProjectData project2 = new ProjectData("Bio Project", "Description for the Bio project", 2l);
    ProjectData project3 =
        new ProjectData("English Project", "Description for the English project", 1l);

    project1.setId(1l);
    project2.setId(2l);
    project3.setId(3l);

    userProjects.add(project1);
    userProjects.add(project2);
    userProjects.add(project3);
    // Projects are not added to database

    // Set attributes of request; retrieve in jsp with
    // ([type]) request.getAttribute([attribute name]);
    request.setAttribute("user", "Jones");
    request.setAttribute("userProjects", userProjects);

    // Load jsp for home page
    request.getRequestDispatcher("home.jsp").forward(request, response);
  }
}
