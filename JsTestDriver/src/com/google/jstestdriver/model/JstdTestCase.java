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

import java.util.List;
import java.util.Set;

/**
 * Defines TestCase information allowing intelligent management and execution
 * of tests.
 * 
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class JstdTestCase {

  private final List<FileInfo> dependencies;
  private final List<FileInfo> tests;
  private final List<FileInfo> plugins;

  public JstdTestCase(List<FileInfo> dependencies,
                      List<FileInfo> tests,
                      List<FileInfo> plugins) {
    this.dependencies = dependencies;
    this.tests = tests;
    this.plugins = plugins;
  }

  public List<FileInfo> getTests() {
    return tests;
  }

  public List<FileInfo> getDependencies() {
    return dependencies;
  }
  
  public List<FileInfo> getPlugins() {
    return plugins;
  }
  
  /**
   * Adaptor to translate to fileset for uploading to the server.
   */
  public Set<FileInfo> toFileSet() {
    final Set<FileInfo> fileSet = Sets.newLinkedHashSet(plugins);
    fileSet.addAll(dependencies);
    fileSet.addAll(tests);
    return fileSet;
  }
  
  public JstdTestCase updatePlugins(List<FileInfo> plugins) {
    final List<FileInfo> combined =
      Lists.newArrayListWithExpectedSize(plugins.size() + this.plugins.size());
    combined.addAll(this.plugins);
    combined.addAll(plugins);
    return new JstdTestCase(dependencies, tests, combined);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dependencies == null) ? 0 : dependencies.hashCode());
    result = prime * result + ((plugins == null) ? 0 : plugins.hashCode());
    result = prime * result + ((tests == null) ? 0 : tests.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JstdTestCase other = (JstdTestCase) obj;
    if (dependencies == null) {
      if (other.dependencies != null) return false;
    } else if (!dependencies.equals(other.dependencies)) return false;
    if (plugins == null) {
      if (other.plugins != null) return false;
    } else if (!plugins.equals(other.plugins)) return false;
    if (tests == null) {
      if (other.tests != null) return false;
    } else if (!tests.equals(other.tests)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "JstdTestCase [dependencies=" + dependencies + ", plugins=" + plugins + ", tests="
        + tests + "]";
  }
}
