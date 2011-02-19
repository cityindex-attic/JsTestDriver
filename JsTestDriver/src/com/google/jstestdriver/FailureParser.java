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

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.model.HandlerPathPrefix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * Parses failures.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FailureParser {
  private static final Logger logger = LoggerFactory.getLogger(FailureParser.class);
  private final Gson gson = new Gson();
  private final HandlerPathPrefix pathPrefix;

  @Inject
  public FailureParser(@Named("serverHandlerPrefix") HandlerPathPrefix pathPrefix) {
    this.pathPrefix = pathPrefix;
  }

  public List<Failure> parse(String failure) {
    String message = "";
    List<Failure> failures;
    String stackStripPrefix = pathPrefix.prefixPath("/static/");
    try {
      Collection<JsException> exceptions =
          gson.fromJson(failure, new TypeToken<Collection<JsException>>() {}.getType());
      failures = Lists.newArrayListWithExpectedSize(exceptions.size());
      for (JsException exception : exceptions) {
        if (exception.getName() != null && !exception.getName().isEmpty()) {
          message = String.format("%s: %s", exception.getName(), exception.getMessage());
        } else {
          message = exception.getMessage();
        }
        String errorStack = exception.getStack();
        String[] lines = errorStack.split("\n");

        final List<String> stack = Lists.newLinkedList();
        for (String l : lines) {
          if (!l.contains(stackStripPrefix)) {
            stack.add(l);
          }
        }
        failures.add(new Failure(message, stack));
      }
    } catch (Exception e) {
      logger.error("Error converting JsExceptions.", e);
      failures = Lists.newArrayList(new Failure(failure, Lists.<String>newArrayList()));
    }
    return failures;
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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((message == null) ? 0 : message.hashCode());
      result = prime * result + ((stack == null) ? 0 : stack.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Failure other = (Failure) obj;
      if (message == null) {
        if (other.message != null) return false;
      } else if (!message.equals(other.message)) return false;
      if (stack == null) {
        if (other.stack != null) return false;
      } else if (!stack.equals(other.stack)) return false;
      return true;
    }

    @Override
    public String toString() {
      return "Failure [stack=" + stack + ", message=" + message + "]";
    }
  }
}
