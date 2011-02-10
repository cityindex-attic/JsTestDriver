/*
 * Copyright 2011 Google Inc.
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
package com.google.jstestdriver.output;

import java.io.File;

/**
 * Escapes and formats a filename.
 *
 * @author Cory Smith (corbinrsmith@gmail.com) 
 */
public class FileNameFormatter {
  public String format(String path, String format) {
    String escaped = path
        .replace(File.separator, "#")
        .replace('/', '#')
        .replace('\\', '#')
        .replace(">", "#")
        .replace(".", "_")
        .replace(";", "_")
        .replace("+", "_")
        .replace(",", "_")
        .replace("<", "#")
        .replace("?", "#")
        .replace("*", "#")
        .replace(" ", "_");

    return String.format(format, escaped.length() > 200 ? escaped.substring(0, 200) : escaped);
  }
}
