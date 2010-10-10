// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.browser;

import java.util.Collections;
import java.util.List;

import com.google.jstestdriver.FileInfo;

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((extraFiles == null) ? 0 : extraFiles.hashCode());
    result = prime * result + ((fileToUpload == null) ? 0 : fileToUpload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BrowserFileSet other = (BrowserFileSet) obj;
    if (extraFiles == null) {
      if (other.extraFiles != null) return false;
    } else if (!extraFiles.equals(other.extraFiles)) return false;
    if (fileToUpload == null) {
      if (other.fileToUpload != null) return false;
    } else if (!fileToUpload.equals(other.fileToUpload)) return false;
    return true;
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

  @Override
  public String toString() {
    return "BrowserFileSet [fileToUpload=" + fileToUpload + ", extraFiles=" + extraFiles + "]";
  }
}
