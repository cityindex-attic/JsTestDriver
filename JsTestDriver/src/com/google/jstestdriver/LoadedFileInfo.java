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

/**
 * A loaded file is never loaded. This useful for plugins to inject source without loading it.
 * The plugin is responsible for any reloading it needs to do.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class LoadedFileInfo extends FileInfo {

  public LoadedFileInfo(String fileName, long timestamp, boolean isPatch,
      boolean serveOnly, String data) {
    super(fileName, timestamp, isPatch, serveOnly, data);
  }
  @Override
  public boolean canLoad() {
    return false;
  }
}
