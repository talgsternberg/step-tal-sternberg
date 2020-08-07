package com.rtb.projectmanagementtool.auth;

import static org.mockito.Mockito.*;

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
          new LocalServiceTestHelper(new LocalUserServiceTestConfig().setOAuthUserId("abc"))
              .setEnvIsLoggedIn(true),
          new LocalDatastoreServiceTestConfig()
              .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

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
    // auth object
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    ds.add(new UserData(1l, "abc"));
    ds.add(new UserData(2l, "opq"));
    ds.add(new UserData(3l, "xyz"));
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
    // auth object
    ds = DatastoreServiceFactory.getDatastoreService();
    ds.add(new UserData(1l, "abc"));
    ds.add(new UserData(2l, "opq"));
    ds.add(new UserData(3l, "xyz"));
    auth = new AuthOps(ds);

    // add list of mock users
    ArrayList<UserData> testUsers = new ArrayList<UserData>();
    testUsers.add(new UserData(1l, "abc"));
    testUsers.add(new UserData(2l, "opq"));
    testUsers.add(new UserData(3l, "xyz"));

    // new controller
    UserController controller = mock(UserController.class);

    // build and send a test cookie for logged out user
    Cookie[] testCookies = new Cookie[1];
    testCookies[0] = new Cookie("SessionUserID", "-1");

    // on this call in class method, return test user
    when(controller.getEveryUser()).thenReturn(testUsers);

    // when get cookies is called, pass cookies
    when(request.getCookies()).thenReturn(testCookies);

    // captor setup
    // ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
    // verify(response).addCookie(cookieCaptor.capture());

    // call loginUser
    auth.loginUser(request, response);

    // extract value
    // Cookie cookie = cookieCaptor.getValue();

    // Assert
    Assert.assertEquals(null, testCookies[0].getValue());
  }
}
