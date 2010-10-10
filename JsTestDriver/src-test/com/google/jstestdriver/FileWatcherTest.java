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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileWatcherTest extends TestCase {

  public void testPickupFilesChange() throws Exception {
    File file = File.createTempFile("file", "watcher");

    file.createNewFile();
    file.deleteOnExit();
    Set<String> defaultFiles = new LinkedHashSet<String>();

    defaultFiles.add(file.getName());
    FileWatcher watcher = new FileWatcher(file.getParentFile().getAbsoluteFile(),
        defaultFiles);    

    file.setLastModified(file.lastModified() + 1000);
    Set<String> whitelist = new HashSet<String>();

    whitelist.add(file.getName());
    List<String> files = watcher.getAllFiles();

    assertEquals(1, files.size());
    String modifiedFile = files.get(0);

    assertTrue(modifiedFile.startsWith("file") && modifiedFile.endsWith("watcher"));
  }

  public void testReturnsEmptyListIfNoFileChanged() throws Exception {
    File file = File.createTempFile("file", "watcher");

    file.createNewFile();
    file.deleteOnExit();
    FileWatcher watcher = new FileWatcher(file.getParentFile().getAbsoluteFile(),
        new LinkedHashSet<String>());    
    List<String> allFiles = watcher.getAllFiles();

    assertEquals(0, allFiles.size());
  }

  public void testOnlyReturnsModifiedFiles() throws Exception {
    File file1 = File.createTempFile("file1", "watcher");
    File file2 = File.createTempFile("file2", "watcher");

    file1.createNewFile();
    file1.deleteOnExit();
    file2.createNewFile();
    file2.deleteOnExit();
    Set<String> defaultFiles = new LinkedHashSet<String>();

    defaultFiles.add(file1.getName());
    defaultFiles.add(file2.getName());
    FileWatcher watcher =
      new FileWatcher(file1.getParentFile().getAbsoluteFile(), defaultFiles);

    file1.setLastModified(file1.lastModified() + 1000);
    List<String> files = watcher.getAllFiles();

    assertEquals(1, files.size());
    String modifiedFile = files.get(0);

    assertTrue(modifiedFile.startsWith("file1") && modifiedFile.endsWith("watcher"));
  }
}
