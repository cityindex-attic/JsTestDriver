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
package com.google.jstestdriver.html;

import com.google.jstestdriver.token.Node;
import com.google.jstestdriver.token.Token;

import java.io.Writer;
import java.util.List;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public abstract class HtmlDocNode implements Node {

  protected Token id;
  protected List<Token> html;

  public HtmlDocNode(Token id, List<Token> html) {
    this.id = id;
    this.html = html;
  }

  abstract public void write(Writer writer);

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (html == null ? 0 : html.hashCode());
    result = prime * result + (id == null ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!getClass().isInstance(obj)) {
      return false;
    }
    HtmlDocNode other = (HtmlDocNode) obj;
    if (html == null) {
      if (other.html != null) {
        return false;
      }
    } else if (!html.equals(other.html)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " [html=" + html + ", id=" + id + "]";
  }
}
