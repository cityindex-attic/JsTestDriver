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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class SlaveResourceServiceTest extends TestCase {

  public void testServeResource() throws Exception {
    String location = getClass().getPackage().getName().replace(".", "/");
    SlaveResourceService service = new SlaveResourceService(location);
    OutputStream out = new ByteArrayOutputStream();

    service.serve("/Test.file", out);
    assertTrue(out.toString().length() > 0);
    out.close();
  }
}
