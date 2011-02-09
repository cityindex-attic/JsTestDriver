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
package com.google.jstestdriver.config;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represent the metdata for a CmdLineFlag.
 *
 * @author Cory Smith (corbinrsmith@gmail.com) 
 */
public class CmdLineFlagMetaData {

  private final String flag;
  private final String usage;
  private final String value;

  public CmdLineFlagMetaData(String flag, String value, String usage) {
    this.flag = flag;
    this.value = value;
    this.usage = usage;
  }
  
  public void printUsage(OutputStream stream) throws IOException {
    stream.write(' ');
    stream.write(flag.getBytes());
    stream.write(' ');
    stream.write(value.getBytes());
    int prefixWidth = 25 - (flag.length() + value.length() + 1);
    for (int i = 0; i < prefixWidth; i++) {
      stream.write(' ');
    }
    stream.write(" : ".getBytes());
    stream.write(usage.getBytes());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((flag == null) ? 0 : flag.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CmdLineFlagMetaData other = (CmdLineFlagMetaData) obj;
    if (flag == null) {
      if (other.flag != null) return false;
    } else if (!flag.equals(other.flag)) return false;
    return true;
  }
}
