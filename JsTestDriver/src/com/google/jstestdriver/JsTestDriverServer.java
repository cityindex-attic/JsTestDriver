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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.kohsuke.args4j.CmdLineException;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.servlet.Servlet;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverServer extends Observable {

  private final Server server = new Server();
  private ServletHandler servletHandler;

  private final int port;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache filesCache;

  public JsTestDriverServer(int port, CapturedBrowsers capturedBrowsers,
      FilesCache preloadedFilesCache) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = preloadedFilesCache;
    initJetty(this.port);
    initServlets();
  }

  private void initServlets() {
    addServlet("/hello", new HelloServlet());
    addServlet("/heartbeat", new HeartbeatServlet(capturedBrowsers, new TimeImpl()));
    addServlet("/capture", new CaptureServlet(new BrowserHunter(capturedBrowsers)));
    addServlet("/runner/*", new StandaloneRunnerServlet(new BrowserHunter(capturedBrowsers),
        filesCache, new StandaloneRunnerFilesFilterImpl(), new SlaveResourceService(
            SlaveResourceService.RESOURCE_LOCATION)));
    addServlet("/slave/*", new SlaveResourceServlet(new SlaveResourceService(
        SlaveResourceService.RESOURCE_LOCATION)));
    addServlet("/cmd", new CommandServlet(capturedBrowsers));
    addServlet("/query/*", new BrowserQueryResponseServlet(capturedBrowsers));

    addServlet("/test/*", new TestResourceServlet(filesCache));
    addServlet("/fileSet", new FileSetServlet(capturedBrowsers, filesCache));
    addServlet("/", new HomeServlet(capturedBrowsers));
    addServlet("/*", new ForwardingServlet(new ForwardingMapper()));
  }

  private void addServlet(String url, Servlet servlet) {
    servletHandler.addServletWithMapping(new ServletHolder(servlet), url);
  }

  private void initJetty(int port) {
    SocketConnector connector = new SocketConnector();

    connector.setPort(port);
    server.addConnector(connector);
    servletHandler = new ServletHandler();
    server.addHandler(servletHandler);
  }

  public void start() {
    try {
      server.start();
      setChanged();
      notifyObservers();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    try {
      server.stop();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    try {
      Flags flags = new FlagsParser().parseArgument(args);
      File config = new File(flags.getConfig());
      Set<FileInfo> fileSet = new LinkedHashSet<FileInfo>();
      List<Class<? extends Module>> plugins = new LinkedList<Class<? extends Module>>();

      // TODO(corysmith): move the handling of the serverAddress into a configuration class
      String serverAddress = flags.getServer();
      if (flags.hasWork()) {
        if (!config.exists()) {
          throw new RuntimeException("Config file doesn't exist: " + flags.getConfig());
        }
        Reader configReader = new java.io.FileReader(flags.getConfig());
        ConfigurationParser configParser = new ConfigurationParser(config.getParentFile(),
            configReader);
        PluginLoader pluginLoader = new PluginLoader();
        configParser.parse();
        fileSet = configParser.getFilesList();
        if (serverAddress == null || serverAddress.length() == 0) {
          serverAddress = configParser.getServer();
        }
        plugins = pluginLoader.load(configParser.getPlugins());
      }
      if (serverAddress == null || serverAddress.length() == 0) {
        if (flags.getPort() == -1) {
          throw new RuntimeException("Oh Snap! No server defined!");
        }
        serverAddress = String.format("http://%s:%d", "127.0.0.1", flags.getPort());
      }
      Injector injector =
          Guice.createInjector(new JsTestDriverModule(flags, fileSet, plugins, serverAddress));
      injector.getInstance(ActionRunner.class).runActions();
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
    } catch (Exception e) {
      System.err.println(e);
      System.exit(1);
    }
  }
}
