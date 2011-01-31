/*
 * Copyright 2009 Google Inc.
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

package com.google.jstestdriver;

import java.util.Collections;
import java.util.List;

/**
 * Handles the determination of which files to reload.
 * 
 * @author Cory Smith (corbinrsmith@gmail.com)
 */
public class DefaultFileFilter implements JsTestDriverFileFilter {
  public List<FileInfo> resolveFilesDeps(FileInfo file, List<FileInfo> fileSet) {
    int index = fileSet.indexOf(file);
    if (index == -1) {
      return Collections.singletonList(file);
    }
    return fileSet.subList(index, fileSet.size());
  }
}
