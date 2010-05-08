/*
 * Copyright 2008 Google Inc.
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
package com.google.jstestdriver;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.jstestdriver.config.Configuration;
import com.google.jstestdriver.config.DefaultConfiguration;
import com.google.jstestdriver.config.YamlParser;
import com.google.jstestdriver.guice.DebugModule;
import com.google.jstestdriver.guice.TestResultPrintingModule;
import com.google.jstestdriver.hooks.FileParsePostProcessor;
import com.google.jstestdriver.html.HtmlDocModule;
import com.google.jstestdriver.runner.RunnerMode;
import com.google.jstestdriver.servlet.BrowserLoggingServlet;

import org.kohsuke.args4j.CmdLineException;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.logging.LogManager;

import javax.servlet.Servlet;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverServer extends Observable {
  private static final Logger logger =
      LoggerFactory.getLogger(JsTestDriverServer.class);

  private final Server server = new Server();

  private final int port;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache filesCache;
  private final URLTranslator urlTranslator;
  private final URLRewriter urlRewriter;
  private Context context;

  private final long browserTimeout;

  public JsTestDriverServer(int port,
                            CapturedBrowsers capturedBrowsers,
                            FilesCache preloadedFilesCache,
                            URLTranslator urlTranslator,
                            URLRewriter urlRewriter,
                            long browserTimeout) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = preloadedFilesCache;
    this.urlTranslator = urlTranslator;
    this.urlRewriter = urlRewriter;
    this.browserTimeout = browserTimeout;
    initJetty(this.port);
    initServlets();
  }

  private void initServlets() {
    ForwardingMapper forwardingMapper = new ForwardingMapper();
    // TODO(corysmith): replace this with Guice injection
    addServlet("/", new HomeServlet(capturedBrowsers));
    addServlet("/hello", new HelloServlet());
    addServlet("/heartbeat", new HeartbeatServlet(capturedBrowsers));
    addServlet("/capture", new CaptureServlet(new BrowserHunter(
      capturedBrowsers, browserTimeout)));
    addServlet("/runner/*", new StandaloneRunnerServlet(new BrowserHunter(
      capturedBrowsers, browserTimeout), filesCache, new StandaloneRunnerFilesFilterImpl(),
      new SlaveResourceService(SlaveResourceService.RESOURCE_LOCATION)));
    addServlet("/slave/*", new SlaveResourceServlet(new SlaveResourceService(
      SlaveResourceService.RESOURCE_LOCATION)));
    addServlet("/cmd", new CommandServlet(capturedBrowsers, urlTranslator,
      urlRewriter, forwardingMapper));
    addServlet("/query/*", new BrowserQueryResponseServlet(capturedBrowsers,
      urlTranslator, forwardingMapper));
    addServlet("/fileSet", new FileSetServlet(capturedBrowsers, filesCache));
    addServlet("/cache", new FileCacheServlet());
    addServlet("/log", new BrowserLoggingServlet());
    addServlet("/test/*", new TestResourceServlet(filesCache));
    addServlet("/forward/*", new ForwardingServlet(forwardingMapper,
      "localhost", port));
  }

  private void addServlet(String url, Servlet servlet) {
    context.addServlet(new ServletHolder(servlet), url);
  }

  private void initJetty(int port) {
    SocketConnector connector = new SocketConnector();

    connector.setPort(port);
    server.addConnector(connector);
    context = new Context(server, "/", Context.SESSIONS);
    context.setMaxFormContentSize(Integer.MAX_VALUE);
  }

  public void start() {
    try {
      server.start();
      setChanged();
      notifyObservers(Event.STARTED);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    try {
      server.stop();
      setChanged();
      notifyObservers(Event.STOPPED);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public enum Event {
    STARTED, STOPPED
  }

  /*public static void main(String[] args) {
    try {
      YamlParser parser = new YamlParser(new DefaultPathRewriter(),
          Collections.<FileParsePostProcessor>emptySet());
      Flags flags = new FlagsParser().parseArgument(args);

      File config = new File(flags.getConfig());
      List<Module> modules = Lists.newLinkedList();
      modules.add(new TestResultPrintingModule(System.out, flags.getTestOutput()));

      LogManager.getLogManager().readConfiguration(
          flags.getRunnerMode().getLogConfig());

      Configuration configuration = new DefaultConfiguration();
      if (flags.hasWork()) {
        if (!config.exists()) {
          throw new RuntimeException("Config file doesn't exist: " + flags.getConfig());
        }
        configuration = parser.parse(config.getParentFile(), new java.io.FileReader(config));
        modules.addAll(new PluginLoader().load(configuration.getPlugins()));
        modules.add(new HtmlDocModule()); // by default the html plugin is installed.
      }

      modules.add(new DebugModule(flags.getRunnerMode() == RunnerMode.DEBUG));

      Injector injector = Guice.createInjector(
          new JsTestDriverModule(flags,
              configuration.getFilesList(),
              modules,
              configuration.createServerAddress(flags.getServer(),
                                                flags.getPort())));

      injector.getInstance(ActionRunner.class).runActions();
    } catch (CmdLineException e){
      System.out.println(e.getMessage());
      System.exit(1);
    } catch (FailureException e) {
      System.out.println("Tests failed.");
      System.exit(1);
    } catch (Exception e) {
      logger.debug("Error {}", e);
      System.out.println("Unexpected Runner Condition: " + e.getMessage() + "\n Use --runnerMode DEBUG for more information.");
      System.exit(1);
    }
  }*/
}
