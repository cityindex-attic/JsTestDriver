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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Accumulates all the lines recorded during a test run.
 * @author corysmith@google.com (Cory Smith)
 */
public class CoverageAccumulator {
  private ConcurrentLinkedQueue<CoveredLine> lines = new ConcurrentLinkedQueue<CoveredLine>();

  // TODO(corysmith): Track which browsers cover what.
  public void add(String browserId, Collection<CoveredLine> coveredLines) {
    for (CoveredLine coveredLine : coveredLines) {
      lines.offer(coveredLine);
    }
  }
  

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((lines == null) ? 0 : lines.hashCode());
    return result;
  }

  /** Writes all non-duplicate accumulated data to the coverage writer in the natural order. */
  public void write(CoverageWriter coverageWriter) {
    List<CoveredLine> covered = new LinkedList<CoveredLine>(lines);
    Collections.sort(covered);
    CoveredLine last = covered.get(0);
    for (CoveredLine coveredLine : covered.subList(0, covered.size())) {
      CoveredLine aggregate = last.aggegrate(coveredLine);
      if (aggregate == null) {
        coverageWriter.addLine(last);
        last = coveredLine;
      } else {
        last = aggregate;
      }
    }
    coverageWriter.addLine(last);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CoverageAccumulator other = (CoverageAccumulator) obj;
    if (lines == null) {
      if (other.lines != null)
        return false;
    } else {
      synchronized (CoverageAccumulator.class) {
        Iterator<CoveredLine> ours = lines.iterator();
        Iterator<CoveredLine> theirs = other.lines.iterator();
        while(ours.hasNext() && theirs.hasNext()) {
          if (!ours.next().equals(theirs.next())) {
            return false;
          }
        }
        if (ours.hasNext() || theirs.hasNext()) {
          return false;
        }
      }
    }
    return true;
  }
  @Override
  public String toString() {
    return String.format("%s(%s)", getClass().getSimpleName(), lines);
  }
}
