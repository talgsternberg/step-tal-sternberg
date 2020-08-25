package com.rtb.projectmanagementtool.auth;

import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.rtb.projectmanagementtool.user.*;
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
  private UserController controller;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalUserServiceTestConfig(),
              new LocalDatastoreServiceTestConfig()
                  .setAutoIdAllocationPolicy(
                      LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL))
          .setEnvEmail("testemail@rtbstep2020.com")
          .setEnvAuthDomain("rtbstep2020.com");

  public void setUserServiceAuthInfo(boolean isLoggedIn, String authId) {
    helper.setEnvIsLoggedIn(true);
    if (isLoggedIn && authId != null) {
      helper.setEnvAttributes(
          new HashMap<String, Object>() {
            {
              put("com.google.appengine.api.users.UserService.user_id_key", authId);
            }
          });
    }

    // Reinitialize helper to set reset the flags
    helper.setUp();
  }

  @Before
  public void setUp() {
    helper.setUp();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    controller = mock(UserController.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testAlreadyLoggedInDontSetCookie() {
    setUserServiceAuthInfo(true, "abc"); // logged in
    // auth object
    auth = new AuthOps(controller);

    // add list of mock users
    ArrayList<UserData> testUsers = new ArrayList<UserData>();
    testUsers.add(new UserData(1l, "abc", ""));
    testUsers.add(new UserData(2l, "opq", ""));
    testUsers.add(new UserData(3l, "xyz", ""));

    // new controller
    UserController controller = mock(UserController.class);

    // build and send test cookies for logged in users
    Cookie[] testCookies = new Cookie[1];
    testCookies[0] = new Cookie("sessionUserID", "1l");

    // on this call in class method, return test user
    when(controller.getEveryUser()).thenReturn(testUsers);

    // when get cookies is called, pass cookies
    when(request.getCookies()).thenReturn(testCookies);

    // call loginUser
    auth.loginUser(request, response);

    // captor setup
    verify(response, never()).addCookie(any());
  }

  @Test
  public void testLoginUser() {
    setUserServiceAuthInfo(true, "abc"); // logged in
    // auth object
    auth = new AuthOps(controller);

    // add list of mock users
    ArrayList<UserData> testUsers = new ArrayList<UserData>();
    UserData user = new UserData(1l, "abc", "");

    // build and send a test cookie for logged out user
    Cookie[] testCookies = new Cookie[1];
    testCookies[0] = new Cookie("sessionUserID", "-1");

    // on this call in class method, return test user
    when(controller.getUserByAuthID("abc")).thenReturn(user);

    // when get cookies is called, pass cookies
    when(request.getCookies()).thenReturn(testCookies);

    // captor setup
    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

    // call loginUser
    auth.loginUser(request, response);

    // verify
    verify(response).addCookie(cookieCaptor.capture());

    // extract value
    Cookie cookie = cookieCaptor.getValue();

    // Assert that the UserID equals the cookie's value
    Assert.assertEquals("1", cookie.getValue());
  }

  @Test
  public void testWhichUser() {
    setUserServiceAuthInfo(true, "abc"); // logged in

    auth = new AuthOps(controller);

    // test cookies
    Cookie[] testCookies = new Cookie[1];
    testCookies[0] = new Cookie("sessionUserID", "2");

    // when get cookies is called, pass cookies
    when(request.getCookies()).thenReturn(testCookies);

    // call method
    long userID = auth.whichUserIsLoggedIn(request, response);

    // Assert that the val returned equals userID as type long
    Assert.assertEquals(2, userID);
  }

  @Test
  public void testLogoutUser() {
    setUserServiceAuthInfo(false, "abc"); // user is logged in

    auth = new AuthOps(controller);

    // test cookies
    Cookie[] testCookies = new Cookie[1];
    testCookies[0] = new Cookie("sessionUserID", "2");

    // when get cookies is called, pass cookies
    when(request.getCookies()).thenReturn(testCookies);

    // captor setup
    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

    // call logoutUser
    auth.logoutUser(request, response);

    // verify
    verify(response).addCookie(cookieCaptor.capture());

    // extract value
    Cookie cookie = cookieCaptor.getValue();

    // Assert that the cookie's value is NO_LOGGED_IN_USER
    Assert.assertEquals("-1", cookie.getValue());
  }
}
