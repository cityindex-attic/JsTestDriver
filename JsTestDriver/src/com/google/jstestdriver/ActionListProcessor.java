// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import java.util.List;

/**
 * A hook for modifying the list JsTestDriver actions before execution.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public interface ActionListProcessor {
  public List<Action> process(List<Action> actions);
}
