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
import com.google.inject.Singleton;
import com.google.jstestdriver.server.proxy.JstdProxyServlet;
import com.google.jstestdriver.server.proxy.ProxyServletConfig;

import org.mortbay.servlet.ProxyServlet.Transparent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * An object that maintains a mapping between {@link RequestMatcher}s and
 * initialized {@link JstdProxyServlet}s pointing to various hosts so that the
 * {@link RequestDispatcher} may instantiate {@link ProxyRequestHandler}s on
 * demand.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyConfiguration {

  private static final Logger logger =
      LoggerFactory.getLogger(ProxyConfiguration.class);

  /**
   * JSON key identifying the {@link RequestMatcher} pattern.
   */
  public static final String MATCHER = "matcher";

  /**
   * JSON key identifying the host path.
   */
  public static final String SERVER = "server";

  private final Provider<JstdProxyServlet> proxyProvider;
  private final ProxyServletConfig.Factory servletConfigFactory;
  private final ProxyRequestHandler.Factory handlerFactory;

  private List<RequestMatcher> matchers;
  private Map<RequestMatcher, JstdProxyServlet> proxyServlets;

  /**
   * Constructs a {@link ProxyConfiguration}. {@link ProxyConfiguration} is
   * bound to the {@link Singleton} scope.
   * @param proxyProvider A Guice {@link Provider} of {@link JstdProxyServlet}s.
   * @param servletConfigFactory A Guice assistedinject factory for
   *     {@link ProxyServletConfig}s.
   * @param handlerFactory A Guice assistedinject factory for
   *     {@link ProxyRequestHandler}s.
   */
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

  /**
   * @return A {@link List} of {@link RequestMatcher}s to hand off to the
   * {@link RequestDispatcher} for proxying matching requests to various hosts.
   */
  public synchronized List<RequestMatcher> getMatchers() {
    return matchers;
  }

  /**
   * Instantiates a {@link ProxyRequestHandler} to proxy the current request
   * along to the matching host using a {@link JstdProxyServlet} behind the
   * scenes.
   * @param matcher The {@link RequestMatcher} that matches the current request.
   * @return A suitable {@link RequestHandler}.
   */
  public synchronized RequestHandler getRequestHandler(RequestMatcher matcher) {
    Transparent proxy = proxyServlets.get(matcher);
    return proxy == null ?
        new NullRequestHandler() : handlerFactory.create(proxy);
  }

  /**
   * Updates this {@link ProxyConfiguration} given the new {@code configuration}
   * encoded as a {@link JsonObject} by discarding previously initialized
   * {@link JstdProxyServlet}s and instantiating new ones with new hosts.
   * @param configuration A {@link JsonObject} specifying a new configuration.
   * @throws ServletException If the new servlets fail to initialize.
   */
  public synchronized void updateConfiguration(JsonArray configuration)
      throws ServletException {
    ImmutableList.Builder<RequestMatcher> listBuilder = ImmutableList.builder();
    ImmutableMap.Builder<RequestMatcher, JstdProxyServlet> mapBuilder = ImmutableMap.builder();
    for (JsonElement element : configuration) {
      JsonObject entry = element.getAsJsonObject();
      RequestMatcher matcher =
          new RequestMatcher(ANY, entry.get(MATCHER).getAsString());
      ServletConfig config = servletConfigFactory.create(
          matcher.getPrefix(), entry.get(SERVER).getAsString());
      JstdProxyServlet proxy;
      if (proxyServlets.get(matcher) != null) {
          logger.debug("Reusing previous proxy bound to {}.", matcher);
          proxy = proxyServlets.get(matcher);
      } else {
        logger.debug("Creating new proxy bound to {}.", matcher);
        proxy = proxyProvider.get();
      }
      proxy.init(config);
      listBuilder.add(matcher);
      mapBuilder.put(matcher, proxy);
    }
    this.matchers = listBuilder.build();
    this.proxyServlets = mapBuilder.build();
  }

  /**
   * Empties the mapping of {@link RequestMatcher}s to
   * {@link JstdProxyServlet}s.
   */
  public synchronized void clearConfiguration() {
    this.matchers = ImmutableList.of();
    this.proxyServlets = ImmutableMap.of();
  }
}
