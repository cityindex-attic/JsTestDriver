/*
 * Copyright 2008 Google Inc.
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
package com.google.jstestdriver.server.handlers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class SlaveResourceHandlerTest extends TestCase {
  
  public void testRenderConsole() throws Exception {
    
  }

  public void testRenderRunner() throws Exception {
    
  }
  
  public void testRenderHeartbeat() throws Exception {
    
  }
  
  private ServletOutputStream buildServletOutputStream(final OutputStream oStream) {
    return new ServletOutputStream() {
      @Override
      public void write(int b) throws IOException {
        oStream.write(b);
      }
    };
  }
}