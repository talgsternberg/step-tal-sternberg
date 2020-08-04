package com.rtb.projectmanagementtool.user;

import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class UserServletTest extends Mockito {

  private UserServlet servlet;
  private HttpServletRequest request;
  private HttpServletResponse response;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

  @Before
  public void setUp() {
    helper.setUp();
    servlet = new UserServlet();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoGet() throws IOException, ServletException {

    // test get ID request with a hardcoded long value
    when(request.getParameter("userID")).thenReturn("3");

    // Create Writer
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Run doGet()
    servlet.doGet(request, response);

    // Get results of doGet()
    String result = stringWriter.getBuffer().toString().trim();

    String expectedResult =
        "[{\"userID\":3,\"AuthID\":3,\"userName\":\"Sarah\",\"userYear\":2023,\"userMajors\":[\"Psychology\"],\"skills\":\"OOP\",\"userTotalCompTasks\":3}]";

    // Assert results are as expected
    Assert.assertEquals("doGet", result, expectedResult);
  }

  @Test
  public void testDoPost() throws IOException, ServletException {
    // When parameters are requested, return test values
    when(request.getParameter("userID")).thenReturn("3");
    when(request.getParameter("AuthID")).thenReturn("3");
    when(request.getParameter("userName")).thenReturn("Sarah");
    when(request.getParameter("userYear")).thenReturn("2023");
    Mockito.when(request.getParameterValues("userMajors")).thenReturn(new String[] {"Psychology"});
    // when(request.getParameterValues("userMajors"))
    //    .thenReturn(new ArrayList<>(Arrays.asList("Psychology")));
    when(request.getParameter("skills")).thenReturn("OOP");
    when(request.getParameter("userTotalCompTasks")).thenReturn("3");

    // Create writer
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Run doPost()
    servlet.doPost(request, response);
  }
}
