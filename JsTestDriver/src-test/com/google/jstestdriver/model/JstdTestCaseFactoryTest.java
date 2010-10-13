// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.JstdTestCaseProcessor;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Tests the creation of JstdTestCases.
 * 
 * @author corbinrsmith@gmail (Cory Smith)
 */
public class JstdTestCaseFactoryTest extends TestCase {

  public void testCreateWithTests() throws Exception {
    List<FileInfo> files = Lists.newArrayList();
    FileInfo one = new FileInfo("one.js", 1234, false, false, null);
    FileInfo two = new FileInfo("two.js", 1234, false, false, null);
    FileInfo three = new FileInfo("three.js", 1234, false, false, null);
    files.add(one);
    files.add(two);
    files.add(three);

    FileInfo testOne = new FileInfo("oneTest.js", 1234, false, false, null);
    FileInfo testTwo = new FileInfo("twoTest.js", 1234, false, false, null);
    FileInfo testThree = new FileInfo("threeTest.js", 1234, false, false, null);
    List<FileInfo> tests = Lists.newArrayList(testOne, testTwo, testThree);
    final JstdTestCaseFactory testCaseFactory = new JstdTestCaseFactory(
        Collections.<JstdTestCaseProcessor> emptySet());

    List<JstdTestCase> testCases = testCaseFactory.createCases(files, tests);
    assertEquals(1, testCases.size());
    JstdTestCase jstdTestCase = testCases.get(0);
    assertEquals(tests, jstdTestCase.getTests());
    assertEquals(Lists.newArrayList(files), jstdTestCase.getDependencies());
  }

  public void testCreateWithOutTests() throws Exception {
    List<FileInfo> fileSet = Lists.newArrayList();
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

    final JstdTestCaseFactory testCaseFactory =
        new JstdTestCaseFactory(Collections.<JstdTestCaseProcessor> emptySet());

    List<JstdTestCase> testCases =
        testCaseFactory.createCases(fileSet, Lists.<FileInfo>newArrayList());
    assertEquals(1, testCases.size());
    JstdTestCase jstdTestCase = testCases.get(0);
    assertTrue(jstdTestCase.getTests().isEmpty());
    assertEquals(Lists.newArrayList(fileSet), jstdTestCase.getDependencies());
  }
  
  public void testUpdateTestCasesFromRunData() throws Exception {
    FileInfo plugin = new FileInfo("plugin.js", 1234, false, false, null);
    List<FileInfo> files = Lists.newArrayList();
    FileInfo one = new FileInfo("one.js", 1234, false, false, null);
    FileInfo two = new FileInfo("two.js", 1234, false, false, null);
    FileInfo three = new FileInfo("three.js", 1234, false, false, null);
    files.add(one);
    files.add(two);
    files.add(three);

    FileInfo testOne = new FileInfo("oneTest.js", 1234, false, false, null);
    FileInfo testTwo = new FileInfo("twoTest.js", 1234, false, false, null);
    FileInfo testThree = new FileInfo("threeTest.js", 1234, false, false, null);
    List<FileInfo> tests = Lists.newArrayList(testOne, testTwo, testThree);
    final JstdTestCaseFactory testCaseFactory = new JstdTestCaseFactory(
        Collections.<JstdTestCaseProcessor> emptySet());

    final List<JstdTestCase> testCases = testCaseFactory.createCases(files, tests);
    
    final Set<FileInfo> fileSet = new RunData(null, testCases, null).getFileSet();
    final Set<FileInfo> updatedFileSet = Sets.newLinkedHashSet();
    updatedFileSet.add(plugin);
    updatedFileSet.addAll(fileSet);

    List<JstdTestCase> updatedTestCases =
        testCaseFactory.updateCases(updatedFileSet, testCases);
    assertEquals(1, updatedTestCases.size());
    JstdTestCase jstdTestCase = updatedTestCases.get(0);
    assertEquals(tests, jstdTestCase.getTests());
    final List<FileInfo> expected = Lists.newArrayList(plugin);
    expected.addAll(files);
    assertEquals(expected, jstdTestCase.getDependencies());
  }
}
