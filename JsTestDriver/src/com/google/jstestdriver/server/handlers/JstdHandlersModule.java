/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver.server.handlers;

import static com.google.jstestdriver.requesthandlers.HttpMethod.GET;
import static com.google.jstestdriver.requesthandlers.HttpMethod.POST;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.mortbay.servlet.ProxyServlet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.ForwardingServlet;
import com.google.jstestdriver.ProxyHandler;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.SlaveResourceService;
import com.google.jstestdriver.StandaloneRunnerFilesFilter;
import com.google.jstestdriver.StandaloneRunnerFilesFilterImpl;
import com.google.jstestdriver.annotations.BaseResourceLocation;
import com.google.jstestdriver.annotations.BrowserTimeout;
import com.google.jstestdriver.annotations.Port;
import com.google.jstestdriver.annotations.ProxyConfig;
import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.hooks.ProxyDestination;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.requesthandlers.HttpMethod;
import com.google.jstestdriver.requesthandlers.RequestHandler;
import com.google.jstestdriver.requesthandlers.RequestHandlersModule;
import com.google.jstestdriver.server.proxy.ProxyServletConfig;
import com.google.jstestdriver.servlet.fileset.BrowserFileCheck;
import com.google.jstestdriver.servlet.fileset.FileSetRequestHandler;
import com.google.jstestdriver.servlet.fileset.ServerFileCheck;
import com.google.jstestdriver.servlet.fileset.ServerFileUpload;

/**
 * Defines {@link RequestHandler} bindings for the JSTD server.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class JstdHandlersModule extends RequestHandlersModule {

  private static final String PREFIX = "Prefix";
  private static final String PROXY_TO = "ProxyTo";
  
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache filesCache;
  private final long browserTimeout;
  private final Set<AuthStrategy> authStrategies;
  private final ProxyDestination destination;
  private final HandlerPathPrefix handlerPrefix;

  /**
   * TODO(rdionne): Refactor so we don't depend upon manually instantiated
   * classes from other object graphs. 
   * @param handlerPrefix TODO
   */
  public JstdHandlersModule(
      CapturedBrowsers capturedBrowsers,
      FilesCache filesCache,
      long browserTimeout,
      Set<AuthStrategy> authStrategies,
      ProxyDestination destination,
      HandlerPathPrefix handlerPrefix) {
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = filesCache;
    this.browserTimeout = browserTimeout;
    this.authStrategies = authStrategies;
    this.destination = destination;
    this.handlerPrefix = handlerPrefix;
  }
  
  @Override
  protected void configureHandlers() {
    // Handler bindings in alphabetical order
    serve( GET, handlerPrefix.prefixPath("/"), HomeHandler.class);
    serve(POST, handlerPrefix.prefixPath("/cache"), FileCacheHandler.class);
    serve( GET, handlerPrefix.prefixPath("/capture"), CaptureHandler.class);
    serve( GET, handlerPrefix.prefixPath("/capture/*"), CaptureHandler.class);
    serve( GET, handlerPrefix.prefixPath("/cmd"), CommandGetHandler.class);
    serve(POST, handlerPrefix.prefixPath("/cmd"), CommandPostHandler.class);
    serve( GET, handlerPrefix.prefixPath("/favicon.ico"), FaviconHandler.class);
    serve( GET, handlerPrefix.prefixPath("/fileSet"), FileSetGetHandler.class);
    serve(POST, handlerPrefix.prefixPath("/fileSet"), FileSetPostHandler.class);

    for (HttpMethod method : HttpMethod.values()) {
      serve(method, handlerPrefix.prefixPath("/forward/*"), ForwardingHandler.class);
    }
    
    serve( GET, handlerPrefix.prefixPath("/heartbeat"), HeartbeatGetHandler.class);
    serve(POST, handlerPrefix.prefixPath("/heartbeat"), HeartbeatPostHandler.class);
    serve( GET, handlerPrefix.prefixPath("/auth", "jstd"), AuthHandler.class);

    if (destination != null) {
      for (HttpMethod method : HttpMethod.values()) {
        serve(method, handlerPrefix.prefixPath("/proxy/*", "jstd"), ProxyRequestHandler.class);
      }
    }
    
    serve( GET, handlerPrefix.prefixPath("/hello"), HelloHandler.class);
    serve(POST, handlerPrefix.prefixPath("/log"), BrowserLoggingHandler.class);
    serve(POST, handlerPrefix.prefixPath("/query/*"), BrowserQueryResponseHandler.class);
    serve( GET, handlerPrefix.prefixPath("/runner/*"), StandaloneRunnerHandler.class);
    serve( GET, handlerPrefix.prefixPath("/slave/*"), SlaveResourceHandler.class);
    serve( GET, handlerPrefix.prefixPath("/test/*"), TestResourceHandler.class);

    // Constant bindings
    bindConstant().annotatedWith(BaseResourceLocation.class)
        .to(SlaveResourceService.RESOURCE_LOCATION);
    bindConstant().annotatedWith(BrowserTimeout.class).to(browserTimeout);

    // Miscellaneous bindings
    bind(CapturedBrowsers.class).toInstance(capturedBrowsers);
    bind(FilesCache.class).toInstance(filesCache);
    bind(new Key<ConcurrentMap<SlaveBrowser, List<String>>>() {})
        .toInstance(new ConcurrentHashMap<SlaveBrowser, List<String>>());
    bind(new Key<ConcurrentMap<SlaveBrowser, Thread>>() {})
        .toInstance(new ConcurrentHashMap<SlaveBrowser, Thread>());
    bind(new Key<Set<AuthStrategy>>() {}).toInstance(authStrategies);
    bind(new Key<Set<FileInfo>>() {}).toInstance(new HashSet<FileInfo>());
    bind(ServletConfig.class).annotatedWith(ProxyConfig.class).to(ProxyServletConfig.class);
    bind(StandaloneRunnerFilesFilter.class).to(StandaloneRunnerFilesFilterImpl.class);
    bind(HandlerPathPrefix.class).toInstance(handlerPrefix);
  }

  @Provides @Singleton List<FileSetRequestHandler<?>> provideFileSetRequestHandlers(
      BrowserFileCheck browserFileCheck, ServerFileCheck serverFileCheck, ServerFileUpload serverFileUpload) {
    return ImmutableList.of(browserFileCheck, serverFileCheck, serverFileUpload);
  }

  @Provides @Singleton
  @ProxyConfig Map<String, String> provideProxyConfig(ImmutableMap.Builder<String, String> builder) {
    builder.put(PREFIX, ProxyHandler.PROXY_PREFIX);
    if (destination != null) {
      builder.put(PROXY_TO, destination.getDestinationAddress());
    }
    return builder.build();
  }

  @Provides @Singleton
  ProxyServlet.Transparent provideTransparentProxy(@ProxyConfig ServletConfig config)
      throws ServletException {
    ProxyServlet.Transparent proxy = new ProxyServlet.Transparent();
    proxy.init(config);
    return proxy;
  }

  @Provides @Singleton
  ForwardingServlet provideForwardingServlet(@Port Integer port, ServletContext context)
      throws ServletException {
    ForwardingServlet servlet = new ForwardingServlet("localhost", port);

    // Need to init the ForwardingServlet because it is a ProxyServlet.Transparent, a class
    // that relies upon ServletContext#log().
    servlet.init(new ProxyServletConfig(context, ImmutableMap.<String, String>of()));
    return servlet;
  }
}
