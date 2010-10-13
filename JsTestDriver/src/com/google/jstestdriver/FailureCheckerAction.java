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
package com.google.jstestdriver;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.jstestdriver.model.RunData;
import com.google.jstestdriver.output.TestResultHolder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FailureCheckerAction implements Action {
  private final FailureAccumulator accumulator;
  private final TestResultHolder holder;

  @Inject
  public FailureCheckerAction(FailureAccumulator accumulator, TestResultHolder holder) {
    this.accumulator = accumulator;
    this.holder = holder;
  }

  public RunData run(RunData runData) {
    if (accumulator.hasFailures()) {
      throw new FailureException("Tests failed. See log for details.");
    }
    List<String> failures = Lists.newLinkedList();
    Map<BrowserInfo, Collection<TestResult>> resultsCollections = holder.getResults().asMap();

    if (holder.getResults().isEmpty()) {
      throw new FailureException("No tests executed.");
    }
    for (Entry<BrowserInfo, Collection<TestResult>> entry : resultsCollections.entrySet()) {
      if (entry.getValue().isEmpty()) {
        failures.add(entry.getKey() + " had no tests executed.");
      }
    }
    if (!failures.isEmpty()) {
      throw new FailureException(Joiner.on("\n").join(failures));
    }
    return runData;
  }
}
