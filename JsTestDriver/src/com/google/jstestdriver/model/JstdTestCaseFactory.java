// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.JstdTestCaseProcessor;

import java.util.List;
import java.util.Set;

/**
 * A factory for the creation of test cases.
 * @author corysmith@google.com (Cory Smith)
 */
public class JstdTestCaseFactory {

  private final Set<JstdTestCaseProcessor> processors;

  @Inject
  public JstdTestCaseFactory(Set<JstdTestCaseProcessor> processors) {
    this.processors = processors;
  }

  public List<JstdTestCase> createCases(List<FileInfo> files, List<FileInfo> tests) {
    List<JstdTestCase> testCases = Lists.newArrayList();
    if (tests.isEmpty()) {
      testCases.add(new JstdTestCase(files, Lists.<FileInfo>newArrayList()));
    } else {
      testCases.add(new JstdTestCase(files, tests));
      for (JstdTestCaseProcessor processor : processors) {
        testCases = processor.process(testCases.iterator());
      }
    }
    return testCases;
  }

  // TODO(corysmith): Remove when RunData no longer allows access to the FileSet.
  public List<JstdTestCase> updateCases(Set<FileInfo> fileSet, List<JstdTestCase> testCases) {
    Set<FileInfo> tests = Sets.newLinkedHashSet();
    for (JstdTestCase testCase : testCases) {
      tests.addAll(testCase.getTests());
    }
    fileSet.removeAll(tests);
    return createCases(Lists.newArrayList(fileSet), Lists.newArrayList(tests));
  }
}
