// Just using this Servlet to test the Project & ProjectController classes.
// I didn't know any other ways to test the classes other than through unit tests, which I
// couldn't get working.
// Feel free to mess around with it and test some of the functionality/see how it can be improved

package com.google.sps.servlets;

import com.rtb.projectmanagementtool.project.ProjectController;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/my-project-test")
public class ProjectTestServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Remove project:
    // projectController.removeProject(Long.parseLong("projectid"))
    // Add project:
    // projectController.createProject("name", "description", Long.parseLong("creatorId"));
    // Get project:
    // projectController.getProject(Long.parseLong("projectid"))
    // Print Project:
    // System.out.println("(description): \n" + projectController.getProjects().toString());

    //ProjectController projectController = new ProjectController();

    //System.out.println("Projects at start: \n" + projectController.getProjects().toString());
    //projectController.createProject(
    //    "Math", "workihng on derivative assignment", Long.parseLong("54672425"));
    // ProjectData mathProject = projectController.getProject(Long.parseLong("6473924464345088"));
    // mathProject.addUser(true, Long.parseLong("4673525"));
    // mathProject.addUser(false, Long.parseLong("75752354"));
    //System.out.println("Projects after change: \n" + projectController.getProjects().toString());
  }
}
