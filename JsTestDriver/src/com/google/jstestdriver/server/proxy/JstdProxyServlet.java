// Copyright 2011 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.proxy;

import org.mortbay.servlet.ProxyServlet;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class JstdProxyServlet extends ProxyServlet.Transparent {
  {_DontProxyHeaders.add("host");}
}
