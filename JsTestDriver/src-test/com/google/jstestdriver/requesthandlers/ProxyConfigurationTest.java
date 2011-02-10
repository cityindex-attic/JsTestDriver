// Copyright 2011 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import static com.google.jstestdriver.requesthandlers.HttpMethod.ANY;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.inject.Provider;
import com.google.jstestdriver.server.proxy.JstdProxyServlet;
import com.google.jstestdriver.server.proxy.ProxyServletConfig;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import javax.servlet.ServletException;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyConfigurationTest extends TestCase {

  private static final String CONFIG = "[" +
      "{\"matcher\":\"/asdf\",\"server\":\"http://www.asdf.com\"}" +
  "]";

  private static interface JstdProxyServletProvider
      extends Provider<JstdProxyServlet> {};

  private IMocksControl control;

  private JstdProxyServlet proxy;
  private Provider<JstdProxyServlet> proxyProvider;
  private ProxyServletConfig.Factory servletConfigFactory;
  private ProxyRequestHandler handler;
  private ProxyRequestHandler.Factory handlerFactory;

  private ProxyConfiguration configuration;

  @Override
  protected void setUp() throws Exception {
    control = EasyMock.createControl();
    proxy = control.createMock(JstdProxyServlet.class);
    proxyProvider = control.createMock(JstdProxyServletProvider.class);
    servletConfigFactory = control.createMock(ProxyServletConfig.Factory.class);
    handler = control.createMock(ProxyRequestHandler.class);
    handlerFactory = control.createMock(ProxyRequestHandler.Factory.class);
    configuration = new ProxyConfiguration(
        proxyProvider, servletConfigFactory, handlerFactory);
  }

  public void testEmptyUponInitialization() {
    assertEquals(0, configuration.getMatchers().size());
  }

  public void testUpdateConfiguration() throws ServletException {
    ProxyServletConfig servletConfig =
        new ProxyServletConfig(null, "/asdf", "http://www.asdf.com");
    EasyMock.expect(servletConfigFactory.create("/asdf", "http://www.asdf.com"))
        .andReturn(servletConfig);
    EasyMock.expect(proxyProvider.get()).andReturn(proxy);
    /* expect */ proxy.init(servletConfig);
    EasyMock.expect(handlerFactory.create(proxy)).andReturn(handler);
    control.replay();
    JsonArray jsonConfig = new JsonParser().parse(CONFIG).getAsJsonArray();
    configuration.updateConfiguration(jsonConfig);
    assertEquals(1, configuration.getMatchers().size());
    assertEquals(handler,
        configuration.getRequestHandler(new RequestMatcher(ANY, "/asdf")));
    control.verify();
  }

  public void testUpdateConfiguration_reuseExistingProxy() throws ServletException {
    ProxyServletConfig servletConfig =
        new ProxyServletConfig(null, "/asdf", "http://www.asdf.com");
    EasyMock.expect(servletConfigFactory.create("/asdf", "http://www.asdf.com"))
        .andReturn(servletConfig).times(2);
    EasyMock.expect(proxyProvider.get()).andReturn(proxy).times(1);
    /* expect */ proxy.init(servletConfig);
    EasyMock.expectLastCall().times(2);
    control.replay();
    JsonArray jsonConfig = new JsonParser().parse(CONFIG).getAsJsonArray();
    configuration.updateConfiguration(jsonConfig);
    configuration.updateConfiguration(jsonConfig);
    control.verify();
  }
}
