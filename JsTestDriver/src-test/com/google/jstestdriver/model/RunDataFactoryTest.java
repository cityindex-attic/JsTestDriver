// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.FileLoadPreProcessor;

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author corysmith@google.com (Your Name Here)
 *
 */
public class RunDataFactoryTest extends TestCase {
  public void testPreProcessFileSet() throws Exception {
    final FileInfo info = new FileInfo("foo.js", 12434, false, false, null);
    final FileInfo addedInfo = new FileInfo("addedfoo.js", 12434, false, false, null);
    final boolean shouldReset = false;

    FileLoadPreProcessor preProcessor = new FileLoadPreProcessor(){
      public List<FileInfo> process(List<FileInfo> files) {
        files.add(addedInfo);
        return new LinkedList<FileInfo>(files);
      }
    };

    final Set<FileInfo> fileSet = Sets.newLinkedHashSet();
    fileSet.add(info);
    final List<FileInfo> actual =
        Lists.newArrayList(new RunDataFactory(fileSet, Sets.newHashSet(preProcessor)).get()
            .getFileSet());

    assertEquals(info.getFilePath(), actual.get(0).getFilePath());
    assertEquals(info.getTimestamp(), actual.get(0).getTimestamp());
    assertEquals(info.isServeOnly(), actual.get(0).isServeOnly());

    assertEquals(addedInfo.getFilePath(), actual.get(1).getFilePath());
    assertEquals(addedInfo.getTimestamp(), actual.get(1).getTimestamp());
    assertEquals(addedInfo.isServeOnly(), actual.get(1).isServeOnly());
  }
}
