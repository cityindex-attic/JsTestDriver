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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javax.servlet.http.HttpServlet;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverServer extends Observable {

  private final Server server = new Server();
  private ServletHandler servletHandler;

  private final int port;
  private final CapturedBrowsers capturedBrowsers;

  public JsTestDriverServer(int port, CapturedBrowsers capturedBrowsers) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    initJetty(this.port);
    initServlets();
  }

  private void initServlets() {
    addServlet("/hello", new HelloServlet());
    addServlet("/heartbeat", new HeartbeatServlet(capturedBrowsers, new TimeImpl()));
    addServlet("/capture", new CaptureServlet(String.format("%s/capture.html",
        SlaveResourceServlet.RESOURCE_LOCATION), capturedBrowsers));
    addServlet("/slave/*", new SlaveResourceServlet(SlaveResourceServlet.RESOURCE_LOCATION));
    addServlet("/cmd", new CommandServlet(capturedBrowsers));
    addServlet("/query/*", new BrowserQueryResponseServlet(capturedBrowsers));
    Map<String, String> filesToServe = new HashMap<String, String>();

    addServlet("/test/*", new TestResourceServlet(filesToServe));
    addServlet("/fileSet", new FileSetServlet(capturedBrowsers, filesToServe));
    addServlet("/", new HomeServlet(capturedBrowsers));
  }

  private void addServlet(String url, HttpServlet servlet) {
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
    FlagsImpl flags = new FlagsImpl();
    CmdLineParser parser = new CmdLineParser(flags);

    if (args.length == 0) {
      parser.printUsage(System.out);
      System.exit(0);
    }
    try {
      parser.parseArgument(args);
      if (flags.getDisplayHelp()) {
        parser.printUsage(System.out);
        System.exit(0);
      }
      File config = new File(flags.getConfig());
      Set<String> fileSet = new LinkedHashSet<String>();
      String defaultServerAddress = null;

      if (flags.getTests().size() > 0 || flags.getReset() || !flags.getArguments().isEmpty()) {
        if (config.exists()) {
          ConfigurationParser configParser = new ConfigurationParser(config.getParentFile());

          try {
            configParser.parse(new FileInputStream(flags.getConfig()));
            fileSet = configParser.getFilesList();
            defaultServerAddress = configParser.getServer();
          } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
          }
        }
      }
      ActionRunner runner =
          new ActionRunner(new ActionParser(new ActionFactory()).parseFlags(flags, fileSet,
              defaultServerAddress));

      runner.runActions();
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
    } catch (Exception e) {
      System.err.println(e);
      System.exit(1);
    }
  }
}
