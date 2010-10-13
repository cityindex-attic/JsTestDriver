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
import com.google.inject.name.Named;
import com.google.jstestdriver.hooks.FileInfoScheme;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.util.StopWatch;

import java.util.Map;
import java.util.Set;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandTaskFactory {

  private final JsTestDriverFileFilter filter;
  private final FileLoader fileLoader;
  private final StopWatch stopWatch;
  private final Set<FileInfoScheme> schemes;
  private final HandlerPathPrefix pathPrefix;

  @Inject
  public CommandTaskFactory(JsTestDriverFileFilter filter,
                            FileLoader fileLoader,
                            Provider<HeartBeatManager> heartBeatProvider,
                            StopWatch stopWatch,
                            Set<FileInfoScheme> schemes,
                            @Named("serverHandlerPrefix") HandlerPathPrefix pathPrefix
                            ) {
    this.filter = filter;
    this.fileLoader = fileLoader;
    this.stopWatch = stopWatch;
    this.schemes = schemes;
    this.pathPrefix = pathPrefix;
  }

  public CommandTask getCommandTask(ResponseStream stream, String baseUrl, Server server,
      Map<String, String> params, boolean upload) {
    return new CommandTask(stream, baseUrl, server, params, upload, stopWatch,
        new FileUploader(stopWatch, server, baseUrl, fileLoader, filter, schemes, pathPrefix));
  }
}
