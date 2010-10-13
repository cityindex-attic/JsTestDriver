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
import com.google.inject.Inject;
import com.google.jstestdriver.Action;
import com.google.jstestdriver.hooks.ActionListProcessor;

import java.util.List;

/**
 * Adds the coverage reporting action to the default list of actions.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CoverageActionDecorator implements ActionListProcessor {

  private final CoverageReporterAction reporter;

  @Inject
  public CoverageActionDecorator(CoverageReporterAction reporter) {
    this.reporter = reporter;
  }

  public List<Action> process(List<Action> actions) {
    List<Action> processed = Lists.newLinkedList(actions);
    processed.add(reporter);
    return processed;
  }
}
