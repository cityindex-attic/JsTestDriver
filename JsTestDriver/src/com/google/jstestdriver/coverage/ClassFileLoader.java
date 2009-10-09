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
package com.google.jstestdriver.coverage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Loads files off the classpath.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class ClassFileLoader {

  public String load(String path) {
    InputStream resourceStream =
        new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(path));
    StringBuilder sb = new StringBuilder();

    try {
      for (int c = resourceStream.read(); c != -1; c = resourceStream.read()) {
        sb.append((char) c);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return sb.toString();
  }
  
  public Reader loadToReader(String path) {
    InputStream resourceStream =
      new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(path));
    return new InputStreamReader(resourceStream);
  }
}
