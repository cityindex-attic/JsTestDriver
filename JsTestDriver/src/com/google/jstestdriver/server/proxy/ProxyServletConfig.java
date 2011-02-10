// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.proxy;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * A {@link ServletConfig} that configures the server-under-test proxy by
 * providing values for "Prefix" and "ProxyTo".
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyServletConfig extends SimpleServletConfig {

  private static final String NAME = "Proxy Servlet";
  private static final String PREFIX = "Prefix";
  private static final String PROXY_TO = "ProxyTo";

  public interface Factory {
    ProxyServletConfig create(
        @Assisted("prefix") String prefix,
        @Assisted("proxyTo") String proxyTo);
  }

  @Inject
  public ProxyServletConfig(
      ServletContext servletContext,
      @Assisted("prefix") String prefix,
      @Assisted("proxyTo") String proxyTo) {
    super(NAME, servletContext, ImmutableMap.of(PREFIX, prefix, PROXY_TO, proxyTo));
  }
}
