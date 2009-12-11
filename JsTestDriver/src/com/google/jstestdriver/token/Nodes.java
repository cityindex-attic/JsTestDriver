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

import com.google.common.collect.Lists;

import java.io.Writer;
import java.util.List;

public class Nodes {
  List<Node> nodes = Lists.<Node>newArrayList();

  public Nodes add(Node node) {
    nodes.add(node);
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Nodes other = (Nodes) obj;
    if (nodes == null) {
      if (other.nodes != null)
        return false;
    } else if (!nodes.equals(other.nodes))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Nodes [nodes=" + nodes + "]";
  }

  public void write(Writer writer) {
    for (Node node : nodes) {
      node.write(writer);
    }
  }
}