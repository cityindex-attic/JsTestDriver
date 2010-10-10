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
import com.google.jstestdriver.hooks.FileLoadPreProcessor;
import com.google.jstestdriver.hooks.JstdTestCaseProcessor;

/**
 * @author corysmith@google.com (Your Name Here)
 *
 */
public class RunDataFactoryTest extends TestCase {
  public void testPreProcessFileSet() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 12434, false, false, null);
    final FileInfo addedInfo = new FileInfo("addedfoo.js", 12434, false, false, null);

    FileLoadPreProcessor preProcessor = new FileLoadPreProcessor(){
      public List<FileInfo> process(List<FileInfo> files) {
        files.add(addedInfo);
        return new LinkedList<FileInfo>(files);
      }
    };

    final Set<FileInfo> fileSet = Sets.newLinkedHashSet();
    fileSet.add(info);
    final List<FileInfo> actual = Lists.newArrayList(new RunDataFactory(
        fileSet, Collections.<FileInfo> emptyList(), Sets
            .newHashSet(preProcessor), Collections
            .<JstdTestCaseProcessor> emptySet()).get().getFileSet());

    assertEquals(info.getFilePath(), actual.get(0).getFilePath());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());

    assertEquals(addedInfo.getFilePath(), actual.get(1).getFilePath());
    assertEquals(addedInfo.getTimestamp(), actual.get(1).getTimestamp());
    assertEquals(addedInfo.isServeOnly(), actual.get(1).isServeOnly());
  }


  public void testCreateWithTests() throws Exception {
    Set<FileInfo> fileSet = Sets.newLinkedHashSet();
    FileInfo one = new FileInfo("one.js", 1234, false, false, null);
    FileInfo two = new FileInfo("two.js", 1234, false, false, null);
    FileInfo three = new FileInfo("three.js", 1234, false, false, null);
    fileSet.add(one);
    fileSet.add(two);
    fileSet.add(three);

    FileInfo testOne = new FileInfo("oneTest.js", 1234, false, false, null);
    FileInfo testTwo = new FileInfo("twoTest.js", 1234, false, false, null);
    FileInfo testThree = new FileInfo("threeTest.js", 1234, false, false, null);
    List<FileInfo> tests = Lists.newArrayList(testOne, testTwo, testThree);
    RunData runData = new RunDataFactory(fileSet, tests, Collections
        .<FileLoadPreProcessor> emptySet(), Collections
        .<JstdTestCaseProcessor> emptySet()).get();

    List<JstdTestCase> testCases = runData.getTestCases();
    assertEquals(1, testCases.size());
    JstdTestCase jstdTestCase = testCases.get(0);
    assertEquals(tests, jstdTestCase.getTests());
    assertEquals(Lists.newArrayList(fileSet), jstdTestCase.getDependencies());
  }

  public void testCreateWithOutTests() throws Exception {
    Set<FileInfo> fileSet = Sets.newLinkedHashSet();
    FileInfo one = new FileInfo("one.js", 1234, false, false, null);
    FileInfo two = new FileInfo("two.js", 1234, false, false, null);
    FileInfo three = new FileInfo("three.js", 1234, false, false, null);
    FileInfo testOne = new FileInfo("oneTest.js", 1234, false, false, null);
    FileInfo testTwo = new FileInfo("twoTest.js", 1234, false, false, null);
    FileInfo testThree = new FileInfo("threeTest.js", 1234, false, false, null);
    fileSet.add(one);
    fileSet.add(two);
    fileSet.add(three);
    fileSet.add(testOne);
    fileSet.add(testTwo);
    fileSet.add(testThree);

    RunData runData = new RunDataFactory(fileSet, Collections
        .<FileInfo> emptyList(), Collections.<FileLoadPreProcessor> emptySet(),
        Collections.<JstdTestCaseProcessor> emptySet()).get();

    List<JstdTestCase> testCases = runData.getTestCases();
    assertEquals(1, testCases.size());
    JstdTestCase jstdTestCase = testCases.get(0);
    assertTrue(jstdTestCase.getTests().isEmpty());
    assertEquals(Lists.newArrayList(fileSet), jstdTestCase.getDependencies());
  }
}
