// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.proxy;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.google.jstestdriver.annotations.ProxyConfig;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * A {@link ServletConfig} that configures the server-under-test proxy by
 * providing values for "Prefix" and "ProxyTo".
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyServletConfig implements ServletConfig {

  private static final String NAME = "Proxy Servlet";

  private final ServletContext servletContext;
  private final Map<String, String> configValues;

  @Inject
  public ProxyServletConfig(
      ServletContext servletContext,
      @ProxyConfig Map<String, String> configValues) {
    this.servletContext = servletContext;
    this.configValues = configValues;
  }

  public String getServletName() {
    return NAME;
  }

  public ServletContext getServletContext() {
    return servletContext;
  }

  public String getInitParameter(String s) {
    return configValues.get(s);
  }

  public Enumeration getInitParameterNames() {
    return Iterators.asEnumeration(configValues.keySet().iterator());
  }
}
