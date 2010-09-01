// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.jstestdriver.annotations.MaxFormContentSize;
import com.google.jstestdriver.annotations.Port;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

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
      SocketConnector connector, @MaxFormContentSize Integer maxFormContentSize, ServletHolder servletHolder) {
    Server server = new Server();
    server.addConnector(connector);
    Context context = new Context(server, "/", Context.SESSIONS);
    context.setMaxFormContentSize(maxFormContentSize);
    context.addServlet(servletHolder, "/*");
    return server;
  }
}
