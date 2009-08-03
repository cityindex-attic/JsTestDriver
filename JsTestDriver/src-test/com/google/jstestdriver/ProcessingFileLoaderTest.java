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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.google.jstestdriver.hooks.FileLoadPostProcessor;
import com.google.jstestdriver.hooks.FileLoadingPreProcessor;

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
      assertEquals(info.getFileName(), file);
      return infoData;
    }
  }

  private final class MockFileFilter implements JsTestDriverFileFilter {

    private final boolean shouldReset;

    private final String infoData;

    private MockFileFilter(boolean shouldReset, String infoData) {
      this.shouldReset = shouldReset;
      this.infoData = infoData;
    }

    public String filterFile(String content, boolean reload) {
      assertEquals(infoData, content);
      assertEquals(!shouldReset, reload);
      return content + "filtered";
    }

    public Collection<String> resolveFilesDeps(String file) {
      throw new UnsupportedOperationException("not expected");
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
    final FileInfo info = new FileInfo("foo.js", 1234, false, true, null);
    final String infoData = "foobar";
    final boolean shouldReset = false;
    List<FileInfo> actual = new ProcessingFileLoader(new MockFileFilter(shouldReset, infoData), new FileReader() {

      public String readFile(String file) {
        assertEquals(info.getFileName(), file);
        return infoData;
      }

    }, Collections.<FileLoadPostProcessor> emptySet(), Collections
        .<FileLoadingPreProcessor> emptySet()).loadFiles(Collections.singleton(info), shouldReset);

    assertEquals(infoData + "filtered", actual.get(0).getData());
    assertEquals(info.getFileName(), actual.get(0).getFileName());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
  }

  public void testLoadFilesWithPostProcessor() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 1234, false, true, null);
    final String infoData = "foobar";
    final boolean shouldReset = false;
    final FileInfo expected = new FileInfo("other.js", 4321, false, true, null);

    FileLoadPostProcessor processor = new FileLoadPostProcessor() {
      public FileInfo process(FileInfo file) {
        assertEquals(infoData + "filtered", file.getData());
        assertEquals(info.getFileName(), file.getFileName());
        assertEquals(info.getTimestamp(), file.getTimestamp());
        assertEquals(info.isServeOnly(), file.isServeOnly());
        return expected;
      }
    };

    List<FileInfo> actual = new ProcessingFileLoader(new MockFileFilter(shouldReset, infoData),
        new FileReaderStub(infoData, info), Collections.singleton(processor), Collections.<FileLoadingPreProcessor> emptySet()).loadFiles(
        Collections.singleton(info), shouldReset);

    assertEquals(expected, actual.get(0));
  }

  public void testLoadFileWithPatches() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 1234, false, true, null);
    FileInfo patch = new FileInfo("patchfoo.js", 1234, false, true, null);
    info.addPatch(patch);
    final String infoData = "foobar";
    final String patchData = "patchbar";
    final boolean shouldReset = false;
    MockFileReader mockFileReader = new MockFileReader();
    mockFileReader.expected(info.getFileName(), infoData).expected(patch.getFileName(), patchData);
    List<FileInfo> actual = new ProcessingFileLoader(new JsTestDriverFileFilter() {

      public String filterFile(String content, boolean reload) {
        assertEquals(infoData, content);
        assertEquals(!shouldReset, reload);
        return content + "filtered";
      }

      public Collection<String> resolveFilesDeps(String file) {
        throw new UnsupportedOperationException("not expected");
      }

    }, mockFileReader, Collections.<FileLoadPostProcessor> emptySet(), Collections
        .<FileLoadingPreProcessor> emptySet()).loadFiles(Collections.singleton(info), shouldReset);

    assertEquals(infoData + "filtered" + patchData, actual.get(0).getData());
    assertEquals(info.getFileName(), actual.get(0).getFileName());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
  }

  public void testRemoteLoadFiles() throws Exception {
    final FileInfo info = new FileInfo("http://local/foo.js", -1, false, false, null);
    final boolean shouldReset = false;
    List<FileInfo> actual = new ProcessingFileLoader(null, null, Collections
        .<FileLoadPostProcessor> emptySet(), Collections.<FileLoadingPreProcessor> emptySet())
        .loadFiles(Collections.singleton(info), shouldReset);

    assertEquals("", actual.get(0).getData());
    assertEquals(info.getFileName(), actual.get(0).getFileName());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
  }

  public void testLoadFilesWithPreProcessor() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 12434, false, false, null);
    final String infoData = "foobar";
    final FileInfo addedInfo = new FileInfo("addedfoo.js", 12434, false, false, null);
    final String addedData = "foobar";
    final boolean shouldReset = false;
    
    FileLoadingPreProcessor preProcessor = new FileLoadingPreProcessor(){
      public List<FileInfo> process(List<FileInfo> files) {
        files.add(addedInfo);
        return new LinkedList<FileInfo>(files);
      }
    };
    
    MockFileReader reader = new MockFileReader();
    reader.expected(info.getFileName(), infoData);
    reader.expected(addedInfo.getFileName(), addedData);
    List<FileInfo> actual = new ProcessingFileLoader(new MockFileFilter(shouldReset, infoData),
        reader,
        Collections.<FileLoadPostProcessor> emptySet(),
        Collections.singleton(preProcessor))
    .loadFiles(Collections.singleton(info), shouldReset);

    assertEquals(infoData + "filtered", actual.get(0).getData());
    assertEquals(info.getFileName(), actual.get(0).getFileName());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());

    assertEquals(addedData + "filtered", actual.get(1).getData());
    assertEquals(addedInfo.getFileName(), actual.get(1).getFileName());
    assertEquals(addedInfo.getTimestamp(), actual.get(1).getTimestamp());
    assertEquals(addedInfo.isServeOnly(), actual.get(1).isServeOnly());
  }
}
