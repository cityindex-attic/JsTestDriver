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

import static org.mortbay.resource.Resource.newClassPathResource;

import org.mortbay.resource.Resource;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class SlaveResourceService {

  public static final String RESOURCE_LOCATION = "/com/google/jstestdriver/javascript";

  private final String baseResourceLocation;

  public SlaveResourceService(String baseResourceLocation) {
    this.baseResourceLocation = baseResourceLocation;
  }

  public void serve(String path, OutputStream out) throws IOException {
    Resource resource = newClassPathResource(baseResourceLocation + path);

    if (resource == null) {
      throw new IllegalArgumentException(baseResourceLocation + path + ": resource is null");
    }
    resource.writeTo(out, 0, resource.length());
  }
}
