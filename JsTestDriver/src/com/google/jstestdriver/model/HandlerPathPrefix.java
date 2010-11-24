/*
 * Copyright 20010 Google Inc.
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
package com.google.jstestdriver.model;

/**
 * Defines the interface for a hander path prefix, an arbitrary string prepended 
 * before all jstd specific server paths.
 * 
 * Example:
 * 
 * String prefix = 'jstd';
 * String server = 'http://localhost:8080';
 * String handlerPath = '/hello';
 * 
 * Creates:
 * String server = 'http://localhost:8080/jstd';
 * String handlerPath = '/jstd/hello';
 * 
 * While no prefix leaves them uinchaged.
 * 
 * @author corbinrsmith@gmail.com (Cory Smith)

 */
public interface HandlerPathPrefix {

  /** Adds the prefix to a server address. */
  String suffixServer(String server);

  /** Prefixes a path, making it absolute to the server root. */
  String prefixPath(String path);

  /**
   * Prefixes a path, making it absolute to the server root,
   * falling back on the supplied prefix.
   */
  String prefixPath(String path, String defaultPrefix);
}