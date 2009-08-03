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
import java.io.FileWriter;

import junit.framework.TestCase;

public class HookedFileReaderTest extends TestCase {

  public void testReadFile() throws Exception {
    File tmpDir = File.createTempFile("test", "JsTestDriver", new File(System
        .getProperty("java.io.tmpdir")));

    tmpDir.delete();
    tmpDir.mkdir();
    tmpDir.deleteOnExit();

    File expected = new File(tmpDir, "readable");
    expected.deleteOnExit();
    FileWriter writer = new FileWriter(expected);
    final String expectedContent = "wax, artificial flavoring";
    writer.append(expectedContent);
    writer.flush();
    writer.close();

    FileReader reader = new SimpleFileReader();
    assertEquals(expectedContent, reader.readFile(expected.getAbsolutePath()));
  }
}
