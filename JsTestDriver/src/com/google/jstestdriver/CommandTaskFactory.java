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

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Map;
import java.util.Set;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandTaskFactory {

  private final JsTestDriverFileFilter filter;
  private final Provider<HeartBeatManager> heartBeatProvider;
  private final FileLoader fileLoader;

  @Inject
  public CommandTaskFactory(JsTestDriverFileFilter filter,
                            FileLoader fileLoader,
                            Provider<HeartBeatManager> heartBeatProvider) {
    this.filter = filter;
    this.fileLoader = fileLoader;
    this.heartBeatProvider = heartBeatProvider;
  }

  public CommandTask getCommandTask(ResponseStream stream, Set<FileInfo> fileSet, String baseUrl,
      Server server, Map<String, String> params, boolean upload) {
    return new CommandTask(filter, stream, fileSet, baseUrl, server, params, heartBeatProvider.get(),
        fileLoader, upload);
  }
}
