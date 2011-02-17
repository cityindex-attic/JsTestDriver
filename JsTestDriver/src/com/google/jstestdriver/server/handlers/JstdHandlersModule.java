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

import static com.google.inject.multibindings.MapBinder.newMapBinder;
import static com.google.jstestdriver.requesthandlers.HttpMethod.GET;
import static com.google.jstestdriver.requesthandlers.HttpMethod.POST;
import static com.google.jstestdriver.server.handlers.CaptureHandler.RUNNER_TYPE;
import static com.google.jstestdriver.server.handlers.pages.PageType.CONSOLE;
import static com.google.jstestdriver.server.handlers.pages.PageType.HEARTBEAT;
import static com.google.jstestdriver.server.handlers.pages.PageType.RUNNER;
import static com.google.jstestdriver.server.handlers.pages.PageType.STANDALONE_RUNNER;
import static com.google.jstestdriver.server.handlers.pages.PageType.VISUAL_STANDALONE_RUNNER;
import static com.google.jstestdriver.server.handlers.pages.SlavePageRequest.UPLOAD_SIZE;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.servlet.RequestScoped;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.ForwardingServlet;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.SlaveResourceService;
import com.google.jstestdriver.StandaloneRunnerFilesFilter;
import com.google.jstestdriver.StandaloneRunnerFilesFilterImpl;
import com.google.jstestdriver.Time;
import com.google.jstestdriver.TimeImpl;
import com.google.jstestdriver.annotations.BaseResourceLocation;
import com.google.jstestdriver.annotations.BrowserTimeout;
import com.google.jstestdriver.annotations.Port;
import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.requesthandlers.HttpMethod;
import com.google.jstestdriver.requesthandlers.RequestHandler;
import com.google.jstestdriver.requesthandlers.RequestHandlersModule;
import com.google.jstestdriver.server.handlers.pages.BrowserControlledRunnerPage;
import com.google.jstestdriver.server.handlers.pages.ConsolePage;
import com.google.jstestdriver.server.handlers.pages.HeartbeatPage;
import com.google.jstestdriver.server.handlers.pages.Page;
import com.google.jstestdriver.server.handlers.pages.PageType;
import com.google.jstestdriver.server.handlers.pages.RunnerPage;
import com.google.jstestdriver.server.handlers.pages.SlavePageRequest;
import com.google.jstestdriver.server.handlers.pages.StandaloneRunnerPage;
import com.google.jstestdriver.server.proxy.SimpleServletConfig;
import com.google.jstestdriver.servlet.fileset.BrowserFileCheck;
import com.google.jstestdriver.servlet.fileset.FileSetRequestHandler;
import com.google.jstestdriver.servlet.fileset.ServerFileCheck;
import com.google.jstestdriver.servlet.fileset.ServerFileUpload;
import com.google.jstestdriver.util.ParameterParser;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Defines {@link RequestHandler} bindings for the JSTD server.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class JstdHandlersModule extends RequestHandlersModule {

  private static final String PAGE = "page";

  private static final String MODE = "mode";

  private static final String ID = "id";

  private static final String TIMEOUT = "timeout";

  private static final String JSTD = "jstd";
  
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache filesCache;
  private final long browserTimeout;
  private final Set<AuthStrategy> authStrategies;
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
      HandlerPathPrefix handlerPrefix) {
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = filesCache;
    this.browserTimeout = browserTimeout;
    this.authStrategies = authStrategies;
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
    serve( GET, handlerPrefix.prefixPath("/auth", JSTD), AuthHandler.class);
    serve( GET, handlerPrefix.prefixPath("/proxy", JSTD), ProxyConfigurationHandler.class);
    serve(POST, handlerPrefix.prefixPath("/proxy", JSTD), ProxyConfigurationHandler.class);

    serve( GET, handlerPrefix.prefixPath("/hello"), HelloHandler.class);
    serve(POST, handlerPrefix.prefixPath("/log"), BrowserLoggingHandler.class);
    serve(POST, handlerPrefix.prefixPath("/query/*"), BrowserQueryResponseHandler.class);
    serve( GET, handlerPrefix.prefixPath("/runner/*"), StandaloneRunnerHandler.class);
    serve( GET, handlerPrefix.prefixPath("/slave/*"), SlaveResourceHandler.class);
    serve( GET, handlerPrefix.prefixPath("/test/*"), TestResourceHandler.class);
    serve( GET, handlerPrefix.prefixPath("/quit"), QuitHandler.class);
    serve( GET, handlerPrefix.prefixPath("/quit/*"), QuitHandler.class);
    serve( GET, handlerPrefix.prefixPath("/static/*"), StaticResourceHandler.class);
    serve( GET, handlerPrefix.prefixPath("/bcr"), BrowserControlledRunnerHandler.class);
    serve( GET, handlerPrefix.prefixPath("/bcr/*"), BrowserControlledRunnerHandler.class);

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
    bind(StandaloneRunnerFilesFilter.class).to(StandaloneRunnerFilesFilterImpl.class);
    bind(HandlerPathPrefix.class).toInstance(handlerPrefix);
    bind(Time.class).to(TimeImpl.class);

    MapBinder<PageType, Page> pageBinder = newMapBinder(binder(), PageType.class, Page.class);
    pageBinder.addBinding(CONSOLE).to(ConsolePage.class).in(RequestScoped.class);
    pageBinder.addBinding(HEARTBEAT).to(HeartbeatPage.class).in(RequestScoped.class);
    pageBinder.addBinding(RUNNER).to(RunnerPage.class).in(RequestScoped.class);
    pageBinder.addBinding(STANDALONE_RUNNER).to(StandaloneRunnerPage.class).in(RequestScoped.class);
    pageBinder.addBinding(VISUAL_STANDALONE_RUNNER).to(BrowserControlledRunnerPage.class).in(RequestScoped.class);
  }

  private static final Map<String, Integer> PARAMETERS = ImmutableMap.<String, Integer>builder()
    .put(JSTD, 0)
    .put(RUNNER_TYPE, 1)
    .put(TIMEOUT, 1)
    .put(ID, 1)
    .put(MODE, 1)
    .put(PAGE, 1)
    .put(UPLOAD_SIZE, 1)
    .build();

  private static final Set<String> BLACKLIST = ImmutableSet.<String>builder().build();

  @Provides SlavePageRequest providePageRequest(
      ParameterParser parser,
      HttpServletRequest request,
      HandlerPathPrefix prefix,
      CapturedBrowsers browsers) {
    return new SlavePageRequest(
        parser.getParameterMap(PARAMETERS, BLACKLIST),
        request,
        prefix,
        browsers);
  }

  @Provides @Singleton List<FileSetRequestHandler<?>> provideFileSetRequestHandlers(
      BrowserFileCheck browserFileCheck, ServerFileCheck serverFileCheck, ServerFileUpload serverFileUpload) {
    return ImmutableList.of(browserFileCheck, serverFileCheck, serverFileUpload);
  }

  @Provides @Singleton
  ForwardingServlet provideForwardingServlet(@Port Integer port, ServletContext context)
      throws ServletException {
    ForwardingServlet servlet = new ForwardingServlet("localhost", port);

    // Need to init the ForwardingServlet because it is a ProxyServlet.Transparent, a class
    // that relies upon ServletContext#log().
    servlet.init(new SimpleServletConfig(
        "forward", context, ImmutableMap.<String, String>of()));
    return servlet;
  }
}
