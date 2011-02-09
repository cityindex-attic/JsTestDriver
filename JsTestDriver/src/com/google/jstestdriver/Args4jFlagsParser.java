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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import com.google.jstestdriver.config.CmdFlags;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parsers the Flags from args.
 * @author corysmith (corbinrsmith@google.com)
 *
 */
public class Args4jFlagsParser implements FlagsParser {

  private final CmdFlags cmdLineFlags;

  public static class StringListOptionHandler extends OptionHandler<List<String>> {
  
    public StringListOptionHandler(CmdLineParser parser, OptionDef option,
        Setter<? super List<String>> setter) {
      super(parser, option, setter);
    }
  
    /**
     * @see org.kohsuke.args4j.spi.OptionHandler#getDefaultMetaVariable()
     */
    @Override
    public String getDefaultMetaVariable() {
      return "VAR";
    }
  
    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
      String[] rawArgs = params.getParameter(0).split(",");
      List<String> args = new ArrayList<String>(rawArgs.length);
      for (String arg : rawArgs) {
        args.add(arg.trim());
      }
      setter.addValue(args);
      return 1;
    }
  }
  
  public static class LongOptionHandler extends OptionHandler<Long> {
    public LongOptionHandler(CmdLineParser parser, OptionDef option,
        Setter<? super Long> setter) {
      super(parser, option, setter);
    }

    @Override
    public String getDefaultMetaVariable() {
      return "VAR";
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
      try {
        setter.addValue(Long.parseLong(params.getParameter(0)));
        return 1;
      } catch (NumberFormatException e) {
        throw new CmdLineException(e);
      }
    }

  }

  /**
   * @param cmdLineFlags The additional flags not handled by the Args4jFlagsParser.
   */
  public Args4jFlagsParser(CmdFlags cmdLineFlags) {
    this.cmdLineFlags = cmdLineFlags;
  }

  public Flags parseArgument(String[] strings) throws CmdLineException {
    FlagsImpl flags = new FlagsImpl();
    CmdLineParser.registerHandler(List.class, StringListOptionHandler.class);
    CmdLineParser.registerHandler(Long.class, LongOptionHandler.class);
    CmdLineParser cmdLineParser = new CmdLineParser(flags);
    try {
      cmdLineParser.parseArgument(strings);
    } catch (CmdLineException e) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      cmdLineParser.printUsage(stream);
      throw new CmdLineException(e.getMessage() + "\n" + stream.toString());
    }
    if (strings.length == 0 || flags.getDisplayHelp()) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      cmdLineParser.printUsage(stream);
      cmdLineFlags.printUsage(stream);
      throw new CmdLineException(stream.toString());
    }
    return flags;
  }
}
