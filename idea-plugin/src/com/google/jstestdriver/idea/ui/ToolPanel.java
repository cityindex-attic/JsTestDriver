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
package com.google.jstestdriver.idea.ui;

import com.intellij.util.ui.UIUtil;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ToolPanel extends JPanel {
  public ToolPanel() {
    setLayout(new BorderLayout());
    setBackground(UIUtil.getTreeTextBackground());
    add(new ServerControlPanel(), BorderLayout.NORTH);
    add(new TestExecutionPanel(), BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new ToolPanel());
    frame.pack();
    frame.setVisible(true);
  }
}
