// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.jstestdriver.ProxyHandler;
import com.google.jstestdriver.annotations.MaxFormContentSize;
import com.google.jstestdriver.annotations.Port;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;

/**
 * Sippin' on Jetty and Guice.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class JettyModule extends AbstractModule {

  private final int port;

  public JettyModule(int port) {
    this.port = port;
  }

  @Override
  protected void configure() {
    bindConstant().annotatedWith(Port.class).to(port);
    bindConstant().annotatedWith(MaxFormContentSize.class).to(Integer.MAX_VALUE);
  }

  @Provides @Singleton SocketConnector provideSocketConnector(@Port Integer port) {
    SocketConnector connector = new SocketConnector();
    connector.setPort(port);
    return connector;
  }

  @Provides @Singleton ServletHolder servletHolder(Servlet handlerServlet) {
    return new ServletHolder(handlerServlet);
  }

  @Provides @Singleton Server provideJettyServer(
      SocketConnector connector,
      ProxyHandler proxyHandler,
      @MaxFormContentSize Integer maxFormContentSize,
      ServletHolder servletHolder) {
    Server server = new Server();
    server.addConnector(connector);
    server.setHandler(proxyHandler);

    Context context = new Context(proxyHandler, "/", Context.SESSIONS);
    context.setMaxFormContentSize(maxFormContentSize);

    // TODO(rdionne): Fix HttpServletRequest#getPathInfo() provided by RequestHandlerServlet.
    context.addServlet(servletHolder, "/");
    context.addServlet(servletHolder, "/cache");
    context.addServlet(servletHolder, "/capture/*");
    context.addServlet(servletHolder, "/cmd");
    context.addServlet(servletHolder, "/favicon.ico");
    context.addServlet(servletHolder, "/fileSet");
    context.addServlet(servletHolder, "/forward/*");
    context.addServlet(servletHolder, "/heartbeat");
    context.addServlet(servletHolder, "/hello");
    context.addServlet(servletHolder, "/jstd/auth");
    context.addServlet(servletHolder, "/jstd/proxy/*");
    context.addServlet(servletHolder, "/log");
    context.addServlet(servletHolder, "/query/*");
    context.addServlet(servletHolder, "/runner/*");
    context.addServlet(servletHolder, "/slave/*");
    context.addServlet(servletHolder, "/test/*");

    return server;
  }
}
