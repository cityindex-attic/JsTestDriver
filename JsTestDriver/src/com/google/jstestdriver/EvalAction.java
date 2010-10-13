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

import com.google.jstestdriver.model.JstdTestCase;
import com.google.jstestdriver.model.RunData;

import java.io.PrintStream;


/**
 * Send javascript to the browser for evaluation.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class EvalAction implements BrowserAction {

  private final String cmd;
  private final ResponseStreamFactory responseStreamFactory;

  public EvalAction(ResponseStreamFactory responseStreamFactory, String cmd) {
    this.responseStreamFactory = responseStreamFactory;
    this.cmd = cmd;
  }

  public static class EvalActionResponseStream implements ResponseStream {

    private final PrintStream out;

    public EvalActionResponseStream(PrintStream out) {
      this.out = out;
    }

    public void finish() {
    }

    public void stream(Response response) {
      BrowserInfo browser = response.getBrowser();

      out.println(String.format("%s %s: %s", browser.getName(), browser.getVersion(),
          response.getResponse()));
    }
  }

  public ResponseStream run(String id, JsTestDriverClient client, RunData runData, JstdTestCase testCase) {
    final ResponseStream responseStream = responseStreamFactory.getEvalActionResponseStream();
    client.eval(id, responseStream, getCmd(), testCase);
    return responseStream;
  }

  public String getCmd() {
    return cmd;
  }
}
