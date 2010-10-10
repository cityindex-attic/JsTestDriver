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

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommonPathResolverTest extends TestCase {

  private static final char PATH_SEPARATOR = File.separatorChar;

  public void testFindLongestCommonPathForOneFile() throws Exception {
    List<FileInfo> files = Lists.newLinkedList();

    files.add(new FileInfo(String.format("%1$shome%1$sjeremie%1$smeh%1$smooh%1$shehe%1$smyfile.js",
        PATH_SEPARATOR), -1, false, false, null));
    CommonPathResolver commonPathResolver = new CommonPathResolver(files);

    assertEquals(String.format("%1$shome%1$sjeremie%1$smeh%1$smooh%1$shehe%1$s", PATH_SEPARATOR),
        commonPathResolver.resolve());
  }

  public void testFindLongestCommonPathEmptyList() throws Exception {
    CommonPathResolver commonPathResolver = new CommonPathResolver(Lists.<FileInfo>newLinkedList());

    assertEquals("", commonPathResolver.resolve());
  }

  public void testFindLongestCommonPath() throws Exception {
    List<FileInfo> files = Lists.newLinkedList();

    files.add(new FileInfo(String.format("%1$shome%1$sjeremie%1$smeh%1$smooh%1$shehe%1$smyfile.js",
        PATH_SEPARATOR), -1, false, false, null));
    files.add(new FileInfo(String.format(
        "%1$shome%1$sjeremie%1$smeh%1$ssomething%1$sanotherfile.js", PATH_SEPARATOR), -1, false,
        false, null));
    files.add(new FileInfo(String.format("%1$shome%1$sjeremie%1$smeh%1$smooh%1$selse%1$syaf.js",
        PATH_SEPARATOR), -1, false, false, null));
    CommonPathResolver commonPathResolver = new CommonPathResolver(files);

    assertEquals(String.format("%1$shome%1$sjeremie%1$smeh%1$s", PATH_SEPARATOR),
        commonPathResolver.resolve());
  }

  public void testDirSubstringOfOtherDir() throws Exception {
    List<FileInfo> files = Lists.newLinkedList();

    files.add(new FileInfo(String.format("%1$shome%1$sjeremie%1$smeh%1$smooh%1$shehe%1$smyfile.js",
        PATH_SEPARATOR), -1, false, false, null));
    files.add(new FileInfo(String.format(
        "%1$shome%1$sjeremie%1$smehmeh%1$ssomething%1$sanotherfile.js", PATH_SEPARATOR), -1, false,
        false, null));
    CommonPathResolver commonPathResolver = new CommonPathResolver(files);

    assertEquals(String.format("%1$shome%1$sjeremie%1$s", PATH_SEPARATOR), commonPathResolver
        .resolve());
  }
}
