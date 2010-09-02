// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import static com.google.jstestdriver.requesthandlers.HttpMethod.GET;
import static com.google.jstestdriver.requesthandlers.HttpMethod.POST;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.ForwardingMapper;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.SlaveResourceService;
import com.google.jstestdriver.StandaloneRunnerFilesFilter;
import com.google.jstestdriver.StandaloneRunnerFilesFilterImpl;
import com.google.jstestdriver.URLRewriter;
import com.google.jstestdriver.URLTranslator;
import com.google.jstestdriver.annotations.BaseResourceLocation;
import com.google.jstestdriver.annotations.BrowserTimeout;
import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.requesthandlers.RequestHandler;
import com.google.jstestdriver.requesthandlers.RequestHandlersModule;
import com.google.jstestdriver.servlet.fileset.BrowserFileCheck;
import com.google.jstestdriver.servlet.fileset.FileSetRequestHandler;
import com.google.jstestdriver.servlet.fileset.ServerFileCheck;
import com.google.jstestdriver.servlet.fileset.ServerFileUpload;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Defines {@link RequestHandler} bindings for the JSTD server.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class JstdHandlersModule extends RequestHandlersModule {

  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache filesCache;
  private final ForwardingMapper forwardingMapper;
  private final long browserTimeout;
  private final URLTranslator urlTranslator;
  private final URLRewriter urlRewriter;
  private final Set<AuthStrategy> authStrategies;

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
      Set<AuthStrategy> authStrategies) {
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = filesCache;
    this.forwardingMapper = forwardingMapper;
    this.browserTimeout = browserTimeout;
    this.urlTranslator = urlTranslator;
    this.urlRewriter = urlRewriter;
    this.authStrategies = authStrategies;
  }

  @Override
  protected void configureHandlers() {
    serve( GET, "/", HomeHandler.class);
    serve(POST, "/cache", FileCacheHandler.class);
    serve( GET, "/capture", CaptureHandler.class);
    serve( GET, "/capture/*", CaptureHandler.class);
    serve( GET, "/cmd", CommandGetHandler.class);
    serve(POST, "/cmd", CommandPostHandler.class);
    serve( GET, "/fileSet", FileSetGetHandler.class);
    serve(POST, "/fileSet", FileSetPostHandler.class);
    serve( GET, "/heartbeat", HeartbeatGetHandler.class);
    serve(POST, "/heartbeat", HeartbeatPostHandler.class);
    serve( GET, "/jstd/auth", AuthHandler.class);
    serve( GET, "/hello", HelloHandler.class);
    serve(POST, "/log", BrowserLoggingHandler.class);
    serve(POST, "/query/*", BrowserQueryResponseHandler.class);
    serve( GET, "/runner/*", StandaloneRunnerHandler.class);
    serve( GET, "/slave/*", SlaveResourceHandler.class);
    serve( GET, "/test/*", TestResourceHandler.class);

    bindConstant().annotatedWith(BaseResourceLocation.class)
        .to(SlaveResourceService.RESOURCE_LOCATION);
    bindConstant().annotatedWith(BrowserTimeout.class).to(browserTimeout);

    bind(CapturedBrowsers.class).toInstance(capturedBrowsers);
    bind(FilesCache.class).toInstance(filesCache);
    bind(ForwardingMapper.class).toInstance(forwardingMapper);
    bind(new Key<ConcurrentMap<SlaveBrowser, List<String>>>() {})
        .toInstance(new ConcurrentHashMap<SlaveBrowser, List<String>>());
    bind(new Key<ConcurrentMap<SlaveBrowser, Thread>>() {})
        .toInstance(new ConcurrentHashMap<SlaveBrowser, Thread>());
    bind(new Key<Set<AuthStrategy>>() {}).toInstance(authStrategies);
    bind(StandaloneRunnerFilesFilter.class).to(StandaloneRunnerFilesFilterImpl.class);
    bind(URLTranslator.class).toInstance(urlTranslator);
    bind(URLRewriter.class).toInstance(urlRewriter);
  }

  @Provides @Singleton List<FileSetRequestHandler<?>> provideFileSetRequestHandlers(
      BrowserFileCheck browserFileCheck, ServerFileCheck serverFileCheck, ServerFileUpload serverFileUpload) {
    return ImmutableList.of(browserFileCheck, serverFileCheck, serverFileUpload);
  }
}
