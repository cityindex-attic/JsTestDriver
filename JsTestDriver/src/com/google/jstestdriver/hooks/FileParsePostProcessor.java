// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.hooks;

import com.google.jstestdriver.FileInfo;

import java.util.Set;

/**
 * Allows post processing of parsed files.
 * @author corysmith@google.com (Cory Smith)
 */
public interface FileParsePostProcessor {
  /**
   * Process the iterator of FileInfo's and return an ordered set
   * @param iterator
   * @return An ordered set of fileInfos.
   */
  Set<FileInfo> process(Set<FileInfo> iterator);
}
