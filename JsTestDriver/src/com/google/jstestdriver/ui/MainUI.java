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

import com.google.jstestdriver.Action;
import com.google.jstestdriver.ActionFactory;
import com.google.jstestdriver.ActionParser;
import com.google.jstestdriver.ActionRunner;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.ConfigurationParser;
import com.google.jstestdriver.Flags;
import com.google.jstestdriver.FlagsImpl;
import com.google.jstestdriver.ServerStartupAction;

import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.mortbay.log.Slf4jLog;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

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
    Flags flags = new FlagsImpl();
    CmdLineParser parser = new CmdLineParser(flags);
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
    }

    configureLogging();
    ResourceBundle bundle = ResourceBundle.getBundle("com.google.jspuppet.ui.messages");
    new MainUI(flags, bundle).startUI();
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
      ActionFactory actionFactory = new ActionFactory();
      actionFactory.registerListener(ServerStartupAction.class, statusBar);
      actionFactory.registerListener(CapturedBrowsers.class, statusBar);
      actionFactory.registerListener(CapturedBrowsers.class, capturedBrowsersPanel);
      File config = new File(flags.getConfig());
      Set<String> fileSet = new LinkedHashSet<String>();
      String defaultServerAddress = null;

      if (flags.getTests().size() > 0 || flags.getReset() || !flags.getArguments().isEmpty() ||
          flags.getPreloadFiles()) {
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
      List<Action> actions = new ActionParser(actionFactory).parseFlags(flags, fileSet,
          defaultServerAddress);
      ActionRunner runner = new ActionRunner(actions);
      runner.runActions();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to start the server: ", e);
    }

    JFrame appFrame = buildMainAppFrame();
    appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    appFrame.pack();
    appFrame.setVisible(true);
  }

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
