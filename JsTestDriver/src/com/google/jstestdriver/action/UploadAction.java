/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.action;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.Action;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FileLoader;
import com.google.jstestdriver.JsTestDriverClient;
import com.google.jstestdriver.model.RunData;

import java.util.Set;

/**
 * Uploads the changed test files to the server
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class UploadAction implements Action {

  private final Set<FileInfo> fileSet;
  private final FileLoader loader;
  private final JsTestDriverClient client;

  @Inject
  public UploadAction(@Named("fileSet") Set<FileInfo> fileSet, FileLoader loader, JsTestDriverClient client) {
    this.fileSet = fileSet;
    this.loader = loader;
    this.client = client;
  }
  
  public RunData run(RunData testCase) {
    return testCase;
  }
}
