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

import com.google.common.collect.Lists;
import com.google.jstestdriver.output.Problem;

import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class TestRunResult {
  private int passes = 0;
  private int fails = 0;
  private int errors = 0;
  private float totalTime;
  private final List<Problem> problems = Lists.newLinkedList();

  public void addPass() {
    passes++;
  }

  public void addFail() {
    fails++;
  }

  public void addError() {
    errors++;
  }

  public void addProblem(Problem problem) {
    problems.add(problem);
  }

  public void addTime(float time) {
    totalTime += time;
  }

  public int getPassed() {
    return passes;
  }

  public int getFailed() {
    return fails;
  }

  public int getErrors() {
    return errors;
  }

  public List<Problem> getProblems() {
    return problems;
  }

  public float getTotalTime() {
    return totalTime;
  }
}
