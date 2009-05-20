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
package com.google.jstestdriver;

import java.util.concurrent.CountDownLatch;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class SynchronousResponseStream implements ResponseStream {

  private final ActionResponseCallback callback;
  private final CountDownLatch latch;

  public SynchronousResponseStream(ActionResponseCallback callback, CountDownLatch latch) {
    this.callback = callback;
    this.latch = latch;
  }

  public void stream(Response response) {
    if (response.getError().equals("PANIC")) {
      System.err.println(response.getResponse());
      System.exit(1);
    }
    callback.callback(response);
  }

  public void finish() {
    latch.countDown();
  }  
}
