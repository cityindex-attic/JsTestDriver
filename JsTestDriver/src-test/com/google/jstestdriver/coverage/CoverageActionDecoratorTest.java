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
package com.google.jstestdriver.coverage;

import com.google.common.collect.Lists;
import com.google.jstestdriver.Action;
import com.google.jstestdriver.ServerStartupAction;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.hooks.AuthStrategy;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class CoverageActionDecoratorTest extends TestCase {
  public void testDecorate() throws Exception {
    CoverageReporterAction reporter = new CoverageReporterAction(null, null);
    List<Action> actions =
        Lists.<Action>newArrayList(new ServerStartupAction(0, null, null,
            SlaveBrowser.TIMEOUT, null, Collections.<AuthStrategy>emptySet(), false, null));
    List<Action> actual = new CoverageActionDecorator(reporter).process(actions);
    assertEquals(2, actual.size());
    assertTrue(actual.get(0) instanceof ServerStartupAction);
    assertTrue(actual.get(1) instanceof CoverageReporterAction);
  }
}
