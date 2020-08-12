// Servlet for loading login page
// TODO: add doPost() for a user logging in

package com.rtb.projectmanagementtool.auth;

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
    // Just redirect to login jsp
    request.getRequestDispatcher("login.jsp").forward(request, response);
  }
}
