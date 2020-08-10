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
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testAlreadyLoggedInDontSetCookie() {
    setUserServiceAuthInfo(false, "abc");
    // auth object
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    auth = new AuthOps(ds);

    // add list of mock users
    ArrayList<UserData> testUsers = new ArrayList<UserData>();
    testUsers.add(new UserData(1l, "abc"));
    testUsers.add(new UserData(2l, "opq"));
    testUsers.add(new UserData(3l, "xyz"));

    // new controller
    UserController controller = mock(UserController.class);

    // build and send test cookies for logged in users
    Cookie[] testCookies = new Cookie[3];
    testCookies[0] = new Cookie("sessionUserID", "abc");
    testCookies[1] = new Cookie("sessionUserID", "zvm");
    testCookies[2] = new Cookie("sessionUserID", "wpk");

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
    setUserServiceAuthInfo(true, "abc");
    // auth object
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    auth = new AuthOps(ds);

    // add list of mock users
    ArrayList<UserData> testUsers = new ArrayList<UserData>();
    testUsers.add(new UserData(1l, "abc"));
    // testUsers.add(new UserData(2l, "opq"));
    // testUsers.add(new UserData(3l, "xyz"));

    // new controller
    UserController controller = mock(UserController.class);

    // build and send a test cookie for logged out user
    Cookie[] testCookies = new Cookie[1];
    testCookies[0] = new Cookie("sessionUserID", "-1");

    // on this call in class method, return test user
    when(controller.getEveryUser()).thenReturn(testUsers);

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

    // Assert
    Assert.assertEquals("abc", cookie.getValue());
  }
}
