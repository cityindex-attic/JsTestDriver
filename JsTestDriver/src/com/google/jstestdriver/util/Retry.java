/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.util;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Retries a set of statements that may fail due to an exception being thrown.
 * 
 * @author Andrew Trenk
 */
public class Retry {
  /** A set of statements that can be retried. */
  public static interface Retryable<T> {
    /** Runs a set of statements that can be retried. */
    public T run() throws Exception;
  }

  private static final Logger logger = Logger.getLogger(Retry.class.getName());

  private int numTries;

  /**
   * @param numTries number of times to try
   */
  public Retry(int numTries) {
    Preconditions.checkArgument(numTries > 0);
    this.numTries = numTries;
  }

  /** Retries the given Retryable. */
  public <T> T retry(Retryable<T> retryable) {
    int numRetries = 0;
    // Save exceptions that are thrown after each failure so they can be printed if all retries fail
    List<Throwable> exceptions = Lists.newArrayList();

    // Loop until Retryable doesn't throw an exception or max number of tries is reached
    while (true) {
      try {
        return retryable.run();
      } catch (Exception e) {
        numRetries++;
        exceptions.add(e);

        // Throw exception if number of tries has been reached
        if (numRetries == numTries) {
          // Get a string of all exceptions that were thrown
          StringBuilder exceptionsString = new StringBuilder();
          for (int i = 0; i < exceptions.size(); i++) {
            exceptionsString.append("\nFailure " + (i + 1) + ": " + exceptions.get(i));
          }

          throw new RuntimeException("Failed after " + numTries + " tries." + exceptionsString, e);
        } else {
          logger.info(
              "Retrying statement; number of times failed: " + numRetries + "; exception\n:" + e);
        }
      }
    }
  }
}