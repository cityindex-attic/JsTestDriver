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

import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FailureParser {
  private final Gson gson = new Gson();

  public Failure parse(String failure) {
    final List<String> stack = new LinkedList<String>();
    String message = "";
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
    return new Failure(message, stack);
  }

  public static class Failure {
    private final List<String> stack;
    private final String message;

    public Failure(String message, List<String> stack) {
      this.message = message;
      this.stack = stack;
    }

    public String getMessage() {
      return message;
    }

    public List<String> getStack() {
      return stack;
    }
  }

}
