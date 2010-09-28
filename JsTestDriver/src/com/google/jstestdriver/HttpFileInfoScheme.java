// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver;

import com.google.jstestdriver.hooks.FileInfoScheme;

/**
 * A {@link FileInfoScheme} for the "http:" URI scheme.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class HttpFileInfoScheme implements FileInfoScheme {

  public boolean matches(String path) {
    return path.startsWith("http:");
  }
}
