// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.eclipse.javascript.jstestdriver.ui.view.actions;

import com.google.eclipse.javascript.jstestdriver.ui.runner.ActionRunnerFactory;
import com.google.eclipse.javascript.jstestdriver.ui.view.JsTestDriverView;
import com.google.eclipse.javascript.jstestdriver.ui.view.TestResultsPanel;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resets captured browsers so that they all return to a clean state.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class ResetBrowsersActionDelegate implements IViewActionDelegate {

  private static final Logger logger =
      Logger.getLogger(ResetBrowsersActionDelegate.class.getCanonicalName());

  private final ActionRunnerFactory actionRunnerFactory;

  private TestResultsPanel view;

  public ResetBrowsersActionDelegate() {
    this(new ActionRunnerFactory());
  }

  public ResetBrowsersActionDelegate(ActionRunnerFactory actionRunnerFactory) {
    this.actionRunnerFactory = actionRunnerFactory;
  }

  @Override
  public void init(IViewPart view) {
    if (view instanceof JsTestDriverView) {
      this.view = ((JsTestDriverView) view).getTestResultsPanel();
    }
  }

  @Override
  public void run(IAction action) {
    if (view.getLastLaunchConfiguration() == null) {
      return;
    }
    try {
      actionRunnerFactory.getResetBrowsersActionRunner(view.getLastLaunchConfiguration());
    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE, "Conf file for JSTestDriver not found while resetting browsers", e);
    }
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
  }

}