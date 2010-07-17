// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.browser;

import com.google.jstestdriver.FileInfo;

import java.util.Collections;
import java.util.List;

/**
 * Represents the state of the files loaded into a browser.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class BrowserFileSet {

  private List<FileInfo> fileToUpload;
  private List<FileInfo> extraFiles;


  public BrowserFileSet() {
    this(Collections.<FileInfo>emptyList(), Collections.<FileInfo>emptyList());
  }

  public BrowserFileSet(List<FileInfo> fileToUpload, List<FileInfo> extraFiles) {
    this.fileToUpload = fileToUpload;
    this.extraFiles = extraFiles;
  }

  /** Files that need to be uploaded to sync client, server and browser state. */
  public List<FileInfo> getFilesToUpload() {
    return fileToUpload;
  }

  /**
   * Extra files are files that have not been requested to be loaded into the
   * browser, but are already there.
   */
  public List<FileInfo> getExtraFiles() {
    return extraFiles;
  }
}
