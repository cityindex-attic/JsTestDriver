// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.hooks;

import java.util.Iterator;
import java.util.List;

/**
 * Allows plugins to modify the list of tests before they are sent to the 
 * Server for running.
 * @author corysmith@google.com (Cory Smith)
 */
public interface TestsPreProcessor {
  public List<String> process(String browserId, Iterator<String> tests);
}
