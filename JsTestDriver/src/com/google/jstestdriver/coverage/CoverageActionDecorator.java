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

import java.util.List;

import com.google.inject.Inject;
import com.google.jstestdriver.Action;
import com.google.jstestdriver.ActionListProvider;
import com.google.jstestdriver.DefaultActionListProvider;

/**
 * Adds the coverage reporting action to the default list of actions.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CoverageActionDecorator implements ActionListProvider {
  
  private final DefaultActionListProvider defaultProvider;
  private final CoverageWriter writer;
  private final CoverageAccumulator accumulator;

  @Inject
  public CoverageActionDecorator(DefaultActionListProvider defaultProvider,
      CoverageAccumulator accumulator, CoverageWriter writer) {
    this.defaultProvider = defaultProvider;
    this.accumulator = accumulator;
    this.writer = writer;
  }
  
  public List<Action> get() {
    List<Action> actions = defaultProvider.get();
    actions.add(new CoverageReporterAction(accumulator, writer));
    return actions;
  }
}
