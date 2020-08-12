/** Servlet responsible for loading project page */
package com.rtb.projectmanagementtool.project;

import com.rtb.projectmanagementtool.project.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/project")
public class ProjectServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Authentication goes here
    // if (something something something) {
    //   // Redirect to /login servlet if authentication fails
    //   request.getRequestDispatcher("/login").forward(request, response);
    //   return;
    // }

    // Create hard-coded project
    Long projectId = Long.parseLong(request.getParameter("id"));
    String projectName = new String("Project " + projectId);
    String projectDesc = projectName + " Description";

    ProjectData project = new ProjectData(projectName, projectDesc, /*random value*/ 2l);
    project.setId(projectId);
    project.addAdminUser(5l);
    project.addAdminUser(3l);
    project.addMemberUser(1l);

    // Create hard-coded project Tasks
    ArrayList<Long> projectTasks = new ArrayList<Long>(Arrays.asList(1l, 2l, 3l, 4l, 5l));

    // Set attributes of request; retrieve in jsp with
    // ([type]) request.getAttribute([attribute name]);
    request.setAttribute("project", project);
    request.setAttribute("projectTasks", projectTasks);

    // Load jsp for project page
    request.getRequestDispatcher("project.jsp").forward(request, response);
  }
}
