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
 * Represents a packet of streamed response.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class StreamMessage {
  private boolean last;
  private Response response;

  public StreamMessage() {}
  
  public StreamMessage(boolean last, Response response) {
    this.last = last;
    this.response = response;
  }

  public StreamMessage(Response response, boolean last) {
    this.last = last;
    this.response = response;
  }

  public boolean isLast() {
    return last;
  }

  public Response getResponse() {
    return response;
  }

  @Override
  public String toString() {
    return "StreamMessage [\n\tlast=" + last + ",\n\tresponse=" + response + "]";
  }
  
}
