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
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.hooks.ResourcePreProcessor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RunDataFactory {
  private final Set<FileInfo> fileSet;
  private final Set<ResourcePreProcessor> processors;
  private final List<FileInfo> tests;
  private final JstdTestCaseFactory testCaseFactory;
  private final List<FileInfo> plugins;

  @Inject
  public RunDataFactory(@Named("fileSet") Set<FileInfo> fileSet,
                        @Named("tests") List<FileInfo> tests,
                        Set<ResourcePreProcessor> processors,
                        @Named("plugins") List<FileInfo> plugins,
                        JstdTestCaseFactory testCaseFactory) {
    this.fileSet = fileSet;
    this.tests = tests;
    this.processors = processors;
    this.plugins = plugins;
    this.testCaseFactory = testCaseFactory;
  }

  public RunData get() {
    List<FileInfo> processedDependencies = Lists.newLinkedList(fileSet);
    List<FileInfo> processedPlugins = Lists.newLinkedList(plugins);
    List<FileInfo> processedTests = Lists.newLinkedList(tests);

    for (ResourcePreProcessor processor : processors) {
      processedPlugins = processor.processPlugins(processedPlugins);
      processedTests = processor.processTests(processedTests);
      processedDependencies = processor.processDependencies(processedDependencies);
    }

    return new RunData(Collections.<ResponseStream>emptyList(),
                       testCaseFactory.createCases(
                           processedPlugins,
                           processedDependencies,
                           processedTests),
                       testCaseFactory);
  }
}
