// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class MockFileLoader implements FileLoader {
  private HashMap<FileInfo, String> expected = new HashMap<FileInfo, String>();

  public void addExpectation(FileInfo file, String contents) {
    expected.put(file, contents);
  }

  public List<FileInfo> loadFiles(
  		Collection<FileInfo> filesToLoad, boolean shouldReset) {
    List<FileInfo> loaded = new LinkedList<FileInfo>();
    for (FileInfo info : filesToLoad) {
      CommandTaskTest.assertTrue("File " + info + " was not found in " + expected.keySet(), expected
          .containsKey(info));
      loaded.add(new FileInfo(info.getFilePath(), info.getTimestamp(), -1, info.isPatch(), info
              .isServeOnly(), expected.get(info)));
    }
    return loaded;
  }
}