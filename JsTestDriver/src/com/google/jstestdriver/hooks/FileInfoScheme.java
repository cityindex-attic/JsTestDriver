// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.hooks;

import com.google.jstestdriver.FileInfo;

/**
 * Interface for registering {@link FileInfo}s whose paths do not need
 * to be prefixed with "/test/". Such {@link FileInfo}s are prefixed with a
 * URI scheme such as "http:", "embedded:" or "auth:".
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public interface FileInfoScheme {

  /**
   * @param path the path of the FileInfo.
   * @return true iff this scheme applies to the given FileInfo.
   */
  boolean matches(String path);
}
