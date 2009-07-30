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

import com.google.gson.Gson;

import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class DryRunAction extends ThreadedAction {

  public static class DryRunActionResponseStream implements ResponseStream {

    private final Gson gson = new Gson();

    private static class DryRunInfo {
      private int numTests;
      private List<String> testNames;

      /**
       * @return the numTests
       */
      public int getNumTests() {
        return numTests;
      }

      /**
       * @return the testNames
       */
      public List<String> getTestNames() {
        return testNames;
      }      
    }

    public void finish() {
    }

    public void stream(Response response) {
      BrowserInfo browser = response.getBrowser();
      DryRunInfo dryRunInfo = gson.fromJson(response.getResponse(), DryRunInfo.class);

      System.out.println(String.format("%s %s: %s tests %s", browser.getName(), browser
          .getVersion(), dryRunInfo.getNumTests(), dryRunInfo.getTestNames()));
    }
  }

  public DryRunAction(ResponseStreamFactory responseStreamFactory) {
    super(responseStreamFactory);
  }

  @Override
  public void run(String id, JsTestDriverClient client) {
    client.dryRun(id, responseStreamFactory.getDryRunActionResponseStream());
  }
}
