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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import junit.framework.TestCase;

import com.google.jstestdriver.hooks.FileReaderHook;

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
    final String expectedProcessedContent = "velveeta";
    writer.append(expectedContent);
    writer.flush();
    writer.close();

    FileReader reader = new HookedFileReader(new HashSet<FileReaderHook>(Arrays
        .asList(new TestHook(expectedContent, expectedProcessedContent))));
    assertEquals(expectedProcessedContent, reader.readFile(expected.getAbsolutePath()));
  }

  public void testReadFileMultipleHooks() throws Exception {
    File tmpDir = File.createTempFile("test", "JsTestDriver", new File(System
        .getProperty("java.io.tmpdir")));

    tmpDir.delete();
    tmpDir.mkdir();
    tmpDir.deleteOnExit();

    File expected = new File(tmpDir, "readable");
    expected.deleteOnExit();
    FileWriter writer = new FileWriter(expected);
    final String expectedContent = "wax, artificial flavoring";
    final String expectedProcessedContent1 = "edible candle";
    final String expectedProcessedContent2 = "melted velveeta";
    writer.append(expectedContent);
    writer.flush();
    writer.close();

    FileReader reader = new HookedFileReader(new LinkedHashSet<FileReaderHook>(Arrays.asList(
        new TestHook(expectedContent, expectedProcessedContent1), new TestHook(
            expectedProcessedContent1, expectedProcessedContent2))));
    assertEquals(expectedProcessedContent2, reader.readFile(expected.getAbsolutePath()));
  }

  private final class TestHook implements FileReaderHook {
    private final String expectedContent;
    private final String expectedProcessedContent;

    private TestHook(String expectedContent, String expectedProcessedContent) {
      this.expectedContent = expectedContent;
      this.expectedProcessedContent = expectedProcessedContent;
    }

    public String process(String contents) {
      assertEquals(expectedContent, contents);
      return expectedProcessedContent;
    }
  }
}
