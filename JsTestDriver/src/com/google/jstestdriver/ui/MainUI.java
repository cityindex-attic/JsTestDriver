/*
 * Copyright 2009 Google Inc.
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
package com.google.jstestdriver.ui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;

import java.awt.BorderLayout;
import java.io.File;
import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.mortbay.log.Slf4jLog;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.jstestdriver.ActionFactory;
import com.google.jstestdriver.ActionRunner;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.ConfigurationParser;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.Flags;
import com.google.jstestdriver.FlagsParser;
import com.google.jstestdriver.JsTestDriverModule;
import com.google.jstestdriver.ServerStartupAction;
import com.google.jstestdriver.guice.PrintStreamClientModule;
import com.google.jstestdriver.guice.XmlClientModule;

/**
 * Entry point for the Swing GUI of JSTestDriver.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class MainUI {

  private static final String JCL_LOG_CONFIG_ATTR = "org.apache.commons.logging.Log";
  private static final String JETTY_LOG_CLASS_PROP = "org.mortbay.log.class";
  private static final String JCL_SIMPLELOG_SHOWLOGNAME = "org.apache.commons.logging.simplelog.showlogname";
  private static final String JCL_SIMPLELOG_SHOW_SHORT_LOGNAME = "org.apache.commons.logging.simplelog.showShortLogname";
  private final ResourceBundle messageBundle;
  private final Flags flags;
  private LogPanel logPanel;
  private StatusBar statusBar;
  private CapturedBrowsersPanel capturedBrowsersPanel;
  private JPanel infoPanel;
  private final Logger logger = Logger.getLogger(MainUI.class.getCanonicalName());

  public MainUI(Flags flags, ResourceBundle bundle) {
    this.flags = flags;
    this.messageBundle = bundle;
  }

  public static void main(String[] args) {
    try {
      Flags flags = new FlagsParser().parseArgument(args);
      configureLogging();
      ResourceBundle bundle = ResourceBundle.getBundle("com.google.jstestdriver.ui.messages");
      new MainUI(flags, bundle).startUI();
    } catch (CmdLineException e) {
      // exit naturally, if the flags suck.
      System.err.println(e.getMessage());
    }
  }

  private static void configureLogging() {
    // Configure commons logging to log to the Swing log panel logger
    LogFactory.getFactory().setAttribute(JCL_LOG_CONFIG_ATTR, LogPanelLog.class.getName());
    // Configure Jetty to log to SLF4J. Since slf4j-jcl.jar is in the classpath, SLF4J will be
    // configured to delegate logging to commons logging.
    System.setProperty(JETTY_LOG_CLASS_PROP, Slf4jLog.class.getName());
    System.setProperty(JCL_SIMPLELOG_SHOW_SHORT_LOGNAME, "false");
    System.setProperty(JCL_SIMPLELOG_SHOWLOGNAME, "false");
  }

  private void startUI() {
    statusBar = new StatusBar(StatusBar.Status.NOT_RUNNING, messageBundle);
    logPanel = new LogPanel();
    capturedBrowsersPanel = new CapturedBrowsersPanel();
    infoPanel = new InfoPanel(flags, messageBundle);
    LogPanelLog.LogPanelHolder.setLogPanel(logPanel);

    try {
      File config = new File(flags.getConfig());
      Set<FileInfo> fileSet = new LinkedHashSet<FileInfo>();
      // TODO(corysmith): move the handling of the serverAddress into a configuration class that 
      // returns an appropriate module configuration.
      String serverAddress = null;
      if (flags.hasWork()) {
        if (!config.exists()) {
          throw new RuntimeException("Config file doesn't exist: " + flags.getConfig());
        }
        Reader configReader = new java.io.FileReader(flags.getConfig());
        ConfigurationParser configParser = new ConfigurationParser(config.getParentFile(),
            configReader);

        configParser.parse();
        fileSet = configParser.getFilesList();
        serverAddress = configParser.getServer();
      }
      serverAddress = flags.getServer() != null ? flags.getServer() : serverAddress;
      if (serverAddress == null || serverAddress.length() == 0) {
        if (flags.getPort() == -1) {
          throw new RuntimeException("Oh Snap! No server defined!");
        }
        serverAddress = String.format("http://%s:%d", "127.0.0.1", flags.getPort());
      }
      List<Module> modules = new LinkedList<Module>();
      // TODO(corysmith): Figure out how to avoid creating a client.
      if (flags.getTestOutput().length() > 0) {
        modules.add(new XmlClientModule(System.out));
      } else {
        modules.add(new PrintStreamClientModule(System.out));
      }
      Injector injector =
        Guice.createInjector(
            new JsTestDriverModule(flags,
                                   fileSet,
                                   modules,
                                   serverAddress));
      ActionFactory actionFactory = injector.getInstance(ActionFactory.class);
      actionFactory.registerListener(ServerStartupAction.class, statusBar);
      actionFactory.registerListener(CapturedBrowsers.class, statusBar);
      actionFactory.registerListener(CapturedBrowsers.class, capturedBrowsersPanel);
      injector.getInstance(ActionRunner.class).runActions();
      JFrame appFrame = buildMainAppFrame();
      appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      appFrame.pack();
      appFrame.setVisible(true);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to start the server: ", e);
    }
  }

  @SuppressWarnings("serial")
  private JFrame buildMainAppFrame() {
    return new JFrame("JSTestDriver Browser Manager") {{
      add(new JSplitPane(JSplitPane.VERTICAL_SPLIT) {{
        setTopComponent(new JPanel(new BorderLayout()) {{
          add(new JPanel(new BorderLayout()) {{
            add(statusBar, NORTH);
            add(infoPanel, CENTER);
            add(capturedBrowsersPanel, SOUTH);
          }}, SOUTH);
        }});
        setBottomComponent(logPanel);
      }});
    }};  
  }
}
