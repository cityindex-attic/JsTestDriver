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

import junit.framework.TestCase;

import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommonPathResolverTest extends TestCase {

  public void testFindLongestCommonPathForOneFile() throws Exception {
    List<FileInfo> files = Lists.newLinkedList();

    files.add(new FileInfo("/home/jeremie/meh/mooh/hehe/myfile.js", -1, false, false, null));
    CommonPathResolver commonPathResolver = new CommonPathResolver(files);

    assertEquals("/home/jeremie/meh/mooh/hehe/", commonPathResolver.resolve());
  }

  public void testFindLongestCommonPathEmptyList() throws Exception {
    CommonPathResolver commonPathResolver = new CommonPathResolver(Lists.<FileInfo>newLinkedList());

    assertEquals("", commonPathResolver.resolve());
  }

  public void testFindLongestCommonPath() throws Exception {
    List<FileInfo> files = Lists.newLinkedList();

    files.add(new FileInfo("/home/jeremie/meh/mooh/hehe/myfile.js", -1, false, false, null));
    files.add(new FileInfo("/home/jeremie/meh/something/anotherfile.js", -1, false, false, null));
    files.add(new FileInfo("/home/jeremie/meh/mooh/else/yaf.js", -1, false, false, null));
    CommonPathResolver commonPathResolver = new CommonPathResolver(files);

    assertEquals("/home/jeremie/meh/", commonPathResolver.resolve());
  }

  public void testDirSubstringOfOtherDir() throws Exception {
    List<FileInfo> files = Lists.newLinkedList();

    files.add(new FileInfo("/home/jeremie/meh/mooh/hehe/myfile.js", -1, false, false, null));
    files
        .add(new FileInfo("/home/jeremie/mehmeh/something/anotherfile.js", -1, false, false, null));
    CommonPathResolver commonPathResolver = new CommonPathResolver(files);

    assertEquals("/home/jeremie/", commonPathResolver.resolve());
  }
}
