// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.proxy;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * A {@link ServletConfig} that uses a {@link Map} to configure a
 * {@link Servlet}.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class SimpleServletConfig implements ServletConfig {

  private final String name;
  private final ServletContext servletContext;
  private final Map<String, String> config;

  @Inject
  public SimpleServletConfig(
      String name,
      ServletContext servletContext,
      Map<String, String> config) {
    this.name = name;
    this.servletContext = servletContext;
    this.config = config;
  }

  public String getServletName() {
    return name;
  }

  public ServletContext getServletContext() {
    return servletContext;
  }

  public String getInitParameter(String s) {
    return config.get(s);
  }

  public Enumeration<String> getInitParameterNames() {
    return Iterators.asEnumeration(config.keySet().iterator());
  }
}
