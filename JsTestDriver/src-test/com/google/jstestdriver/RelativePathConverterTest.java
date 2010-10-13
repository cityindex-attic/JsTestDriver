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

import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class RelativePathConverterTest extends TestCase {

  public void testConvertRelativePathFromBaseDir() throws Exception {
    String baseDir = "/home/mooh/";
    List<FileInfo> files = Lists.newLinkedList();

    files.add(new FileInfo("/home/mooh/adirectory/afile.js", -1, -1, false, false, null));
    files.add(new FileInfo("/home/mooh/myfile.js", -1, -1, false, false, null));
    files.add(new FileInfo("/home/mooh/hehe/hihi/hoho/againafile.js", -1, -1, false, false, null));
    files.add(new FileInfo("/home/mooh/meuh/coin/quack/woof/notagain.js", -1, -1, false, false, null));
    RelativePathConverter relativePathConverter = new RelativePathConverter(baseDir, files);
    List<FileInfo> relativePaths = relativePathConverter.convert();

    assertEquals(4, relativePaths.size());
    assertEquals("adirectory/afile.js", relativePaths.get(0).getFilePath());
    assertEquals("myfile.js", relativePaths.get(1).getFilePath());
    assertEquals("hehe/hihi/hoho/againafile.js", relativePaths.get(2).getFilePath());
    assertEquals("meuh/coin/quack/woof/notagain.js", relativePaths.get(3).getFilePath());
  }
}
