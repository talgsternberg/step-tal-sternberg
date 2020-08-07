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
  private AuthOps auth;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig().setOAuthUserId("abc"))
          .setEnvIsAdmin(true)
          .setEnvIsLoggedIn(true);

  @Before
  public void setUp() {
    helper.setUp();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    cookie = mock(Cookie.class);
    auth = mock(AuthOps.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testAlreadyLoggedInDontSetCookie() {
    // add list of mock users
    ArrayList<UserData> testUsers = new ArrayList<UserData>();
    testUsers.add(new UserData(1l, "abc"));
    testUsers.add(new UserData(2l, "opq"));
    testUsers.add(new UserData(3l, "xyz"));

    // new controller
    UserController controller = mock(UserController.class);

    // build and send test cookies for logged in users
    Cookie[] testCookies = new Cookie[3];
    testCookies[0] = new Cookie("SessionUserID", "abc");
    testCookies[1] = new Cookie("SessionUserID", "zvm");
    testCookies[2] = new Cookie("SessionUserID", "wpk");

    // on this call in class method, return test user
    when(controller.getEveryUser()).thenReturn(testUsers);

    // when get cookies is called, pass cookies
    when(request.getCookies()).thenReturn(testCookies);

    // call loginUser
    auth.loginUser(request, response);

    // captor setup
    verify(response, never()).addCookie(any());

    // Assert.assertEquals(null, cookie);
  }

  @Test
  public void testLoginUser() {

    System.out.println("Checkpoint 1");

    // add list of mock users
    ArrayList<UserData> testUsers = new ArrayList<UserData>();
    testUsers.add(new UserData(1l, "abc"));
    testUsers.add(new UserData(2l, "opq"));
    testUsers.add(new UserData(3l, "xyz"));

    System.out.println("Checkpoint 2");

    // new controller
    UserController controller = mock(UserController.class);

    System.out.println("Checkpoint 3");

    // build and send a test cookie for logged out user
    Cookie[] testCookies = new Cookie[1];
    testCookies[0] = new Cookie("SessionUserID", "-1");

    System.out.println("Checkpoint 4");

    // on this call in class method, return test user
    when(controller.getEveryUser()).thenReturn(testUsers);

    System.out.println("Checkpoint 5");

    // when get cookies is called, pass cookies
    when(request.getCookies()).thenReturn(testCookies);

    System.out.println("Checkpoint 6");

    // call loginUser
    auth.loginUser(request, response);

    System.out.println("Checkpoint 7");

    // captor setup
    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

    System.out.println("Checkpoint 8");

    verify(response).addCookie(cookieCaptor.capture());

    System.out.println("Checkpoint 9");

    Cookie cookie = cookieCaptor.getValue();

    // prints to debug (none print)
    System.out.println("from captor: ");
    System.out.println(cookie.getValue());
    System.out.println("from test cookies: ");
    System.out.println(testCookies[0].getValue());

    Assert.assertEquals(cookie.getValue(), testCookies[0].getValue());
  }
}
