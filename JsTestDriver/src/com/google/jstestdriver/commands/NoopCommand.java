// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.commands;

import com.google.gson.Gson;
import com.google.jstestdriver.Command;
import com.google.jstestdriver.JsonCommand;

/**
 * A command that does nothing.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class NoopCommand extends Command {

  public NoopCommand() {
    super(new Gson().toJson(new JsonCommand(JsonCommand.CommandType.NOOP, null)));
  }
}
