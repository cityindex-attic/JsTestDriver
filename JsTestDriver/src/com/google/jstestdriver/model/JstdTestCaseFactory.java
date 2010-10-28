// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.JstdTestCaseProcessor;
import com.google.jstestdriver.hooks.ResourceDependencyResolver;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A factory for the creation of test cases.
 * @author corysmith@google.com (Cory Smith)
 */
public class JstdTestCaseFactory {

  private final Set<JstdTestCaseProcessor> processors;
  private final Set<ResourceDependencyResolver> resolvers;

  @Inject
  public JstdTestCaseFactory(Set<JstdTestCaseProcessor> processors,
                             Set<ResourceDependencyResolver> resolvers) {
    this.processors = processors;
    this.resolvers = resolvers;
  }

  public List<JstdTestCase> createCases(
      List<FileInfo> plugins,
      List<FileInfo> deps,
      List<FileInfo> tests) {
    final List<JstdTestCase> testCases = Lists.newArrayList();
    if (!(deps.isEmpty() && tests.isEmpty())) {
      testCases.add(
          new JstdTestCase(
              deps,
              tests,
              plugins));
    }
    return processTestCases(resolveDependencies(testCases));
  }

  private List<JstdTestCase> resolveDependencies(List<JstdTestCase> testCases) {
    final List<JstdTestCase> resolved =
      Lists.newArrayListWithExpectedSize(testCases.size());
    for (JstdTestCase jstdTestCase : testCases) {
      for (ResourceDependencyResolver resolver : resolvers) {
        jstdTestCase = resolver.resolve(jstdTestCase);
      }
      resolved.add(jstdTestCase);
    }
    return resolved;
  }

  private List<JstdTestCase> processTestCases(List<JstdTestCase> testCases) {
    for (JstdTestCaseProcessor processor : processors) {
      testCases = processor.process(testCases.iterator());
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
    return createCases(Collections.<FileInfo>emptyList(), Lists.newArrayList(fileSet), Lists.newArrayList(tests));
  }
}
