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
package com.google.jstestdriver.token;

import java.io.BufferedInputStream;
import java.io.Writer;



/**
 * Represents an parsed token.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public interface Token {
  //TODO(corysmith): extract the token matcher from the token.
  public abstract Token create(BufferedInputStream stream);

  public abstract void write(Writer out);

  public abstract boolean contains(char chr);
}
