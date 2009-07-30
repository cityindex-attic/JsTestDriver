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


/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class EvalAction extends ThreadedAction {

  private final String cmd;

  public EvalAction(ResponseStreamFactory responseStreamFactory, String cmd) {
    super(responseStreamFactory);
    this.cmd = cmd;
  }

  public static class EvalActionResponseStream implements ResponseStream {

    public void finish() {
    }

    public void stream(Response response) {
      BrowserInfo browser = response.getBrowser();

      System.out.println(String.format("%s %s: %s", browser.getName(), browser.getVersion(),
          response.getResponse()));
    }
  }

  @Override
  public void run(String id, JsTestDriverClient client) {
    client.eval(id, responseStreamFactory.getEvalActionResponseStream(), getCmd());
  }

  public String getCmd() {
    return cmd;
  }
}
