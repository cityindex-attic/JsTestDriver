// Copyright 2011 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import static com.google.jstestdriver.requesthandlers.HttpMethod.ANY;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.jstestdriver.server.handlers.NullRequestHandler;
import com.google.jstestdriver.server.handlers.ProxyRequestHandler;
import com.google.jstestdriver.server.proxy.JstdProxyServlet;
import com.google.jstestdriver.server.proxy.ProxyServletConfig;

import org.mortbay.servlet.ProxyServlet.Transparent;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyConfiguration {

  public static final String MATCHER = "matcher";
  public static final String SERVER = "server";

  private final Provider<JstdProxyServlet> proxyProvider;
  private final ProxyServletConfig.Factory servletConfigFactory;
  private final ProxyRequestHandler.Factory handlerFactory;

  private List<RequestMatcher> matchers;
  private Map<RequestMatcher, JstdProxyServlet> proxyServlets;

  @Inject
  public ProxyConfiguration(
      Provider<JstdProxyServlet> proxyProvider,
      ProxyServletConfig.Factory servletConfigFactory,
      ProxyRequestHandler.Factory handlerFactory) {
    this.proxyProvider = proxyProvider;
    this.servletConfigFactory = servletConfigFactory;
    this.handlerFactory = handlerFactory;
    clearConfiguration();
  }

  public synchronized List<RequestMatcher> getMatchers() {
    return matchers;
  }

  public synchronized RequestHandler getRequestHandler(RequestMatcher matcher) {
    Transparent proxy = proxyServlets.get(matcher);
    return proxy == null ? new NullRequestHandler() : handlerFactory.create(proxy);
  }

  public synchronized void updateConfiguration(JsonArray configuration)
      throws ServletException {
    ImmutableList.Builder<RequestMatcher> listBuilder = ImmutableList.builder();
    ImmutableMap.Builder<RequestMatcher, JstdProxyServlet> mapBuilder = ImmutableMap.builder();
    for (JsonElement element : configuration) {
      JsonObject entry = element.getAsJsonObject();
      RequestMatcher matcher =
          new RequestMatcher(ANY, entry.get(MATCHER).getAsString());
      JstdProxyServlet proxy = proxyProvider.get();
      ServletConfig config = servletConfigFactory.create(
          matcher.getPrefix(), entry.get(SERVER).getAsString());
      proxy.init(config);
      listBuilder.add(matcher);
      mapBuilder.put(matcher, proxy);
    }
    this.matchers = listBuilder.build();
    this.proxyServlets = mapBuilder.build();
  }

  public synchronized void clearConfiguration() {
    this.matchers = ImmutableList.of();
    this.proxyServlets = ImmutableMap.of();
  }
}
