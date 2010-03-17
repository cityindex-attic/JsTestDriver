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
package com.google.jstestdriver.coverage;

import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.LoadedFileInfo;

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CoverageJsAdderTest extends TestCase {

  public void testAddJs() throws Exception {
    LinkedList<FileInfo> files = new LinkedList<FileInfo>();
    String lcovSource = "lcov";
    List<FileInfo> processed = new CoverageJsAdder(new FileLoaderStub(lcovSource)).process(files);
    FileInfo lcov = processed.get(0);
    assertEquals(new LoadedFileInfo(CoverageJsAdder.LCOV_JS, -1, false, false, lcovSource), lcov);
    assertEquals(lcovSource, lcov.getData());
    assertFalse(lcov.canLoad());
  }
  
  public void testAddJsWithExistingFiles() throws Exception {
    LinkedList<FileInfo> files = new LinkedList<FileInfo>();
    FileInfo expected = new FileInfo("foo.js", 1, false, false, null);
    files.add(expected);
    String lcovSource = "lcov";
    List<FileInfo> processed = new CoverageJsAdder(new FileLoaderStub(lcovSource)).process(files);
    FileInfo lcov = processed.get(0);
    assertEquals(new LoadedFileInfo(CoverageJsAdder.LCOV_JS, -1, false, false, lcovSource), lcov);
    assertEquals(lcovSource, lcov.getData());
    assertFalse(lcov.canLoad());
    assertSame(expected, processed.get(1));
  }

  public class FileLoaderStub extends ClassFileLoader{

    private final String source;

    public FileLoaderStub(String source) {
      this.source = source;
    }
    
    @Override
    public String load(String path) {
      return source;
    }
  }
}
