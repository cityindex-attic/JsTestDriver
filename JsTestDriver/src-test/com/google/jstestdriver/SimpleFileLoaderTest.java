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
import java.util.List;

import junit.framework.TestCase;

public class SimpleFileLoaderTest extends TestCase {

  private final class MockFileReader implements FileReader {
    private HashMap<String,String> expected = new HashMap<String, String>();
    
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
    List<FileInfo> actual = new SimpleFileLoader(new JsTestDriverFileFilter(){

      public String filterFile(String content, boolean reload) {
        assertEquals(infoData, content);
        assertEquals(!shouldReset, reload);
        return content + "filtered";
      }

      public Collection<String> resolveFilesDeps(String file) {
        throw new UnsupportedOperationException("not expected");
      }
      
    }, new FileReader() {

      public String readFile(String file) {
        assertEquals(info.getFileName(), file);
        return infoData;
      }
      
    }).loadFiles(Collections.singleton(info), shouldReset);
    
    assertEquals(infoData + "filtered", actual.get(0).getData());
    assertEquals(info.getFileName(), actual.get(0).getFileName());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
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
    List<FileInfo> actual = new SimpleFileLoader(new JsTestDriverFileFilter(){
      
      public String filterFile(String content, boolean reload) {
        assertEquals(infoData, content);
        assertEquals(!shouldReset, reload);
        return content + "filtered";
      }
      
      public Collection<String> resolveFilesDeps(String file) {
        throw new UnsupportedOperationException("not expected");
      }
      
    }, mockFileReader).loadFiles(Collections.singleton(info), shouldReset);
    
    assertEquals(infoData + "filtered" + patchData, actual.get(0).getData());
    assertEquals(info.getFileName(), actual.get(0).getFileName());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
  }

  public void testRemoteLoadFiles() throws Exception {
    final FileInfo info = new FileInfo("http://local/foo.js", -1, false, false, null);
    final boolean shouldReset = false;
    List<FileInfo> actual = new SimpleFileLoader(null, null).loadFiles(Collections.singleton(info),
        shouldReset);
    
    assertEquals("", actual.get(0).getData());
    assertEquals(info.getFileName(), actual.get(0).getFileName());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());
  }
}
