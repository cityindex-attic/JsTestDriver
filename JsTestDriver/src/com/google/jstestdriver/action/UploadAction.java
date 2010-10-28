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
import com.google.jstestdriver.Action;
import com.google.jstestdriver.FileUploader;
import com.google.jstestdriver.model.RunData;

/**
 * Uploads the test files to the server. Used by the standalone runner.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class UploadAction implements Action {

  private final FileUploader uploader;

  @Inject
  public UploadAction(FileUploader uploader) {
    this.uploader = uploader;
  }

  public RunData run(RunData runData) {
    uploader.uploadToServer(uploader.determineServerFileSet(runData.getFileSet()));
    return runData;
  }
}
