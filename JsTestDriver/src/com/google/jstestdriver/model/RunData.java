/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.ResponseStream;

import java.util.List;
import java.util.Set;

/**
 * An immutable container, for the necessary data and responses from a JsTestDriver run.
 * @author corysmith@google.com (Cory Smith)
 *
 */
final public class RunData {
  private final List<ResponseStream> responses;
  private final List<JstdTestCase> testCases;
  private final JstdTestCaseFactory testCaseFactory;

  public RunData(List<ResponseStream> responses,
      List<JstdTestCase> testCases,
      JstdTestCaseFactory testCaseFactory) {
    this.responses = responses;
    this.testCases = testCases;
    this.testCaseFactory = testCaseFactory;
  }

  public RunData recordResponse(ResponseStream responseStream) {
    final List<ResponseStream> newResponses = Lists.newLinkedList(responses);
    newResponses.add(responseStream);
    return new RunData(newResponses, testCases, testCaseFactory);
  }

  public RunData aggregateResponses(RunData runData) {
    final List<ResponseStream> newResponses = Lists.newLinkedList(responses);
    newResponses.addAll(runData.responses);
    return new RunData(newResponses, testCases, testCaseFactory);
  }

  public Set<FileInfo> getFileSet() {
    final Set<FileInfo> fileSet = Sets.newLinkedHashSet();
    for (JstdTestCase testCase : testCases) {
      fileSet.addAll(testCase.toFileSet());
    }
    return fileSet;
  }

  public void finish() {
    for (ResponseStream response : responses) {
      response.finish();
    }
  }

  /**
   * Deprecated in favor of modifying the {@link JstdTestCase}'s directly.
   * @deprecated
   */
  @Deprecated
  public RunData updateFileSet(Set<FileInfo> fileSet) {
    return new RunData(
      responses,
      testCaseFactory.updateCases(fileSet, testCases),
      testCaseFactory);
  }

  public RunData updateTestCases(List<JstdTestCase> testCases) {
    return new RunData(
      responses,
      testCases,
      testCaseFactory);
  }

  @Override
  public String toString() {
    return "RunData [responses=" + responses + ", testCaseFactory=" + testCaseFactory
        + ", testCases=" + testCases + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((responses == null) ? 0 : responses.hashCode());
    result = prime * result + ((testCaseFactory == null) ? 0 : testCaseFactory.hashCode());
    result = prime * result + ((testCases == null) ? 0 : testCases.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RunData other = (RunData) obj;
    if (responses == null) {
      if (other.responses != null) return false;
    } else if (!responses.equals(other.responses)) return false;
    if (testCaseFactory == null) {
      if (other.testCaseFactory != null) return false;
    } else if (!testCaseFactory.equals(other.testCaseFactory)) return false;
    if (testCases == null) {
      if (other.testCases != null) return false;
    } else if (!testCases.equals(other.testCases)) return false;
    return true;
  }

  public List<JstdTestCase> getTestCases() {
    return testCases;
  }
}
