// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import static com.google.jstestdriver.requesthandlers.HttpMethod.GET;
import static com.google.jstestdriver.requesthandlers.HttpMethod.POST;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.ForwardingMapper;
import com.google.jstestdriver.ForwardingServlet;
import com.google.jstestdriver.ProxyHandler;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.SlaveResourceService;
import com.google.jstestdriver.StandaloneRunnerFilesFilter;
import com.google.jstestdriver.StandaloneRunnerFilesFilterImpl;
import com.google.jstestdriver.URLRewriter;
import com.google.jstestdriver.URLTranslator;
import com.google.jstestdriver.annotations.BaseResourceLocation;
import com.google.jstestdriver.annotations.BrowserTimeout;
import com.google.jstestdriver.annotations.Port;
import com.google.jstestdriver.annotations.ProxyConfig;
import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.hooks.ProxyDestination;
import com.google.jstestdriver.requesthandlers.HttpMethod;
import com.google.jstestdriver.requesthandlers.RequestHandler;
import com.google.jstestdriver.requesthandlers.RequestHandlersModule;
import com.google.jstestdriver.server.proxy.ProxyServletConfig;
import com.google.jstestdriver.servlet.fileset.BrowserFileCheck;
import com.google.jstestdriver.servlet.fileset.FileSetRequestHandler;
import com.google.jstestdriver.servlet.fileset.ServerFileCheck;
import com.google.jstestdriver.servlet.fileset.ServerFileUpload;

import org.mortbay.servlet.ProxyServlet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

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
  private final ForwardingMapper forwardingMapper;
  private final long browserTimeout;
  private final URLTranslator urlTranslator;
  private final URLRewriter urlRewriter;
  private final Set<AuthStrategy> authStrategies;
  private final ProxyDestination destination;

  /**
   * TODO(rdionne): Refactor so we don't depend upon manually instantiated
   * classes from other object graphs. 
   */
  public JstdHandlersModule(
      CapturedBrowsers capturedBrowsers,
      FilesCache filesCache,
      ForwardingMapper forwardingMapper,
      long browserTimeout,
      URLTranslator urlTranslator,
      URLRewriter urlRewriter,
      Set<AuthStrategy> authStrategies,
      ProxyDestination destination) {
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = filesCache;
    this.forwardingMapper = forwardingMapper;
    this.browserTimeout = browserTimeout;
    this.urlTranslator = urlTranslator;
    this.urlRewriter = urlRewriter;
    this.authStrategies = authStrategies;
    this.destination = destination;
  }

  @Override
  protected void configureHandlers() {
    // Handler bindings in alphabetical order
    serve( GET, "/", HomeHandler.class);
    serve(POST, "/cache", FileCacheHandler.class);
    serve( GET, "/capture", CaptureHandler.class);
    serve( GET, "/capture/*", CaptureHandler.class);
    serve( GET, "/cmd", CommandGetHandler.class);
    serve(POST, "/cmd", CommandPostHandler.class);
    serve( GET, "/favicon.ico", FaviconHandler.class);
    serve( GET, "/fileSet", FileSetGetHandler.class);
    serve(POST, "/fileSet", FileSetPostHandler.class);

    for (HttpMethod method : HttpMethod.values()) {
      serve(method, "/forward/*", ForwardingHandler.class);
    }
    
    serve( GET, "/heartbeat", HeartbeatGetHandler.class);
    serve(POST, "/heartbeat", HeartbeatPostHandler.class);
    serve( GET, "/jstd/auth", AuthHandler.class);

    if (destination != null) {
      for (HttpMethod method : HttpMethod.values()) {
        serve(method, "/jstd/proxy/*", ProxyRequestHandler.class);
      }
    }
    
    serve( GET, "/hello", HelloHandler.class);
    serve(POST, "/log", BrowserLoggingHandler.class);
    serve(POST, "/query/*", BrowserQueryResponseHandler.class);
    serve( GET, "/runner/*", StandaloneRunnerHandler.class);
    serve( GET, "/slave/*", SlaveResourceHandler.class);
    serve( GET, "/test/*", TestResourceHandler.class);

    // Constant bindings
    bindConstant().annotatedWith(BaseResourceLocation.class)
        .to(SlaveResourceService.RESOURCE_LOCATION);
    bindConstant().annotatedWith(BrowserTimeout.class).to(browserTimeout);

    // Miscellaneous bindings
    bind(CapturedBrowsers.class).toInstance(capturedBrowsers);
    bind(FilesCache.class).toInstance(filesCache);
    bind(ForwardingMapper.class).toInstance(forwardingMapper);
    bind(new Key<ConcurrentMap<SlaveBrowser, List<String>>>() {})
        .toInstance(new ConcurrentHashMap<SlaveBrowser, List<String>>());
    bind(new Key<ConcurrentMap<SlaveBrowser, Thread>>() {})
        .toInstance(new ConcurrentHashMap<SlaveBrowser, Thread>());
    bind(new Key<Set<AuthStrategy>>() {}).toInstance(authStrategies);
    bind(new Key<Set<FileInfo>>() {}).toInstance(new HashSet<FileInfo>());
    bind(ServletConfig.class).annotatedWith(ProxyConfig.class).to(ProxyServletConfig.class);
    bind(StandaloneRunnerFilesFilter.class).to(StandaloneRunnerFilesFilterImpl.class);
    bind(URLTranslator.class).toInstance(urlTranslator);
    bind(URLRewriter.class).toInstance(urlRewriter);
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
    ForwardingServlet servlet = new ForwardingServlet(forwardingMapper, "localhost", port);

    // Need to init the ForwardingServlet because it is a ProxyServlet.Transparent, a class
    // that relies upon ServletContext#log().
    servlet.init(new ProxyServletConfig(context, ImmutableMap.<String, String>of()));
    return servlet;
  }
}
