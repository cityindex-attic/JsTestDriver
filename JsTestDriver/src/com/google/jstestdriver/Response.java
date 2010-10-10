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

import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.reflect.TypeToken;


/**
 * The Response from the browser by way of the server.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
// TODO(corysmith): move to the protocol package
public class Response {
  
  public static enum ResponseType {
    FILE_LOAD_RESULT(LoadedFiles.class),
    REGISTER_RESULT(null),
    TEST_RESULT(new TypeToken<Collection<TestResult>>() {}.getType()),
    TEST_QUERY_RESULT(null),
    RESET_RESULT(null),
    BROWSER_PANIC(BrowserPanic.class),
    UNKNOWN(null),
    BROWSER_READY(null),
    COMMAND_RESULT(null),
    LOG(null);

    public final Type type;

    ResponseType(Type type) {
      this.type = type;
    }
  }

  private String type;
  private String response = "";
  private BrowserInfo browser = new BrowserInfo();
  private String error = "";
  private long executionTime = 0L;

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public BrowserInfo getBrowser() {
    return browser;
  }

  public void setBrowser(BrowserInfo browser) {
    this.browser = browser;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public long getExecutionTime() {
    return executionTime;
  }

  public void setExecutionTime(long executionTime) {
    this.executionTime = executionTime;
  }
  
  /** Sets the string representation of the type. */
  public void setType(String type) {
    this.type = type;
  }
  
  /** Gets the string representation of the type. */
  public ResponseType getResponseType() {
    if (type == null) {
      return ResponseType.UNKNOWN;
    }
    return ResponseType.valueOf(type);
  }
  
  /** The type for Gson use to deserialize the response. */
  public Type getGsonType() {
    return ResponseType.valueOf(type).type;
  }

  @Override
  public String toString() {
    return "Response (\nbrowser=[" + browser + "], \nerror=[" + error + "], \nexecutionTime=[" + executionTime
        + "], \nresponse=[" + response + "], \ntype=[" + type + "])";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((browser == null) ? 0 : browser.hashCode());
    result = prime * result + ((error == null) ? 0 : error.hashCode());
    result = prime * result + (int) (executionTime ^ (executionTime >>> 32));
    result = prime * result + ((response == null) ? 0 : response.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Response)) return false;
    Response other = (Response) obj;
    if (browser == null) {
      if (other.browser != null) return false;
    } else if (!browser.equals(other.browser)) return false;
    if (error == null) {
      if (other.error != null) return false;
    } else if (!error.equals(other.error)) return false;
    if (executionTime != other.executionTime) return false;
    if (response == null) {
      if (other.response != null) return false;
    } else if (!response.equals(other.response)) return false;
    if (type == null) {
      if (other.type != null) return false;
    } else if (!type.equals(other.type)) return false;
    return true;
  }
}
