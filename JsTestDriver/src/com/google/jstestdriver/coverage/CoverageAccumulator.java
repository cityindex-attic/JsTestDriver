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

import com.google.inject.Singleton;

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
@Singleton
public class CoverageAccumulator {
  private ConcurrentLinkedQueue<FileCoverage> fileCoverages =
      new ConcurrentLinkedQueue<FileCoverage>();

  // TODO(corysmith): Track which browsers cover what.
  public void add(String browserId, Collection<FileCoverage> rawCoverage) {
    for (FileCoverage fileCoverage : rawCoverage) {
      fileCoverages.offer(fileCoverage);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fileCoverages == null) ? 0 : fileCoverages.hashCode());
    return result;
  }

  /** Writes all non-duplicate accumulated data to the coverage writer in the natural order. */
  public void write(CoverageWriter coverageWriter) {
    if (!fileCoverages.isEmpty()) {
      List<FileCoverage> rawCoverage = new LinkedList<FileCoverage>(fileCoverages);
      Collections.sort(rawCoverage);
      FileCoverage last = rawCoverage.get(0);
      for (FileCoverage fileCoverage : rawCoverage.subList(1, rawCoverage.size())) {
        FileCoverage aggregate = last.aggegrate(fileCoverage);
        if (aggregate == null) {
          last.write(coverageWriter);
          last = fileCoverage;
        } else {
          last = aggregate;
        }
      }
      last.write(coverageWriter);
    } else {
      System.out.println("No lines of coverage found.");
    }
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
    if (fileCoverages == null) {
      if (other.fileCoverages != null)
        return false;
    } else {
      synchronized (CoverageAccumulator.class) {
        Iterator<FileCoverage> ours = fileCoverages.iterator();
        Iterator<FileCoverage> theirs = other.fileCoverages.iterator();
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
    return String.format("%s(%s)", getClass().getSimpleName(), fileCoverages);
  }
}
