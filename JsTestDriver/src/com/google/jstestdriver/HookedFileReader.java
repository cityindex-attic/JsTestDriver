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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import com.google.inject.Inject;
import com.google.jstestdriver.hooks.FileReaderHook;

/**
 * A simple filereader that processes a set of hooks before returning the file contents.
 * @author corysmith
 */
public class HookedFileReader implements FileReader {
  private final Set<FileReaderHook> hooks;

  @Inject
  public HookedFileReader(Set<FileReaderHook> hooks) {
    this.hooks = hooks;
  }

  public String readFile(String file)  {
    BufferedInputStream bis = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(file));
      StringBuilder sb = new StringBuilder();

      for (int c = bis.read(); c != -1; c = bis.read()) {
        sb.append((char) c);
      }
      String contents = sb.toString();
      for (FileReaderHook hook : hooks) {
        contents = hook.process(contents);
      }
      return contents;
    } catch (IOException e) {
      throw new RuntimeException("Impossible to read file: " + file, e);
    } finally {
      if (bis != null) {
        try {
          bis.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }
}
