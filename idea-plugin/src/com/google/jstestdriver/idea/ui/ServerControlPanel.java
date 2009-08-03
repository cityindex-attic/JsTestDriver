// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.ServerShutdownAction;
import com.google.jstestdriver.ServerStartupAction;
import com.google.jstestdriver.idea.MessageBundle;
import com.google.jstestdriver.idea.PluginResources;
import com.google.jstestdriver.ui.CapturedBrowsersPanel;
import com.google.jstestdriver.ui.StatusBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A section of the Tool Window which controls the server and shows the captured browser status.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ServerControlPanel extends JPanel {
  private StatusBar statusBar;
  private CapturedBrowsersPanel capturedBrowsersPanel;
  private ServerStartupAction serverStartupAction;
  // TODO - make configurable
  private static int serverPort = 9876;
  private FilesCache cache = new FilesCache(new HashMap<String, FileInfo>());

  public ServerControlPanel() {
    statusBar = new StatusBar(StatusBar.Status.NOT_RUNNING, MessageBundle.getBundle());
    capturedBrowsersPanel = new CapturedBrowsersPanel();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(new JPanel() {{
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      add(statusBar);
      add(new JButton(PluginResources.getServerStartIcon()) {{
        setToolTipText("Start a local server");
        addActionListener(new ServerStartActionListener());
      }});
      add(new JButton(PluginResources.getServerStopIcon()) {{
        setToolTipText("Stop the local server");
        addActionListener(new ServerStopActionListener());
      }});
    }});
    add(new JPanel() {{
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      add(new JLabel(PluginResources.getCaptureUrlMessage()));
      add(new JTextField("http://localhost:" + serverPort + "/capture") {{
        setEditable(false);
      }});
    }});
    add(capturedBrowsersPanel);
  }

  private class ServerStartActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      CapturedBrowsers browsers = new CapturedBrowsers();
      browsers.addObserver(capturedBrowsersPanel);
      browsers.addObserver(statusBar);
      serverStartupAction = new ServerStartupAction(serverPort, browsers, cache);
      serverStartupAction.addObservers(Arrays.<Observer>asList(statusBar));
      serverStartupAction.run();
    }
  }

  private class ServerStopActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (serverStartupAction != null) {
        new ServerShutdownAction(serverStartupAction).run();
      }
    }
  }
}
