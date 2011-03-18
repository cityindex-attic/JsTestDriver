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
package com.google.jstestdriver.util;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 *
 *
 * @author Cory Smith (corbinrsmith@gmail.com) 
 */
public class RetryingCallable<V> implements Callable<V> {
  private final Logger logger = LoggerFactory.getLogger(RetryingCallable.class);
  private final Callable<V> callable;
  private final int retries;

  public RetryingCallable(int retries, Callable<V> callable) {
    this.retries = retries;
    this.callable = callable;
  }

  public V call() throws Exception {
    int retried = 0;
    // Save exceptions that are thrown after each failure so they can be printed if all retries fail
    List<Throwable> exceptions = Lists.newArrayList();

    // Loop until Retryable doesn't throw an exception or max number of tries is reached
    while (true) {
      try {
        return callable.call();
      } catch (Exception e) {
        retried++;
        exceptions.add(e);

        // Throw exception if number of tries has been reached
        if (retried == retries) {
          // Get a string of all exceptions that were thrown
          StringBuilder exceptionsString = new StringBuilder();
          for (int i = 0; i < exceptions.size(); i++) {
            exceptionsString.append("\nFailure " + (i + 1) + ": " + exceptions.get(i));
          }

          throw new RuntimeException("Failed after " + retried + " tries." + exceptionsString, e);
        } else {
          logger.info(
              "Retrying statement; number of times failed: " + retried + "; exception\n:" + e);
        }
      }
    }
  }

}
