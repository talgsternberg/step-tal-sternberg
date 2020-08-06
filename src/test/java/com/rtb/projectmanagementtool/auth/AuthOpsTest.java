package com.rtb.projectmanagementtool.auth;

import static org.mockito.Mockito.*;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.rtb.projectmanagementtool.user.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class AuthOpsTest {

  private HttpServletRequest request;
  private HttpServletResponse response;
  private Cookie cookie;

  // test user
  private static final long userID = 2l;
  private static final String AuthID = "abc";
  private static final String name = "Tal";
  private static final long year = 2023;
  private static final ArrayList<String> majors =
      new ArrayList<>(Arrays.asList("Comp Sci", "Earth Sciences"));
  private static final Skills skills = Skills.LEADERSHIP;
  private static final long totalCompTasks = 4;

  private static final UserData testUser =
      new UserData(userID, AuthID, name, year, majors, skills, totalCompTasks);

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig())
          .setEnvIsAdmin(true)
          .setEnvIsLoggedIn(true);

  @Before
  public void setUp() {
    helper.setUp();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    cookie = mock(Cookie.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testLoginUser() {
    // new controller
    UserController controller = mock(UserController.class);
    controller.addUser(testUser);

    // build and send a test cookie
    cookie = mock(Cookie.class);
    cookie.setName("sessionUserID");
    cookie.setValue("out");

    // call loginUser
    loginUser(request, response);

    Assert.assertEquals(cookie.getValue(), String.valueOf(userID));
  }
}
