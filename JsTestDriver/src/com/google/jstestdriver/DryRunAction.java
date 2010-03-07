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

import java.io.PrintStream;
import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class DryRunAction extends ThreadedAction {

  private final List<String> expressions;

  public static class DryRunActionResponseStream implements ResponseStream {

    private final Gson gson = new Gson();
    private final PrintStream out;

    public DryRunActionResponseStream(PrintStream out) {
      this.out = out;
    }

    public void finish() {
    }

    public void stream(Response response) {
      BrowserInfo browser = response.getBrowser();
      DryRunInfo dryRunInfo = gson.fromJson(response.getResponse(), DryRunInfo.class);

      out.println(String.format("%s %s: %s tests %s", browser.getName(), browser
          .getVersion(), dryRunInfo.getNumTests(), dryRunInfo.getTestNames()));
    }
  }

  public DryRunAction(ResponseStreamFactory responseStreamFactory, List<String> expressions) {
    super(responseStreamFactory);
    this.expressions = expressions;
  }

  @Override
  public void run(String id, JsTestDriverClient client) {
    if (expressions.size() == 1 && expressions.get(0).equals("all")) {
      client.dryRun(id, responseStreamFactory.getDryRunActionResponseStream());
    } else {
      client.dryRunFor(id, responseStreamFactory.getDryRunActionResponseStream(), expressions);
    }
  }
}
