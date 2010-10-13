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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.ResourcePreProcessor;
import com.google.jstestdriver.hooks.JstdTestCaseProcessor;
import com.google.jstestdriver.hooks.ResourceDependencyResolver;

/**
 * @author corysmith@google.com (Your Name Here)
 *
 */
public class RunDataFactoryTest extends TestCase {
  public void testPreProcessFileSet() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 12434, -1, false, false, null);
    final FileInfo addedInfo = new FileInfo("addedfoo.js", 12434, -1, false, false, null);

    ResourcePreProcessor preProcessor = new ResourcePreProcessor(){
      public List<FileInfo> processDependencies(List<FileInfo> files) {
        files.add(addedInfo);
        return new LinkedList<FileInfo>(files);
      }

      public List<FileInfo> processPlugins(List<FileInfo> plugins) {
        return plugins;
      }

      public List<FileInfo> processTests(List<FileInfo> tests) {
        return tests;
      }
    };

    final Set<FileInfo> fileSet = Sets.newLinkedHashSet();
    fileSet.add(info);
    final RunDataFactory factory = new RunDataFactory(
      fileSet,
      Collections.<FileInfo> emptyList(),
      Sets.newHashSet(preProcessor),
      Collections.<FileInfo>emptyList(), new JstdTestCaseFactory(
          Collections.<JstdTestCaseProcessor> emptySet(),
          Collections.<ResourceDependencyResolver>emptySet()));
    
    final List<FileInfo> actual = Lists.newArrayList(factory.get().getFileSet());

    assertEquals(info.getFilePath(), actual.get(0).getFilePath());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());

    assertEquals(addedInfo.getFilePath(), actual.get(1).getFilePath());
    assertEquals(addedInfo.getTimestamp(), actual.get(1).getTimestamp());
    assertEquals(addedInfo.isServeOnly(), actual.get(1).isServeOnly());
  }
}
