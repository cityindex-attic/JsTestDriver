// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The lone {@link Servlet}, a @Singleton. Sets up the {@link RequestScope} and
 * instantiates a @RequestScoped {@link RequestDispatcher} to dispatch the
 * request. 
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class RequestHandlerServlet extends HttpServlet {

  private static final long serialVersionUID = -186242854065156745L;

  private static ThreadLocal<Context> localContext = new ThreadLocal<Context>();

  private final RequestScope requestScope;
  private final Provider<RequestDispatcher> dispatcherProvider;

  @Inject
  public RequestHandlerServlet(
      RequestScope requestScope,
      Provider<RequestDispatcher> dispatcherProvider) {
    this.requestScope = requestScope;
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO(rdionne): Wrap request in an HttpServletRequestWrapper that corrects
    // #getPathInfo() before we clean up JsTestDriverServer.
    localContext.set(new Context(request, response));
    requestScope.enter();
    try {
      requestScope.seed(HttpServletRequest.class, request);
      requestScope.seed(HttpServletResponse.class, response);
      dispatcherProvider.get().dispatch();
    } finally {
      localContext.remove();
      requestScope.exit();
    }
  }

  static HttpServletRequest getRequest() {
    return getContext().request;
  }

  static HttpServletResponse getResponse() {
    return getContext().response;
  }

  private static Context getContext() {
    Context context = localContext.get();
    if (context == null) {
      throw new OutOfScopeException("Cannot access scoped object.");
    }
    return context;
  }

  private static class Context {
    
    HttpServletRequest request;
    HttpServletResponse response;

    public Context(HttpServletRequest request, HttpServletResponse response) {
      this.request = request;
      this.response = response;
    }
  }
}
