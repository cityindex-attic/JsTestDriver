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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A section of the Tool Window which controls the server and shows the captured browser status.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ServerControlPanel extends JPanel {
  private StatusBar statusBar;
  private CapturedBrowsersPanel capturedBrowsersPanel;
  private ServerStartupAction serverStartupAction;
  private FilesCache cache = new FilesCache(new HashMap<String, FileInfo>());

  public ServerControlPanel() {
    statusBar = new StatusBar(StatusBar.Status.NOT_RUNNING, MessageBundle.getBundle());
    capturedBrowsersPanel = new CapturedBrowsersPanel();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(new JPanel() {{
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      add(statusBar);
      add(new JButton(PluginResources.getServerStartIcon()) {{
        addActionListener(new ServerStartActionListener());
      }});
      add(new JButton(PluginResources.getServerStopIcon()) {{
        addActionListener(new ServerStopActionListener());
      }});
    }});
    add(capturedBrowsersPanel);

  }

  private class ServerStartActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      CapturedBrowsers browsers = new CapturedBrowsers();
      browsers.addObserver(capturedBrowsersPanel);
      browsers.addObserver(statusBar);
      serverStartupAction = new ServerStartupAction(9876, browsers, cache);
      List<Observer> observers = new LinkedList<Observer>();
      observers.add(statusBar);
      serverStartupAction.addObservers(observers);
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
