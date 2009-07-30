// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.eclipse.ui.launch;

import com.google.jstestdriver.JsTestDriverClient;
import com.google.jstestdriver.ResponsePrinterFactory;
import com.google.jstestdriver.RunTestsAction;

import java.util.List;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class EclipseRunTestsAction extends RunTestsAction {

  public EclipseRunTestsAction(List<String> tests, ResponsePrinterFactory factory,
      boolean captureConsole) {
    super(tests, factory, captureConsole);
  }

  @Override
  public void run(String id, JsTestDriverClient client) {
    if (getTests().size() == 1 && getTests().get(0).equals("all")) {
      client.runAllTests(id, new EclipseResponseStreamFactory(), isCaptureConsole());
    } else if (getTests().size() > 0) {
      // Not handled yet.
      //client.runTests(id, new EclipseResponseStreamFactory(), createTestCaseList(getTests()),
      //    isCaptureConsole());
    }
  }

}
