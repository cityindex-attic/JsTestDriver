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

import com.google.jstestdriver.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reports the coverage information to the file system.
 * @author corysmith@google.com (Cory Smith)
 */
public class CoverageReporterAction implements Action {
  private static final Logger logger = LoggerFactory.getLogger(CoverageReporterAction.class);

  private final CoverageAccumulator accumulator;
  private final CoverageWriter writer;

  public CoverageReporterAction(CoverageAccumulator accumulator,
      CoverageWriter writer) {
    this.accumulator = accumulator;
    this.writer = writer;
  }

  public void run() {
    logger.debug("Writing coverage to {}", writer);
    accumulator.write(writer);
    writer.flush();
  }
}
