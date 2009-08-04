// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResult.Result;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * Tests for {@link com.google.jstestdriver.idea.ui.TestResultTreeModel}
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestResultTreeModelTest extends TestCase {

  private TestResultTreeNode root = new TestResultTreeNode("root");
  private TestResult passed;
  private TestResult failed;
  private TestResult errored;
  private BrowserInfo browser;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    browser = new BrowserInfo();
    browser.setId(1);
    browser.setName("Firefox");
    browser.setOs("Windows");
    browser.setVersion("2.0");
    passed = new TestResult(browser, "passed", "message", "log",
      "testCaseName", "testName", 1f);
    failed = new TestResult(browser, "failed", "message", "log",
      "testCaseName", "failingTest", 1f);
    errored = new TestResult(browser, "error", "message", "log",
      "testCaseName", "erroredTest", 1f);
  }

  public void testAddingTestAddsBrowserAndTestCaseNodes() throws Exception {
    TestResultTreeModel model = new TestResultTreeModel(root);
    model.addResult(passed);
    waitForAwtEvents();

    assertEquals(1, model.getChildCount(root));
    Object browserNode = model.getChild(root, 0);
    assertEquals(1, model.getChildCount(browserNode));
    Object testCaseNode = model.getChild(browserNode, 0);
    assertEquals(1, model.getChildCount(testCaseNode));
    Object testNode = model.getChild(testCaseNode, 0);

    assertEquals("Firefox 2.0 Windows", browserNode.toString());
    assertEquals("testCaseName", testCaseNode.toString());
    assertEquals("testName", testNode.toString());
  }

  public void testAddingFailedTestMarksBrowserAndTestCaseAsFailed() throws Exception {
    TestResultTreeModel model = new TestResultTreeModel(root);
    model.addResult(passed);
    model.addResult(failed);
    waitForAwtEvents();
    TestResultTreeNode browserNode = (TestResultTreeNode) model.getChild(root, 0);
    TestResultTreeNode testCaseNode = (TestResultTreeNode) model.getChild(browserNode, 0);

    assertEquals(Result.failed, browserNode.getResult());
    assertEquals(Result.failed, testCaseNode.getResult());
  }

  public void testAddingErroredTestMarksBrowserAndTestCaseError() throws Exception {
    TestResultTreeModel model = new TestResultTreeModel(root);
    model.addResult(passed);
    model.addResult(failed);
    model.addResult(errored);
    waitForAwtEvents();
    TestResultTreeNode browserNode = (TestResultTreeNode) model.getChild(root, 0);
    TestResultTreeNode testCaseNode = (TestResultTreeNode) model.getChild(browserNode, 0);

    assertEquals(Result.error, browserNode.getResult());
    assertEquals(Result.error, testCaseNode.getResult());
  }

  public void testModelCanBeCleared() throws Exception {
    TestResultTreeModel model = new TestResultTreeModel(root);
    model.addResult(passed);
    waitForAwtEvents();
    model.clear();
    waitForAwtEvents();
    // clear() creates a new root, can't use our reference
    assertEquals(0, model.getChildCount(model.getRoot()));
    model.addResult(failed);
    waitForAwtEvents();
    assertEquals(1, model.getChildCount(model.getRoot()));
    assertEquals(1, model.getChildCount(model.getChild(model.getRoot(), 0)));
  }

  public void testFilteringRemovesPassingTests() throws Exception {
    TestResultTreeModel model = new TestResultTreeModel(root);
    model.addResult(passed);
    model.addResult(failed);
    model.setPassingTestsFilter(true);
    waitForAwtEvents();
    Object testCaseNode = model.getChild(model.getChild(root, 0), 0);
    assertEquals(1, model.getChildCount(testCaseNode));
    assertEquals("failingTest", model.getChild(testCaseNode, 0).toString());
    try {
      model.getChild(testCaseNode, 1);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      // expected
    }
  }

  private void waitForAwtEvents() throws InterruptedException, InvocationTargetException {
    SwingUtilities.invokeAndWait(new Runnable() {
      public void run() {
        // make sure AWT event thread is done processing
      }
    });
  }
}
