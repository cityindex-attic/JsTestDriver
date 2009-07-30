// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.eclipse.ui.launch;

import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.TestResultGenerator;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class EclipseResponseStreamFactory implements ResponseStreamFactory {

  public ResponseStream getDryRunActionResponseStream() {
    return null;
  }

  public ResponseStream getEvalActionResponseStream() {
    return null;
  }

  public ResponseStream getResetActionResponseStream() {
    return null;
  }

  public ResponseStream getRunTestsActionResponseStream() {
    return new EclipseRunTestsResponseStream(new TestResultGenerator());
  }
}
