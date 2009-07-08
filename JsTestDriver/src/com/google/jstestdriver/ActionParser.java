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

import java.util.List;
import java.util.Set;

/**
 * Parses the flags into a sequence of actions.
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ActionParser {

  private final ActionFactory actionFactory;

  public ActionParser(ActionFactory actionFactory) {
    this.actionFactory = actionFactory;
  }

  public List<Action> parseFlags(Flags flags, Set<String> fileSet, String defaultServerAddress) {
    ActionSequenceBuilder builder = new ActionSequenceBuilder(actionFactory);
    builder.usingFiles(fileSet, flags.getPreloadFiles())
           .addTests(flags.getTests(),
                     flags.getTestOutput(),
                     flags.getVerbose(), 
                     flags.getCaptureConsole())
           .addCommands(flags.getArguments())
           .onBrowsers(flags.getBrowser())
           .reset(flags.getReset())
           .asDryRun(flags.getDryRun());
    if (flags.getPort() != -1) {
      builder.withLocalServerPort(flags.getPort());
    } else {
      builder.withRemoteServer(flags.getServer() != null ?
                               flags.getServer() : defaultServerAddress);
    }
    return builder.build();
  }
}
