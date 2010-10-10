// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.servlet.fileset;

import java.util.Collection;

import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.SlaveBrowser;



/**
 * Defines the handler for the FileSetServlet. Each handler processes the 
 * request and data, returning an object indicating the response.
 * @author corysmith@google.com (Cory Smith)
 */
public interface FileSetRequestHandler<T> {
  T handle(SlaveBrowser browser, Collection<FileInfo> data);
  boolean canHandle(String action);
}
