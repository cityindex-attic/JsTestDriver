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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import com.google.jstestdriver.hooks.FileLoadPostProcessor;

public class ProcessingFileLoaderTest extends TestCase {

  /**
   * @author corysmith
   *
   */
  private final class FileReaderStub implements FileReader {
    /**
     *
     */
    private final String infoData;
    /**
     *
     */
    private final FileInfo info;

    /**
     * @param infoData
     * @param info
     */
    private FileReaderStub(String infoData, FileInfo info) {
      this.infoData = infoData;
      this.info = info;
    }

    public String readFile(String file) {
      assertEquals(info.getAbsoluteFileName(new File(".")), file);
      return infoData;
    }
  }

  private final class MockFileReader implements FileReader {
    private HashMap<String, String> expected = new HashMap<String, String>();

    public MockFileReader expected(String path, String content) {
      expected.put(path, content);
      return this;
    }

    public String readFile(String path) {
      assertTrue(expected.containsKey(path));
      return expected.get(path);
    }
  }

  public void testLoadFiles() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 1234, -1, false, true, null);
    final String infoData = "foobar";
    final boolean shouldReset = false;
    List<FileInfo> actual = new ProcessingFileLoader(new FileReader() {
      public String readFile(String file) {
        assertEquals(info.getAbsoluteFileName(new File(".")), file);
        return infoData;
      }
    },
        Collections.<FileLoadPostProcessor> emptySet(), new File("."),
        new com.google.jstestdriver.util.NullStopWatch()).loadFiles(
            Collections.singleton(info), shouldReset);

    assertEquals(infoData, actual.get(0).getData());
    assertEquals(info.getFilePath(), actual.get(0).getFilePath());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
  }

  public void testLoadFilesWithPostProcessor() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 1234, -1, false, true, null);
    final String infoData = "foobar";
    final boolean shouldReset = false;
    final FileInfo expected = new FileInfo("other.js", 4321, -1, false, true, null);

    FileLoadPostProcessor processor = new FileLoadPostProcessor() {
      public FileInfo process(FileInfo file) {
        assertEquals(infoData, file.getData());
        assertEquals(info.getFilePath(), file.getFilePath());
        assertEquals(info.getTimestamp(), file.getTimestamp());
        assertEquals(info.isServeOnly(), file.isServeOnly());
        return expected;
      }
    };

    List<FileInfo> actual = new ProcessingFileLoader(new FileReaderStub(infoData, info),
        Collections.singleton(processor), new File("."),
        new com.google.jstestdriver.util.NullStopWatch()).loadFiles(
            Collections.singleton(info), shouldReset);

    assertEquals(expected, actual.get(0));
  }

  public void testLoadFileWithPatches() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 1234, -1, false, true, null);
    FileInfo patch = new FileInfo("patchfoo.js", 1234, -1, false, true, null);
    info.addPatch(patch);
    final String infoData = "foobar";
    final String patchData = "patchbar";
    final boolean shouldReset = false;
    MockFileReader mockFileReader = new MockFileReader();
    mockFileReader.expected(info.getAbsoluteFileName(new File(".")), infoData).expected(
        patch.getAbsoluteFileName(new File(".")), patchData);
    List<FileInfo> actual =
        new ProcessingFileLoader(mockFileReader,
            Collections.<FileLoadPostProcessor>emptySet(),
            new File("."),
            new com.google.jstestdriver.util.NullStopWatch()
        ).loadFiles(Collections.singleton(info), shouldReset);

    assertEquals(infoData + patchData, actual.get(0).getData());
    assertEquals(info.getFilePath(), actual.get(0).getFilePath());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
  }

  public void testRemoteLoadFiles() throws Exception {
    final FileInfo info = new FileInfo("http://local/foo.js", -1, -1, false, false, "");
    final boolean shouldReset = false;
    List<FileInfo> actual = new ProcessingFileLoader(null, Collections
        .<FileLoadPostProcessor> emptySet(), new File("."),
        new com.google.jstestdriver.util.NullStopWatch())
            .loadFiles(Collections.singleton(info), shouldReset);

    assertEquals("", actual.get(0).getData());
    assertEquals(info.getFilePath(), actual.get(0).getFilePath());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
  }
}
