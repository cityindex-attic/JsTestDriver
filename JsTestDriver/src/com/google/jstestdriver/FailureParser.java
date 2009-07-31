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

import java.util.LinkedList;
import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FailureParser {

  private final Gson gson = new Gson();

  private final List<String> stack = new LinkedList<String>();
  private String message = "";

  public void parse(String failure) {
    try {
      JsException exception = gson.fromJson(failure, JsException.class);

      message = exception.getMessage();
      String errorStack = exception.getStack();
      String[] lines = errorStack.split("\n");

      for (String l : lines) {
        if (l.contains("/test/")) {
          stack.add(l);
        }
      }
    } catch (Exception e) {
      message = failure;
    }
  }

  public String getMessage() {
    return message;
  }

  public List<String> getStack() {
    return stack;
  }
}
